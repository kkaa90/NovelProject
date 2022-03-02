package com.e.myapplication.menu

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.e.myapplication.user.Login
import com.e.myapplication.user.LoginActivity
import com.e.myapplication.user.ProtoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


class DrawerMenu(val route : String, val title: String, val activity: Activity)

@Composable
fun Drawer(){
    val context = LocalContext.current
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }
    val ac = read()
    val drawers = listOf(
        DrawerMenu("home","Home", MainActivity()),
        DrawerMenu("account","Account", LoginActivity()),
        DrawerMenu("board","Board", FreeBoardActivity()),
        DrawerMenu("novel","Novel", NovelCoverActivity())
    )
    Column() {
        Image(painter = painterResource(id = R.drawable.schumi), contentDescription = "", 
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .padding(10.dp)
                .clickable(onClick = {
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                }))
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(5.dp))
        Text(text = ac.memUserid)
        Text(text = ac.memNick)
        drawers.forEach { drawer ->
            Column(modifier = Modifier.clickable {
                val intent = Intent(context,drawer.activity::class.java)
                context.startActivity(intent)
            }) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = drawer.title)
            }
        }


    }
}

@Preview
@Composable
fun previewDrawer(){
    Drawer()
}