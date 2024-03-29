package com.e.treenovel.novel

import android.content.ContentValues
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberImagePainter
import com.e.treenovel.R
import com.e.treenovel.*
import com.e.treenovel.dataclass.*
import com.e.treenovel.menu.Drawer
import com.e.treenovel.retrofit.RetrofitClass
import com.e.treenovel.ui.theme.dimGray
import com.e.treenovel.ui.theme.gray
import com.e.treenovel.ui.theme.primaryBlue
import com.e.treenovel.ui.theme.skyBlue
import com.e.treenovel.user.ProtoRepository
import com.e.treenovel.user.getAToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowPostList(
    routeAction: RouteAction,
    num: Int,
    viewModel: NovelViewModel
) {
    var c by remember {
        mutableStateOf(true)
    }
    val epList = remember {
        mutableStateListOf<NovelsInfo.NovelInfo>()
    }
    val dMenu: MutableList<String> = ArrayList()
    dMenu.add("전체")
    dMenu.add("조회순")
    val novelInfo = viewModel.d.collectAsState()
    val episode = viewModel.e.collectAsState()
    val t = viewModel.h.collectAsState()
    val cover = viewModel.c.collectAsState()
    val test = viewModel.tree.collectAsState()
    val sub = viewModel.nvcList.collectAsState()
    var reportVisibility = remember { mutableStateOf(false) }
    LaunchedEffect(true) {
        viewModel.detailNow = -1
        viewModel.getNovelsList(num)
        if (lCheck) {
            viewModel.getSubList()
        }
        c = true
        Handler(Looper.getMainLooper()).postDelayed({
            c = false;
            if (epList.size == 0) {
                epList.addAll(t.value)
            }
        }, 1000)
    }


    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val m = remember {
        mutableStateOf("")
    }
//    var tabIndex by remember { mutableStateOf(0) }
//    val tabs = listOf("목록", "댓글")
    for (key in episode.value.keys) {
        dMenu.add(key.toString())
    }
    var dMenuExpanded by remember { mutableStateOf(false) }
    var dMenuName: String by remember { mutableStateOf(dMenu[1]) }
    var eIsEmpty by remember { mutableStateOf(false) }
    getToken(m)
    Scaffold(
        topBar = { TopMenu(scaffoldState, scope, routeAction) },
        drawerContent = {
            Drawer(routeAction, scaffoldState)
        },
        drawerGesturesEnabled = true,
        scaffoldState = scaffoldState
    ) { p ->
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
        if (c) {
            Dialog(
                onDismissRequest = { c = false },
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        if(reportVisibility.value){
            Box() {
                NovelCoverReport(num = num, visibility = reportVisibility, viewModel = viewModel)
            }
        }
        LazyColumn() {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(primaryBlue)

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
                                    NovelsIconButton(
                                        icon = R.drawable.ic_baseline_remove_red_eye_24,
                                        count = cover.value.nvcHit.toString()
                                    )
                                    Spacer(modifier = Modifier.padding(4.0.dp))
                                    NovelsIconButton(
                                        icon =
                                        R.drawable.ic_baseline_notifications_24,
                                        count = cover.value.nvcSubscribeCount.toString()
                                    )
                                    if (lCheck) {
                                        TextButton(onClick = {
                                            println(m)
                                            val nvc = Nvc(
                                                num.toString(),
                                                m.value
                                            )
                                            addSubscribe(context, nvc, viewModel)
                                        }) {
                                            Text(text = if (sub.value.contains(cover.value.nvcId)) "구독중" else "구독")
                                        }
                                    }
                                }
                            }
                            TextButton(onClick = {
                                if(lCheck) {
                                    reportVisibility.value = true
                                }
                                else {
                                    routeAction.navTo(NAVROUTE.LOGIN)
                                }
                            }) {
                                Text(text = "신고", color = Color.Red)
                            }
                        }

                    }
                    Row {
                        Spacer(modifier = Modifier.padding(8.dp))
                        Column {
                            Text(cover.value.nvcContents, fontSize = 21.sp)
                            Spacer(modifier = Modifier.padding(4.dp))
//                            Text(
//                                "장르 : $temp",
//                                color = dimGray,
//                                fontSize = 14.sp
//                            )
//                            Text("태그 : #아무거나", color = dimGray, fontSize = 14.sp)
                            Spacer(modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }
//            stickyHeader{
//                TabRow(
//                    selectedTabIndex = tabIndex,
//                    modifier = Modifier.height(36.dp),
//                    contentColor = Color.Black
//
//                ) {
//                    tabs.forEachIndexed { index, text ->
//                        Tab(
//                            selected = tabIndex == index,
//                            onClick = { tabIndex = index },
//                            text = {
//                                Text(
//                                    text,
//                                    color = if (tabIndex == index) Color.White else Color.Black
//                                )
//                            },
//                            modifier = Modifier.background(if (tabIndex == index) Color.Black else Color.White)
//                        )
//                    }
//                }
//            }
//            when (tabIndex) {
//                0 -> {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(gray),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.padding(8.dp))
                        TextButton(onClick = {
//                            for (key in episode.value.keys) {
//                                println(key)
//                            }
                            c = true
                            Handler(Looper.getMainLooper()).postDelayed({
                                dMenuName = dMenu[1]
                                epList.clear()
                                epList.addAll(t.value)
                                c = false
                            }, 1000)
                        }) {
                            Text(text = "새로 고침")
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
                                        if (novelInfo.value.size != 0) {
                                            eIsEmpty = false
                                            if (dMenuItem == "전체") {
                                                epList.addAll(novelInfo.value)
                                                println(epList.size)
                                            } else if (dMenuItem == "조회순") {
                                                viewModel.sortList()
                                            } else {
                                                epList.add(novelInfo.value.find { it.nvId == dMenuItem.toInt() }!!)
                                                val e = episode.value[dMenuItem.toInt()]
                                                if (e!!.isNotEmpty()) {
                                                    for (i in e.indices) {
                                                        epList.add(novelInfo.value.find { it.nvId == e[i] }!!)
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
                    IconButton(onClick = {
                        if(lCheck) {
                            routeAction.navWithNum(NAVROUTE.WRITINGNOVELDETAIL.routeName + "?num=${num}&state=0")
                        }
                        else {
                            routeAction.navTo(NAVROUTE.LOGIN)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = ""
                        )
                    }
                }
            }
            if (eIsEmpty) {
                item { Text(text = "글이 없습니다.") }
            } else {

                itemsIndexed(epList) { index, n ->
                    Column(
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                    ) {
                        NovelDetailListItem1(n, num, test.value, routeAction, index, viewModel)
                    }

                }
            }
//                }
//                1 -> {
//                    items(items = test.keys.toList()) { i ->
//                        Text(i.toString())
//                    }
//                }
//            }
        }
    }
}

@Composable
fun NovelsIconButton(icon: Int, count: String) {
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
    test: MutableMap<Int, List<NovelsInfo.NovelInfo>>,
    routeAction: RouteAction,
    index: Int,
    viewModel: NovelViewModel
) {
    val context = LocalContext.current
    var visibility = rememberSaveable { mutableStateOf(false) }
    val t = test[novelsInfo.nvId]
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .clickable(onClick = {
                if (lCheck) {
                    viewModel.getParent(novelsInfo.nvId)
                    viewModel.nDImageNum = novelsInfo.imgUrl
                    if (viewModel.detailNow == -1) {
                        routeAction.navWithNum("novelDetail?nNum=${num}&bNum=${novelsInfo.nvId}&state=${novelsInfo.nvState}")
                    } else {
                        routeAction.goList("novelDetail?nNum=${num}&bNum=${novelsInfo.nvId}&state=${novelsInfo.nvState}")
                    }
                } else {
                    routeAction.navTo(NAVROUTE.LOGIN)
                }
            }),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (viewModel.detailNow == novelsInfo.nvId) Color.Yellow else {
                        if (visibility.value) skyBlue else Color.White
                    }
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Spacer(modifier = Modifier.padding(10.0.dp))
                    Column {
                        Text(
                            text = index.plus(1).toString() + "화 " + novelsInfo.nvTitle,
                            color = MaterialTheme.colors.secondaryVariant,
                            style = MaterialTheme.typography.subtitle2
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (novelsInfo.nvWriter.length > 15) novelsInfo.nvWriter.substring(
                                        0,
                                        15
                                    ) else novelsInfo.nvWriter,
                                    style = MaterialTheme.typography.body2,
                                )
                                Text(
                                    text = "  조회 ${novelsInfo.nvHit}",
                                    style = MaterialTheme.typography.body2,
                                )
                            }
                            Text(
                                text = "포인트 : ${novelsInfo.nvPoint}",
                                style = MaterialTheme.typography.body2
                            )
                        }

                    }
                }

                if (t?.size!! <= 2) {
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
            if (t?.size!! >= 3) {
                AnimatedVisibility(visible = visibility.value) {
                    Column() {
                        for (i in 1 until t.size) {
                            Spacer(modifier = Modifier.height(2.dp))
                            NovelDetailListItem2(
                                novelsInfo = t[i],
                                num = num,
                                test = test,
                                visibility,
                                routeAction,
                                index.plus(2).toString(),
                                i,
                                viewModel
                            )
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
    test: MutableMap<Int, List<NovelsInfo.NovelInfo>>,
    vis: MutableState<Boolean>,
    routeAction: RouteAction,
    indexed: String,
    index: Int,
    viewModel: NovelViewModel
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
                if (lCheck) {
                    viewModel.getParent(novelsInfo.nvId)
                    viewModel.nDImageNum = novelsInfo.imgUrl
                    if (viewModel.detailNow == -1) {
                        routeAction.navWithNum("novelDetail?nNum=${num}&bNum=${novelsInfo.nvId}&state=${novelsInfo.nvState}")
                    } else {
                        routeAction.goList("novelDetail?nNum=${num}&bNum=${novelsInfo.nvId}&state=${novelsInfo.nvState}")
                    }
                } else {
                    routeAction.navTo(NAVROUTE.LOGIN)
                }
            }),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (viewModel.detailNow == novelsInfo.nvId) Color.Yellow else {
                        if (visibility.value) skyBlue else Color.White
                    }
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Spacer(modifier = Modifier.padding(10.0.dp))
                    Column {
                        Text(
                            text = "${indexed}-${index}화 " + novelsInfo.nvTitle,
                            color = MaterialTheme.colors.secondaryVariant,
                            style = MaterialTheme.typography.subtitle2
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (novelsInfo.nvWriter.length > 15) novelsInfo.nvWriter.substring(
                                        0,
                                        15
                                    ) else novelsInfo.nvWriter,
                                    style = MaterialTheme.typography.body2,
                                )
                                Text(
                                    text = "  조회 ${novelsInfo.nvHit}",
                                    style = MaterialTheme.typography.body2,
                                )
                            }

                            Text(
                                text = "포인트 : ${novelsInfo.nvPoint}",
                                style = MaterialTheme.typography.body2
                            )
                        }

                    }
                }

                if (t.size <= 2) {
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
            if (t.size >= 3) {
                AnimatedVisibility(visible = visibility.value) {
                    Column() {
                        for (i in 1 until t.size) {
                            Spacer(modifier = Modifier.height(2.dp))
                            NovelDetailListItem2(
                                novelsInfo = t[i],
                                num = num,
                                test = test,
                                visibility,
                                routeAction,
                                "${indexed}-${index}",
                                i,
                                viewModel
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NovelCoverReport(num: Int, visibility: MutableState<Boolean>, viewModel: NovelViewModel) {
    val context = LocalContext.current
    var mSelected by remember {
        mutableStateOf(reportState[0])
    }
    var mVisibility by remember {
        mutableStateOf(false)
    }
    Dialog(onDismissRequest = {
        viewModel.reportContent = ""
        visibility.value = false
    }) {
        Surface(
            modifier = Modifier
                .wrapContentSize(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "신고", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Column {
                    Row(modifier = Modifier.clickable { mVisibility = !mVisibility }) {
                        Text(text = "  ${mSelected.presentState}")
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "")
                    }
                    DropdownMenu(
                        expanded = mVisibility,
                        onDismissRequest = { mVisibility = false }) {
                        reportState.forEach {
                            DropdownMenuItem(onClick = { mSelected = it; mVisibility = false }) {
                                Text(text = it.presentState)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = viewModel.reportContent,
                    onValueChange = { viewModel.reportContent = it },
                    label = { Text(text = "자세한 사유") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    OutlinedButton(onClick = {
                        viewModel.reportContent = ""
                        visibility.value = false
                    }) {
                        Text(text = "취소")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedButton(onClick = {
                        viewModel.reportNovelCover(context, num, mSelected)
                        visibility.value = false
                        println("게시글 신고")
                    }, enabled = visibility.value) {
                        Text(text = "확인")
                    }
                }
            }
        }
    }
}


fun addSubscribe(context: Context, nvc: Nvc, viewModel: NovelViewModel) {
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
                "delete" -> {
                    viewModel.refreshCover(nvc.nvcId.toInt())
                    viewModel.getSubList()
                    message = "구독이 취소 되었습니다."
                }
                "subscribe" -> {
                    viewModel.refreshCover(nvc.nvcId.toInt())
                    viewModel.getSubList()
                    message = "구독 되었습니다."
                }
                else -> {
                    getAToken(context)
                    Handler(Looper.getMainLooper()).postDelayed(
                        { addSubscribe(context, nvc, viewModel) },
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