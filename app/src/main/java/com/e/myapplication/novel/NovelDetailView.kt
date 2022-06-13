package com.e.myapplication.novel

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.Visibility
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.compose.currentBackStackEntryAsState
import com.e.myapplication.AccountInfo
import com.e.myapplication.NAVROUTE
import com.e.myapplication.R
import com.e.myapplication.RouteAction
import com.e.myapplication.board.FreeBoardViewModel
import com.e.myapplication.dataclass.*
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.gray
import com.e.myapplication.user.ProtoRepository
import com.e.myapplication.user.getAToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response

@Composable
fun NovelDetailView(
    nNum: Int,
    bNum: Int,
    viewModel: NovelViewModel,
    routeAction: RouteAction
) {
    println("nNum: $nNum, bNum: $bNum")
    println("parent : ${viewModel.parent}")
    val context = LocalContext.current
    val rPoint = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    var rName: String by remember { mutableStateOf(rPoint[9]) }
    var rExpanded by remember {
        mutableStateOf(false)
    }
    val novel =
        remember {
            mutableStateOf(
                NovelsDetail(
                    NovelsDetail.Novel(
                        listOf(), 0, 0, "",
                        "", 0, 0, 0, 0,
                        0, 0, "", "", ""
                    ),
                    NovelsDetail.User("", "", ""), ""
                )
            )
        }
    val comments = remember { mutableStateListOf<NvComments.Comment>() }
    val replys = remember { mutableStateMapOf<Int, MutableList<NvComments.Comment>>() }
    var visibility by remember { mutableStateOf(false) }
    var commentV by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    var listVisibility = remember { mutableStateOf(false) }
    var rnVisibility = remember { mutableStateOf(false) }
    var rcVisibility = remember { mutableStateOf(false) }
    var dnVisibility = remember { mutableStateOf(false) }
    var dcVisibility = remember { mutableStateOf(false) }
    val backStackEntry = routeAction.getNow.backQueue
    LaunchedEffect(true) {
        viewModel.detailNow = bNum
        getNovelBoard(context, nNum = nNum, bNum = bNum, novel, routeAction)
        getC(nNum, bNum, comments, replys)
        viewModel.a = viewModel.read()
        println(backStackEntry)
    }
    Scaffold(topBar = {
        AnimatedVisibility(visible = visibility) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        viewModel.parent = -1
                        routeAction.goBack()
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = novel.value.novel.nvTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            listVisibility.value = !listVisibility.value
                        }) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "",
                            modifier = Modifier.size(14.sp.value.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("목록", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (viewModel.a.memId == novel.value.novel.memId.toString()) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                viewModel.editing(novel.value.novel)
                                routeAction.navWithNum(NAVROUTE.WRITINGNOVELDETAIL.routeName+"/${nNum}")
                            }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "",
                                modifier = Modifier.size(14.sp.value.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("수정", fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { dnVisibility.value = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "",
                                modifier = Modifier.size(14.sp.value.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("삭제", fontSize = 14.sp)
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                rnVisibility.value = true
                            }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_baseline_report_24),
                                contentDescription = "",
                                modifier = Modifier.size(14.sp.value.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("신고", fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

            }
        }

    },
        bottomBar = {
            AnimatedVisibility(visible = visibility) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Text(text = "댓글", modifier = Modifier
                        .clickable { commentV = !commentV }
                        .padding(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "평점")
                        Spacer(modifier = Modifier.width(4.0.dp))
                        Column {
                            Row(
                                modifier = Modifier
                                    .border(
                                        1.dp,
                                        MaterialTheme.colors.secondary,
                                        RectangleShape
                                    )
                                    .clickable { rExpanded = !rExpanded }) {
                                Text(text = rName)
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = ""
                                )
                            }
                            DropdownMenu(
                                expanded = rExpanded,
                                onDismissRequest = { rExpanded = false }) {
                                rPoint.forEach { rItem ->
                                    DropdownMenuItem(onClick = {
                                        rName = rItem; rExpanded = false
                                    }) {
                                        Text(text = rItem)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(4.0.dp))
                        TextButton(onClick = { sendR(context, bNum, nNum, rName) }) {
                            Text(text = "리뷰 전송")
                        }
                    }
                }
            }
        }) { innerpadding ->
        BackHandler {
            if (commentV || listVisibility.value) {
                commentV = false
                listVisibility.value = false
            } else if ((!commentV && !listVisibility.value) && visibility) {
                visibility = false
            } else {
                viewModel.parent=-1
                routeAction.goBack()
            }
        }
        Box(modifier = Modifier.zIndex(if (listVisibility.value) 1f else 0f)) {
            AnimatedVisibility(visible = listVisibility.value) {
                NovelDetailList(
                    paddingValues = innerpadding,
                    viewModel = viewModel,
                    num = nNum,
                    routeAction = routeAction,
                    visibility = listVisibility
                )
            }
            AnimatedVisibility(visible = dnVisibility.value) {
                DeleteNovelDialog(
                    context = context,
                    visibility = dnVisibility,
                    nNum = nNum,
                    bNum = bNum,
                    routeAction = routeAction
                )
            }
            AnimatedVisibility(visible = dcVisibility.value) {
                DeleteNovelCDialog(
                    context = context,
                    visibility = dcVisibility,
                    nNum = nNum,
                    bNum = bNum,
                    comments = comments,
                    viewModel = viewModel,
                    replys = replys
                )
            }
        }
        Box() {
            AnimatedVisibility(visible = rnVisibility.value) {
                ShowNovelReport(num = bNum, visibility = rnVisibility, viewModel = viewModel)
            }

        }
        Box() {
            AnimatedVisibility(visible = rcVisibility.value) {
                ShowNovelCReport(bNum = bNum, visibility = rcVisibility, viewModel = viewModel)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding)
        ) {
            if (!commentV) {
                LazyColumn(
                    Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { visibility = !visibility }
                        .fillMaxSize()) {
                    item {
                        ShowBoard(novel.value)
                    }
                }
            } else {
                var content by remember {
                    mutableStateOf("")
                }
                Column(Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = content, onValueChange = { content = it }, modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        )
                        OutlinedButton(onClick = {
                            sendC(context, nNum, bNum, 0, content, comments, replys)
                        }, enabled = content.isNotEmpty()) {
                            Text(text = "작성")
                        }
                    }
                    LazyColumn(Modifier.fillMaxWidth()) {
                        itemsIndexed(comments.reversed()) { index, c ->

                            ShowComment(
                                comment = c,
                                nNum,
                                bNum,
                                comments,
                                index,
                                rcVisibility,
                                viewModel,
                                dcVisibility,
                                replys
                            )

                        }
                    }
                }

            }
        }

    }

}

@Composable
fun ShowBoard(board: NovelsDetail) {
    Column {
        Text(text = board.novel.nvContents)
    }
}

@Composable
fun ShowComment(
    comment: NvComments.Comment,
    nNum: Int,
    bNum: Int,
    comments: SnapshotStateList<NvComments.Comment>,
    index: Int,
    rcVisibility: MutableState<Boolean>,
    viewModel: NovelViewModel,
    dcVisibility: MutableState<Boolean>,
    replys: SnapshotStateMap<Int, MutableList<NvComments.Comment>>
) {
    val context = LocalContext.current
    var visibility by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.25.dp, Color.Gray)
    )
    {
        Column(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = comment.memNickname,
                modifier = Modifier.padding(horizontal = 8.dp),
                fontWeight = FontWeight.Bold
            )
            Row() {
                Spacer(modifier = Modifier.width(24.dp))
                Column() {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = comment.nvCmtDatetime)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = comment.nvCmtContents)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Row(modifier = Modifier.clickable { visibility = !visibility }) {
                    Text(text = "댓글", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Row(
                    modifier = Modifier.clickable { likeNComment(context, bNum, comment.nvCmtId) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "",
                        modifier = Modifier.size(12.sp.value.dp)
                    )
                    Text(text = " 추천 ${comment.nvCmtLike}", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Row(modifier = Modifier.clickable {
                    dislikeNComment(
                        context,
                        bNum,
                        comment.nvCmtId
                    )
                }, verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "",
                        modifier = Modifier.size(12.sp.value.dp)
                    )
                    Text(text = " 비추천 ${comment.nvCmtDislike}", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(4.dp))
                if (viewModel.a.memId == comment.memId.toString()) {
                    Row(modifier = Modifier.clickable {
                        viewModel.reportComment = comment.nvCmtId
                        dcVisibility.value = true
                    }) {
                        Text(text = "삭제", fontSize = 12.sp)
                    }
                } else {
                    Row(modifier = Modifier.clickable {
                        viewModel.reportComment = comment.nvCmtId
                        rcVisibility.value = !rcVisibility.value
                    }) {
                        Text(text = "신고", fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
            AnimatedVisibility(visible = visibility) {
                var content by remember {
                    mutableStateOf("")
                }
                Column(Modifier.fillMaxWidth()) {
                    Row {
                        Spacer(modifier = Modifier.width(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = content,
                                onValueChange = { content = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                            )
                            OutlinedButton(onClick = {
                                sendC(
                                    context,
                                    nNum,
                                    bNum,
                                    comment.nvCmtId,
                                    content,
                                    comments,
                                    replys
                                )
                            }, enabled = content.isNotEmpty()) {
                                Text(text = "작성")
                            }
                        }
                    }


                }
            }
            val c = replys[comment.nvCmtId]
            if (!c.isNullOrEmpty()) {
                for (i: Int in c.size-1 downTo 0) {
                    Divider(thickness = 0.25.dp, color = Color.Gray)
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = c[i].memNickname,
                                modifier = Modifier.padding(horizontal = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Row() {
                                Spacer(modifier = Modifier.width(24.dp))
                                Column() {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = c[i].nvCmtDatetime)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = c[i].nvCmtContents)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Row(modifier = Modifier.clickable {
                                    likeNComment(
                                        context,
                                        bNum,
                                        c[i].nvCmtId
                                    )
                                }, verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp,
                                        contentDescription = "",
                                        modifier = Modifier.size(12.sp.value.dp)
                                    )
                                    Text(text = " 추천 ${c[i].nvCmtLike}", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Row(modifier = Modifier.clickable {
                                    dislikeNComment(
                                        context,
                                        bNum,
                                        c[i].nvCmtId
                                    )
                                }, verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp,
                                        contentDescription = "",
                                        modifier = Modifier.size(12.sp.value.dp)
                                    )
                                    Text(text = " 비추천 ${c[i].nvCmtDislike}", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                if (viewModel.a.memId == c[i].memId.toString()) {
                                    Row(modifier = Modifier.clickable {
                                        viewModel.reportComment = c[i].nvCmtId
                                        dcVisibility.value = true
                                    }) {
                                        Text(text = "삭제", fontSize = 12.sp)
                                    }
                                } else {
                                    Row(modifier = Modifier.clickable {
                                        viewModel.reportComment = c[i].nvCmtId
                                        rcVisibility.value = !rcVisibility.value
                                    }) {
                                        Text(text = "신고", fontSize = 12.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }

                    }
                }
            }

        }
    }

}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NovelDetailList(
    paddingValues: PaddingValues,
    viewModel: NovelViewModel,
    num: Int,
    routeAction: RouteAction,
    visibility: MutableState<Boolean>,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(gray)
    ) {
        val epList = viewModel.h.collectAsState().value
        Row() {
            IconButton(onClick = { visibility.value = false }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "")
            }
        }
        LazyColumn {
            itemsIndexed(epList) { index,  d ->
                NovelDetailListItem1(
                    novelsInfo = d,
                    num = num,
                    test = viewModel.tree.value,
                    routeAction = routeAction,
                    index = index,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun ShowNovelReport(num: Int, visibility: MutableState<Boolean>, viewModel: NovelViewModel) {
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
                        viewModel.reportingNovel(num, mSelected)
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

@Composable
fun ShowNovelCReport(bNum: Int, visibility: MutableState<Boolean>, viewModel: NovelViewModel) {
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
                Text(text = "댓글 신고", fontSize = 24.sp)
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
                        viewModel.reportingNComment(bNum, mSelected)
                        visibility.value = false
                        println("댓글 신고")
                    }, enabled = visibility.value) {
                        Text(text = "확인")
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteNovelDialog(
    context: Context,
    visibility: MutableState<Boolean>,
    nNum: Int,
    bNum: Int,
    routeAction: RouteAction
) {
    AlertDialog(onDismissRequest = { visibility.value = false },
        title = { Text(text = "소설 삭제") },
        text = { Text(text = "소설이 삭제 됩니다.") },
        dismissButton = {
            TextButton(onClick = { visibility.value = false }) {
                Text(text = "취소")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                deleteNovel(context, nNum, bNum, routeAction)
                visibility.value = false
            }) {
                Text(text = "확인")
            }
        }
    )
}

@Composable
fun DeleteNovelCDialog(
    context: Context,
    visibility: MutableState<Boolean>,
    nNum: Int,
    bNum: Int,
    comments: SnapshotStateList<NvComments.Comment>,
    replys: SnapshotStateMap<Int, MutableList<NvComments.Comment>>,
    viewModel: NovelViewModel
) {
    AlertDialog(onDismissRequest = { visibility.value = false },
        title = { Text(text = "댓글 삭제") },
        text = { Text(text = "댓글이 삭제 됩니다.") },
        dismissButton = {
            TextButton(onClick = { visibility.value = false }) {
                Text(text = "취소")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                deleteNComment(context, nNum, bNum, comments, replys, viewModel)
                visibility.value = false
            }) {
                Text(text = "확인")
            }
        }
    )
}


fun getNovelBoard(
    context: Context,
    nNum: Int,
    bNum: Int,
    novel: MutableState<NovelsDetail>,
    routeAction: RouteAction
) {
    val repository = ProtoRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()
        }
        return accountInfo
    }

    val ac = read()
    val retrofitClass = RetrofitClass.api.getNovel(ac.authorization, nNum, bNum)
    println(retrofitClass.request().url())
    retrofitClass.enqueue(object : retrofit2.Callback<NovelsDetail> {
        override fun onResponse(call: Call<NovelsDetail>, response: Response<NovelsDetail>) {
            val r = response.body()
            var m = r?.msg
            if (m == null) {
                m = "null"
            }
            println(r.toString())

            when (m) {
                "JWT expiration" -> {
                    getAToken(context)
                    Handler(Looper.getMainLooper()).postDelayed({
                        getNovelBoard(
                            context,
                            nNum,
                            bNum,
                            novel,
                            routeAction
                        )
                    }, 1000)
                }
                "point lack" -> {
                    Toast.makeText(
                        context,
                        "포인트가 부족합니다.",
                        Toast.LENGTH_LONG
                    ).show()
                    routeAction.goBack()
                }
                "ERROR" -> {
                    routeAction.goBack()
                    Toast.makeText(
                        context,
                        "로그인이 필요합니다.",
                        Toast.LENGTH_LONG
                    ).show()
                    routeAction.navTo(NAVROUTE.LOGIN)
                }
                "null" -> {
                    novel.value = r!!
                }
            }
        }

        override fun onFailure(call: Call<NovelsDetail>, t: Throwable) {
            t.printStackTrace()
        }
    })
}

fun sendC(
    context: Context,
    nNum: Int,
    bNum: Int,
    nCR: Int,
    cmt: String,
    comments: SnapshotStateList<NvComments.Comment>,
    replys: SnapshotStateMap<Int, MutableList<NvComments.Comment>>
) {
    val repository = ProtoRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()
        }
        return accountInfo
    }

    val ac = read()
    val retrofitClass = RetrofitClass.api.sendNComment(
        ac.authorization, nNum, PostNvComments(ac.memNick, cmt, nCR, 0)
    )
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            println("댓글 전송 테스트 결과 : " + response.body()!!.msg)
            when (response.body()!!.msg) {
                "JWT expiration" -> {
                    getAToken(context)
                    Handler(Looper.getMainLooper()).postDelayed({
                        sendC(
                            context,
                            nNum,
                            bNum,
                            nCR,
                            cmt,
                            comments,
                            replys
                        )
                    }, 1000)
                }
                "OK" -> {
                    getC(nNum, bNum, comments, replys)
                }
            }

        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun getC(
    nNum: Int,
    bNum: Int,
    comments: SnapshotStateList<NvComments.Comment>,
    replys: SnapshotStateMap<Int, MutableList<NvComments.Comment>>
) {
    comments.clear()
    val retrofitClass = RetrofitClass.api.getNComment(nNum, bNum, 1)
    retrofitClass.enqueue(object : retrofit2.Callback<NvComments> {
        override fun onResponse(call: Call<NvComments>, response: Response<NvComments>) {
            val r = response.body()
            if (r?.pagenum != 0) {

                for (i: Int in 0 until r?.comments?.size!!) {
                    if (r.comments[i].size == 1) {
                        comments.add(r.comments[i][0])
                    } else {
                        comments.add(r.comments[i][0])
                        val temp = mutableListOf<NvComments.Comment>()
                        for (j: Int in 1 until r.comments[i].size) {
                            temp.add(r.comments[i][j])
                        }
                        replys[r.comments[i][0].nvCmtId] = temp
                    }
                }
            }
        }

        override fun onFailure(call: Call<NvComments>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun sendR(context: Context, bNum: Int, nNum: Int, rvPoint: String) {
    val repository = ProtoRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }

    val ac = read()

    val retrofitClass =
        RetrofitClass.api.sendReview(ac.authorization, bNum, ReviewBody(rvPoint))
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            when (response.body()!!.msg) {
                "JWT expiration" -> {
                    getAToken(context)
                    Handler(Looper.getMainLooper()).postDelayed({
                        sendR(
                            context,
                            bNum,
                            nNum,
                            rvPoint
                        )
                    }, 1000)
                }
                "OK" -> {
                    Toast.makeText(
                        context,
                        "리뷰가 완료되었습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                "reduplication" -> {
                    Toast.makeText(
                        context,
                        "이미 리뷰한 게시물입니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }
    })
}

fun deleteNovel(context: Context, nNum: Int, bNum: Int, routeAction: RouteAction) {
    val repository = ProtoRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }

    val ac = read()
    val retrofitClass = RetrofitClass.api.deleteNovel(ac.authorization, nNum, bNum)
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            when (response.body()!!.msg) {
                "JWT expiration" -> {
                    getAToken(context)
                    Handler(Looper.getMainLooper()).postDelayed({
                        deleteNovel(context, nNum, bNum, routeAction)
                    }, 1000)
                }
                "OK" -> {
                    Toast.makeText(
                        context,
                        "삭제 되었습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                    routeAction.goBack()
                }
                else -> {
                    Toast.makeText(
                        context,
                        "오류가 발생했습니다. 잠시후 시도해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun deleteNComment(
    context: Context,
    nNum: Int,
    bNum: Int,
    comments: SnapshotStateList<NvComments.Comment>,
    replys: SnapshotStateMap<Int, MutableList<NvComments.Comment>>,
    viewModel: NovelViewModel
) {
    val repository = ProtoRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }

    val ac = read()
    val retrofitClass =
        RetrofitClass.api.deleteNComment(ac.authorization, bNum, viewModel.reportComment)
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            when (response.body()!!.msg) {
                "JWT expiration" -> {
                    getAToken(context)
                    Handler(Looper.getMainLooper()).postDelayed({
                        deleteNComment(
                            context,
                            nNum,
                            bNum,
                            comments,
                            replys,
                            viewModel
                        )
                    }, 1000)
                }
                "OK" -> {
                    Toast.makeText(
                        context,
                        "댓글이 삭제 되었습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                    getC(nNum, bNum, comments, replys)
                }
                else -> {
                    Toast.makeText(
                        context,
                        "오류 발생. 잠시후 시도해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun likeNComment(context: Context, nNum: Int, cNum: Int) {
    val repository = ProtoRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }

    val ac = read()
    val retrofitClass = RetrofitClass.api.likeNovelComment(ac.authorization, nNum, cNum)
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            when (response.body()!!.msg) {
                "JWT expiration" -> {
                    getAToken(context)
                    Handler(Looper.getMainLooper()).postDelayed({
                        likeNComment(context, nNum, cNum)
                    }, 1000)
                }
                "OK" -> {
                    Toast.makeText(
                        context,
                        "좋아요를 누르셨습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    Toast.makeText(
                        context,
                        "이미 좋아요를 누르셨습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun dislikeNComment(context: Context, nNum: Int, cNum: Int) {
    val repository = ProtoRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }

    val ac = read()
    val retrofitClass = RetrofitClass.api.dislikeNovelComment(ac.authorization, nNum, cNum)
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            when (response.body()!!.msg) {
                "JWT expiration" -> {
                    getAToken(context)
                    Handler(Looper.getMainLooper()).postDelayed({
                        dislikeNComment(context, nNum, cNum)
                    }, 1000)
                }
                "OK" -> {
                    Toast.makeText(
                        context,
                        "싫어요를 누르셨습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    Toast.makeText(
                        context,
                        "이미 싫어요를 누르셨습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })
}
