package com.e.myapplication.novel

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewModelScope
import com.e.myapplication.NovelCoverListItem
import com.e.myapplication.NAVROUTE
import com.e.myapplication.RouteAction
import com.e.myapplication.TopMenu
import com.e.myapplication.dataclass.Novels
import com.e.myapplication.menu.Drawer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.concurrent.timer

@Composable
fun NovelCovers(
    routeAction: RouteAction, viewModel: NovelViewModel
) {
    fun LazyListState.isScrolledToTheEnd() =
        layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
    val novels = remember {
        mutableStateListOf<Novels.Content>()
    }
    val tags = remember {
        mutableStateListOf<List<String>>()
    }
    val listState = rememberLazyListState()
    LaunchedEffect(true){
        viewModel.progress = true
        viewModel.p=1
        viewModel.updateNovels()
        timer(period = 100){
            if(!viewModel.progress){
                viewModel.viewModelScope.launch {
                    viewModel.n.collect{
                        novels.clear()
                        novels.addAll(it)
                    }
                }
                viewModel.viewModelScope.launch {
                    viewModel.t.collect{
                        tags.clear()
                        tags.addAll(it)
                    }
                }
                cancel()
            }
        }
    }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    var visibility by remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = { TopMenu(scaffoldState, scope, routeAction) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                routeAction.navTo(NAVROUTE.WRITINGNOVELCOVER)
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
        BackHandler {
            if (scaffoldState.drawerState.isClosed) routeAction.goBack()
            else {
                scope.launch {
                    scaffoldState.drawerState.apply {
                        close()
                    }
                }
            }
        }
        if (viewModel.progress) {
            Dialog(
                onDismissRequest = { viewModel.progress = false },
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
        Row(modifier = Modifier.fillMaxHeight()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("실시간 랭킹", fontSize = 32.sp, modifier = Modifier.padding(4.0.dp))
                        Column {
                            Text(viewModel.sNow.present, fontSize = 18.sp, modifier = Modifier
                                .padding(4.0.dp)
                                .clickable { visibility = !visibility })
                            DropdownMenu(expanded = visibility, onDismissRequest = { visibility=false }) {
                                viewModel.sList.forEach { list ->
                                    DropdownMenuItem(onClick = {
                                        viewModel.sNow = list
                                        viewModel.updateNovels()
                                        visibility=false
                                    }) {
                                        Text(text = list.present)
                                    }
                                }
                            }
                        }
                        IconButton(onClick = {
                            if(viewModel.asc=="ASC") {
                                viewModel.asc = "DESC"
                            }
                            else {
                                viewModel.asc="ASC"
                            }
                        }) {
                            Icon(if(viewModel.asc=="DESC")Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,"")
                        }
                    }
                }
                LazyColumn(state = listState) {
                    itemsIndexed(novels) { index, novel ->
                        Spacer(modifier = Modifier.padding(8.dp))
                        NovelCoverListItem(novel, tags[index], routeAction)
                        if(listState.isScrolledToTheEnd()&&viewModel.pageNum>viewModel.p){
                            viewModel.progress = true
                            viewModel.p++
                            viewModel.updateNovels()
                            timer(period = 100){
                                if(!viewModel.progress){
                                    viewModel.viewModelScope.launch {
                                        viewModel.n.collect{
                                            novels.clear()
                                            novels.addAll(it)
                                        }
                                    }
                                    viewModel.viewModelScope.launch {
                                        viewModel.t.collect{
                                            tags.clear()
                                            tags.addAll(it)
                                        }
                                    }
                                    cancel()
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
