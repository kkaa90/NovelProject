package com.e.myapplication.notification

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.myapplication.dataclass.Notification
import com.e.myapplication.notifyDB
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.e.myapplication.ui.theme.gray

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val notification : MutableList<Notify> = arrayListOf()
        var db = listOf<Notify>()
        val r = Runnable {
            db = notifyDB.dao().getAll()
            notification.addAll(db)
        }
        Thread(r).start()
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting10(notification)
                }
            }
        }
    }
}

@Composable
fun Greeting10(notifications : MutableList<Notify>) {
    val context = LocalContext.current
    var list = remember { mutableStateListOf<Notify>()}
    list.addAll(notifications)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().background(gray)
        , verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { (context as Activity).finish() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
            Text(text = "알림 리스트")
            IconButton(onClick = {
                val r = Runnable { 
                    notifyDB.dao().deleteAll()
                    list.removeAll(list)
                }
                Thread(r).start()
            }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(list) {n->
                ShowNotification(notification = n)
            }
        }
    }
    
}

@Composable
fun ShowNotification(notification: Notify){
    Column(modifier = Modifier
        .border(width = 1.dp, color = Color.Blue, shape = RectangleShape)
        .fillMaxWidth()) {
        Text(text = notification.title.toString())
        Text(text = notification.body.toString())
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview12() {
    val notification : MutableList<Notify> = arrayListOf()
    notification.add(Notify("테스트","1234"))
    notification.add(Notify("테스트2","테스트"))
    MyApplicationTheme {
        Greeting10(notification)
    }
}