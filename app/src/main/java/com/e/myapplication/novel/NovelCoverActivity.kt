package com.e.myapplication.novel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.e.myapplication.Greeting3
import com.e.myapplication.TopMenu
import com.e.myapplication.dataclass.Novels
import com.e.myapplication.menu.Drawer
import com.e.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class NovelCoverActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ncvm = ViewModelProvider(this).get(NovelCoverViewModel::class.java)
        ncvm.updateNovels()
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                val novels = ncvm.n.collectAsState()
                val tags = ncvm.t.collectAsState()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting8(novels, tags)
                }
            }
        }
    }
}

@Composable
fun Greeting8(
    novels: State<List<Novels.Content>>,
    tags: State<List<List<String>>>
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = { TopMenu(scaffoldState, scope) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val intent = Intent(context, WriteNCoverActivity::class.java)
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
        BackHandler {
            if (scaffoldState.drawerState.isClosed) (context as Activity).finish()
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
                        Greeting3(novel, tags.value[index])
                    }

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview10() {
    MyApplicationTheme {

    }
}
