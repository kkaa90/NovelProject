package com.e.myapplication.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.e.myapplication.AccountInfo
import com.e.myapplication.R
import com.e.myapplication.dataclass.CallMethod
import com.e.myapplication.dataclass.ChangePwd
import com.e.myapplication.dataclass.ChkLogin
import com.e.myapplication.dataclass.User
import com.e.myapplication.lCheck
import com.e.myapplication.menu.point
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response

class ChangeProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting12()
                }
            }
        }
    }
}

@Composable
fun Greeting12() {
    val context = LocalContext.current
    val repository = ProtoRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }
    val ac = read()
    var pwdDialog = remember {
        mutableStateOf(false)
    }
    Box() {
        AnimatedVisibility(visible = pwdDialog.value) {
            ChangePasswordDialog(visibility = pwdDialog)
        }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { (context as Activity).finish() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("계정 정보 변경")
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()){
            Column {
                Text(text = "아이디")
                Text(text = ac.memUserid)
            }
            Row(modifier = Modifier
                .clickable { }
                .height(60.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = R.drawable.schumi), contentDescription = "", modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape))
                Text(text = "아이콘 변경")
            }
        }

        Column(modifier = Modifier.clickable {  }) {
            Text(text = "닉네임")
            Text(text = ac.memNick)
        }

        Text("비밀번호 변경",Modifier.clickable { pwdDialog.value = true })
    }
}
@Composable
fun ChangePasswordDialog(visibility: MutableState<Boolean>){
    val context = LocalContext.current
    var pwd by remember {
        mutableStateOf("")
    }
    Dialog(onDismissRequest = { visibility.value=false }) {
        Surface(modifier = Modifier
            .width(240.dp)
            .height(400.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White) {
            Column() {
                Text(text = "비밀번호 변경")
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(value = pwd, onValueChange = {pwd=it})
                Spacer(modifier = Modifier.height(20.dp))
                Row() {
                    OutlinedButton(onClick = { visibility.value = false }) {
                        Text(text = "취소")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedButton(onClick = { changePWD(context, pwd) }) {
                        Text(text = "확인")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview14() {
    MyApplicationTheme {
        Greeting12()
    }
}

fun changePWD(context : Context, pwd:String){
    val repository = ProtoRepository(context = context)
    val repository2 = LoginRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()
        }
        return accountInfo
    }
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
    val ac = read()
    val retrofitClass = RetrofitClass.api.changePassword(ac.authorization, ChangePwd(pwd))
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod>{
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            val r= response.body()!!.msg
            println(r)
            if(r=="OK"){
                lCheck = false
                accountSave(User("","","","","","","",""))
                loginSave(ChkLogin(chkIdSave = false, chkAutoLogin = false, id = "", pwd = ""))
                point = 0
                val intent = Intent(context,LoginActivity::class.java)
                context.startActivity(intent)
                Toast.makeText(
                    context,
                    "비밀번호가 변경되었습니다\n다시 로그인 해주세요.",
                    Toast.LENGTH_LONG
                ).show()
                (context as Activity).finish()
            }
            else{
                Toast.makeText(
                    context,
                    "기존 비밀번호로는 변경할 수 없습니다.",
                    Toast.LENGTH_LONG
                ).show()
            }
            
        }
        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })
}