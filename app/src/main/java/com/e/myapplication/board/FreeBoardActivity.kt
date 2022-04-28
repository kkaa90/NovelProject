package com.e.myapplication.board

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.e.myapplication.R
import com.e.myapplication.TopMenu
import com.e.myapplication.dataclass.Board
import com.e.myapplication.dataclass.Boards
import com.e.myapplication.menu.Drawer
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import retrofit2.Call
import retrofit2.Response

class FreeBoardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val boards = mutableStateListOf<Board>()
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    ShowFreeBoardList(boards)
                }
            }
        }
    }
}

@Composable
fun Greeting6(board: Board) {
    val context = LocalContext.current
    Card(modifier = Modifier
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .fillMaxWidth()
        .clickable(onClick = {
            val intent = Intent(context, ShowFreeBoardActivity::class.java)
            intent.putExtra("num", board.brdId)
            context.startActivity(intent)
        })
        .clip(RoundedCornerShape(12.dp))
        , elevation = 8.dp) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(15.dp))
                Image(
                    painter = painterResource(id = R.drawable.schumi), contentDescription = "",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.width(15.dp))
                Column {
                    Text(text = board.brdTitle, fontSize = 24.sp, maxLines = 1)
                    Text(text = board.memNickname, maxLines = 1)
                    Text(text = board.brdContents, fontSize = 20.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(modifier = Modifier.width(15.dp))
            }
        }
    }


}

@Composable
fun ShowFreeBoardList(boards: SnapshotStateList<Board>) {

    val getBoard = RetrofitClass.api.getBoards(1)
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    getBoard.enqueue(object : retrofit2.Callback<Boards> {
        override fun onResponse(call: Call<Boards>, response: Response<Boards>) {
            val boardList = response.body()
            if (boardList != null) {
                boards.addAll(boardList.boards)
                println(boards)
            }

        }

        override fun onFailure(call: Call<Boards>, t: Throwable) {
            t.printStackTrace()
        }

    })

    Scaffold(
        topBar = { TopMenu(scaffoldState, scope) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val intent = Intent(context, WriteFreeBoardActivity::class.java)
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
        Column(modifier = Modifier.fillMaxSize()) {

            LazyColumn {
                items(boards) { board ->
                    Greeting6(board)
                }
            }
        }

    }


}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun DefaultPreview6() {
    val boards = mutableStateListOf<Board>()
    boards.add(
        Board(
            0, "가나다라", "",
            0, 0, 0, 0, 0, 0, 0, 0, "제목",
            "", "", 0, "닉네임"
        )
    )
    boards.add(
        Board(
            0, "가나다라", "",
            0, 0, 0, 0, 0, 0, 0, 0, "제목",
            "", "", 0, "닉네임"
        )
    )
    boards.add(
        Board(
            0, "가나다라zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz", "",
            0, 0, 0, 0, 0, 0, 0, 0, "제목",
            "", "", 0, "닉네임"
        )
    )
    MyApplicationTheme {
        ShowFreeBoardList(boards = boards)
    }
}