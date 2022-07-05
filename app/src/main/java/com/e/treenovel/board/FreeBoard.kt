package com.e.treenovel.board

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.e.treenovel.*
import com.e.treenovel.R
import com.e.treenovel.dataclass.BoardList
import com.e.treenovel.menu.Drawer
import com.e.treenovel.ui.theme.gray


@Composable
fun ShowFreeBoardList(boardViewModel: FreeBoardViewModel, routeAction: RouteAction) {
    fun LazyListState.isScrolledToTheEnd() =
        layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

//    val boards = remember {
//        mutableStateListOf<BoardList.BoardListItem>()
//    }
    val boards = boardViewModel.boards.collectAsState()
    LaunchedEffect(true) {
        if (boards.value.isEmpty()) {
            boardViewModel.progress = true
            boardViewModel.p = 1
            boardViewModel.updateBoardList()
//            timer(period = 100) {
//                if (!boardViewModel.progress) {
//                    boardViewModel.viewModelScope.launch {
//                        boardViewModel.boards.collect {
//                            boards.clear()
//                            boards.addAll(it)
//                        }
//                    }
//                    cancel()
//                }
//            }
        }
    }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val sMenu: List<String> = listOf("제목", "글쓴이", "전체")
    var sMenuExpanded by remember { mutableStateOf(false) }
    var sMenuName: String by rememberSaveable { mutableStateOf(sMenu[0]) }
    var sKeyword by rememberSaveable { mutableStateOf("") }
    var cancelVisibility by remember { mutableStateOf(false)}
    Scaffold(
        topBar = { TopMenu(scaffoldState, scope, routeAction) },
//        floatingActionButton = {
//            FloatingActionButton(onClick = {
//                routeAction.navTo(NAVROUTE.WRITINGBOARD)
//            }) {
//                Icon(Icons.Filled.Add, contentDescription = "")
//            }
//        },
        drawerContent = {
            Drawer(routeAction, scaffoldState)
        },
        drawerGesturesEnabled = true,
        scaffoldState = scaffoldState
    ) {
        if (boardViewModel.progress) {
            Dialog(
                onDismissRequest = { boardViewModel.progress = false },
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        Column(modifier = Modifier.fillMaxSize()) {
            //Divider(color = Color(0xFFCFCECE))
            LazyColumn(state = listState) {
                item {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .border(width = 1.dp, shape = RectangleShape, color = gray),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(Modifier.clickable { sMenuExpanded = !sMenuExpanded }) {
                                    Text(text = sMenuName)
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = sMenuExpanded,
                                onDismissRequest = { sMenuExpanded = false }) {
                                sMenu.forEach { sMenuItem ->
                                    DropdownMenuItem(onClick = {
                                        sMenuExpanded = false; sMenuName = sMenuItem
                                    }) {
                                        Text(text = sMenuItem)
                                    }
                                }
                            }
                            TextField(
                                value = sKeyword,
                                onValueChange = { sKeyword = it },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp),
                                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                            )
                            if(cancelVisibility) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable {
                                            boardViewModel.progress = true
                                            sKeyword = ""
                                            boardViewModel.p = 1
                                            boardViewModel.updateBoardList()
                                            cancelVisibility=false
                                        })
                            }
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clickable {
                                        boardViewModel.progress = true
                                        boardViewModel.getBSearch(sMenuName, sKeyword)
                                        cancelVisibility = true
                                    })
                        }
                        OutlinedButton(onClick = {
                            if(lCheck){
                            routeAction.navTo(NAVROUTE.WRITINGBOARD)}
                            else {
                                routeAction.navTo(NAVROUTE.LOGIN)
                            }
                        }) {
                            Text("글쓰기")
                        }
                    }
                }
                if (boards.value.isNotEmpty()) {
                    items(boards.value) { board ->
                        FreeBoardListItem(board, boardViewModel, routeAction)
                        if (listState.isScrolledToTheEnd() && boardViewModel.pageNum > boardViewModel.p) {
                            boardViewModel.progress = true
                            boardViewModel.p++
                            boardViewModel.updateBoardList()
//                        timer(period = 100) {
//                            if (!boardViewModel.progress) {
//                                boardViewModel.viewModelScope.launch {
//                                    boardViewModel.boards.collect {
//                                        boards.clear()
//                                        boards.addAll(it)
//                                    }
//                                }
//                                cancel()
//                            }
//                        }
                        }
                    }
                }
                else {
                    item { 
                        Text(text = "검색 결과가 없습니다.",modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
                    }
                }
            }
        }

    }
}

@Composable
fun FreeBoardListItem(
    board: BoardList.BoardListItem,
    viewModel: FreeBoardViewModel,
    routeAction: RouteAction
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                viewModel.imageNum = board.imgUrl
                viewModel.boardNum = board.brdId
                routeAction.navWithNum("boardDetail/${board.brdId}")

            })
            .border(1.dp, shape = RectangleShape, color = Color.Gray)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = board.brdTitle,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(2.dp)
                )
                Text(
                    text = board.memNickname,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(2.dp)
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row() {
                        Text(
                            text = board.brdDatetime.split(".")[0],
                            fontSize = 14.sp,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                    Row() {
                        Text(
                            text = "조회 ${board.brdHit}",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(2.dp)
                        )
                        Text(
                            text = "추천 ${board.brdLike}",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(2.dp)
                        )
                        Text(
                            text = "댓글 ${board.brdCommentCount}",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }
            }
            if (board.brdImg == 1) {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_image_24),
                    contentDescription = "",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp),
                    alignment = Alignment.Center
                )
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
