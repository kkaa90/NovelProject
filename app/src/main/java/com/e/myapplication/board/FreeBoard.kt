package com.e.myapplication.board

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.e.myapplication.dataclass.BoardList
import com.e.myapplication.menu.Drawer
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import retrofit2.Call
import retrofit2.Response


@Composable
fun ShowFreeBoardList(boardViewModel: FreeBoardViewModel, routeAction: RouteAction) {

    LaunchedEffect(true){
        boardViewModel.updateBoardList()
    }
    val boards = boardViewModel.boards.collectAsState().value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
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
            Drawer(routeAction,scaffoldState)
        },
        drawerGesturesEnabled = true,
        scaffoldState = scaffoldState
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            //Divider(color = Color(0xFFCFCECE))
            LazyColumn() {
                items(boards) { board ->
                    FreeBoardListItem(board,boardViewModel, routeAction)
                    //Divider(color = Color(0xFFCFCECE))
                }
            }
        }

    }
}
@Composable
fun FreeBoardListItem(board: BoardList.BoardListItem,viewModel: FreeBoardViewModel, routeAction: RouteAction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                viewModel.imageNum=board.imgUrl
                viewModel.boardNum=board.brdId
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
