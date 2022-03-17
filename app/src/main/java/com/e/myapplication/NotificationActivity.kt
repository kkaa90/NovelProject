package com.e.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.myapplication.dataclass.Notification
import com.e.myapplication.ui.theme.MyApplicationTheme

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val notification : MutableList<Notification> = arrayListOf()
        notification.add(Notification("테스트","1234"))
        notification.add(Notification("테스트2","테스트"))
        
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
fun Greeting10(notifications : MutableList<Notification>) {
    LazyColumn {
        items(notifications) {n->
            ShowNotification(notification = n)
        }
    }
}

@Composable
fun ShowNotification(notification: Notification){
    Column(modifier = Modifier.border(width = 1.dp, color = Color.Blue, shape = RectangleShape).fillMaxWidth()) {
        Text(text = notification.title)
        Text(text = notification.body)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview12() {
    val notification : MutableList<Notification> = arrayListOf()
    notification.add(Notification("테스트","1234"))
    notification.add(Notification("테스트2","테스트"))
    MyApplicationTheme {
        Greeting10(notification)
    }
}