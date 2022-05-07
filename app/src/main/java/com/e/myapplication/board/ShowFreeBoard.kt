package com.e.myapplication.board

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.e.myapplication.*
import com.e.myapplication.R
import com.e.myapplication.dataclass.*
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.user.ProtoRepository
import com.e.myapplication.user.getAToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response


@Composable
fun ShowBoard(
    routeAction: RouteAction,
    num: Int
) {
    val board = remember {
        mutableStateOf(Board(
            0, "", "",
            0, 0, 0, 0, 0, 0, 0, 0, "제목",
            "", listOf(""), 0, "닉네임"
        ))
    }
    val comment = remember {
        mutableStateListOf<Comment>()
    }
    LaunchedEffect(true){
        getBoard(num, board)
        getComment(num, comment)
    }
    var content by remember { mutableStateOf("") }
    val context = LocalContext.current
    val writeComment = RetrofitClass
//    var visibility by remember {
//        mutableStateOf(false)
//    }
    var commentVisibility by remember {
        mutableStateOf(false)
    }
    val rdVisibility = remember {
        mutableStateOf(false)
    }
    Scaffold(topBar = {

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { routeAction.goBack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = board.value.brdTitle,
                fontSize = 24.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

        }

    },
        bottomBar = {

            Row(Modifier.fillMaxWidth()) {
                TextButton(onClick = { commentVisibility = !commentVisibility }) {
                    Text(text = "댓글")
                }
                TextButton(onClick = { rdVisibility.value = true }) {
                    Text(text = "신고")
                }
            }

        }) { p ->
        Box {
            AnimatedVisibility(visible = rdVisibility.value) {
                ShowReportDialog(num = num, visibility = rdVisibility)
            }

        }
        if (commentVisibility) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(p)
            ) {
                Row {
                    if (lCheck) {
                        OutlinedTextField(value = content, onValueChange = { content = it })
                        Button(onClick = {
                            sendComment(
                                writeComment,
                                content,
                                board.value.brdId,
                                context,
                                num,
                                comment,
                                0
                            )
                            content = ""
                        }) {
                            Text(text = "댓글 작성")
                        }
                    } else {
                        Text("댓글을 작성하시려면 로그인 하셔야 합니다.")
                    }
                }
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    itemsIndexed(comment) { index, c ->
                        if (c.brdCmtReply == 0) {
                            ShowComment(comment = c, comment, num, writeComment, context, index)

                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(p)
            ) {

                LazyColumn {
                    items(items = board.value.imgUrls) { url ->
                        Image(
                            painter = rememberImagePainter(url),
                            contentDescription = "",
                            modifier = Modifier
                                .size(300.dp), alignment = Alignment.Center
                        )
                    }
                    item {
                        Text(text = Html.fromHtml(board.value.brdContents).toString())
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                if (lCheck) {
                                    like(context, num)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "로그인이 필요합니다.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    routeAction.navTo(NAVROUTE.LOGIN)
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_thumb_up_24),
                                    contentDescription = ""
                                )
                            }
                            Text(text = board.value.brdLike.toString())
                            IconButton(onClick = {
                                if (lCheck) {
                                    dislike(context, num)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "로그인이 필요합니다.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    routeAction.navTo(NAVROUTE.LOGIN)
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_thumb_down_24),
                                    contentDescription = ""
                                )
                            }
                            Text(text = board.value.brdDislike.toString())
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun ShowComment(
    comment: Comment,
    comments: SnapshotStateList<Comment>,
    num: Int,
    writeComment: RetrofitClass,
    context: Context,
    index: Int
) {
    var isExpanded by remember(key1 = comment.brdCmtId) { mutableStateOf(false) }
    var content by remember { mutableStateOf("") }
    Column(modifier = Modifier
        .clickable { isExpanded = !isExpanded }
        .fillMaxWidth()) {
        Text(text = comment.memNickname, fontSize = 20.sp)
        Text(text = comment.brdCmtContents)
        AnimatedVisibility(visible = isExpanded) {
            Column {
                Row {
                    if (lCheck) {
                        OutlinedTextField(value = content, onValueChange = { content = it })
                        Button(onClick = {
                            sendComment(
                                writeComment,
                                content,
                                comment.brdId,
                                context,
                                num,
                                comments,
                                comment.brdCmtId
                            )
                            content = ""
                        }) {
                            Text(text = "댓글 작성")
                        }
                    } else {
                        Text("댓글을 작성하시려면 로그인 하셔야 합니다.")
                    }
                }
            }
        }

        if (comment.brdCmtReplynum != 0) {
            for (i: Int in index + 1 until comment.brdCmtReplynum + index + 1) {
                Row {
                    Spacer(modifier = Modifier.width(10.dp))
                    ShowComment2(comment = comments[i])
                }

            }
        }
    }
}

@Composable
fun ShowComment2(comment: Comment) {
    Column {
        Text(text = comment.memNickname)
        Text(text = comment.brdCmtContents)
    }
}

@Composable
fun ShowReportDialog(num: Int, visibility: MutableState<Boolean>) {
    val context = LocalContext.current
    var mSelected by remember {
        mutableStateOf(reportState[0])
    }
    var mVisibility by remember {
        mutableStateOf(false)
    }
    var content by remember {
        mutableStateOf("")
    }
    Dialog(onDismissRequest = { visibility.value = false }) {
        Surface(
            modifier = Modifier
                .width(240.dp)
                .height(400.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column {
                Text(text = "신고")
                Spacer(modifier = Modifier.height(20.dp))
                Column {
                    Row(modifier = Modifier.clickable { mVisibility = !mVisibility }) {
                        Text(text = mSelected.presentState)
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
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(text = "자세한 사유") })
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    OutlinedButton(onClick = { visibility.value = false }) {
                        Text(text = "취소")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedButton(onClick = {
                        rBoard(context, num, mSelected, content)
                        visibility.value = false
                    }) {
                        Text(text = "확인")
                    }
                }
            }
        }

    }
}

fun sendComment(
    writeComment: RetrofitClass, content: String, brdId: Int,
    context: Context, num: Int, comments: SnapshotStateList<Comment>,
    commentNum: Int
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
    val rc = writeComment.api.writeComment(
        ac.authorization.toString(),
        PostComments(
            content,
            commentNum.toString(),
            "0",
            ac.memNick.toString()
        ), brdId
    )
    rc.enqueue(object : retrofit2.Callback<PostBoardResponse> {
        override fun onResponse(
            call: Call<PostBoardResponse>,
            response: Response<PostBoardResponse>
        ) {
            val result = response.body()?.msg
            println(result)
            if (result == "OK") {
                getComment(num, comments)
            } else {
                getAToken(context)
                rc.cancel()
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        sendComment(
                            writeComment,
                            content,
                            brdId,
                            context,
                            num,
                            comments,
                            commentNum
                        )
                    }, 1000
                )

            }

        }

        override fun onFailure(call: Call<PostBoardResponse>, t: Throwable) {
            t.printStackTrace()
        }
    })
}

fun getBoard(num: Int, board: MutableState<Board>) {
    val getBoard = RetrofitClass.api.getBoard(num)

    getBoard.enqueue(object : retrofit2.Callback<Board> {
        override fun onResponse(call: Call<Board>, response: Response<Board>) {
            response.body()?.let { board.value = it }
        }


        override fun onFailure(call: Call<Board>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun getComment(num: Int, comment: SnapshotStateList<Comment>) {
    if (comment.size != 0) {
        comment.removeAll(comment)
    }
    val getComment = RetrofitClass.api.getComment(num)
    getComment.enqueue(object : retrofit2.Callback<Comments> {
        override fun onResponse(call: Call<Comments>, response: Response<Comments>) {
            if (response.body()?.pagenum != 0) {
                for (i: Int in 0 until response.body()?.comments?.size!!) {
                    let { comment.addAll(response.body()?.comments!![i]) }
                }
            }
        }

        override fun onFailure(call: Call<Comments>, t: Throwable) {
            t.printStackTrace()
        }

    })
}


fun read(repository: ProtoRepository): AccountInfo {
    var accountInfo: AccountInfo
    runBlocking(Dispatchers.IO) {
        accountInfo = repository.readAccountInfo()
    }
    return accountInfo
}

fun like(context: Context, num: Int) {
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()
        }
        return accountInfo
    }

    val ac = read()
    val retrofitClass = RetrofitClass.api.likeBoard(ac.authorization, num)
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            when (response.body()!!.msg) {
                "OK" -> {
                    Toast.makeText(
                        context,
                        "추천을 누르셨습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                "reduplication" -> {
                    Toast.makeText(
                        context,
                        "이미 추천을 누르셨습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    getAToken(context)
                    retrofitClass.cancel()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            like(context, num)
                        }, 1000
                    )
                }
            }

        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })

}

fun dislike(context: Context, num: Int) {
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()
        }
        return accountInfo
    }

    val ac = read()
    val retrofitClass = RetrofitClass.api.dislikeBoard(ac.authorization, num)
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            when (response.body()!!.msg) {
                "OK" -> {
                    Toast.makeText(
                        context,
                        "비추천을 누르셨습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                "reduplication" -> {
                    Toast.makeText(
                        context,
                        "이미 비추천을 누르셨습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    getAToken(context)
                    retrofitClass.cancel()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            dislike(context, num)
                        }, 1000
                    )
                }
            }
        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun rBoard(context: Context, num: Int, reportState: ReportState, content: String) {
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()
        }
        return accountInfo
    }
    val ac = read()
    val retrofitClass = RetrofitClass.api.reportBoard(
        ac.authorization,
        num,
        ReportMethod(reportState.sendState, content)
    )
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            when (response.body()!!.msg) {
                "OK" -> {
                    Toast.makeText(
                        context,
                        "신고가 완료되었습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                "reduplication" -> {
                    Toast.makeText(
                        context,
                        "이미 신고한 게시물입니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    getAToken(context)
                    retrofitClass.cancel()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            rBoard(context, num, reportState, content)
                        }, 1000
                    )
                }
            }
        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

//fun getImage(brdImg: Int, iUrl: SnapshotStateList<ImageUrl>) {
//    val getImage = RetrofitClass.api.getImageUrl(brdImg)
//    getImage.enqueue(object : retrofit2.Callback<ImageUrl> {
//        override fun onResponse(
//            call: Call<ImageUrl>,
//            response: Response<ImageUrl>
//        ) {
//            response.body()?.let { iUrl.add(it) }
//        }
//
//        override fun onFailure(call: Call<ImageUrl>, t: Throwable) {
//            t.printStackTrace()
//        }
//    })
//}
