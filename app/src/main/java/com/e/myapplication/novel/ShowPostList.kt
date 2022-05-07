package com.e.myapplication.novel

import android.content.ContentValues
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.e.myapplication.*
import com.e.myapplication.R
import com.e.myapplication.dataclass.NovelsInfo
import com.e.myapplication.dataclass.Nvc
import com.e.myapplication.dataclass.nvcr
import com.e.myapplication.menu.Drawer
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.dimGray
import com.e.myapplication.ui.theme.skyBlue
import com.e.myapplication.user.ProtoRepository
import com.e.myapplication.user.getAToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response


@Composable
fun ShowPostList(
    routeAction: RouteAction,
    num: Int
) {
    val novelInfo = remember { mutableStateListOf<NovelsInfo.NovelInfo>() }
    val episode = remember { mutableStateMapOf<Int, List<Int>>() }
    val cover = remember {
        mutableStateOf(
            NovelsInfo.NovelCover(
                "1", "", 0, 0,
                0, "", 0, 0
            )
        )
    }
    val test = remember {
        mutableStateMapOf<Int, List<NovelsInfo.NovelInfo>>()
    }
    LaunchedEffect(true){
        getNovelsList(num, novelInfo, episode, cover, test)
    }
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val m = remember {
        mutableStateOf("")
    }
    getToken(m)
    Scaffold(
        topBar = { TopMenu(scaffoldState, scope, routeAction) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                routeAction.navWithNum(NAVROUTE.WRITINGNOVELDETAIL.routeName+"/${num}")
            }) {
                Icon(Icons.Filled.Add, contentDescription = "")
            }
        },
        drawerContent = {
            Drawer(routeAction)
        },
        drawerGesturesEnabled = true,
        scaffoldState = scaffoldState
    ) {
        BackHandler {
            if (scaffoldState.drawerState.isClosed) routeAction.goBack()
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
                Row {

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
                            Text(cover.value.nvId.toString(), color = dimGray, fontSize = 18.sp)
                            Spacer(modifier = Modifier.padding(4.dp))
                            Row {
                                NovelsIconButton(icon = R.drawable.ic_baseline_remove_red_eye_24, count = cover.value.nvcHit.toString())
                                Spacer(modifier = Modifier.padding(4.0.dp))
                                NovelsIconButton(icon = R.drawable.ic_baseline_thumb_up_24,
                                    count = if(cover.value.nvcReviewcount!=0) (cover.value.nvcReviewpoint/cover.value.nvcReviewcount).toString() else "0"
                                )
                                Spacer(modifier = Modifier.padding(4.0.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    IconButton(onClick = {
                                        println(m)
                                        val nvc = Nvc(
                                            num.toString(),
                                            m.value
                                        )
                                        addSubscribe(context, nvc)
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_baseline_notifications_24),
                                            contentDescription = null
                                        )
                                    }
                                    Text("1000", fontSize = 12.sp)
                                }
                            }
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.MoreVert, contentDescription = "")
                        }
                    }

                }
                Row {
                    Spacer(modifier = Modifier.padding(8.dp))
                    Column {
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
                    Column {
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
                                Column {
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
                                                        for (key in episode.keys) {
                                                            epList.add(novelInfo.find { it.nvId == key }!!)
                                                        }
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

                            IconButton(onClick = { }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_sort_24),
                                    contentDescription = ""
                                )
                            }
                        }
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (eIsEmpty) {
                                item { Text(text = "글이 없습니다.") }
                            } else {
                                items(epList) { n ->
                                    NovelDetailListItem1(n, num, test, routeAction)
                                }
                            }
                        }


                    }

                }
                1 -> Column() {

                    LazyColumn {
//                        test.forEach { (i, list) ->
//                            items(list){ l ->
//                                Text(l.nvId.toString())
//                            }
//                        }
                        items(items = test.keys.toList()) { i ->

                            Text(i.toString())
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun NovelsIconButton(icon : Int, count : String){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null
            )
        }
        Text(count, fontSize = 12.sp)
    }
}

@Composable
fun NovelDetailListItem1(
    novelsInfo: NovelsInfo.NovelInfo,
    num: Int,
    test: SnapshotStateMap<Int, List<NovelsInfo.NovelInfo>>,
    routeAction: RouteAction
) {
    val context = LocalContext.current
    var visibility = rememberSaveable { mutableStateOf(false) }
    val t = test[novelsInfo.nvId]!!
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .clickable(onClick = {
                routeAction.navWithNum("novelDetail?nNum=${num}&bNum=${novelsInfo.nvId}")
            }),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (visibility.value) skyBlue else Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row() {
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

                if (t.size == 1) {
                    IconButton(onClick = { }) {

                    }
                } else {
                    IconButton(onClick = { visibility.value = !visibility.value }) {
                        Icon(
                            imageVector = if (visibility.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = ""
                        )
                    }
                }
            }
            if (t.size != 1) {
                AnimatedVisibility(visible = visibility.value) {
                    Column() {
                        for (i in 1 until t.size) {
                            Spacer(modifier = Modifier.height(2.dp))
                            NovelDetailListItem2(novelsInfo = t[i], num = num, test = test, visibility,routeAction)
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun NovelDetailListItem2(
    novelsInfo: NovelsInfo.NovelInfo,
    num: Int,
    test: SnapshotStateMap<Int, List<NovelsInfo.NovelInfo>>,
    vis: MutableState<Boolean>,
    routeAction: RouteAction
) {
    val context = LocalContext.current
    var visibility = rememberSaveable { mutableStateOf(false) }
    val t = test[novelsInfo.nvId]!!
    BackHandler(vis.value) {
        vis.value = false
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .clickable(onClick = {
                routeAction.navWithNum("novelDetail?nNum=${num}&bNum=${novelsInfo.nvId}")
            }),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (visibility.value) skyBlue else Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row() {
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

                if (t.size == 1) {
                    IconButton(onClick = { }) {

                    }
                } else {
                    IconButton(onClick = { visibility.value = !visibility.value }) {
                        Icon(
                            imageVector = if (visibility.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = ""
                        )
                    }
                }
            }
            if (t.size != 1) {
                AnimatedVisibility(visible = visibility.value) {
                    Column() {
                        for (i in 1 until t.size) {
                            Spacer(modifier = Modifier.height(2.dp))
                            NovelDetailListItem2(novelsInfo = t[i], num = num, test = test, visibility, routeAction)
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }

                }
            }
        }
    }
}



fun getNovelsList(
    num: Int, novelInfo: SnapshotStateList<NovelsInfo.NovelInfo>,
    episode: SnapshotStateMap<Int, List<Int>>, cover: MutableState<NovelsInfo.NovelCover>,
    test: SnapshotStateMap<Int, List<NovelsInfo.NovelInfo>>
) {

    val retrofitClass = RetrofitClass.api.getNovelList(num)
    retrofitClass.enqueue(object : retrofit2.Callback<NovelsInfo> {
        override fun onResponse(call: Call<NovelsInfo>, response: Response<NovelsInfo>) {
            val r = response.body()
            novelInfo.addAll(r!!.novelInfo)
            episode.putAll(r.episode)
            if (novelInfo.size != 0) {
                for (key in episode.keys) {
                    println(key)
                    val epList = mutableListOf<NovelsInfo.NovelInfo>()
                    epList.add(novelInfo.find { it.nvId == key }!!)
                    val e = episode[key]
                    if (e!!.isNotEmpty()) {
                        for (i in e.indices) {
                            epList.add(novelInfo.find { it.nvId == e[i] }!!)
                        }
                    }
                    test[key] = epList
                }
            }
            cover.value = r.novelCover
        }

        override fun onFailure(call: Call<NovelsInfo>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun addSubscribe(context: Context, nvc: Nvc) {
    val repository = ProtoRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }

    val ac = read()
    val retrofitClass = RetrofitClass.api.subscribe(ac.authorization.toString(), nvc)
    retrofitClass.enqueue(object : retrofit2.Callback<nvcr> {
        override fun onResponse(call: Call<nvcr>, response: Response<nvcr>) {
            val r = response.body()!!.msg
            var message = ""
            when (r) {
                "delete" -> message = "구독이 취소 되었습니다."
                "subscribe" -> message = "구독 되었습니다."
                else -> {
                    getAToken(context)
                    Handler(Looper.getMainLooper()).postDelayed(
                        { addSubscribe(context, nvc) },
                        1000
                    )
                }
            }
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
            ).show()
            retrofitClass.cancel()
        }

        override fun onFailure(call: Call<nvcr>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun getToken(m: MutableState<String>) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }

        val token = task.result
        m.value = token.toString()
    })
}