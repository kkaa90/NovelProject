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
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.e.myapplication.AccountInfo
import com.e.myapplication.RouteAction
import com.e.myapplication.dataclass.ImageUpload
import com.e.myapplication.dataclass.PostBoard
import com.e.myapplication.dataclass.PostBoardResponse
import com.e.myapplication.dataclass.reportState
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
fun WritingBoard(viewModel: FreeBoardViewModel, routeAction: RouteAction) {
    val context = LocalContext.current
    val v = viewModel
    var requestBody: RequestBody
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            if(uri!=null) {
                v.imageUri.add(uri)
                v.bitmap.add(
                    if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    } else {
                        val source =
                            uri.let { ImageDecoder.createSource(context.contentResolver, it) }
                        source.let { ImageDecoder.decodeBitmap(it) }
                    }
                )
                v.files.add(bitmapToFile(v.bitmap[v.bitmap.size - 1]!!, v.bitmap.size.toString() + ".png"))
                println(v.files[v.bitmap.size - 1]!!.absolutePath)
                requestBody =
                    RequestBody.create(MediaType.parse("image/*"), v.files[v.bitmap.size - 1]!!)
                v.body.add(
                    MultipartBody.Part.createFormData(
                        "images",
                        v.bitmap.size.toString() + ".png",
                        requestBody
                    )
                )
            }
        }
    BackHandler() {
        v.backVisibility=true
    }
    Box(){
        AnimatedVisibility(visible = v.backVisibility) {
            BackAskingDialog(viewModel = v, routeAction = routeAction)
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "제목")
        OutlinedTextField(value = v.title, onValueChange = { v.title = it }, maxLines = 1)
        Text(text = "내용")
        OutlinedTextField(value = v.content, onValueChange = { v.content = it })
        Row {
            for (i: Int in 0 until v.bitmap.size) {
                Image(
                    bitmap = v.bitmap[i]!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    v.imageUri.remove(v.imageUri[i])
                                    v.bitmap.remove(v.bitmap[i])
                                    v.files.remove(v.files[i])
                                    v.body.remove(v.body[i])
                                }
                            )
                        }
                )
            }
            Button(onClick = {
                if (v.bitmap.size > 3) {
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
            if (v.bitmap.size == 0) {
                Toast.makeText(
                    context,
                    "이미지가 없습니다.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                v.uploadImage()
            }

        }) {
            Text("이미지 등록")
        }
        Button(onClick = { println(v.imageNum) }) {
            Text(text = "테스트")
        }
        Button(onClick = {
            if (v.body.size!=0) {
                println("파일 있음")
                v.writeBoard("1", routeAction)
            } else {
                println("파일 없음")
                v.writeBoard("0", routeAction)
            }
        }) {
            Text(text = "글쓰기")
        }
    }

}

@Composable
fun BackAskingDialog(viewModel: FreeBoardViewModel, routeAction: RouteAction) {
    Dialog(onDismissRequest = { viewModel.backVisibility=false }) {
        Surface(
            modifier = Modifier
                .wrapContentSize(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "게시글 작성 취소", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "게시글 작성을 취소하시겠습니까?\n입력한 내용은 삭제됩니다.")
                Spacer(modifier = Modifier.height(20.dp))
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row {
                        OutlinedButton(onClick = { viewModel.backVisibility=false }) {
                            Text(text = "취소")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedButton(onClick = {
                            viewModel.backPressed(routeAction)
                        }) {
                            Text(text = "확인")
                        }
                    }
                }

            }
        }

    }
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

