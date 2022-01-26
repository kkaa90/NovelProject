package com.e.myapplication.board

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotMutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.myapplication.AccountInfo
import com.e.myapplication.dataclass.Board
import com.e.myapplication.dataclass.Comments
import com.e.myapplication.dataclass.PostBoardResponse
import com.e.myapplication.dataclass.PostComments
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.e.myapplication.user.ProtoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.w3c.dom.Comment
import retrofit2.Call
import retrofit2.Response

class ShowFreeBoardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var intent = getIntent()
            var num = intent.getIntExtra("num", 0)
            val getBoard = RetrofitClass.api.getBoard(num)
            val getComment = RetrofitClass.api.getComment(num)
            println(getBoard.request().url())
            println(getComment.request().url())
            val board = mutableStateListOf<Board>()
            val comment = mutableStateListOf<com.e.myapplication.dataclass.Comment>()
            getBoard.enqueue(object : retrofit2.Callback<Board> {
                override fun onResponse(call: Call<Board>, response: Response<Board>) {
                    response.body()?.let { board.add(it) }
                    println("응답")
                    println(response.body())
                    println("보드")
                    println(board[0])
                }

                override fun onFailure(call: Call<Board>, t: Throwable) {
                    t.printStackTrace()
                }

            })
            getComment.enqueue(object : retrofit2.Callback<Comments> {
                override fun onResponse(call: Call<Comments>, response: Response<Comments>) {
                    if (response.body()?.pagenum!=0){
                        for (i: Int in 0 until response.body()?.comments?.size!!) {
                            let { comment.addAll(response.body()?.comments!![i]) }
                        }
                    }
                }

                override fun onFailure(call: Call<Comments>, t: Throwable) {
                    t.printStackTrace()
                }

            })
            MyApplicationTheme {
                Surface(color = MaterialTheme.colors.background) {
                    ShowBoard(boards = board, comment = comment)
                }
            }
        }
    }
}

@Composable
fun ShowBoard(boards: SnapshotStateList<Board>, comment: SnapshotStateList<com.e.myapplication.dataclass.Comment>) {
    var content by remember { mutableStateOf("") }
    val context = LocalContext.current
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }

    val writeComment = RetrofitClass
    Column() {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(boards) { board ->
                ShowBoard2(board)
            }
        }
        Spacer(
            modifier = Modifier
                .padding(10.dp)
                .background(Color.Blue)
        )
        Row() {
            OutlinedTextField(value = content, onValueChange = { content = it })
            Button(onClick = {
                val ac = read()
                val rc = writeComment.api.writeComment(
                    ac.authorization.toString(),
                    PostComments(
                        content,
                        "0",
                        "0",
                        boards[0].brdId.toString(),
                        ac.memId.toString(),
                        ac.memNick.toString()
                    ), boards[0].brdId
                )
                rc.enqueue(object :retrofit2.Callback<PostBoardResponse>{
                    override fun onResponse(
                        call: Call<PostBoardResponse>,
                        response: Response<PostBoardResponse>
                    ) {
                        println(response.body().toString())
                    }

                    override fun onFailure(call: Call<PostBoardResponse>, t: Throwable) {
                        t.printStackTrace()
                    }

                })
            }) {
                Text(text = "댓글 작성")
            }
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(comment){
                c->
                ShowComment(comment = c)
            }
        }
    }


}

@Composable
fun ShowBoard2(board: Board) {
    Text(text = board.brdTitle)
}

@Composable
fun ShowComment(comment: com.e.myapplication.dataclass.Comment) {
    Text(text = comment.brdCmtContents)
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview7() {
    MyApplicationTheme {

    }
}