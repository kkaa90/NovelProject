package com.e.myapplication.board

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.myapplication.AccountInfo
import com.e.myapplication.dataclass.ImageUpload
import com.e.myapplication.dataclass.PostBoard
import com.e.myapplication.dataclass.PostBoardResponse
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.e.myapplication.user.LoginActivity
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

class WriteFreeBoardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting7()
                }
            }
        }

    }
}

@Composable
fun Greeting7() {
    val context = LocalContext.current
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }

    val writeBoard = RetrofitClass
    val service = writeBoard.api
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imageUri = remember { mutableStateListOf<Uri?>() }
    var bitmap = remember { mutableStateListOf<Bitmap?>() }
    var files = remember {
        mutableStateListOf<File?>()
    }
    var f = false
    var requestBody: RequestBody
    var body = remember {
        mutableStateListOf<MultipartBody.Part?>()
    }
    var imageNum by remember {
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

                val ac = read()
                println(body.size)
                val upI = service.uploadImageTest(ac.authorization.toString(), body)
                upI.enqueue(object : retrofit2.Callback<ImageUpload> {
                    override fun onResponse(
                        call: Call<ImageUpload>,
                        response: Response<ImageUpload>
                    ) {
                        imageNum = response.body()?.msg.toString()
                        println(imageNum)
                        if (imageNum == "JWT expiration") {
                            Toast.makeText(
                                context,
                                "토큰이 만료되었습니다.\n 다시 로그인 해주세요.",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                            upI.cancel()

                        }
                        println("이미지 : " + response.body()!!.msg)
                        println(imageNum)
                    }

                    override fun onFailure(call: Call<ImageUpload>, t: Throwable) {
                        t.printStackTrace()
                    }

                })
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
                    imageNum,
                    "0"
                )
                wB(context, postBoard)

            } else {
                println("파일 없음")
                val postBoard =
                    PostBoard(content, "", "0", title, ac.memNick.toString(), "1", "")
                wB(context, postBoard)
            }


        }) {
            Text(text = "글쓰기")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview9() {
    MyApplicationTheme {
        Greeting7()
    }
}

fun wB(context: Context, postBoard: PostBoard) {
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
                    Handler(Looper.getMainLooper()).postDelayed({ wB(context, postBoard) }, 1000)
                }
                "OK" -> {
                    println("글쓰기 성공")
                    (context as Activity).finish()
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