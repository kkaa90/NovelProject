package com.e.treenovel.notification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import androidx.compose.ui.unit.dp
import com.e.treenovel.NAVROUTE
import com.e.treenovel.RouteAction
import com.e.treenovel.notifyDB
import com.e.treenovel.ui.theme.gray

@Composable
fun NotificationsView(routeAction: RouteAction) {
    val list = remember { mutableStateListOf<Notify>() }
    LaunchedEffect(true) {
        val r = Runnable {
            val db = notifyDB.dao().getAll()
            list.addAll(db)
        }
        Thread(r).start()
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                .fillMaxWidth()
                .background(gray), verticalAlignment = Alignment.CenterVertically
        ) {
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
        LazyColumn(contentPadding = PaddingValues(vertical = 1.dp)) {
            items(items = list) { n ->
                ShowNotification(notification = n, routeAction)

            }
        }
    }

}

@Composable
fun ShowNotification(notification: Notify, routeAction: RouteAction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { routeAction.navWithNum(NAVROUTE.NOVELDETAILSLIST.routeName + "/${notification.titleId}") },
        shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, Color.Black)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = notification.title.toString())
            Text(text = notification.body.toString())
        }
    }

}
