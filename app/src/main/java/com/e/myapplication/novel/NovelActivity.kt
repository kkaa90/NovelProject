package com.e.myapplication.novel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotMutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.e.myapplication.AccountInfo
import com.e.myapplication.dataclass.NovelsDetail
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.e.myapplication.user.LoginActivity
import com.e.myapplication.user.ProtoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response

class NovelActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val bNum = intent.getIntExtra("boardNum", 0)
        val nNum = intent.getIntExtra("novelNum", 0)
        val board = mutableStateListOf<NovelsDetail>()
        val repository = ProtoRepository(this)
        fun read(): AccountInfo {
            var accountInfo: AccountInfo
            runBlocking(Dispatchers.IO) {
                accountInfo = repository.readAccountInfo()

            }
            return accountInfo
        }

        val ac = read()
        getNovelBoard(this, ac, nNum = nNum, bNum = bNum, board)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting2(board)
                }
            }
        }
    }
}

@Composable
fun Greeting2(boards: SnapshotStateList<NovelsDetail>) {
    Column() {
        Text("왜 안나옴", modifier = Modifier.clickable { println(boards.size) })
        LazyColumn {
            items(boards) { b ->
                ShowBoard(board = b)
            }
        }
    }

}

@Composable
fun ShowBoard(board: NovelsDetail) {
    val context = LocalContext.current

    Text(
        text = "Hello ${board.nvTitle}!",
        modifier = Modifier.clickable(onClick = { (context as Activity).finish() })
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    MyApplicationTheme {
    }
}

fun getNovelBoard(
    context: Context,
    ac: AccountInfo,
    nNum: Int,
    bNum: Int,
    snapshotStateList: SnapshotStateList<NovelsDetail>
) {

    val retrofitClass = RetrofitClass.api.getNovel(ac.authorization, nNum, bNum)

    retrofitClass.enqueue(object : retrofit2.Callback<NovelsDetail> {
        override fun onResponse(call: Call<NovelsDetail>, response: Response<NovelsDetail>) {
            val r = response.body()
            println(r!!.msg)
            if (r.msg != "null") {
                snapshotStateList.add(r)
            } else if (r.msg == "JWT expiration") {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
                (context as Activity).finish()
            }
        }

        override fun onFailure(call: Call<NovelsDetail>, t: Throwable) {
            t.printStackTrace()
        }

    })

}