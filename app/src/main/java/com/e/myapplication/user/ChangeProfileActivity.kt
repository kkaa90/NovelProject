package com.e.myapplication.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.e.myapplication.AccountInfo
import com.e.myapplication.R
import com.e.myapplication.RouteAction
import com.e.myapplication.board.bitmapToFile
import com.e.myapplication.dataclass.*
import com.e.myapplication.lCheck
import com.e.myapplication.menu.point
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

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
                    //Greeting12()
                }
            }
        }
    }
}

@Composable
fun ProfileView(routeAction: RouteAction) {
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
    val pwdDialog = remember { mutableStateOf(false) }
    val emailDialog = remember { mutableStateOf(false) }
    val nickDialog = remember { mutableStateOf(false) }
    val emailContent = remember { mutableStateOf("") }
    val nickContent = remember { mutableStateOf("") }
    val iconContent = remember { mutableStateOf(ac.memIcon) }
    val uploadCheck = remember { mutableStateOf(false) }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val files = remember { mutableStateOf<File?>(null) }
    var requestBody: RequestBody
    val body = remember { mutableStateOf<MultipartBody.Part?>(null) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                uploadCheck.value = true
                imageUri.value = uri
                println(imageUri)
                bitmap.value =
                    if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    } else {
                        val source =
                            uri.let { ImageDecoder.createSource(context.contentResolver, it) }
                        source.let { ImageDecoder.decodeBitmap(it) }
                    }

                files.value = bitmapToFile(bitmap.value!!, "icon.png")
                println(files.value!!.absolutePath)
                requestBody =
                    RequestBody.create(MediaType.parse("image/*"), files.value!!)
                body.value =
                    MultipartBody.Part.createFormData(
                        "images",
                        "icon.png",
                        requestBody
                    )
                Toast.makeText(
                    context,
                    "아이콘은 업로드 후 저장을 눌러야 변경됩니다.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    Box {
        AnimatedVisibility(visible = pwdDialog.value) {
            ChangePasswordDialog(visibility = pwdDialog)
        }
        AnimatedVisibility(visible = emailDialog.value) {
            ChangeProfileDialog(
                present = "이메일",
                ac.email,
                change = emailContent,
                visibility = emailDialog
            )
        }
        AnimatedVisibility(visible = nickDialog.value) {
            ChangeProfileDialog(
                present = "닉네임",
                now = ac.memNick,
                change = nickContent,
                visibility = nickDialog
            )
        }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { (context as Activity).finish() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("계정 정보 변경")
            }
            Row {
                TextButton(
                    onClick = { emailContent.value = ""; nickContent.value = ""; bitmap.value = null },
                    enabled = (emailContent.value != "" || nickContent.value != "" || ac.memIcon!=iconContent.value || bitmap.value!=null)
                ) {
                    Text(text = "변경 취소")
                }
                TextButton(
                    onClick = { changeProfile(context, emailContent, nickContent, iconContent, bitmap) },
                    enabled = (emailContent.value != "" || nickContent.value != "" || ac.memIcon!=iconContent.value)
                ) {
                    Text(text = "저장")
                }
            }

        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier
            .clickable { launcher.launch("image/*") }
            .height(60.dp)
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "아이콘 변경")
            if (ac.memIcon == "1") {
                Image(
                    painter = painterResource(id = R.drawable.schumi),
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            } else {
                Image(
                    painter = rememberImagePainter(ac.memIcon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            }

            if (bitmap.value != null) {
                Text(text = " - > ")
                Image(
                    bitmap = bitmap.value!!.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                if (uploadCheck.value) {
                    TextButton(onClick = { uploadIcon(context, body, iconContent, uploadCheck) }) {
                        Text(text = "업로드")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "아이디")
            Text(text = ac.memUserid)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier
            .clickable { nickDialog.value = true }
            .fillMaxWidth()) {
            Text(text = "닉네임")
            Text(text = ac.memNick)
            if (nickContent.value != "") {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "[${nickContent.value}]으로 변경됩니다.")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(modifier = Modifier
            .clickable { emailDialog.value = true }
            .fillMaxWidth()) {
            Text(text = "이메일")
            Text(text = ac.email)
            if (emailContent.value != "") {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "[${emailContent.value}]으로 변경됩니다.")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("비밀번호 변경", Modifier.clickable { pwdDialog.value = true })
    }
}

@Composable
fun ChangeProfileDialog(
    present: String,
    now: String,
    change: MutableState<String>,
    visibility: MutableState<Boolean>
) {
    Dialog(onDismissRequest = { visibility.value = false }) {
        Surface(
            modifier = Modifier
                .width(240.dp)
                .height(400.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column {
                Text(text = "$present 변경")
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "현재 닉네임 : $now")
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(value = change.value, onValueChange = { change.value = it })
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    OutlinedButton(onClick = {
                        change.value = ""
                        visibility.value = false
                    }) {
                        Text(text = "취소")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedButton(onClick = { visibility.value = false }) {
                        Text(text = "확인")
                    }
                }
            }
        }
    }


}

@Composable
fun ChangePasswordDialog(visibility: MutableState<Boolean>) {
    val context = LocalContext.current
    var pwd by remember {
        mutableStateOf("")
    }
    Dialog(onDismissRequest = { visibility.value = false }) {
        Surface(
            modifier = Modifier
                .width(240.dp)
                .height(400.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column {
                Text(text = "비밀번호 변경")
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(value = pwd, onValueChange = { pwd = it })
                Spacer(modifier = Modifier.height(20.dp))
                Row {
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
        //Greeting12()
    }
}

fun uploadIcon(
    context: Context,
    body: MutableState<MultipartBody.Part?>,
    cIcon: MutableState<String>,
    uploadCheck: MutableState<Boolean>
) {
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()
        }
        return accountInfo
    }

    val ac = read()
    val retrofitClass = RetrofitClass.api.uploadImage(ac.authorization, body.value)
    retrofitClass.enqueue(object : retrofit2.Callback<ImageUploadSingle> {
        override fun onResponse(
            call: Call<ImageUploadSingle>,
            response: Response<ImageUploadSingle>
        ) {
            val r = response.body()!!.imgUrl
            cIcon.value = r
            uploadCheck.value=false
            Toast.makeText(
                context,
                "업로드 되었습니다.",
                Toast.LENGTH_LONG
            ).show()
        }

        override fun onFailure(call: Call<ImageUploadSingle>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun changeProfile(
    context: Context,
    cEmail: MutableState<String>,
    cNick: MutableState<String>,
    cIcon: MutableState<String>,
    bitmap: MutableState<Bitmap?>
) {
    val repository = ProtoRepository(context = context)
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

    val ac = read()
    val retrofitClass = RetrofitClass.api.changeProfile(
        ac.authorization,
        ChangeProfile(cEmail.value, cNick.value, cIcon.value)
    )
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            if (response.body()!!.msg == "OK") {
                val email =
                    if (ac.email != cEmail.value && cEmail.value != "") cEmail.value else ac.email
                val nick =
                    if (ac.memNick != cNick.value && cNick.value != "") cNick.value else ac.memNick
                val icon =
                    if (ac.memIcon != cIcon.value && cIcon.value != "") cIcon.value else ac.memIcon
                val user = User(
                    ac.memUserid, ac.authorization, icon, ac.memId, nick,
                    point.toString(), "", ac.refreshToken, email
                )
                accountSave(user)
                Toast.makeText(
                    context,
                    "계정 정보가 변경되었습니다.",
                    Toast.LENGTH_LONG
                ).show()
                cEmail.value = ""
                cNick.value = ""
                cIcon.value = ""
                bitmap.value = null
            } else {
                Toast.makeText(
                    context,
                    "오류가 발생하였습니다.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun changePWD(context: Context, pwd: String) {
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

    fun loginSave(chkLogin: ChkLogin?) {
        runBlocking(Dispatchers.IO) {
            if (chkLogin != null) {
                repository2.writeLoginInfo(chkLogin)
            }
        }
        return
    }

    val ac = read()
    val retrofitClass = RetrofitClass.api.changePassword(ac.authorization, ChangePwd(pwd))
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            val r = response.body()!!.msg
            println(r)
            if (r == "OK") {
                lCheck = false
                accountSave(User("", "", "", "", "", "", "", "", ""))
                loginSave(ChkLogin(chkIdSave = false, chkAutoLogin = false, id = "", pwd = ""))
                point = 0
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
                Toast.makeText(
                    context,
                    "비밀번호가 변경되었습니다\n다시 로그인 해주세요.",
                    Toast.LENGTH_LONG
                ).show()
                (context as Activity).finish()
            } else {
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