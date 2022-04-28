package com.e.myapplication.novel

import android.app.Activity
import android.content.Context
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
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.myapplication.AccountInfo
import com.e.myapplication.dataclass.ImageUpload
import com.e.myapplication.dataclass.SNCR
import com.e.myapplication.dataclass.SendNCover
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.e.myapplication.user.ProtoRepository
import com.e.myapplication.user.getAToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class WriteNCoverActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting4()
                }
            }
        }
    }
}

@Composable
fun Greeting4() {
    val context = LocalContext.current
    var title by remember { mutableStateOf("")}
    var content by remember { mutableStateOf("")}
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var tag by remember { mutableStateOf("") }
    val tags = remember {mutableListOf<String>()}
    var file: File?
    var f = false
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            println(imageUri)

        }
    var requestBody: RequestBody
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "제목")
        OutlinedTextField(value = title, onValueChange = { title = it }, maxLines = 1)
        Text(text = "내용")
        OutlinedTextField(value = content, onValueChange = { content = it })
        Row {

            imageUri?.let {
                bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }

                f = true

            }
            bitmap?.let { btm ->
                Image(
                    bitmap = btm.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )

            }
            Button(onClick = { launcher.launch("image/*") }) {
                Text(text = "이미지 선택")
            }
        }
        Text(text = "태그 : $tags")
        Button(onClick = { tags.removeAll(tags) }) {
            Text("태그 초기화")
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = tag, onValueChange = {tag = it}, maxLines = 1)
            Button(onClick = {
                tags.add(tag)
                tag=""
            }) {
                Text("태그 추가")
            }
        }

        Button(onClick = {
            if (f) {
                println("파일 있음")
                file = bitmapToFile(bitmap!!, "image.jpg")
                println(file?.absolutePath)
                requestBody = RequestBody.create(MediaType.parse("image/*"), file)
                val body = MultipartBody.Part.createFormData("images", "image.png", requestBody)
                sImage(context,body,content,title,tags)
            } else {
                println("파일 없음")
                val nc = SendNCover(SendNCover.NovelCover("0","0",content,title),tags)
                wNCover(context,nc)
            }


        }) {
            Text(text = "작성")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview8() {
    MyApplicationTheme {
        Greeting4()
    }
}




fun wNCover(context: Context, nc : SendNCover){
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }
    val ac =read()
    val retrofitClass = RetrofitClass.api.writeNCover(ac.authorization.toString(),nc)
    retrofitClass.enqueue(object : Callback<SNCR>{
        override fun onResponse(call: Call<SNCR>, response: Response<SNCR>) {
            val r = response.body()?.msg
            if(r=="JWT expiration"){
                getAToken(context)
                retrofitClass.cancel()
                Handler(Looper.getMainLooper()).postDelayed({ wNCover(context, nc) },1000)
            }
            else {
                println(r)
                Toast.makeText(
                    context,
                    "커버 작성 완료",
                    Toast.LENGTH_LONG
                ).show()
                (context as Activity).finish()
            }

        }

        override fun onFailure(call: Call<SNCR>, t: Throwable) {
            t.printStackTrace()
        }


    })
}

fun sImage(context: Context, body: MultipartBody.Part, content : String, title : String,
    tag : List<String>){
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }
    val ac =read()
    val retrofitClass = RetrofitClass.api.uploadImage(ac.authorization.toString(),body)
    retrofitClass.enqueue(object : Callback<ImageUpload>{
        override fun onResponse(call: Call<ImageUpload>, response: Response<ImageUpload>) {
            val r = response.body()?.msg
            if(r=="JWT expiration"){
                getAToken(context)
                retrofitClass.cancel()
                Handler(Looper.getMainLooper()).postDelayed({ sImage(context, body, content, title, tag) },1000)
            }
            else {
                println("사진 번호 : $r")
                val nc = SendNCover(SendNCover.NovelCover(r!!,"0",content,title),tag)
                wNCover(context, nc)
            }
        }

        override fun onFailure(call: Call<ImageUpload>, t: Throwable) {
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