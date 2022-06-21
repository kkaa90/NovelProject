package com.e.myapplication.menu

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


var point: Int = 0

@Composable
fun Drawer(routeAction: RouteAction, scaffoldState: ScaffoldState) {
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

    val scope = rememberCoroutineScope()
    var userId by remember { mutableStateOf("") }
    var userNick by remember { mutableStateOf("") }
    var memIcon by remember { mutableStateOf("1") }
    var p by remember { mutableStateOf(point) }
    fun read() {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()
        }
        userId = accountInfo.memUserid
        userNick = accountInfo.memNick
        memIcon = accountInfo.memIcon
        p = point
    }

    fun readLoginInfo() : LoginInfo{
        var loginInfo : LoginInfo
        runBlocking(Dispatchers.IO) {
            loginInfo = repository2.readLoginInfo()
        }
        return loginInfo
    }


    LaunchedEffect(scaffoldState.drawerState.isOpen) {
        read()
    }

    Column(modifier = Modifier.fillMaxWidth()) {

        if (lCheck) {
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically){
                if (memIcon == "1") {
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_person_24), contentDescription = "",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = rememberImagePainter(memIcon), contentDescription = "",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                }
                Text(text = " $userNick")
            }


        } else {
            Column(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clickable(onClick = {
                        routeAction.navTo(NAVROUTE.LOGIN)
                        scope.launch {
                            scaffoldState.drawerState.apply {
                                close()
                            }
                        }
                    }),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text("로그인", fontSize = 30.sp)
            }
        }
        Text(text = if (lCheck) "포인트 : $p" else "", modifier = Modifier.padding(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            if (lCheck) {
                OutlinedButton(onClick = {
                    routeAction.navTo(NAVROUTE.PROFILE)
                }) {
                    Text(text = "회원정보")
                }
                Spacer(Modifier.width(16.dp))
                OutlinedButton(onClick = {
                    lCheck = false
                    val l =readLoginInfo()
                    accountSave(User("", "", "", "", "", "", "", "", ""))
                    loginSave(ChkLogin(chkIdSave = l.chkIdSave, chkAutoLogin = false, id = l.id, pwd = ""))
                    point = 0
                    read()
                    routeAction.clearBack()
                }) {
                    Text(text = "로그아웃")
                }
            } else {
                OutlinedButton(onClick = {
                }, enabled = false, border = BorderStroke(0.dp, Color.White)) {

                }
            }

        }
        Column(
            Modifier
                .clickable { routeAction.navTo(NAVROUTE.MAIN) }
                .fillMaxWidth()) {
            Text(text = NAVROUTE.MAIN.description, modifier = Modifier.padding(8.dp))

        }
        Column(
            Modifier
                .clickable { routeAction.navTo(NAVROUTE.BOARD) }
                .fillMaxWidth()) {
            Text(text = NAVROUTE.BOARD.description, modifier = Modifier.padding(8.dp))

        }
        Column(
            Modifier
                .clickable { routeAction.navTo(NAVROUTE.NOVELCOVERLIST) }
                .fillMaxWidth()) {
            Text(text = NAVROUTE.NOVELCOVERLIST.description, modifier = Modifier.padding(8.dp))
        }
        Column(Modifier
            .clickable { routeAction.navTo(NAVROUTE.MESSAGE) }
            .fillMaxWidth()) {
            Text(text = NAVROUTE.MESSAGE.description, modifier = Modifier.padding(8.dp))

        }
    }
}
