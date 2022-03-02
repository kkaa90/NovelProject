package com.e.myapplication.board

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.e.myapplication.TopMenu
import com.e.myapplication.dataclass.Board
import com.e.myapplication.dataclass.Boards
import com.e.myapplication.menu.Drawer
import com.e.myapplication.menu.DrawerMenu
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import retrofit2.Call
import retrofit2.Response
import java.util.ArrayList
import javax.security.auth.callback.Callback

class FreeBoardActivity : ComponentActivity() {
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val boards = mutableStateListOf<Board>()
                    ShowFreeBoardList(boards)
                }
            }
        }
    }
}

@Composable
fun Greeting6(board: Board) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                val intent = Intent(context, ShowFreeBoardActivity::class.java)
                intent.putExtra("num", board.brdId)
                context.startActivity(intent)
            })
    ) {
        Text(text = board.brdTitle)
        Text(text = board.memNickname)
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
            var boardList = response.body()
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
        topBar = { TopMenu(scaffoldState,scope) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val intent = Intent(context, WriteFreeBoardActivity::class.java)
                context.startActivity(intent) }) {
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

            LazyColumn() {
                items(boards) { board ->
                    Greeting6(board)
                }
            }
        }

    }


}

@Preview(showBackground = true)
@Composable
fun DefaultPreview6() {
    MyApplicationTheme {

    }
}