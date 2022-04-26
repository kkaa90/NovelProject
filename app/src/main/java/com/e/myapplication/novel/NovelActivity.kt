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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.myapplication.AccountInfo
import com.e.myapplication.board.read
import com.e.myapplication.board.sendComment
import com.e.myapplication.dataclass.CallMethod
import com.e.myapplication.dataclass.NovelsDetail
import com.e.myapplication.dataclass.ReviewBody
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
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting2(board, bNum, nNum, nvTitle!!)
                }
            }
        }
    }
}

@Composable
fun Greeting2(boards: SnapshotStateList<NovelsDetail>, bNum: Int, nNum: Int, nvTitle: String) {
    val context = LocalContext.current
    val rPoint = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    var rName: String by remember { mutableStateOf(rPoint[9]) }
    var rExpanded by remember {
        mutableStateOf(false)
    }
    var visibility by remember { mutableStateOf(false) }
    var commentV by remember { mutableStateOf(false) }
    Column(modifier = Modifier
        .clickable { }
        .fillMaxSize()) {
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Text(text = "댓글", modifier = Modifier.clickable { commentV = !commentV }.padding(8.dp))

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
            }) {
            BackHandler() {
                if (commentV) commentV = false
                else if (!commentV && visibility) visibility = false
                else (context as Activity).finish()
            }
            if (!commentV) {
                LazyColumn(
                    Modifier
                        .clickable { visibility = !visibility }
                        .fillMaxSize()) {
                    items(boards) { b ->
                        ShowBoard(board = b)
                    }
                }
            } else {
                var content by rememberSaveable {
                    mutableStateOf("")
                }
                Row {
                    OutlinedTextField(value = content, onValueChange = { content = it })
                    Button(onClick = {

                    }) {
                        Text(text = "댓글 작성")
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
        RetrofitClass.api.sendReview(ac.authorization, bNum, ReviewBody(nNum.toString(), rvPoint))
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