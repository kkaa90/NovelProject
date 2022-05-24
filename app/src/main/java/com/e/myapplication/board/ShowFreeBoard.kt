package com.e.myapplication.board

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.e.myapplication.NAVROUTE
import com.e.myapplication.R
import com.e.myapplication.RouteAction
import com.e.myapplication.dataclass.Comment
import com.e.myapplication.dataclass.reportState
import com.e.myapplication.lCheck
import com.e.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch


@Composable
fun ShowBoard(
    viewModel: FreeBoardViewModel,
    routeAction: RouteAction,
    num: Int
) {
    val board by viewModel.board.collectAsState()
    val comment = remember {
        mutableStateListOf<Comment>()
    }
    LaunchedEffect(true) {
        viewModel.updateBoard(num)
        viewModel.updateComments(num, 1)
        viewModel.a=viewModel.read()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                viewModel.viewModelScope.launch {
                    viewModel.comments.collect {
                        comment.addAll(it)
                    }
                }
            }, 1000
        )
    }
    val context = LocalContext.current
    val rdVisibility = remember {
        mutableStateOf(false)
    }
    val rcVisibility = remember {
        mutableStateOf(false)
    }
    Scaffold(topBar = {

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { routeAction.goBack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = board.board.brdTitle,
                    fontSize = 24.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TextButton(onClick = { rdVisibility.value = true }) {
                Text(text = "신고")
            }
        }
    }) { p ->
        Box {
            AnimatedVisibility(visible = rdVisibility.value) {
                ShowReportDialog(num = num, visibility = rdVisibility, viewModel)
            }
            AnimatedVisibility(visible = rcVisibility.value) {
                ShowCReportDialog(bNum = num, visibility = rcVisibility, viewModel = viewModel)
            }

        }
        BackHandler {
            if (viewModel.currentCommentPosition != -1) {
                viewModel.currentCommentPosition = -1
            } else {
                viewModel.comment = ""
                routeAction.goBack()
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(p)
        ) {

            LazyColumn(Modifier.fillMaxWidth(), state = viewModel.scrollState) {
                item {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_baseline_person_24),
                            contentDescription = "",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.White, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(text = board.user.memNick, fontSize = 14.sp)
                            Text(
                                text = board.board.brdDatetime.split(".")[0] + " 조회 ${board.board.brdHit}",
                                fontSize = 14.sp
                            )
                        }
                    }
                    Divider(thickness = 1.dp, color = Color(0xFFA0A0A0))

                }
                items(items = board.board.imgUrls) { url ->
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = rememberImagePainter(url),
                            contentDescription = "",
                            modifier = Modifier
                                .size(300.dp), alignment = Alignment.Center
                        )
                    }
                }
                item {
                    Column {
                        Text(
                            text = Html.fromHtml(board.board.brdContents).toString()
                                .replace("<br>", "\n")
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            FreeBoardIconButton(
                                context = context,
                                num = num,
                                routeAction = routeAction,
                                l = true,
                                icon = R.drawable.ic_baseline_thumb_up_24,
                                count = board.board.brdLike.toString(),
                                viewModel = viewModel
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FreeBoardIconButton(
                                context = context,
                                num = num,
                                routeAction = routeAction,
                                l = false,
                                icon = R.drawable.ic_baseline_thumb_down_24,
                                count = board.board.brdDislike.toString(),
                                viewModel = viewModel
                            )

                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            OutlinedTextField(
                                value = viewModel.comment,
                                onValueChange = {
                                    if (!lCheck) {
                                        Toast
                                            .makeText(
                                                context,
                                                "로그인이 필요합니다.",
                                                Toast.LENGTH_LONG
                                            )
                                            .show()
                                        routeAction.navTo(NAVROUTE.LOGIN)
                                    } else {
                                        viewModel.comment = it
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                            )
                            OutlinedButton(onClick = {
                                viewModel.sendComment(viewModel.comment, "0", num)
//                            sendComment(
//                                writeComment,
//                                content,
//                                board.board.brdId,
//                                context,
//                                num,
//                                comment,
//                                0
//                            )
                                viewModel.comment = ""
                                Handler(Looper.getMainLooper()).postDelayed(
                                    {
                                        comment.clear()
                                        viewModel.viewModelScope.launch {
                                            viewModel.comments.collect {
                                                comment.addAll(it)
                                            }
                                        }
                                    }, 1000
                                )

                            }, enabled = (viewModel.comment != "")) {
                                Text(text = "작성")
                            }

                        }
                        Divider(thickness = 1.dp, color = Color(0xFFA0A0A0))
                        Row(Modifier.padding(8.dp)) {

                            Text(text = "댓글(${board.board.brdCommentCount})")
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "",
                                Modifier.clickable {
                                    Handler(Looper.getMainLooper()).postDelayed(
                                        {
                                            comment.clear()
                                            viewModel.viewModelScope.launch {
                                                viewModel.comments.collect {
                                                    comment.addAll(it)
                                                }
                                            }
                                        }, 1000
                                    )
                                })
                        }
                        Divider(thickness = 1.dp, color = Color(0xFFA0A0A0))
                    }

                }
                itemsIndexed(comment.reversed()) { index, c ->
                    if (c.brdCmtReply == 0) {
                        ShowComment(
                            comment = c,
                            comment,
                            num,
                            context,
                            index,
                            viewModel,
                            routeAction,
                            rcVisibility
                        )
                    }
                }
            }

        }

    }
}

@Composable
fun FreeBoardIconButton(
    context: Context,
    num: Int,
    routeAction: RouteAction,
    l: Boolean,
    icon: Int,
    count: String,
    viewModel: FreeBoardViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .border(1.dp, Color.Black)
            .clickable {
                if (lCheck) {
                    if (l) {
                        viewModel.likeClick(num)
                    } else {
                        viewModel.dislikeClick(num)
                    }

                } else {
                    Toast
                        .makeText(
                            context,
                            "로그인이 필요합니다.",
                            Toast.LENGTH_LONG
                        )
                        .show()
                    routeAction.navTo(NAVROUTE.LOGIN)
                }
            }
    ) {

        Icon(
            painter = painterResource(id = icon),
            contentDescription = "",
            modifier = Modifier.padding(all = 12.dp)
        )

        Text(count, fontSize = 20.sp)
    }

}

@Composable
fun ShowComment(
    comment: Comment,
    comments: MutableList<Comment>,
    num: Int,
    context: Context,
    index: Int,
    viewModel: FreeBoardViewModel,
    routeAction: RouteAction,
    visibility: MutableState<Boolean>
) {
    println("index : $index")
    Card(modifier = Modifier
        .clickable {
            viewModel.comment2 = ""
            if (viewModel.currentCommentPosition == index) {
                viewModel.currentCommentPosition = -1
            } else {
                viewModel.currentCommentPosition = index
            }
        }
        .fillMaxWidth()
        .border(0.25.dp, Color.Gray)) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = comment.memNickname, fontSize = 16.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.likeComment(comment.brdId,comment.brdCmtId) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_thumb_up_24),
                            contentDescription = "",
                            modifier = Modifier.size(16.sp.value.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(comment.brdCmtLike.toString(), fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.dislikeComment(comment.brdId,comment.brdCmtId) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_thumb_down_24),
                            contentDescription = "",
                            modifier = Modifier.size(16.sp.value.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(comment.brdCmtDislike.toString(), fontSize = 16.sp)
                    }
                    println(viewModel.a.memId)
                    println(comment.memId.toString())
                    if(viewModel.a.memId==comment.memId.toString()){
                        TextButton(onClick = {

                        }) {
                            Text(text="삭제")
                        }
                    }
                    else {
                        TextButton(onClick = {
                            viewModel.reportComment=comment.brdCmtId
                            visibility.value=true}) {
                            Text(text = "신고")
                        }
                    }
                    
                }

            }
            Row {
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = comment.brdCmtContents, fontSize = 18.sp)
            }
            Row {
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = comment.brdCmtDatetime, fontSize = 14.sp, color = Color.Gray)
            }

            AnimatedVisibility(visible = (viewModel.currentCommentPosition == index)) {
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        OutlinedTextField(
                            value = viewModel.comment2,
                            onValueChange = {
                                if (!lCheck) {
                                    Toast
                                        .makeText(
                                            context,
                                            "로그인이 필요합니다.",
                                            Toast.LENGTH_LONG
                                        )
                                        .show()
                                    routeAction.navTo(NAVROUTE.LOGIN)
                                } else {
                                    viewModel.comment2 = it
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        )
                        OutlinedButton(onClick = {
                            viewModel.sendComment(
                                viewModel.comment2,
                                comment.brdCmtId.toString(),
                                num
                            )
                            viewModel.comment2 = ""
                            Handler(Looper.getMainLooper()).postDelayed(
                                {
                                    viewModel.currentCommentPosition = -1
                                    comments.clear()
                                    viewModel.viewModelScope.launch {
                                        viewModel.comments.collect {
                                            comments.addAll(it)
                                        }
                                    }
                                }, 1000
                            )
                        }, enabled = (viewModel.comment2 != "")) {
                            Text(text = "작성")
                        }

                    }
                }
            }

            if (comment.brdCmtReplynum != 0) {
                for (i: Int in index - comment.brdCmtReplynum until index) {
                    println(i)
                    ShowComment2(comment = comments.reversed()[i], visibility, viewModel)
                }
            }
        }
    }
}

@Composable
fun ShowComment2(comment: Comment,visibility: MutableState<Boolean>, viewModel: FreeBoardViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, Color.Gray)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(20.dp))
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = comment.memNickname, fontSize = 16.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.likeComment(comment.brdId,comment.brdCmtId) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_thumb_up_24),
                                contentDescription = "",
                                modifier = Modifier.size(16.sp.value.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(comment.brdCmtLike.toString(), fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.dislikeComment(comment.brdId,comment.brdCmtId) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_thumb_down_24),
                                contentDescription = "",
                                modifier = Modifier.size(16.sp.value.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(comment.brdCmtDislike.toString(), fontSize = 16.sp)
                        }
                        if(viewModel.a.memId==comment.memId.toString()){
                            TextButton(onClick = {

                            }) {
                                Text(text="삭제")
                            }
                        }
                        else {
                            TextButton(onClick = {
                                viewModel.reportComment=comment.brdCmtId
                                visibility.value=true}) {
                                Text(text = "신고")
                            }
                        }
                    }
                }
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = comment.brdCmtContents, fontSize = 18.sp)
                }
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = comment.brdCmtDatetime, fontSize = 14.sp, color = Color.Gray)
                }
            }
        }

    }

}

@Composable
fun ShowReportDialog(num: Int, visibility: MutableState<Boolean>, viewModel: FreeBoardViewModel) {
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
                        .height(100.dp))
                Spacer(modifier = Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    OutlinedButton(onClick = {
                        viewModel.reportContent = ""
                        visibility.value = false }) {
                        Text(text = "취소")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedButton(onClick = {
                        viewModel.reportingBoard(num, mSelected)
                        visibility.value = false
                    }) {
                        Text(text = "확인")
                    }
                }
            }
        }
    }
}
@Composable
fun ShowCReportDialog(bNum: Int, visibility: MutableState<Boolean>, viewModel: FreeBoardViewModel) {
    var mSelected by remember {
        mutableStateOf(reportState[0])
    }
    var mVisibility by remember {
        mutableStateOf(false)
    }
    Dialog(onDismissRequest = {
        viewModel.reportContent = ""
        visibility.value = false }) {
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
                        .height(100.dp))
                Spacer(modifier = Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    OutlinedButton(onClick = {
                        viewModel.reportContent = ""
                        visibility.value = false }) {
                        Text(text = "취소")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedButton(onClick = {
                        viewModel.reportingComment(bNum, mSelected)
                        visibility.value = false
                    }) {
                        Text(text = "확인")
                    }
                }
            }
        }
    }
}

@Composable
fun deleteBoardDialog(){
    Dialog(onDismissRequest = { /*TODO*/ }) {
        Surface() {
            
        }
    }
}

@Composable
fun deleteCommentDialog(){
    Dialog(onDismissRequest = { /*TODO*/ }) {
        Surface() {

        }
    }
}