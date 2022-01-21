package com.e.myapplication.board

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.tooling.preview.Preview
import com.e.myapplication.dataclass.Boards
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import retrofit2.Call
import retrofit2.Response
import java.util.ArrayList
import javax.security.auth.callback.Callback

class FreeBoardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val boards = mutableStateListOf<Boards>()
                    ShowFreeBoardList(boards)
                }
            }
        }
    }
}

@Composable
fun Greeting6(board: Boards) {
    Text(board.brdTitle)

}

@Composable
fun ShowFreeBoardList(boards : SnapshotStateList<Boards>){
    val getBoard = RetrofitClass.api.getBoards(1)
    
    getBoard.enqueue(object : retrofit2.Callback<List<Boards>> {
        override fun onResponse(call: Call<List<Boards>>, response: Response<List<Boards>>) {
            var boardList = response.body()
            if (boardList != null) {
                boards.addAll(boardList)
                println(boards)

            }

        }

        override fun onFailure(call: Call<List<Boards>>, t: Throwable) {
            t.printStackTrace()
        }

    })

    LazyColumn(){
        items(boards){
            board -> Greeting6(board)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview6() {
    MyApplicationTheme {

    }
}