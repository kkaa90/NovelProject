package com.e.myapplication.user

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.myapplication.AccountInfo
import com.e.myapplication.R
import com.e.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

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
            Row(modifier = Modifier.clickable {  }.height(60.dp), verticalAlignment = Alignment.CenterVertically) {
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

        Text("비밀번호 변경",Modifier.clickable {  })
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview14() {
    MyApplicationTheme {
        Greeting12()
    }
}