package com.e.myapplication

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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.e.myapplication.board.ShowBoardActivity
import com.e.myapplication.dataclass.Novel
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.e.myapplication.ui.theme.gray

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ShowNovelList(SampleData.novelList)
                }
            }
        }
    }
}



@Composable
fun ShowNovelList(novels : List<Novel>){
    Row(modifier = Modifier.fillMaxHeight()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TopMenu()
            Image(painter = rememberImagePainter(""), contentDescription = "", modifier = Modifier
                .fillMaxWidth()
                .height(180.dp))
            Spacer(modifier = Modifier.height(8.0.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("실시간 랭킹", fontSize = 32.sp, modifier = Modifier.padding(4.0.dp))
                    Text("좋아요 순", fontSize = 18.sp, modifier = Modifier.padding(4.0.dp))
                }
                Text(text = "더보기 ", fontSize = 14.sp, modifier = Modifier
                    .clickable(onClick = {})
                    .padding(4.0.dp))

            }
            LazyColumn {
                items(novels) { novel ->
                    Spacer(modifier = Modifier.padding(8.dp))
                    Greeting3(novel)
                }
            }
        }
    }

}
@Composable
fun TopMenu(){
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
        .fillMaxWidth()
        .background(gray)) {
        IconButton(onClick = {}) {
            Icon(
                Icons.Default.Menu,
                contentDescription = null
            )
        }
        Row {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null
                )
            }
        }
    }
}
@Composable
fun Greeting3(novel : Novel) {
    val context = LocalContext.current
    Row (verticalAlignment = CenterVertically, modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = {
            val intent = Intent(context, ShowBoardActivity::class.java)
            intent.putExtra("novelNum",novel.n)
            context.startActivity(intent)
        })){
        Text(novel.rank.toString(), modifier = Modifier.padding(16.dp), fontSize = 24.sp)
        Image(
            painter = painterResource(R.drawable.schumi), contentDescription = "schumi",
            modifier = Modifier
                .size(60.dp)
                .clip(RectangleShape)
                .border(1.5.dp, MaterialTheme.colors.secondary, RectangleShape),

        )
        Spacer(modifier = Modifier.width(16.0.dp))
        Column {
            Text(novel.title)
            Text(novel.writer)
            Spacer(modifier = Modifier.height(4.0.dp))
            Text("장르 : "+novel.genre)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview3() {
    MyApplicationTheme {
        ShowNovelList(SampleData.novelList)
    }
}