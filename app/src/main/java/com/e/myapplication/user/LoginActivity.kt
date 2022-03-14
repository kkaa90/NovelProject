package com.e.myapplication.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.e.myapplication.AccountInfo
import com.e.myapplication.TopMenu
import com.e.myapplication.dataclass.GetBody
import com.e.myapplication.dataclass.U
import com.e.myapplication.dataclass.User
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response
import java.net.URLDecoder
import javax.security.auth.callback.Callback

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent() {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme

                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {
                        Login()
                    }
                }
            }
        }
    }
}




@Composable
fun Login() {
    val login = RetrofitClass
    val service = login.api
    var id by remember { mutableStateOf("") }
    var pwd by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val repository = ProtoRepository(context = context)
    fun AccountSave(user: User?) {
        runBlocking(Dispatchers.IO){
            if (user != null) {
                repository.writeAccountInfo(user)
            }
        }
        return
    }

    Column() {
        OutlinedTextField(value = id, onValueChange = { id = it }, label = { Text("ID") })
        OutlinedTextField(value = pwd, onValueChange = { pwd = it }, label = { Text("패스워드") })
        OutlinedButton(onClick = {
            val getResult = service.getUser(GetBody(id, pwd))
            println(getResult.request().url())
            println(getResult.request().toString())
            getResult.enqueue(object : retrofit2.Callback<U> {
                override fun onFailure(call: Call<U>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<U>, response: Response<U>) {
                    val r = response.code()
                    val u = response.headers()
                    val d = u.values("Set-Cookie")
                    if (r == 200) {
                        val nick = URLDecoder.decode(d[1].split("=")[1],"UTF-8")
                        val ll = URLDecoder.decode(d[5].split("=")[1],"UTF-8")
                        val user = User(u.get("mem_userid")!!,u.get("Authorization")!!,u.get("mem_icon")!!,
                            u.get("mem_id")!!,nick,d[0].split("=")[1],ll)
                        println(nick)
                        println(ll)
                        AccountSave(user)
                        (context as Activity).finish()
                    } else {
                        println("로그인 실패")
                    }
                    getResult.cancel()
                }
            })
        }) {
            Text(text = "로그인")

        }
        OutlinedButton(onClick = {
            val intent = Intent(context, RegisterActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "회원가입")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview4() {
    MyApplicationTheme {

    }
}