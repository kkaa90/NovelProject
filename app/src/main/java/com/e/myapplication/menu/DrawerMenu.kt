package com.e.myapplication.menu

import android.app.Activity
import android.content.Intent
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import com.e.myapplication.board.TestActivity2
import com.e.myapplication.dataclass.ChkLogin
import com.e.myapplication.dataclass.User
import com.e.myapplication.lCheck
import com.e.myapplication.novel.NovelCoverActivity
import com.e.myapplication.user.ChangeProfileActivity
import com.e.myapplication.user.LoginActivity
import com.e.myapplication.user.LoginRepository
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
    val repository2 = LoginRepository(context)
    fun accountSave(user: User?) {
        runBlocking(Dispatchers.IO) {
            if (user != null) {
                repository.writeAccountInfo(user)
            }
        }
        return
    }
    fun loginSave(chkLogin: ChkLogin?){
        runBlocking(Dispatchers.IO){
            if(chkLogin!=null){
                repository2.writeLoginInfo(chkLogin)
            }
        }
        return
    }
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
        Row(){
            OutlinedButton(onClick = {
                val intent = Intent(context, ChangeProfileActivity::class.java)
                context.startActivity(intent)
            }) {
                Text(text = "회원정보")
            }
            Spacer(Modifier.width(4.dp))
            OutlinedButton(onClick = {
                lCheck = false
                accountSave(User("","","","","","","",""))
                loginSave(ChkLogin(chkIdSave = false, chkAutoLogin = false, id = "", pwd = ""))
                point = 0
            }) {
                Text(text = "로그아웃")
            }
        }
        drawers.forEach { drawer ->
            Column(modifier = Modifier.clickable {
                val intent = Intent(context, drawer.activity::class.java)
                context.startActivity(intent)
            }) {
                Text(text = drawer.title)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
//        Button(onClick = {
//            val intent = Intent(context,TestActivity2::class.java)
//            context.startActivity(intent)
//        }) {
//            Text(text = "Test")
//        }

    }
}

@Preview
@Composable
fun PreviewDrawer() {
    Drawer()
}
