package com.e.treenovel.user

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.e.treenovel.*
import com.e.treenovel.R
import com.e.treenovel.board.bitmapToFile
import com.e.treenovel.dataclass.*
import com.e.treenovel.menu.point
import com.e.treenovel.retrofit.RetrofitClass
import com.e.treenovel.ui.theme.gray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

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
    val deleteDialog = remember { mutableStateOf(false) }
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
                    "???????????? ????????? ??? ????????? ????????? ???????????????.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    Box {
        AnimatedVisibility(visible = pwdDialog.value) {
            ChangePasswordDialog(visibility = pwdDialog, routeAction)
        }
        AnimatedVisibility(visible = emailDialog.value) {
            ChangeProfileDialog(
                present = "?????????",
                ac.email,
                change = emailContent,
                visibility = emailDialog
            )
        }
        AnimatedVisibility(visible = nickDialog.value) {
            ChangeProfileDialog(
                present = "?????????",
                now = ac.memNick,
                change = nickContent,
                visibility = nickDialog
            )
        }
        AnimatedVisibility(visible = deleteDialog.value) {
            DeleteAccountDialog(visibility = deleteDialog, routeAction = routeAction)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(gray),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { routeAction.goBack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("?????? ?????? ??????")
            }
            Row {
                TextButton(
                    onClick = {
                        emailContent.value = ""; nickContent.value = ""; bitmap.value = null
                    },
                    enabled = (emailContent.value != "" || nickContent.value != "" || ac.memIcon != iconContent.value || bitmap.value != null)
                ) {
                    Text(text = "?????? ??????")
                }
                TextButton(
                    onClick = {
                        changeProfile(
                            context,
                            emailContent,
                            nickContent,
                            iconContent,
                            bitmap
                        )
                    },
                    enabled = (emailContent.value != "" || nickContent.value != "" || ac.memIcon != iconContent.value)
                ) {
                    Text(text = "??????")
                }
            }

        }
        Row(modifier = Modifier
            .clickable { launcher.launch("image/*") }
            .height(60.dp)
            .border(width = 1.dp, shape = RectangleShape, color = gray)
            .padding(8.dp)
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "?????????", fontSize = 20.sp)
            if (ac.memIcon == "1") {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_person_24),
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
                        Text(text = "?????????")
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, shape = RectangleShape, color = gray)
                .padding(8.dp)
        ) {
            Text(text = "?????????", fontSize = 20.sp)
            Text(text = ac.memUserid)
        }
        Column(modifier = Modifier
            .clickable { nickDialog.value = true }
            .fillMaxWidth()
            .border(width = 1.dp, shape = RectangleShape, color = gray)
            .padding(8.dp)) {
            Text(text = "?????????", fontSize = 20.sp)
            Text(text = ac.memNick)
            if (nickContent.value != "") {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "[${nickContent.value}]?????? ???????????????.")
            }
        }
        Column(modifier = Modifier
            .clickable {
                if (ac.memUserid.contains("Google")) {
                    Toast
                        .makeText(
                            context,
                            "?????? ????????? ????????? ??? ????????????.",
                            Toast.LENGTH_LONG
                        )
                        .show()
                } else {
                    emailDialog.value = true
                }

            }
            .fillMaxWidth()
            .border(width = 1.dp, shape = RectangleShape, color = gray)
            .padding(8.dp)) {
            Text(text = "?????????", fontSize = 20.sp)
            Text(text = ac.email)
            if (emailContent.value != "") {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "[${emailContent.value}]?????? ???????????????.")
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, shape = RectangleShape, color = gray)
                .padding(8.dp)
                .clickable {
                    if (ac.memUserid.contains("Google")) {
                        Toast
                            .makeText(
                                context,
                                "?????? ????????? ????????? ??? ????????????.",
                                Toast.LENGTH_LONG
                            )
                            .show()
                    } else {
                        pwdDialog.value = true
                    }
                }) {
            Text("???????????? ??????", fontSize = 20.sp)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, shape = RectangleShape, color = gray)
                .padding(8.dp)
                .clickable { deleteDialog.value = true }
        ) {
            Text(text = "?????? ??????", fontSize = 20.sp)
        }

    }
}

@Composable
fun ChangeProfileDialog(
    present: String,
    now: String,
    change: MutableState<String>,
    visibility: MutableState<Boolean>
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = { visibility.value = false }) {
        Surface(
            modifier = Modifier.wrapContentSize(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "$present ??????")
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "?????? $present : $now")
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(value = change.value, onValueChange = { change.value = it })
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(onClick = {
                        change.value = ""
                        visibility.value = false
                    }) {
                        Text(text = "??????")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedButton(onClick = {
                        if (change.value.contains(" ")) {
                            Toast.makeText(
                                context,
                                "????????? ???????????? ??? ????????????.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            visibility.value = false
                        }

                    }) {
                        Text(text = "??????")
                    }
                }
            }
        }
    }


}

@Composable
fun ChangePasswordDialog(visibility: MutableState<Boolean>, routeAction: RouteAction) {
    val context = LocalContext.current
    var pwd by remember {
        mutableStateOf("")
    }
    Dialog(onDismissRequest = { visibility.value = false }) {
        Surface(
            modifier = Modifier
                .wrapContentSize(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "???????????? ??????")
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(value = pwd, onValueChange = { pwd = it })
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(onClick = { visibility.value = false }) {
                        Text(text = "??????")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedButton(onClick = { changePWD(context, pwd, routeAction) }) {
                        Text(text = "??????")
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteAccountDialog(visibility: MutableState<Boolean>, routeAction: RouteAction) {
    val context = LocalContext.current
    Dialog(onDismissRequest = { visibility.value = false }) {
        Surface(
            modifier = Modifier
                .wrapContentSize(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(text = "????????????")
                Spacer(modifier = Modifier.height(20.dp))
                Text("??????????????? ???????????????????")
                Spacer(modifier = Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    OutlinedButton(onClick = { visibility.value = false }) {
                        Text(text = "??????")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedButton(onClick = {
                        deleteUser(
                            context,
                            visibility,
                            routeAction
                        )
                    }) {
                        Text(text = "??????")
                    }
                }
            }
        }
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
            println(response.body().toString())
            if(response.body()!!.msg=="JWT expiration"){
                getAToken(context)
                retrofitClass.cancel()
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        uploadIcon(context, body, cIcon, uploadCheck)
                    }, 1000
                )
            }
            else {
                val r = response.body()!!.imgUrl
                cIcon.value = r
                uploadCheck.value = false
                Toast.makeText(
                    context,
                    "????????? ???????????????.",
                    Toast.LENGTH_LONG
                ).show()
            }
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
    val email =
        if (cEmail.value != "") cEmail.value else ac.email
    val nick =
        if (cNick.value != "") cNick.value else ac.memNick
    val icon =
        if (cIcon.value != "") cIcon.value else ac.memIcon
    val retrofitClass = RetrofitClass.api.changeProfile(
        ac.authorization,
        ChangeProfile(email, nick, icon)
    )
    println(retrofitClass.request().toString())
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            println(response.body().toString())
            if (response.body()!!.msg == "OK") {
                val user = User(
                    ac.memUserid, ac.authorization, icon, ac.memId, nick,
                    point.toString(), "", ac.refreshToken, email
                )
                accountSave(user)
                Toast.makeText(
                    context,
                    "?????? ????????? ?????????????????????.",
                    Toast.LENGTH_LONG
                ).show()
                cEmail.value = ""
                cNick.value = ""
                bitmap.value = null
            } else if (response.body()!!.msg == "JWT expiration") {
                getAToken(context)
                retrofitClass.cancel()
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        changeProfile(context, cEmail, cNick, cIcon, bitmap)
                    }, 1000
                )
            } else {
                Toast.makeText(
                    context,
                    "????????? ?????????????????????.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun changePWD(context: Context, pwd: String, routeAction: RouteAction) {
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
                Toast.makeText(
                    context,
                    "??????????????? ?????????????????????\n?????? ????????? ????????????.",
                    Toast.LENGTH_LONG
                ).show()
                routeAction.navTo(NAVROUTE.LOGIN)
            } else if (response.body()!!.msg == "JWT expiration") {
                getAToken(context)
                retrofitClass.cancel()
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        changePWD(context, pwd, routeAction)
                    }, 1000
                )
            } else {
                Toast.makeText(
                    context,
                    "?????? ?????????????????? ????????? ??? ????????????.",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun deleteUser(
    context: Context,
    visibility: MutableState<Boolean>,
    routeAction: RouteAction
) {
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
    val retrofitClass = RetrofitClass.api.deleteUser(ac.authorization)
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            val r = response.body()!!.msg
            println(r)
            when (r) {
                "OK" -> {
                    accountSave(User("", "", "", "", "", "", "", "", ""))
                    loginSave(ChkLogin(chkIdSave = false, chkAutoLogin = false, "", ""))
                    lCheck = false
                    visibility.value = false
                    Toast.makeText(
                        context,
                        "??????????????? ?????????????????????.",
                        Toast.LENGTH_LONG
                    ).show()
                    routeAction.clearBack()
                }
                "JWT expiration" -> {

                    getAToken(context)
                    retrofitClass.cancel()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            deleteUser(context, visibility, routeAction)
                        }, 1000
                    )

                }
            }
        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })

}