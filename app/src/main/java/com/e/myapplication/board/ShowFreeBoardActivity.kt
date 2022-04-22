package com.e.myapplication.board

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.e.myapplication.AccountInfo
import com.e.myapplication.R
import com.e.myapplication.dataclass.*
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.e.myapplication.user.LoginActivity
import com.e.myapplication.user.ProtoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response

class ShowFreeBoardActivity : ComponentActivity() {
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val num = intent.getIntExtra("num", 0)
        val board = mutableStateListOf<Board>()
        val comment = mutableStateListOf<Comment>()
        getBoard(num, board)
        getComment(num, comment)
        setContent {
            MyApplicationTheme {
                Surface(color = MaterialTheme.colors.background) {
                    ShowBoard(boards = board, comment = comment, num = num)
                }
            }
        }
    }
}

@Composable
fun ShowBoard(
    boards: SnapshotStateList<Board>,
    comment: SnapshotStateList<Comment>,
    num: Int
) {
    var content by remember { mutableStateOf("") }
    val context = LocalContext.current
    val repository = ProtoRepository(context = context)
    val writeComment = RetrofitClass
    Column {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(boards) { board ->
                ShowBoard2(board, num)
            }
        }
        Spacer(
            modifier = Modifier
                .padding(10.dp)
                .background(Color.Blue)
        )
        Row {
            OutlinedTextField(value = content, onValueChange = { content = it })
            Button(onClick = {
                val ac = read(repository)
                sendComment(ac, writeComment, content, boards[0].brdId, context, num, comment, 0)
            }) {
                Text(text = "댓글 작성")
            }
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(comment) { index, c ->
                if (c.brdCmtReply == 0) {
                    ShowComment(comment = c, comment, num, writeComment, context, repository, index)

                }
            }
        }
    }


}

@Composable
fun ShowBoard2(board: Board, num : Int) {
    val context = LocalContext.current
    val repository = ProtoRepository(context = context)
    val ac = read(repository)

    Column {
        Text(text = board.brdTitle)
        if (board.brdImg != 0) {
            val url = board.imgUrl
            println(url)
            Image(
                painter = rememberImagePainter(url),
                contentDescription = "schumi",
                modifier = Modifier
                    .size(400.dp)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically){
            IconButton(onClick = { like(context,ac,num) }) {
                Icon(painter = painterResource(id = R.drawable.ic_baseline_thumb_up_24), contentDescription = "")
            }
            Text(text = board.brdLike.toString())
            IconButton(onClick = { dislike(context, ac, num) }) {
                Icon(painter = painterResource(id = R.drawable.ic_baseline_thumb_down_24), contentDescription = "")
            }
            Text(text = board.brdDislike.toString())
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
    repository: ProtoRepository,
    index: Int
) {
    var isExpanded by remember(key1 = comment.brdCmtId) { mutableStateOf(false) }
    var content by remember { mutableStateOf("") }
    Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
        Text(text = comment.brdCmtContents)
        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row {
                    OutlinedTextField(value = content, onValueChange = { content = it })
                    Button(onClick = {
                        val ac = read(repository)
                        sendComment(
                            ac,
                            writeComment,
                            content,
                            comment.brdId,
                            context,
                            num,
                            comments,
                            comment.brdCmtId
                        )
                    }) {
                        Text(text = "댓글 작성")
                    }
                }
                if (comment.brdCmtReplynum != 0) {
                    for (i: Int in index + 1 until comment.brdCmtReplynum + index + 1) {
                        ShowComment2(comment = comments[i])
                    }
                }
            }
        }
    }
}

@Composable
fun ShowComment2(comment: Comment) {
    Text(text = comment.brdCmtContents)
}

fun sendComment(
    ac: AccountInfo, writeComment: RetrofitClass, content: String, brdId: Int,
    context: Context, num: Int, comments: SnapshotStateList<Comment>,
    commentNum: Int
) {
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
            if (result == "1") {
                getComment(num, comments)
            } else {
                Toast.makeText(
                    context,
                    "토큰이 만료되었습니다.\n 다시 로그인 해주세요.",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)

            }

        }

        override fun onFailure(call: Call<PostBoardResponse>, t: Throwable) {
            t.printStackTrace()
        }
    })
}

fun getBoard(num: Int, board: SnapshotStateList<Board>) {
    val getBoard = RetrofitClass.api.getBoard(num)

    getBoard.enqueue(object : retrofit2.Callback<Board> {
        override fun onResponse(call: Call<Board>, response: Response<Board>) {
            response.body()?.let { board.add(it) }
            println("응답")
            println(response.body())
            println("보드")
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

fun like(context: Context, ac: AccountInfo, num: Int) {
    val retrofitClass = RetrofitClass.api.likeBoard(ac.authorization, num)
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            val r = response.body()!!.msg
            println(r)
            if (r == "OK") {
                Toast.makeText(
                    context,
                    "좋아요를 누르셨습니다.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "토큰이 만료되었습니다.\n 다시 로그인 해주세요.",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            }

        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })

}

fun dislike(context: Context, ac: AccountInfo, num: Int) {
    val retrofitClass = RetrofitClass.api.dislikeBoard(ac.authorization,num)
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod>{
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            val r = response.body()!!.msg
            if (r == "OK") {
                Toast.makeText(
                    context,
                    "싫어요를 누르셨습니다.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "토큰이 만료되었습니다.\n 다시 로그인 해주세요.",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview7() {
    MyApplicationTheme {

    }
}
