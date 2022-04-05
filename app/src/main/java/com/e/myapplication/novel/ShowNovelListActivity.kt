package com.e.myapplication.novel

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.e.myapplication.AccountInfo
import com.e.myapplication.R
import com.e.myapplication.TopMenu
import com.e.myapplication.dataclass.NovelsInfo
import com.e.myapplication.dataclass.Nvc
import com.e.myapplication.dataclass.nvcr
import com.e.myapplication.menu.Drawer
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.e.myapplication.ui.theme.dimGray
import com.e.myapplication.ui.theme.skyBlue
import com.e.myapplication.user.ProtoRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response


class ShowNovelListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val num = intent.getIntExtra("novelNum", 0)
        val novelInfo = mutableStateListOf<NovelsInfo.NovelInfo>()
        val episode = mutableStateMapOf<Int, List<Int>>()
        val cover = mutableStateOf(
            NovelsInfo.NovelCover(
                "1", "", 0, 0,
                0, "", 0, 0
            )
        )
        getNovelsList(num, novelInfo, episode, cover)
        println(episode)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ShowPostList(
                        novelInfo,
                        episode,
                        num,
                        cover
                    )
                }
            }
        }


    }
}

@Composable
fun Greeting(novelsInfo: NovelsInfo.NovelInfo, num: Int) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .clickable(onClick = {
                val intent = Intent(context, NovelActivity::class.java)
                intent
                    .putExtra("boardNum", novelsInfo.nvId)
                    .putExtra("novelNum", num)
                context.startActivity(intent)
            })
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.padding(10.0.dp))
        Column {
            Text(
                text = novelsInfo.nvId.toString() + "화 " + novelsInfo.nvTitle,
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.subtitle2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = novelsInfo.nvWriter,
                style = MaterialTheme.typography.body2
            )


        }


    }

}

@Composable
fun ShowPostList(
    novelInfo: SnapshotStateList<NovelsInfo.NovelInfo>,
    episode: SnapshotStateMap<Int, List<Int>>, num: Int,
    cover: MutableState<NovelsInfo.NovelCover>
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val repository = ProtoRepository(context)
    val m = remember {
        mutableStateOf("")
    }
    getToken(m)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }
    val ac = read()
    Scaffold(
        topBar = { TopMenu(scaffoldState, scope) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val intent = Intent(context, WriteNovelActivity::class.java)
                intent.putExtra("num",num)
                context.startActivity(intent)
            }) {
                Icon(Icons.Filled.Add, contentDescription = "")
            }
        },
        drawerContent = {
            Drawer()
        },
        drawerGesturesEnabled = true,
        scaffoldState = scaffoldState
    ) {
        BackHandler {
            if (scaffoldState.drawerState.isClosed) (context as Activity).finish()
            else {
                scope.launch {
                    scaffoldState.drawerState.apply {
                        close()
                    }
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(skyBlue)

            ) {
                Row() {

                    Image(
                        painter = rememberImagePainter(cover.value.imgUrl),
                        contentDescription = "schumi",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RectangleShape)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text(
                                cover.value.nvcTitle,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(cover.value.nvid.toString(), color = dimGray, fontSize = 18.sp)
                            Spacer(modifier = Modifier.padding(4.dp))
                            Row() {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_baseline_remove_red_eye_24),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(Color.Black)
                                    )
                                    Text("1000", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.padding(4.0.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_baseline_thumb_up_24),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(Color.Black)
                                    )
                                    Text("1000", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.padding(4.0.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_baseline_comment_24),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(Color.Black)
                                    )
                                    Text("1000", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.padding(4.0.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_baseline_notifications_24),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(Color.Black),
                                        modifier = Modifier.clickable {
                                            println(m)
                                            val nvc = Nvc(ac.memId.toString(),num.toString(),
                                                m.value)
                                            addSubscribe(context, ac, nvc)
                                        }
                                    )
                                    Text("1000", fontSize = 12.sp)
                                }
                            }
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.MoreVert, contentDescription = "")
                        }
                    }

                }
                Row() {
                    Spacer(modifier = Modifier.padding(8.dp))
                    Column() {
                        Text(cover.value.nvcContents, fontSize = 21.sp)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            "장르 : " + cover.value.nvcReviewcount,
                            color = dimGray,
                            fontSize = 14.sp
                        )
                        Text("태그 : #아무거나", color = dimGray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.padding(4.dp))
                    }
                }
            }
            var tabIndex by remember { mutableStateOf(0) }
            val tabs = listOf("목록", "댓글")
            TabRow(
                selectedTabIndex = tabIndex,
                modifier = Modifier.height(36.dp),
                contentColor = Color.Black

            ) {
                tabs.forEachIndexed { index, text ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        text = {
                            Text(
                                text,
                                color = if (tabIndex == index) Color.White else Color.Black
                            )
                        },
                        modifier = Modifier.background(if (tabIndex == index) Color.Black else Color.White)
                    )
                }
            }
            val dMenu: MutableList<String> = ArrayList()
            dMenu.add("전체")
            for (key in episode.keys) {
                dMenu.add(key.toString())
            }

            var dMenuExpanded by remember { mutableStateOf(false) }
            var dMenuName: String by remember { mutableStateOf(dMenu[0]) }
            val epList = remember {
                mutableStateListOf<NovelsInfo.NovelInfo>()
            }
            if (epList.size == 0) {
                epList.addAll(novelInfo)
            }
            var eIsEmpty by remember { mutableStateOf(false) }
            when (tabIndex) {
                0 -> {
                    Column() {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.padding(8.dp))
                                Button(onClick = {
                                    for (key in episode.keys) {
                                        println(key)
                                    }
                                }) {
                                    Text(text = "공지 숨기기")
                                }
                                Spacer(modifier = Modifier.padding(30.dp))
                                Column() {
                                    Row(Modifier.clickable { dMenuExpanded = !dMenuExpanded }) {
                                        Text(dMenuName)
                                        Icon(imageVector = Icons.Filled.ArrowDropDown, "")
                                    }
                                    DropdownMenu(
                                        expanded = dMenuExpanded,
                                        onDismissRequest = { dMenuExpanded = false }) {
                                        dMenu.forEach { dMenuItem ->
                                            DropdownMenuItem(onClick = {
                                                dMenuExpanded = false; dMenuName = dMenuItem
                                                epList.clear()
                                                if (novelInfo.size != 0) {
                                                    eIsEmpty = false
                                                    if (dMenuItem == "전체") {
                                                        epList.addAll(novelInfo)
                                                        println(epList.size)
                                                    } else {
                                                        epList.add(novelInfo.find { it.nvId == dMenuItem.toInt() }!!)
                                                        val e = episode[dMenuItem.toInt()]
                                                        if (e!!.isNotEmpty()) {
                                                            for (i in e.indices) {
                                                                epList.add(novelInfo.find { it.nvId == e[i] }!!)
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    eIsEmpty = true
                                                }
                                                println(epList.size)
                                            }) {
                                                Text(dMenuItem)
                                            }
                                        }

                                    }
                                }
                            }

                            IconButton(onClick = {  }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_sort_24),
                                    contentDescription = ""
                                )
                            }
                        }
                        LazyColumn {
                            if (eIsEmpty) {
                                item { Text(text = "글이 없습니다.") }
                            } else {
                                items(epList) { n ->
                                    Greeting(n, num)
                                }
                            }
                        }


                    }

                }
                1 -> Text("test")
            }

        }
    }
}


@Preview(
    name = "밝은 테마",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun DefaultPreview() {
    Column(modifier = Modifier.fillMaxHeight()) {
        MyApplicationTheme {

        }
    }
}

fun getNovelsList(
    num: Int, novelInfo: SnapshotStateList<NovelsInfo.NovelInfo>,
    episode: SnapshotStateMap<Int, List<Int>>, cover: MutableState<NovelsInfo.NovelCover>
) {
    val retrofitClass = RetrofitClass.api.getNovelList(num)
    retrofitClass.enqueue(object : retrofit2.Callback<NovelsInfo> {
        override fun onResponse(call: Call<NovelsInfo>, response: Response<NovelsInfo>) {
            val r = response.body()
            novelInfo.addAll(r!!.novelInfo)
            episode.putAll(r.episode)
            cover.value = r.novelCover
        }

        override fun onFailure(call: Call<NovelsInfo>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun addSubscribe(context: Context, ac: AccountInfo, nvc: Nvc){
    val retrofitClass = RetrofitClass.api.subscribe(ac.authorization.toString(), nvc)
    println(retrofitClass.request().url())
    println(retrofitClass.request().toString())
    retrofitClass.enqueue(object : retrofit2.Callback<nvcr>{
        override fun onResponse(call: Call<nvcr>, response: Response<nvcr>) {
            Toast.makeText(
                context,
                response.body()!!.msg.toString(),
                Toast.LENGTH_LONG
            ).show()
            retrofitClass.cancel()
        }

        override fun onFailure(call: Call<nvcr>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun getToken(m: MutableState<String>){
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task->
        if(!task.isSuccessful){
            Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }

        val token = task.result
        m.value= token.toString()
    })
}