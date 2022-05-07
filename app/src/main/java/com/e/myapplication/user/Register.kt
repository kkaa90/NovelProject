package com.e.myapplication.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.e.myapplication.RouteAction
import com.e.myapplication.dataclass.PostBody
import com.e.myapplication.dataclass.SendBody
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import retrofit2.Call
import retrofit2.Response

@Composable
fun Register(routeAction: RouteAction) {
    val login = RetrofitClass
    val service = login.api
    var id by remember { mutableStateOf("") }
    var pwd by rememberSaveable { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nick by remember { mutableStateOf("") }
    Column {
        OutlinedTextField(value = id, onValueChange = { id = it }, label = { Text("ID") })
        OutlinedTextField(value = pwd, onValueChange = { pwd = it }, label = { Text("패스워드") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("이메일") })
        OutlinedTextField(value = nick, onValueChange = { nick = it }, label = { Text("닉네임") })
        OutlinedButton(onClick = {

            val getResult = service.register(SendBody(id, pwd, email, nick, "1"))
            println(getResult.request().url())
            println(getResult.request().toString())
            getResult.enqueue(object : retrofit2.Callback<PostBody> {
                override fun onResponse(call: Call<PostBody>, response: Response<PostBody>) {
                    val msg = response.body()?.msg
                    println(msg)
                    if (msg != "") {
                        println(msg)
                        routeAction.goBack()
                    } else {
                        println("회원가입 실패")
                    }
                    getResult.cancel()
                }

                override fun onFailure(call: Call<PostBody>, t: Throwable) {
                    t.printStackTrace()
                }

            })
        }) {
            Text("회원가입")
        }
        OutlinedButton(onClick = { id = ""; pwd = ""; email = ""; nick = "" }) {
            Text("비우기")
        }
    }
}