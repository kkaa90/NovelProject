package com.e.myapplication.novel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotMutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.myapplication.AccountInfo
import com.e.myapplication.R
import com.e.myapplication.board.read
import com.e.myapplication.board.sendComment
import com.e.myapplication.dataclass.*
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.e.myapplication.user.LoginActivity
import com.e.myapplication.user.ProtoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response

class NovelActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val bNum = intent.getIntExtra("boardNum", 0)
        val nNum = intent.getIntExtra("novelNum", 0)
        val nvTitle = intent.getStringExtra("nvTitle")
        val board = mutableStateListOf<NovelsDetail>()
        val comment = mutableStateListOf<NvComments.Comment>()
        val repository = ProtoRepository(this)
        fun read(): AccountInfo {
            var accountInfo: AccountInfo
            runBlocking(Dispatchers.IO) {
                accountInfo = repository.readAccountInfo()

            }
            return accountInfo
        }

        val ac = read()
        getNovelBoard(this, ac, nNum = nNum, bNum = bNum, board)
        getC(nNum, bNum, comment)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting2(board, bNum, nNum, nvTitle!!, comment)
                }
            }
        }
    }
}

@Composable
fun Greeting2(
    boards: SnapshotStateList<NovelsDetail>,
    bNum: Int,
    nNum: Int,
    nvTitle: String,
    comments: SnapshotStateList<NvComments.Comment>
) {
    val context = LocalContext.current
    val rPoint = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    var rName: String by remember { mutableStateOf(rPoint[9]) }
    var rExpanded by remember {
        mutableStateOf(false)
    }
    var visibility by remember { mutableStateOf(false) }
    var commentV by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Scaffold(topBar = {
            AnimatedVisibility(visible = visibility) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = nvTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                            Text(text = "리뷰 점수")
                            Spacer(modifier = Modifier.width(4.0.dp))
                            Column() {
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
                            Button(onClick = { sendR(context, bNum, nNum, rName) }) {
                                Text(text = "리뷰 전송")
                            }
                        }
                    }
                }
            }) { innerpadding ->
            BackHandler() {
                if (commentV) commentV = false
                else if (!commentV && visibility) visibility = false
                else (context as Activity).finish()
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
                        items(boards) { b ->
                            ShowBoard(board = b)
                        }
                    }
                } else {
                    var content by remember {
                        mutableStateOf("")
                    }
                    Column(Modifier.fillMaxSize()) {
                        Row {
                            OutlinedTextField(value = content, onValueChange = { content = it })
                            Button(onClick = {
                                sendC(context, nNum, bNum, 0, content, comments)
                            }) {
                                Text(text = "댓글 작성")
                            }
                        }
                        LazyColumn(Modifier.fillMaxWidth()) {
                            itemsIndexed(comments) { index, c ->
                                if (c.nvCmtReply == 0) {
                                    ShowComment(comment = c, nNum, bNum, comments, index)
                                }
                            }
                        }
                    }

                }
            }

        }
    }
}

@Composable
fun ShowBoard(board: NovelsDetail) {
    val context = LocalContext.current
    Column() {
        Text(text = board.nvContents)
    }
}

@Composable
fun ShowComment(
    comment: NvComments.Comment,
    nNum: Int,
    bNum: Int,
    comments: SnapshotStateList<NvComments.Comment>,
    index: Int
) {
    val context = LocalContext.current
    var visibility by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .clickable { visibility = !visibility }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.schumi),
                contentDescription = "",
                modifier = Modifier
                    .clip(CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
                    .size(16.dp)
            )
            Text(text = comment.memNickname)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = comment.nvCmtContents)
        AnimatedVisibility(visible = visibility) {
            var content by remember {
                mutableStateOf("")
            }
            Column(Modifier.fillMaxWidth()) {
                Row {
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(value = content, onValueChange = { content = it })
                    Button(onClick = {
                        sendC(context, nNum, bNum, comment.nvCmtId, content, comments)
                    }) {
                        Text(text = "댓글 작성")
                    }

                }
                if (comment.nvCmtReplynum != 0) {
                    for (i: Int in index + 1 until comment.nvCmtReplynum + index + 1) {
                        Row() {
                            Spacer(modifier = Modifier.width(20.dp))
                            Column() {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.schumi),
                                        contentDescription = "",
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .border(1.dp, Color.Black, CircleShape)
                                            .size(16.dp)
                                    )
                                    Text(text = comments[i].memNickname)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = comments[i].nvCmtContents)
                            }
                            
                        }
                        
                    }
                }

            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    MyApplicationTheme {
    }
}

fun getNovelBoard(
    context: Context,
    ac: AccountInfo,
    nNum: Int,
    bNum: Int,
    snapshotStateList: SnapshotStateList<NovelsDetail>
) {

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
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    Toast.makeText(
                        context,
                        "토큰이 만료되었습니다.\n 다시 로그인 해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                    (context as Activity).finish()
                }
                "point lack" -> {
                    Toast.makeText(
                        context,
                        "포인트가 부족합니다.",
                        Toast.LENGTH_LONG
                    ).show()
                    (context as Activity).finish()
                }
                "null" -> {
                    snapshotStateList.add(r!!)

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
    comments: SnapshotStateList<NvComments.Comment>
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
        ac.authorization, nNum, PostNvComments(ac.memNick, cmt, nCR, 0, bNum)
    )
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            println("댓글 전송 테스트 결과 : "+response.body()!!.msg)
            when (response.body()!!.msg) {
                "JWT expiration" -> {
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    Toast.makeText(
                        context,
                        "토큰이 만료되었습니다.\n 다시 로그인 해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                "1" -> {
                    getC(nNum, bNum, comments)
                }
            }

        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun getC(nNum: Int, bNum: Int, comments: SnapshotStateList<NvComments.Comment>) {
    comments.removeAll(comments)
    val retrofitClass = RetrofitClass.api.getNComment(nNum, bNum, 1)
    retrofitClass.enqueue(object : retrofit2.Callback<NvComments> {
        override fun onResponse(call: Call<NvComments>, response: Response<NvComments>) {
            val r = response.body()
            if (r?.pagenum != 0) {
                for (i: Int in 0 until r?.comments?.size!!) {
                    let { comments.addAll(r.comments[i]) }
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
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    Toast.makeText(
                        context,
                        "토큰이 만료되었습니다.\n 다시 로그인 해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
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