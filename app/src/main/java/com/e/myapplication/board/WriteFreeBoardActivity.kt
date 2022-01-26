package com.e.myapplication.board

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.e.myapplication.AccountInfo
import com.e.myapplication.dataclass.PostBoard
import com.e.myapplication.dataclass.PostBoardResponse
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.e.myapplication.user.ProtoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response

class WriteFreeBoardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting7("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting7(name: String) {
    val context = LocalContext.current
    val repository = ProtoRepository(context = context)
    fun read() : AccountInfo{
        var accountInfo : AccountInfo
        runBlocking (Dispatchers.IO){
            accountInfo=repository.readAccountInfo()

        }
        return accountInfo
    }
    val writeBoard = RetrofitClass
    val service = writeBoard.api
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "제목")
        OutlinedTextField(value = title, onValueChange = { title = it }, maxLines = 1)
        Text(text = "내용")
        OutlinedTextField(value = content, onValueChange = { content = it })
        Button(onClick = {
            val ac = read()
            val wb = service.writeBoard(ac.authorization.toString(),
                PostBoard(content,"","",title,ac.memId.toString(),ac.memNick.toString()))
            wb.enqueue(object : retrofit2.Callback<PostBoardResponse>{
                override fun onResponse(call: Call<PostBoardResponse>, response: Response<PostBoardResponse>) {
                    val r = response.body()?.msg
                    println(response.body().toString())
                    println(r)
                    when (r) {
                        "error" -> {
                            println("토큰 만료")
                        }
                        "1" -> {
                            println("글쓰기 성공")
                            (context as Activity).finish()
                        }
                        else -> {
                            println("글쓰기 오류")
                        }
                    }
                    wb.cancel()
                }

                override fun onFailure(call: Call<PostBoardResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }) {
            Text(text = "글쓰기")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview9() {
    MyApplicationTheme {
        Greeting7("Android")
    }
}