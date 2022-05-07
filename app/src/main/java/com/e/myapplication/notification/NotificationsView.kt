package com.e.myapplication.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.e.myapplication.RouteAction
import com.e.myapplication.notifyDB
import com.e.myapplication.ui.theme.gray

@Composable
fun NotificationsView(routeAction: RouteAction) {
    val list = remember { mutableStateListOf<Notify>()}
    LaunchedEffect(true){
        val r = Runnable {
            val db = notifyDB.dao().getAll()
            list.addAll(db)
        }
        Thread(r).start()
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
            .fillMaxWidth()
            .background(gray)
        , verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { routeAction.goBack() }) {
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