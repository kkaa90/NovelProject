package com.e.myapplication.novel

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.e.myapplication.RouteAction
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@Composable
fun WritingNCover(viewModel: NovelViewModel, routeAction: RouteAction) {
    val context = LocalContext.current
    var requestBody: RequestBody
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                viewModel.nCImageUri = uri
                viewModel.nCBitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source =
                        uri.let { ImageDecoder.createSource(context.contentResolver, it) }
                    source.let { ImageDecoder.decodeBitmap(it) }
                }
                viewModel.nCFile = bitmapToFile(viewModel.nCBitmap!!, "nC.png")
                requestBody = RequestBody.create(MediaType.parse("image/*"), viewModel.nCFile!!)
                viewModel.nCBody = MultipartBody.Part.createFormData(
                    "images",
                    viewModel.nCFile!!.name,
                    requestBody
                )
            }

        }
    Box(){
        AnimatedVisibility(visible = viewModel.nCBackVisibility) {
            NCBackAskingDialog(viewModel = viewModel, routeAction = routeAction)
        }
    }
    BackHandler() {
        viewModel.nCBackVisibility = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "제목")
        OutlinedTextField(
            value = viewModel.nCTitle,
            onValueChange = { viewModel.nCTitle = it },
            maxLines = 1
        )
        Text(text = "내용")
        OutlinedTextField(value = viewModel.nCContent, onValueChange = { viewModel.nCContent = it })
        Row {
            if (viewModel.nCBitmap != null) {
                Image(bitmap = viewModel.nCBitmap!!.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier
                        .size(100.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    viewModel.nCImageUri = null
                                    viewModel.nCBitmap = null
                                    viewModel.nCFile = null
                                    viewModel.nCBody = null
                                }
                            )
                        })
            }
            Button(onClick = { launcher.launch("image/*") }) {
                Text(text = "이미지 선택")
            }
        }
        Button(onClick = {
            if (viewModel.nCBitmap==null) {
                Toast.makeText(
                    context,
                    "이미지가 없습니다.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                viewModel.uploadNCImage()
            }

        }) {
            Text("이미지 등록")
        }
        Row() {
            Text(text = "태그 : ")
            for(i in viewModel.tags.indices){
                Text(text = "#${viewModel.tags[i]} ")
            }
        }
        
        Button(onClick = { viewModel.tags.clear() }) {
            Text("태그 초기화")
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = viewModel.tag,
                onValueChange = { viewModel.tag = it },
                maxLines = 1
            )
            Button(onClick = {
                viewModel.tags.add(viewModel.tag)
                viewModel.tag = ""
            }) {
                Text("태그 추가")
            }
        }

        Button(onClick = {
            viewModel.writeNCover(routeAction)
        }) {
            Text(text = "작성")
        }
    }
}
@Composable
fun NCBackAskingDialog(viewModel: NovelViewModel, routeAction: RouteAction) {
    Dialog(onDismissRequest = { viewModel.nCBackVisibility=false }) {
        Surface(
            modifier = Modifier
                .wrapContentSize(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "커버 작성 취소", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "커버 작성을 취소하시겠습니까?\n입력한 내용은 삭제됩니다.")
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    Modifier
                        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row {
                        OutlinedButton(onClick = { viewModel.nCBackVisibility=false }) {
                            Text(text = "취소")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedButton(onClick = {
                            viewModel.nCBackPressed(routeAction)
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