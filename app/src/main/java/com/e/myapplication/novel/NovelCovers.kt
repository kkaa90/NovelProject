package com.e.myapplication.novel

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.e.myapplication.NovelCoverListItem
import com.e.myapplication.NAVROUTE
import com.e.myapplication.RouteAction
import com.e.myapplication.TopMenu
import com.e.myapplication.menu.Drawer
import kotlinx.coroutines.launch

@Composable
fun NovelCovers(
    routeAction: RouteAction, viewModel: NovelViewModel
) {
    viewModel.updateNovels()
    val novels = viewModel.n.collectAsState()
    val tags = viewModel.t.collectAsState()
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
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
        Row(modifier = Modifier.fillMaxHeight()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("실시간 랭킹", fontSize = 32.sp, modifier = Modifier.padding(4.0.dp))
                        Text("좋아요 순", fontSize = 18.sp, modifier = Modifier.padding(4.0.dp))
                    }
                    Text(
                        text = "더보기 ", fontSize = 14.sp, modifier = Modifier
                            .clickable(onClick = {})
                            .padding(4.0.dp)
                    )

                }
                LazyColumn {
                    itemsIndexed(novels.value) { index, novel ->
                        Spacer(modifier = Modifier.padding(8.dp))
                        NovelCoverListItem(novel, tags.value[index], routeAction)
                    }
                }
            }
        }
    }
}
