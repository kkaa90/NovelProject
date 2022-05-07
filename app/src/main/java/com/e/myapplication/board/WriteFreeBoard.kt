package com.e.myapplication.board

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.myapplication.AccountInfo
import com.e.myapplication.RouteAction
import com.e.myapplication.dataclass.ImageUpload
import com.e.myapplication.dataclass.PostBoard
import com.e.myapplication.dataclass.PostBoardResponse
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.user.ProtoRepository
import com.e.myapplication.user.getAToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@Composable
fun WriteBoard(routeAction: RouteAction) {
    val context = LocalContext.current
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    val imageUri = remember { mutableStateListOf<Uri?>() }
    val bitmap = remember { mutableStateListOf<Bitmap?>() }
    val files = remember {
        mutableStateListOf<File?>()
    }
    var requestBody: RequestBody
    val body = remember {
        mutableStateListOf<MultipartBody.Part?>()
    }
    var imageNum = remember {
        mutableStateOf("")
    }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            if(uri!=null) {
                imageUri.add(uri)
                println(imageUri)
                bitmap.add(
                    if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    } else {
                        val source =
                            uri.let { ImageDecoder.createSource(context.contentResolver, it) }
                        source.let { ImageDecoder.decodeBitmap(it) }
                    }
                )
                files.add(bitmapToFile(bitmap[bitmap.size - 1]!!, bitmap.size.toString() + ".png"))
                println(files[bitmap.size - 1]!!.absolutePath)
                requestBody =
                    RequestBody.create(MediaType.parse("image/*"), files[bitmap.size - 1]!!)
                body.add(
                    MultipartBody.Part.createFormData(
                        "images",
                        bitmap.size.toString() + ".png",
                        requestBody
                    )
                )
            }
        }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "제목")
        OutlinedTextField(value = title, onValueChange = { title = it }, maxLines = 1)
        Text(text = "내용")
        OutlinedTextField(value = content, onValueChange = { content = it })
        Row {
            for (i: Int in 0 until bitmap.size) {
                Image(
                    bitmap = bitmap[i]!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    imageUri.remove(imageUri[i])
                                    bitmap.remove(bitmap[i])
                                    files.remove(files[i])
                                    body.remove(body[i])
                                }
                            )
                        }
                )
            }
            Button(onClick = {
                if (bitmap.size > 3) {
                    Toast.makeText(
                        context,
                        "이미지는 최대 3개까지 가능합니다.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    launcher.launch("image/*")
                }

            }) {
                Text(text = "이미지 선택")
            }

        }
        Button(onClick = {
            if (bitmap.size == 0) {
                Toast.makeText(
                    context,
                    "이미지가 없습니다.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                uploadImages(context,body,routeAction,imageNum)
            }

        }) {
            Text("이미지 등록")
        }
        Button(onClick = { println(imageNum) }) {
            Text(text = "테스트")
        }
        Button(onClick = {


            val ac = read()
            if (body.size!=0) {
                println("파일 있음")
                val postBoard = PostBoard(
                    content,
                    "",
                    "1",
                    title,
                    ac.memNick.toString(),
                    imageNum.value,
                    "0"
                )
                wB(context, postBoard, routeAction)

            } else {
                println("파일 없음")
                val postBoard =
                    PostBoard(content, "", "0", title, ac.memNick.toString(), "1", "")
                wB(context, postBoard, routeAction)
            }
            content = ""
            title = ""
            imageNum.value=""


        }) {
            Text(text = "글쓰기")
        }
    }

}
fun uploadImages(context: Context, body: SnapshotStateList<MultipartBody.Part?>, routeAction: RouteAction, imageNum : MutableState<String>){
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }

    val ac = read()
    val retrofitClass = RetrofitClass.api.uploadImageTest(ac.authorization.toString(), body)
    retrofitClass.enqueue(object : retrofit2.Callback<ImageUpload> {
        override fun onResponse(
            call: Call<ImageUpload>,
            response: Response<ImageUpload>
        ) {
            imageNum.value = response.body()?.msg.toString()
            println(imageNum)
            if (imageNum.value == "JWT expiration") {
                getAToken(context)
                retrofitClass.cancel()
                Handler(Looper.getMainLooper()).postDelayed({ uploadImages(context, body, routeAction, imageNum) }, 1000)
            }
            else {
                Toast.makeText(
                    context,
                    "이미지가 업로드 되었습니다.",
                    Toast.LENGTH_LONG
                ).show()
            }
            println("이미지 : " + response.body()!!.msg)
            println(imageNum)
        }

        override fun onFailure(call: Call<ImageUpload>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun wB(context: Context, postBoard: PostBoard, routeAction: RouteAction) {
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }

    val ac = read()
    val writeBoard = RetrofitClass
    val service = writeBoard.api
    val wb = service.writeBoard(
        ac.authorization.toString(),
        postBoard
    )


    wb.enqueue(object : retrofit2.Callback<PostBoardResponse> {
        override fun onResponse(
            call: Call<PostBoardResponse>,
            response: Response<PostBoardResponse>
        ) {
            val r = response.body()?.msg
            println(response.body().toString())
            println(r)
            when (r) {
                "JWT expiration" -> {
                    println("토큰 만료")
                    getAToken(context)
                    wb.cancel()
                    Handler(Looper.getMainLooper()).postDelayed({ wB(context, postBoard, routeAction) }, 1000)
                }
                "OK" -> {
                    println("글쓰기 성공")
                    routeAction.goBack()
                }
                else -> {
                    println("글쓰기 오류")
                }
            }
            wb.cancel()
        }

        override fun onFailure(call: Call<PostBoardResponse>, t: Throwable) {
            t.printStackTrace()
        }
    })
}

fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
    //create a file to write bitmap data
    var file: File? = null
    return try {
        file = File(
            Environment.getExternalStorageDirectory().toString() + File.separator + fileNameToSave
        )
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
        val bitmapdata = bos.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        file // it will return null
    }
}
