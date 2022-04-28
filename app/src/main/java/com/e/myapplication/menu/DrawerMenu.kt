package com.e.myapplication.menu

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.myapplication.AccountInfo
import com.e.myapplication.MainActivity
import com.e.myapplication.R
import com.e.myapplication.board.FreeBoardActivity
import com.e.myapplication.novel.NovelCoverActivity
import com.e.myapplication.user.ChangeProfileActivity
import com.e.myapplication.user.LoginActivity
import com.e.myapplication.user.ProtoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.*


class DrawerMenu(val route: String, val title: String, val activity: Activity)
var point : Int = 0

@Composable
fun Drawer() {
    val context = LocalContext.current
    val repository = ProtoRepository(context = context)
    var userId by remember { mutableStateOf("") }
    var userNick by remember { mutableStateOf("") }
    var p by remember { mutableStateOf(point) }

    fun read() {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()
        }
        userId = accountInfo.memUserid
        userNick = accountInfo.memNick
        p = point
    }


    val timer = Timer()
    timer.schedule(object : TimerTask() {
        override fun run() {
            read()
        }
    }, 1000, 1000)
    val drawers = listOf(
        DrawerMenu("home", "Home", MainActivity()),
        DrawerMenu("account", "Account", ChangeProfileActivity()),
        DrawerMenu("board", "Board", FreeBoardActivity()),
        DrawerMenu("novel", "Novel", NovelCoverActivity())
    )


    Column {
        Image(painter = painterResource(id = R.drawable.schumi), contentDescription = "",
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .padding(10.dp)
                .clickable(onClick = {
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                })
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )
        Text(text = userId)
        Text(text = userNick)
        Text(text = p.toString())
        drawers.forEach { drawer ->
            Column(modifier = Modifier.clickable {
                val intent = Intent(context, drawer.activity::class.java)
                context.startActivity(intent)
            }) {
                Text(text = drawer.title)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }


    }
}

@Preview
@Composable
fun PreviewDrawer() {
    Drawer()
}
