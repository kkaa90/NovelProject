package com.e.myapplication.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.e.myapplication.R
import com.e.myapplication.board.Greeting6
import com.e.myapplication.dataclass.Board
import com.e.myapplication.dataclass.Boards
import com.e.myapplication.dataclass.Novels
import com.e.myapplication.novel.ShowNovelListActivity
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import retrofit2.Call
import retrofit2.Response

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting13("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting13(name: String) {
    val context = LocalContext.current
    val sMenu: List<String> = listOf("제목", "글쓴이", "본문")
    var sMenuExpanded by remember { mutableStateOf(false) }
    var sMenuName: String by remember { mutableStateOf(sMenu[0]) }
    var sKeyword by remember { mutableStateOf("") }
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("소설", "게시판")
    var nResult = remember { mutableStateListOf<Novels.Content>() }
    var bResult = remember { mutableStateListOf<Board>() }
    Column() {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { (context as Activity).finish() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
            Text(text = "검색")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column() {
                Row(Modifier.clickable { sMenuExpanded = !sMenuExpanded }) {
                    Text(sMenuName)
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
                    Spacer(modifier = Modifier.width(4.dp))

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
            Row() {
                OutlinedTextField(
                    value = sKeyword, onValueChange = { sKeyword = it },
                    Modifier.width(220.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                OutlinedButton(onClick = {
                    getNSearch(sKeyword, nResult)
                    val t = if (sMenuName=="제목") "title" else if(sMenuName=="글쓴이") "" else ""
                    getBSearch(t,sKeyword,bResult)
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
                if(nResult.size!=0){
                    LazyColumn {
                        items(nResult){ novel ->
                            SNResult(novel = novel)
                        }
                    }
                }
                else {
                    Text("검색 결과가 없습니다.")
                }
            }
            1 -> {
                if(bResult.size!=0){
                    LazyColumn {
                        items(bResult){ board ->
                            Greeting6(board = board)
                        }
                    }
                }
                else {
                    Text("검색 결과가 없습니다.")
                }
            }
        }
    }
}

@Composable
fun SNResult(novel: Novels.Content){
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                val intent = Intent(context, ShowNovelListActivity::class.java)
                intent.putExtra("novelNum", novel.nvcid)
                context.startActivity(intent)
            })
    ) {
        Text("-", modifier = Modifier.padding(16.dp), fontSize = 24.sp)
        if(novel.imgUrl=="1"||novel.imgUrl=="23"){
            Image(
                painter = painterResource(R.drawable.schumi), contentDescription = "schumi",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RectangleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondary, RectangleShape),

                )
        }
        else {
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
            Text(novel.nvcid.toString())
            Spacer(modifier = Modifier.height(4.0.dp))
            Text(" ")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview15() {
    MyApplicationTheme {
        Greeting13("Android")
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

fun getBSearch(srcType: String, keyword: String, list: SnapshotStateList<Board>) {
    list.removeAll(list)
    val retrofitClass = RetrofitClass.api.searchBoard(srcType, keyword)
    retrofitClass.enqueue(object : retrofit2.Callback<Boards> {
        override fun onResponse(call: Call<Boards>, response: Response<Boards>) {
            val r = response.body()!!.boards
            list.addAll(r)
        }

        override fun onFailure(call: Call<Boards>, t: Throwable) {
            t.printStackTrace()
        }


    })
}
