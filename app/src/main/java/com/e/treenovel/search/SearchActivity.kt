package com.e.treenovel.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.e.treenovel.R
import com.e.treenovel.NAVROUTE
import com.e.treenovel.RouteAction
import com.e.treenovel.board.FreeBoardListItem
import com.e.treenovel.board.FreeBoardViewModel
import com.e.treenovel.dataclass.BoardList
import com.e.treenovel.dataclass.Novels
import com.e.treenovel.retrofit.RetrofitClass
import retrofit2.Call
import retrofit2.Response


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchView(routeAction: RouteAction, viewModel: FreeBoardViewModel) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val sMenu: List<String> = listOf("제목", "글쓴이", "전체")
    var sMenuExpanded by remember { mutableStateOf(false) }
    var sMenuName: String by rememberSaveable { mutableStateOf(sMenu[0]) }
    var sKeyword by rememberSaveable { mutableStateOf("") }
    var tabIndex by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf("소설", "게시판")
    val nResult = remember { mutableStateListOf<Novels.Content>() }
    val bResult = remember { mutableStateListOf<BoardList.BoardListItem>() }
    LaunchedEffect(true){
        if(sKeyword!="") {
            getNSearch(sKeyword, nResult)
            val t = if (sMenuName == "제목") "title" else if (sMenuName == "글쓴이") "" else ""
            getBSearch(t, sKeyword, bResult)
        }
    }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { routeAction.goBack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
            Text(text = "검색")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(Modifier.clickable { sMenuExpanded = !sMenuExpanded }) {
                    Text(sMenuName)
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
                }
                DropdownMenu(
                    expanded = sMenuExpanded,
                    onDismissRequest = { sMenuExpanded = false }) {
                    sMenu.forEach { sMenuItem ->
                        DropdownMenuItem(onClick = {
                            sMenuExpanded = false; sMenuName = sMenuItem
                        }) {
                            Text(sMenuItem)
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = sKeyword, onValueChange = { sKeyword = it },
                    singleLine = true, modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                OutlinedButton(onClick = {
                    getNSearch(sKeyword, nResult)
                    val t = if (sMenuName == "제목") "title" else if (sMenuName == "글쓴이") "" else ""
                    getBSearch(t, sKeyword, bResult)
                    keyboardController?.hide()
                }) {
                    Text(text = "검색")
                }
            }
        }

        TabRow(
            selectedTabIndex = tabIndex,
            modifier = Modifier.height(36.dp),
            contentColor = Color.Black
        ) {
            tabs.forEachIndexed { index, s ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = {
                        Text(
                            text = s,
                            color = if (tabIndex == index) Color.White else Color.Black
                        )
                    },
                    modifier = Modifier.background(if (tabIndex == index) Color.Black else Color.White)
                )
            }
        }
        when (tabIndex) {
            0 -> {
                if (nResult.size != 0) {
                    LazyColumn {
                        items(nResult) { novel ->
                            SNResult(novel = novel, routeAction)
                        }
                    }
                } else {
                    Text("검색 결과가 없습니다.")
                }
            }
            1 -> {
                if (bResult.size != 0) {
                    LazyColumn {
                        items(bResult) { board ->
                            FreeBoardListItem(board = board, viewModel ,routeAction)
                        }
                    }
                } else {
                    Text("검색 결과가 없습니다.")
                }
            }
        }
    }
}

@Composable
fun SNResult(novel: Novels.Content, routeAction: RouteAction) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                routeAction.navWithNum(NAVROUTE.NOVELDETAILSLIST.routeName + "/${novel.nvcId}")
            })
    ) {
        Text("-", modifier = Modifier.padding(16.dp), fontSize = 24.sp)
        if (novel.imgUrl == "1" || novel.imgUrl == "23") {
            Image(
                painter = painterResource(R.drawable.schumi), contentDescription = "schumi",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RectangleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondary, RectangleShape),

                )
        } else {
            Image(
                painter = rememberImagePainter(novel.imgUrl), contentDescription = "schumi",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RectangleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondary, RectangleShape),

                )
        }
        Spacer(modifier = Modifier.width(16.0.dp))
        Column {
            Text(novel.nvcTitle)
            Text(novel.nvcId.toString())
            Spacer(modifier = Modifier.height(4.0.dp))
            Text(" ")
        }
    }
}


fun getNSearch(keyword: String, list: SnapshotStateList<Novels.Content>) {
    list.removeAll(list)
    val retrofitClass = RetrofitClass.api.searchNovel(keyword)
    retrofitClass.enqueue(object : retrofit2.Callback<Novels> {
        override fun onResponse(call: Call<Novels>, response: Response<Novels>) {
            val r = response.body()!!.content
            list.addAll(r)
        }

        override fun onFailure(call: Call<Novels>, t: Throwable) {
            t.printStackTrace()
        }
    })
}

fun getBSearch(srcType: String, keyword: String, list: SnapshotStateList<BoardList.BoardListItem>) {
    list.removeAll(list)
    val retrofitClass = RetrofitClass.api.searchBoard(srcType, keyword)
    retrofitClass.enqueue(object : retrofit2.Callback<BoardList> {
        override fun onResponse(call: Call<BoardList>, response: Response<BoardList>) {
            val r = response.body()!!.boards
            list.addAll(r)
        }

        override fun onFailure(call: Call<BoardList>, t: Throwable) {
            t.printStackTrace()
        }


    })
}
