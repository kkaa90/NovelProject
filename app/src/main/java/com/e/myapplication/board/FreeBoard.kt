package com.e.myapplication.board

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.e.myapplication.NAVROUTE
import com.e.myapplication.R
import com.e.myapplication.RouteAction
import com.e.myapplication.TopMenu
import com.e.myapplication.dataclass.Board
import com.e.myapplication.dataclass.Boards
import com.e.myapplication.menu.Drawer
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import retrofit2.Call
import retrofit2.Response

@Composable
fun Greeting6(board: Board, routeAction: RouteAction) {
    Card(modifier = Modifier
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .fillMaxWidth()
        .clickable(onClick = {
            routeAction.navWithNum("boardDetail/${board.brdId}")
        })
        .clip(RoundedCornerShape(12.dp))
        , elevation = 8.dp) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(15.dp))
                Image(
                    painter =
                    painterResource(id = R.drawable.schumi),
//                    if(board.imgUrls.isEmpty()) painterResource(id = R.drawable.schumi) else rememberImagePainter(board.imgUrls[0]),
                    contentDescription = "",
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
fun ShowFreeBoardList(routeAction: RouteAction) {

    val boards = remember{ mutableStateListOf<Board>() }
    val getBoard = RetrofitClass.api.getBoards(1)
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
                routeAction.navTo(NAVROUTE.WRITINGBOARD)
            }) {
                Icon(Icons.Filled.Add, contentDescription = "")
            }
        },
        drawerContent = {
            Drawer(routeAction)
        },
        drawerGesturesEnabled = true,
        scaffoldState = scaffoldState
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            LazyColumn {
                items(boards) { board ->
                    Greeting6(board, routeAction)
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