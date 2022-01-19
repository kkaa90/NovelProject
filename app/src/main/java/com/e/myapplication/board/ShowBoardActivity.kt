package com.e.myapplication.board

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Space
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
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.e.myapplication.R
import com.e.myapplication.SampleData
import com.e.myapplication.TopMenu
import com.e.myapplication.dataclass.Novel
import com.e.myapplication.dataclass.Posting
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.e.myapplication.ui.theme.dimGray
import com.e.myapplication.ui.theme.gray
import com.e.myapplication.ui.theme.skyBlue


class ShowBoardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ShowPostList(SampleData.novelList[0], SampleData.postingList)
                }
            }
        }


    }
}

@Composable
fun Greeting(posting: Posting) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .clickable(onClick = {
                val intent = Intent(context, BoardActivity::class.java)
                context.startActivity(intent)
            })
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.padding(10.0.dp))
        Column {
            Text(
                text = posting.title,
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.subtitle2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "000화",
                style = MaterialTheme.typography.body2
            )


        }


    }

}

@Composable
fun ShowPostList(novel: Novel, postings: List<Posting>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TopMenu()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(skyBlue)
        ) {
            Row() {

                Image(
                    painter = rememberImagePainter("https://img.hankyung.com/photo/202012/PAF20201202254601055_P4.jpg"),
                    contentDescription = "schumi",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RectangleShape)
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(novel.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(novel.writer, color = dimGray, fontSize = 18.sp)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Row() {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_baseline_remove_red_eye_24),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(Color.Black)
                                )
                                Text("1000", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.padding(4.0.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_baseline_thumb_up_24),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(Color.Black)
                                )
                                Text("1000", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.padding(4.0.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_baseline_comment_24),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(Color.Black)
                                )
                                Text("1000", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.padding(4.0.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_baseline_notifications_24),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(Color.Black)
                                )
                                Text("1000", fontSize = 12.sp)
                            }
                        }
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = "")
                    }
                }

            }
            Row() {
                Spacer(modifier = Modifier.padding(8.dp))
                Column() {
                    Text(novel.description, fontSize = 21.sp)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text("장르 : " + novel.genre, color = dimGray, fontSize = 14.sp)
                    Text("태그 : #아무거나", color = dimGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.padding(4.dp))
                }
            }
        }
        var tabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("목록", "댓글")
        TabRow(
            selectedTabIndex = tabIndex,
            modifier = Modifier.height(36.dp),
            contentColor = Color.Black

        ) {
            tabs.forEachIndexed { index, text ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = {
                        Text(
                            text,
                            color = if (tabIndex == index) Color.White else Color.Black
                        )
                    },
                    modifier = Modifier.background(if (tabIndex == index) Color.Black else Color.White)
                )
            }
        }
        val dMenu = listOf("인기순", "작가1", "작가2")
        var dMenuExpanded by remember { mutableStateOf(false) }
        var dMenuName : String by remember { mutableStateOf(dMenu[0])}
        when (tabIndex) {
            0 -> {
                Column() {
                    Row(modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.padding(8.dp))
                            Button(onClick = { /*TODO*/ }) {
                                Text(text = "공지 숨기기")
                            }
                            Spacer(modifier = Modifier.padding(30.dp))
                            Column() {
                                Row(Modifier.clickable { dMenuExpanded = !dMenuExpanded }) {
                                    Text(dMenuName)
                                    Icon(imageVector = Icons.Filled.ArrowDropDown, "")
                                }
                                DropdownMenu(
                                    expanded = dMenuExpanded,
                                    onDismissRequest = { dMenuExpanded = false }) {
                                    dMenu.forEach { dMenuItem ->
                                        DropdownMenuItem(onClick = {
                                            dMenuExpanded = false; dMenuName = dMenuItem
                                        }) {
                                            Text(dMenuItem)
                                        }
                                    }

                                }
                            }
                        }

                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_sort_24),
                                contentDescription = ""
                            )
                        }
                    }
                    LazyColumn {
                        items(postings) { posting ->
                            Greeting(posting)
                        }
                    }
                }

            }
            1 -> Text("test")
        }

    }
}


@Preview(
    name = "밝은 테마",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun DefaultPreview() {
    Column(modifier = Modifier.fillMaxHeight()) {
        MyApplicationTheme {
            ShowPostList(SampleData.novelList[0], SampleData.postingList)
        }
    }

}