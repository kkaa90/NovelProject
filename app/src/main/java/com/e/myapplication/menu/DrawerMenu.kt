package com.e.myapplication.menu

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.e.myapplication.*
import com.e.myapplication.R
import com.e.myapplication.dataclass.ChkLogin
import com.e.myapplication.dataclass.User
import com.e.myapplication.user.LoginRepository
import com.e.myapplication.user.ProtoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.*


var point: Int = 0

@Composable
fun Drawer(routeAction: RouteAction) {
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

    fun loginSave(chkLogin: ChkLogin?) {
        runBlocking(Dispatchers.IO) {
            if (chkLogin != null) {
                repository2.writeLoginInfo(chkLogin)
            }
        }
        return
    }

    var userId by remember { mutableStateOf("") }
    var userNick by remember { mutableStateOf("") }
    var memIcon by remember { mutableStateOf("1") }
    var p by remember { mutableStateOf(point) }
    var count = remember {
        mutableStateOf(0)
    }

    fun read() {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()
        }
        userId = accountInfo.memUserid
        userNick = accountInfo.memNick
        memIcon = accountInfo.memIcon
        p = point
        count.value++
    }


    val timer = Timer()
    timer.schedule(object : TimerTask() {
        override fun run() {
            read()
        }
    }, 1000, 1000)


    Column {
        if (lCheck) {
            if(memIcon=="1"){
                Image(
                    painter = painterResource(id = R.drawable.schumi), contentDescription = "",
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
            else {
                Image(
                    painter = rememberImagePainter(memIcon), contentDescription = "",
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }

        } else {
            Column(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clickable(onClick = {
                        routeAction.navTo(NAVROUTE.LOGIN)
                    }),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("로그인이 필요합니다.", fontSize = 30.sp)
            }
        }


        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )
        Text(text = if (lCheck) userId else "")
        Text(text = if (lCheck) userNick else "")
        Text(text = if (lCheck) p.toString() else "")
        Row() {
            if (lCheck) {
                OutlinedButton(onClick = {
                    routeAction.navTo(NAVROUTE.PROFILE)
                }) {
                    Text(text = "회원정보")
                }
                Spacer(Modifier.width(4.dp))
                OutlinedButton(onClick = {
                    lCheck = false
                    accountSave(User("", "", "", "", "", "", "", "", ""))
                    loginSave(ChkLogin(chkIdSave = false, chkAutoLogin = false, id = "", pwd = ""))
                    point = 0
                }) {
                    Text(text = "로그아웃")
                }
            } else {
                OutlinedButton(onClick = {
                }, enabled = false, border = BorderStroke(0.dp, Color.White)) {

                }
            }

        }
        Column(Modifier.clickable { routeAction.navTo(NAVROUTE.MAIN) }) {
            Text(text = NAVROUTE.MAIN.description)
            Spacer(modifier = Modifier.height(20.dp))
        }
        Column(Modifier.clickable { routeAction.navTo(NAVROUTE.BOARD) }) {
            Text(text = NAVROUTE.BOARD.description)
            Spacer(modifier = Modifier.height(20.dp))
        }
        Column(Modifier.clickable { routeAction.navTo(NAVROUTE.NOVELCOVERLIST) }) {
            Text(text = NAVROUTE.NOVELCOVERLIST.description)
            Spacer(modifier = Modifier.height(20.dp))
        }


        Text(text = count.value.toString(), color = Color.White)

    }
}
