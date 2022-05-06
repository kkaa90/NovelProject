package com.e.myapplication.novel

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.myapplication.AccountInfo
import com.e.myapplication.dataclass.*
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
import retrofit2.Response
import java.io.File

class WriteNovelActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val num = intent.getIntExtra("num", 0)
        val novelInfo = mutableStateListOf<NovelsInfo.NovelInfo>()
        getNovelEpisode(num,novelInfo)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting9(num,novelInfo)
                }
            }
        }
    }
}

@Composable
fun Greeting9(num: Int, novelInfo: SnapshotStateList<NovelsInfo.NovelInfo>) {
    val context = LocalContext.current
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var point by remember { mutableStateOf("0") }
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
        Row {
            Text(text = "포인트 : ")
            OutlinedTextField(
                value = point,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = {
                    val d = it.toIntOrNull()
                    point = if (d == null) {
                        "0"
                    } else {
                        if (d > 50)
                            "0"
                        else {
                            it
                        }
                    }
                }
            )
        }
        val dMenu: MutableList<String> = ArrayList()
        dMenu.add("첫화")
        if(novelInfo.size!=0){
            for(i in novelInfo.indices){
                dMenu.add(novelInfo[i].nvId.toString()+"화 : "+novelInfo[i].nvTitle)
            }
            dMenu.removeAt(0)
        }
        var dMenuExpanded by remember { mutableStateOf(false) }
        var dMenuName: String by remember { mutableStateOf("선택") }
        var dMenuIndex : Int by remember { mutableStateOf(0) }
        Row {
            Text(text = "부모 화 : ")
            Column {
                Row(Modifier.clickable { dMenuExpanded = !dMenuExpanded }) {
                    Text(dMenuName)
                    Icon(imageVector = Icons.Filled.ArrowDropDown, "")
                }
                DropdownMenu(
                    expanded = dMenuExpanded,
                    onDismissRequest = { dMenuExpanded = false }) {
                    dMenu.forEachIndexed { index ,dMenuItem ->
                        DropdownMenuItem(onClick = {
                            dMenuExpanded = false; dMenuName = dMenuItem
                            dMenuIndex = index
                        }) {
                            Text(dMenuItem)
                        }
                    }

                }
            }
        }

        Button(onClick = {
            val ac = read()
            val parent : String = when (dMenuName) {
                "첫화" -> {
                    "0"
                }
                "선택" -> {
                    "-"
                }
                else -> {
                    novelInfo[dMenuIndex].nvId.toString()
                }
            }
            if(parent=="-"){
                Toast.makeText(
                    context,
                    "부모 화를 선택해야 합니다.",
                    Toast.LENGTH_LONG
                ).show()
            }
            else{
                if (f) {
                    println("파일 있음")
                    file = bitmapToFile(bitmap!!, "image.jpg")
                    println(file?.absolutePath)
                    requestBody = RequestBody.create(MediaType.parse("image/*"), file)
                    val body = MultipartBody.Part.createFormData("images", "image.png", requestBody)
                    nImage(context, body, content, title, num, point)
                } else {
                    println("파일 없음")
                    val novel = PostNovelsDetail(
                        PostNovelsDetail.Novel(
                            "0",
                            content,
                            "0",
                            title,
                            ac.memNick,
                            point
                        ), parent
                    )
                    writeNovel(context, num, novel)
                }
            }
        }) {
            Text(text = "작성")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview11() {
    MyApplicationTheme {
    }
}

fun writeNovel(
    context: Context,
    num: Int,
    novel: PostNovelsDetail,
) {
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }
    val ac = read()
    val retrofitClass = RetrofitClass.api.writeNovel(ac.authorization, num, novel)
    retrofitClass.enqueue(object : retrofit2.Callback<SNCR> {
        override fun onResponse(call: Call<SNCR>, response: Response<SNCR>) {
            when (response.body()!!.msg) {
                "JWT expiration" -> {
                    getAToken(context)
                    retrofitClass.cancel()
                    Handler(Looper.getMainLooper()).postDelayed({ writeNovel(context, num, novel) },1000)
                }
                "1" -> {
                    (context as Activity).finish()
                }
                else -> {

                }
            }
        }

        override fun onFailure(call: Call<SNCR>, t: Throwable) {
            t.printStackTrace()
        }

    })

}

fun nImage(
    context: Context,
    body: MultipartBody.Part,
    content: String,
    title: String,
    num: Int,
    point: String
) {
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }
    val ac = read()
    val retrofitClass = RetrofitClass.api.uploadImage(ac.authorization.toString(), body)
    retrofitClass.enqueue(object : retrofit2.Callback<ImageUploadSingle> {
        override fun onResponse(call: Call<ImageUploadSingle>, response: Response<ImageUploadSingle>) {
            val r = response.body()?.msg
            if (r == "JWT expiration") {
                getAToken(context)
                retrofitClass.cancel()
                Handler(Looper.getMainLooper()).postDelayed({ nImage(context, body, content, title, num, point) },1000)
            } else {
                println("사진 번호 : $r")
                val novel = PostNovelsDetail(
                    PostNovelsDetail.Novel(r!!, content, "0", title, ac.memNick, point),
                    "0"
                )
                writeNovel(context, num, novel)
            }
        }

        override fun onFailure(call: Call<ImageUploadSingle>, t: Throwable) {
            t.printStackTrace()
        }

    })

}
fun getNovelEpisode(
    num: Int, novelInfo: SnapshotStateList<NovelsInfo.NovelInfo>
) {
    val retrofitClass = RetrofitClass.api.getNovelList(num)
    retrofitClass.enqueue(object : retrofit2.Callback<NovelsInfo> {
        override fun onResponse(call: Call<NovelsInfo>, response: Response<NovelsInfo>) {
            val r = response.body()
            novelInfo.addAll(r!!.novelInfo)
        }

        override fun onFailure(call: Call<NovelsInfo>, t: Throwable) {
            t.printStackTrace()
        }

    })
}