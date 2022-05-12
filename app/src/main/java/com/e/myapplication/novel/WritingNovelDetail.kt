package com.e.myapplication.novel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.myapplication.AccountInfo
import com.e.myapplication.RouteAction
import com.e.myapplication.dataclass.ImageUploadSingle
import com.e.myapplication.dataclass.NovelsInfo
import com.e.myapplication.dataclass.PostNovelsDetail
import com.e.myapplication.dataclass.SNCR
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

@Composable
fun WritingNovelDetail(num: Int, routeAction: RouteAction, viewModel: NovelViewModel) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val novelInfo = remember { mutableStateListOf<NovelsInfo.NovelInfo>() }
    LaunchedEffect(true){
        getNovelEpisode(num, novelInfo)
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
    var requestBody: RequestBody
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            if(uri!=null){
                viewModel.nDImageUri.add(uri)
                viewModel.nDBitmap.add(
                    if (Build.VERSION.SDK_INT < 28){
                        MediaStore.Images.Media.getBitmap(context.contentResolver,uri)
                    } else{
                        val source =
                            uri.let { ImageDecoder.createSource(context.contentResolver,it) }
                        source.let { ImageDecoder.decodeBitmap(it) }
                    }
                )
                viewModel.nDFiles.add(
                    bitmapToFile(
                        viewModel.nDBitmap[viewModel.nDBitmap.size-1]!!, viewModel.nDBitmap.size.toString() + ".png"
                    )
                )
                requestBody = RequestBody.create(MediaType.parse("image/*"), viewModel.nDFiles[viewModel.nDBitmap.size-1]!!)
                viewModel.nDBody.add(
                    MultipartBody.Part.createFormData(
                        "images", viewModel.nDBitmap.size.toString() + ".png",
                        requestBody
                    )
                )
            }
        }
    Scaffold(topBar = {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { routeAction.goBack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
            TextButton(onClick = {
                if (viewModel.nDBody.size != 0) {
                    println("파일 있음")

                } else {
                    println("파일 없음")

                }
            }) {
                Text(text = "글쓰기")
            }
        }
    }) { p->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(p)) {
            OutlinedTextField(
                value = viewModel.nDTitle,
                onValueChange = {viewModel.nDTitle = it},
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                label = { Text(text = "제목")}
            )
            Divider(thickness = 1.dp, color = Color.Gray)
            OutlinedTextField(
                value = viewModel.nDContent,
                onValueChange = { viewModel.nDContent = it },
                label = { Text("내용") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                })
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "포인트 : ")
                OutlinedTextField(
                    value = viewModel.nDPoint,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        val d = it.toIntOrNull()
                        if (d == null) {
                            viewModel.nDPoint =  "0"
                        } else {
                            if (d > 50) {
                                viewModel.nDPoint = "0"
                                Toast.makeText(
                                    context,
                                    "포인트는 0부터 50까지 설정 가능합니다.",
                                    Toast.LENGTH_LONG
                                ).show()
                                viewModel.nDPoint = "0"

                            }
                            else {
                                viewModel.nDPoint = it
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                )

            }
            Row(Modifier.fillMaxWidth().border(0.5.dp,Color.Gray).padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "부모 화 선택 [")
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
                Text(text = "]")
            }
            Row {
                for (i: Int in 0 until viewModel.nDBitmap.size) {
                    Image(
                        bitmap = viewModel.nDBitmap[i]!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        viewModel.nDImageUri.remove(viewModel.nDImageUri[i])
                                        viewModel.nDBitmap.remove(viewModel.nDBitmap[i])
                                        viewModel.nDFiles.remove(viewModel.nDFiles[i])
                                        viewModel.nDBody.remove(viewModel.nDBody[i])
                                    }
                                )
                            }
                    )
                }
            }
            Row {
                TextButton(onClick = { launcher.launch("image/*") }) {
                    Text(text = "이미지 선택")
                }
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "이미지 업로드")
                }
            }
        }
    }

//    Column(modifier = Modifier.fillMaxSize()) {
//
//
//
//
//
//
//        Button(onClick = {
//            val ac = read()
//            val parent : String = when (dMenuName) {
//                "첫화" -> {
//                    "0"
//                }
//                "선택" -> {
//                    "-"
//                }
//                else -> {
//                    novelInfo[dMenuIndex].nvId.toString()
//                }
//            }
//            if(parent=="-"){
//                Toast.makeText(
//                    context,
//                    "부모 화를 선택해야 합니다.",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//            else{
//                if (f) {
//                    println("파일 있음")
//                    file = bitmapToFile(bitmap!!, "image.jpg")
//                    println(file?.absolutePath)
//                    requestBody = RequestBody.create(MediaType.parse("image/*"), file)
//                    val body = MultipartBody.Part.createFormData("images", "image.png", requestBody)
//                    nImage(context, body, content, title, num, point, routeAction)
//                } else {
//                    println("파일 없음")
//                    val novel = PostNovelsDetail(
//                        PostNovelsDetail.Novel(
//                            "0",
//                            content,
//                            "0",
//                            title,
//                            ac.memNick,
//                            point
//                        ), parent
//                    )
//                    writeNovel(context, num, novel, routeAction)
//                }
//            }
//        }) {
//            Text(text = "작성")
//        }
//    }
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
    routeAction: RouteAction
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
                    Handler(Looper.getMainLooper()).postDelayed({ writeNovel(context, num, novel, routeAction) },1000)
                }
                "1" -> {
                    routeAction.goBack()
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
    point: String,
    routeAction: RouteAction
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
                Handler(Looper.getMainLooper()).postDelayed({ nImage(context, body, content, title, num, point, routeAction) },1000)
            } else {
                println("사진 번호 : $r")
                val novel = PostNovelsDetail(
                    PostNovelsDetail.Novel(r!!, content, "0", title, ac.memNick, point),
                    "0"
                )
                writeNovel(context, num, novel, routeAction)
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