package com.e.myapplication.user

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
import com.e.myapplication.TopMenu
import com.e.myapplication.dataclass.GetBody
import com.e.myapplication.dataclass.User
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import retrofit2.Call
import retrofit2.Response
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
                        Greeting4()
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting4() {
    val login = RetrofitClass
    val service = login.api
    var id by remember { mutableStateOf("") }
    var pwd by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current



    Column() {
        OutlinedTextField(value = id, onValueChange = { id = it }, label = { Text("ID") })
        OutlinedTextField(value = pwd, onValueChange = { pwd = it }, label = { Text("패스워드") })
        OutlinedButton(onClick = {
            val getResult = service.getUser(GetBody(id, pwd))
            println(getResult.request().url())
            println(getResult.request().toString())
            getResult.enqueue(object : retrofit2.Callback<User> {
                override fun onFailure(call: Call<User>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    val r = response.body()?.authorization
                    if (r != "") {
                        println(r)
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