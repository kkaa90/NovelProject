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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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
        topBar = { TopMenu(scaffoldState, scope, routeAction) },
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
            //Divider(color = Color(0xFFCFCECE))
            LazyColumn() {
                items(boards) { board ->
                    FreeBoardListItem(board, routeAction)
                    //Divider(color = Color(0xFFCFCECE))
                }
            }
        }

    }
}
@Composable
fun FreeBoardListItem(board: Board, routeAction: RouteAction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                routeAction.navWithNum("boardDetail/${board.brdId}")
            })
    ) {
        Column(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if(board.brdImg==1) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(
                        painter = painterResource(id = R.drawable.schumi),
                        contentDescription = "",
                        modifier = Modifier.size(16.sp.value.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = board.brdTitle, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if(board.brdCommentCount!=0) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "[${board.brdCommentCount}]", fontSize = 16.sp)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(){
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = board.memNickname, fontSize = 14.sp, modifier = Modifier.widthIn(max=140.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "조회 ${board.brdHit}",fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "추천 ${board.brdLike}",fontSize = 14.sp)
                }
                Row() {
                    Text(text = board.brdDatetime.split(".")[0], fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
@Preview
fun Test(){
    val context = LocalContext.current
    MyApplicationTheme() {
        Surface(color = MaterialTheme.colors.background){
            Column(Modifier.fillMaxSize()) {
                FreeBoardListItem(board = Board(1, "테스트", "2222-22-22", 3, 0,
                    10,0,1,10,0,0,"제목입니다1234","2222-22-22",
                    listOf(),1,"닉네임_123"),
                    routeAction = RouteAction(NavHostController(context)))

                FreeBoardListItem(board = Board(0, "테스트", "2222-22-22", 3, 0,
                    10,0,0,10,0,0,"제목입니다1234","2222-22-22",
                    listOf(),1,"닉네임_123"),
                    routeAction = RouteAction(NavHostController(context)))
            }
            
            
        }
    }
}