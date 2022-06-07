package com.e.myapplication.user

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.e.myapplication.AccountInfo
import com.e.myapplication.NAVROUTE
import com.e.myapplication.RouteAction
import com.e.myapplication.dataclass.CallMethod
import com.e.myapplication.dataclass.Message
import com.e.myapplication.dataclass.MessagePost
import com.e.myapplication.dataclass.SingleMessage
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.e.myapplication.ui.theme.gray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response

@Composable
fun MessageListView(routeAction: RouteAction) {
    val context = LocalContext.current
    var sMessages = remember {
        mutableStateListOf<Message.Msg.Content>()
    }
    var rMessages = remember {
        mutableStateListOf<Message.Msg.Content>()
    }
    var isChecked = remember {
        mutableListOf<Int>()
    }
    val tabs = listOf("받은 쪽지", "보낸 쪽지")
    var tabIndex by rememberSaveable {
        mutableStateOf(0)
    }
    LaunchedEffect(true) {
        getSMessages(context, sMessages)
        getRMessages(context, rMessages)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = { routeAction.goBack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
        }
        Row() {
            TextButton(onClick = {
                deleteMessage(context, isChecked)
                Handler(Looper.getMainLooper()).postDelayed({
                    if (tabIndex == 0) {
                        rMessages.clear()
                        getRMessages(context, rMessages)
                    } else {
                        sMessages.clear()
                        getSMessages(context, sMessages)
                    }
                }, 1000)
            }) {
                Text(text = "삭제")
            }
            TextButton(onClick = {
                if (tabIndex == 0) {
                    rMessages.forEach {
                        isChecked.add(it.msgId)

                    }
                    deleteMessage(context, isChecked)
                    rMessages.clear()
                } else {
                    sMessages.forEach {
                        isChecked.add(it.msgId)

                    }
                    deleteMessage(context, isChecked)
                    sMessages.clear()
                }

            }) {
                Text(text = "전체 삭제")
            }
        }
        TabRow(
            selectedTabIndex = tabIndex,
            modifier = Modifier.height(36.dp),
            contentColor = Color.Black
        ) {
            tabs.forEachIndexed { index, s ->
                Tab(
                    selected = tabIndex == index, onClick = {
                        isChecked.clear()
                        tabIndex = index
                    },
                    text = {
                        Text(text = s, color = if (tabIndex == index) Color.White else Color.Black)
                    },
                    modifier = Modifier.background(if (tabIndex == index) Color.Black else Color.White)
                )
            }
        }
        when (tabIndex) {
            0 -> {
                LazyColumn {
                    items(rMessages) { m ->
                        MessageItem(message = m, isChecked, routeAction)
                    }
                }
            }
            1 -> {
                LazyColumn {
                    items(sMessages) { m ->
                        MessageItem(message = m, isChecked, routeAction)
                    }
                }
            }
        }

    }
}

@Composable
fun MessageItem(
    message: Message.Msg.Content,
    isChecked: MutableList<Int>,
    routeAction: RouteAction
) {
    var checked by remember {
        mutableStateOf(false)
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = checked, onCheckedChange = {
                checked = it
                if (it) {
                    isChecked.add(message.msgId)
                } else {
                    isChecked.remove(message.msgId)
                }
            })
            Column(modifier = Modifier
                .weight(1f)
                .clickable {
                    routeAction.navWithNum(NAVROUTE.MESSAGEDETAIL.routeName + "/${message.msgId}")
                }) {
                Row {
                    Text(
                        text = message.senderNickname,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp,
                        color = Color(0xFF949494), modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = message.datetime.split(" ")[0] + "  ",
                        fontSize = 14.sp,
                        color = Color(0xFF949494)
                    )
                }

                Text(text = message.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}


@Composable
fun MessageView(
    num: Int,
    routeAction: RouteAction
) {
    val context = LocalContext.current
    var message = remember {
        mutableStateOf(
            Message.Msg.Content(
                "", "", 0, 0,
                0, 0, 0, 0, "", "", ""
            )
        )
    }
    LaunchedEffect(true) {
        println(num)
        getMessage(context, message, num)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = { routeAction.goBack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = gray, shape = RectangleShape)
        ) {
            TextButton(onClick = {
                routeAction.navWithNum(NAVROUTE.WRITEMESSAGE.routeName + "?num=${message.value.senderId}&nick=${message.value.senderNickname}")
            }) {
                Text(text = "답장")
            }
            TextButton(onClick = { deleteM(context, num, routeAction) }) {
                Text(text = "삭제")
            }
        }
        Text(text = "보낸 사람 : " + message.value.senderNickname, modifier = Modifier.padding(4.dp))
        Divider(thickness = 0.5.dp)
        Text(text = "받는 사람 : " + message.value.receiverNickname, modifier = Modifier.padding(4.dp))
        Divider(thickness = 0.5.dp)
        Text(
            text = "받은 시간 : " + message.value.datetime,
            color = Color(0xFF666464),
            modifier = Modifier.padding(4.dp)
        )
        Divider(thickness = 0.5.dp)
        Text(text = "제목 : " + message.value.title, modifier = Modifier.padding(4.dp))
        Divider(thickness = 0.5.dp)
        Text(text = message.value.content, modifier = Modifier.padding(4.dp))
    }
}

@Composable
fun SendMessageView(memId: Int, memNick: String, routeAction: RouteAction) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var title by remember {
        mutableStateOf("")
    }
    var content by remember {
        mutableStateOf("")
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { routeAction.goBack() }) {
                Text(text = "취소")
            }
            TextButton(onClick = {
                sendMessage(context, memId, title, content, routeAction)
            }) {
                Text(text = "전송")
            }
        }
        Text(text = "받는사람 : $memNick", modifier = Modifier.padding(4.dp))
        Divider(thickness = 0.5.dp, color = gray)
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(text = "제목") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            })
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            label = { Text(text = "내용") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            })
        )
    }
}

fun getRMessages(context: Context, rMessage: SnapshotStateList<Message.Msg.Content>) {
    rMessage.clear()
    val repository = ProtoRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }

    val ac = read()
    val retrofitClass = RetrofitClass.api.getReceiveMessages(ac.authorization)
    retrofitClass.enqueue(object : retrofit2.Callback<Message> {
        override fun onResponse(call: Call<Message>, response: Response<Message>) {
            val r = response.body()?.msg
            if (r == null) {
                getAToken(context)
                retrofitClass.cancel()
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        getRMessages(context, rMessage)
                    }, 1000
                )
            } else {
                rMessage.addAll(r.content)
            }
        }

        override fun onFailure(call: Call<Message>, t: Throwable) {
            t.printStackTrace()
        }
    })
}

fun getSMessages(context: Context, sMessage: SnapshotStateList<Message.Msg.Content>) {
    sMessage.clear()
    val repository = ProtoRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }

    val ac = read()
    val retrofitClass = RetrofitClass.api.getSendMessages(ac.authorization)
    retrofitClass.enqueue(object : retrofit2.Callback<Message> {
        override fun onResponse(call: Call<Message>, response: Response<Message>) {
            val r = response.body()?.msg
            if (r == null) {
                getAToken(context)
                retrofitClass.cancel()
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        getSMessages(context, sMessage)
                    }, 1000
                )
            } else {
                sMessage.addAll(r.content)
            }
        }

        override fun onFailure(call: Call<Message>, t: Throwable) {
            t.printStackTrace()
        }
    })
}

fun getMessage(context: Context, message: MutableState<Message.Msg.Content>, num: Int) {

    val repository = ProtoRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }

    val ac = read()
    val retrofitClass = RetrofitClass.api.getMessage(ac.authorization, num)
    println(retrofitClass.request().toString())
    retrofitClass.enqueue(object : retrofit2.Callback<SingleMessage> {
        override fun onResponse(
            call: Call<SingleMessage>,
            response: Response<SingleMessage>
        ) {
            val r = response.body()
            if (r == null) {
                getAToken(context)
                retrofitClass.cancel()
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        getMessage(context, message, num)
                    }, 1000
                )
            } else {
                println(r)
                message.value = r.msg
            }
        }

        override fun onFailure(call: Call<SingleMessage>, t: Throwable) {
            t.printStackTrace()
        }
    })
}

fun sendMessage(
    context: Context,
    num: Int,
    title: String,
    content: String,
    routeAction: RouteAction
) {
    val repository = ProtoRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }

    val ac = read()
    val retrofitClass =
        RetrofitClass.api.sendMessage(ac.authorization, MessagePost(content, num.toString(), title))
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            val r = response.body()!!.msg
            if (r == "JWT expiration") {
                getAToken(context)
                retrofitClass.cancel()
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        sendMessage(context, num, title, content, routeAction)
                    }, 1000
                )
            } else {
                routeAction.goBack()
            }
        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun deleteMessage(context: Context, isChecked: MutableList<Int>) {
    val repository = ProtoRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()
        }
        return accountInfo
    }

    val ac = read()
    if (isChecked.isNotEmpty()) {
        val retrofitClass = RetrofitClass.api.deleteMessage(ac.authorization, isChecked.first())
        retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
            override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
                val r = response.body()!!.msg
                if (r == "JWT expiration") {
                    getAToken(context)
                    retrofitClass.cancel()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            deleteMessage(context, isChecked)
                        }, 1000
                    )
                } else {
                    isChecked.removeFirst()
                    deleteMessage(context, isChecked)
                }
            }

            override fun onFailure(call: Call<CallMethod>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
}

fun deleteM(context: Context, num: Int, routeAction: RouteAction) {
    val repository = ProtoRepository(context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()
        }
        return accountInfo
    }

    val ac = read()

    val retrofitClass = RetrofitClass.api.deleteMessage(ac.authorization, num)
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            val r = response.body()!!.msg
            if (r == "JWT expiration") {
                getAToken(context)
                retrofitClass.cancel()
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        deleteM(context, num, routeAction)
                    }, 1000
                )
            } else {
                routeAction.goBack()
            }
        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })

}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview1() {
//    MyApplicationTheme {
//        MessageListView()
//    }
//}
//
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview2() {
//    MyApplicationTheme {
////        MessageView(
////            Message.Msg.Content(
////                "테스트\n테스트\n가나다라마바사", "2222-22-22 00:00:00", 0, 0,
////                0, 0, 0, 0, "테스트중임"
////            )
////        )
//        MessageView(0)
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun Preview1() {
//    MyApplicationTheme() {
//        SendMessageView(memId = 0, memNick = "테스트")
//    }
//}