package com.e.myapplication

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberImagePainter
import com.e.myapplication.board.FreeBoardActivity
import com.e.myapplication.dataclass.Novels
import com.e.myapplication.menu.Drawer
import com.e.myapplication.novel.NovelCoverActivity
import com.e.myapplication.novel.ShowNovelListActivity
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.e.myapplication.ui.theme.gray
import com.e.myapplication.user.ProtoRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response


class MainActivity : ComponentActivity() {
    private val multiplePermissionsCode = 100
    private val requiredPermissions =
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    private lateinit var mainActivityViewModel : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        checkPermissions()
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        mainActivityViewModel.updateNovels()
        setContent {
            MyApplicationTheme {
                val vmn = mainActivityViewModel.n.collectAsState()
                val vmt = mainActivityViewModel.t.collectAsState()
                Surface(color = MaterialTheme.colors.background) {
                    ShowNovelList(vmn, vmt)
                }
            }
        }
    }

    private fun checkPermissions() {
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if (rejectedPermissionList.isNotEmpty()) {
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(
                this,
                rejectedPermissionList.toArray(array),
                multiplePermissionsCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            multiplePermissionsCode -> {
                if (grantResults.isNotEmpty()) {
                    for ((i, permission) in permissions.withIndex()) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.i("TAG", "The user has denied to $permission")
                            Log.i("TAG", "I can't work for you anymore then. ByeBye!")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ShowNovelList(novels: State<List<Novels.Content>>, tags: State<List<List<String>>>) {
    val context = LocalContext.current
    val repository = ProtoRepository(context = context)
    fun read(): String {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo.authorization
    }

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = { TopMenu(scaffoldState, scope) },
        scaffoldState = scaffoldState,
        drawerContent = { Drawer() },
        drawerGesturesEnabled = true
    ) {
        BackHandler() {
            if (scaffoldState.drawerState.isClosed) (context as Activity).finish()
            else {
                scope.launch {
                    scaffoldState.drawerState.apply {
                        close()
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxHeight()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberImagePainter(""), contentDescription = "", modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clickable(onClick = {
                            read()
                            getToken()
                        })
                )
                Spacer(modifier = Modifier.height(8.0.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("실시간 랭킹", fontSize = 32.sp, modifier = Modifier.padding(4.0.dp))
                        Text("좋아요 순", fontSize = 18.sp, modifier = Modifier.padding(4.0.dp))
                    }
                    Text(
                        text = "더보기 ", fontSize = 14.sp, modifier = Modifier
                            .clickable(onClick = {
                                val intent = Intent(context, NovelCoverActivity::class.java)
                                context.startActivity(intent)
                            })
                            .padding(4.0.dp)
                    )

                }
                LazyColumn {
                    itemsIndexed(novels.value) { index, novel ->
                        Spacer(modifier = Modifier.padding(8.dp))
                        Greeting3(novel, tags.value[index])
                    }

                }
            }
        }
    }


}

@Composable
fun TopMenu(scaffoldState: ScaffoldState, scope: CoroutineScope) {
    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
            .fillMaxWidth()
            .background(gray)
    ) {
        IconButton(onClick = {
//            val intent = Intent(context, LoginActivity::class.java)
//            context.startActivity(intent)
            scope.launch {
                scaffoldState.drawerState.apply {
                    if (isClosed) open() else close()
                }
            }
        }) {
            Icon(
                Icons.Default.Menu,
                contentDescription = null
            )
        }
        Row {
            IconButton(onClick = {
                val intent = Intent(context, NotificationActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null
                )
            }
            IconButton(onClick = {
                val intent = Intent(context, FreeBoardActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun Greeting3(novel: Novels.Content, tag: List<String>) {
    var t = ""
    if(tag.isNotEmpty()){
        t+=tag[0]
        for(i in 1 until tag.size){
            t+=", "
            t+=tag[i]
        }
    }

    val context = LocalContext.current
    Row(
        verticalAlignment = CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                val intent = Intent(context, ShowNovelListActivity::class.java)
                intent.putExtra("novelNum", novel.nvcid)
                context.startActivity(intent)
            })
    ) {
        Text("-", modifier = Modifier.padding(16.dp), fontSize = 24.sp)
        if(novel.imgUrl=="1"||novel.imgUrl=="23"){
            Image(
                painter = painterResource(R.drawable.schumi), contentDescription = "schumi",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RectangleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondary, RectangleShape),

                )
        }
        else {
            Image(
                painter = rememberImagePainter(novel.imgUrl), contentDescription = "schumi",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RectangleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondary, RectangleShape),

                )
        }
        Spacer(modifier = Modifier.width(16.0.dp))
        Column {
            Text(novel.nvcTitle)
            Text(novel.nvcid.toString())
            Spacer(modifier = Modifier.height(4.0.dp))
            Text("태그 : $t")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview3() {
    MyApplicationTheme {

    }
}
fun getToken(){
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task->
        if(!task.isSuccessful){
            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }

        val token = task.result
        println(token)
    })
}