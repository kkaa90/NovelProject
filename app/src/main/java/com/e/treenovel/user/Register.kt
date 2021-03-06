package com.e.treenovel.user

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.e.treenovel.RouteAction
import com.e.treenovel.board.bitmapToFile
import com.e.treenovel.dataclass.PostBody
import com.e.treenovel.dataclass.SendBody
import com.e.treenovel.retrofit.RetrofitClass
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.regex.Pattern

@Composable
fun Register(routeAction: RouteAction) {
    val context = LocalContext.current
    var id by rememberSaveable { mutableStateOf("") }
    var idError by rememberSaveable { mutableStateOf(true) }
    var pwd by rememberSaveable { mutableStateOf("") }
    var pwdError by rememberSaveable { mutableStateOf(true) }
    var pwd2 by rememberSaveable { mutableStateOf("") }
    var pwd2Error by rememberSaveable { mutableStateOf(true) }
    var email by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf(true) }
    var nick by rememberSaveable { mutableStateOf("") }
    var nickError by rememberSaveable { mutableStateOf(true) }
    val iconContent = remember { mutableStateOf("1") }
    val uploadCheck = remember { mutableStateOf(false) }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val files = remember { mutableStateOf<File?>(null) }
    var requestBody: RequestBody
    val body = remember { mutableStateOf<MultipartBody.Part?>(null) }
    var visibility = remember { mutableStateOf(false) }
    var serviceAgreement = remember { mutableStateOf(false) }
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
    if(visibility.value){
        Box() {
            TermOfServiceDialog(visibility = visibility, agreement = serviceAgreement)
        }
    }
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column() {
            OutlinedTextField(
                value = id,
                onValueChange = {
                    idError = !Pattern.matches("^[a-zA-z0-9]{6,16}\$", it) || it.contains("Google")
                    id = it
                },
                label = { Text("ID") },
                isError = idError,
                singleLine = true
            )
            Text(
                text = if (idError && id.isNotEmpty()) {
                    if (id.contains("Google")) {
                        "ID??? Google??? ????????? ??? ????????????."
                    } else {
                        "ID??? 6~16 ????????? ?????? ??? ????????? ??????????????? ?????????."
                    }
                } else "",
                color = Color.Red,
                fontSize = 12.sp
            )
        }
        Column() {
            OutlinedTextField(
                value = pwd,
                onValueChange = {
                    pwdError = !Pattern.matches("^[a-zA-z0-9]{8,16}\$", it)
                    pwd2Error = (pwd2 != it)
                    pwd = it
                },
                label = { Text("????????????") },
                isError = pwdError,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Text(
                text = if (pwdError && pwd.isNotEmpty()) "??????????????? 8~16 ????????? ?????? ??? ????????? ??????????????? ?????????." else "",
                color = Color.Red,
                fontSize = 10.sp
            )
        }
        Column() {
            OutlinedTextField(
                value = pwd2,
                onValueChange = {
                    pwd2Error = (pwd != it)
                    pwd2 = it
                },
                label = { Text("???????????? ??????") },
                isError = pwd2Error,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Text(
                text = if (pwd2Error && pwd2.isNotEmpty()) "??????????????? ?????? ????????????." else "",
                color = Color.Red,
                fontSize = 12.sp
            )
        }
        Column() {
            OutlinedTextField(value = email, onValueChange = {
                emailError = !Patterns.EMAIL_ADDRESS.matcher(it).matches()
                email = it
            }, label = { Text("?????????") }, isError = emailError, singleLine = true)
            Text(
                text = if (emailError && email.isNotEmpty()) "????????? ????????? ?????? ????????????." else "",
                color = Color.Red,
                fontSize = 12.sp
            )
        }
        Column() {
            OutlinedTextField(
                value = nick,
                onValueChange = {
                    nickError = !Pattern.matches("^[a-zA-z0-9???-???]{2,10}\$", it)
                    nick = it
                },
                label = { Text("?????????") },
                isError = nickError,
                singleLine = true
            )
            Text(
                text = if (nickError && nick.isNotEmpty()) "???????????? ??????????????? ????????? 2~10 ????????? ????????? ????????????????????????." else "",
                color = Color.Red,
                fontSize = 10.sp
            )
        }
//        Row(modifier = Modifier.width(200.dp).height(50.dp), verticalAlignment = Alignment.CenterVertically){
//            Text(text = "???????????? ?????? ",modifier = Modifier.clickable { launcher.launch("image/*")})
//            if (bitmap.value != null) {
//                Image(
//                    bitmap = bitmap.value!!.asImageBitmap(),
//                    contentDescription = "",
//                    modifier = Modifier
//                        .size(40.dp)
//                        .clip(CircleShape)
//                )
//                if (uploadCheck.value) {
//                    TextButton(onClick = { uploadIcon(context, body, iconContent, uploadCheck) }) {
//                        Text(text = "?????????")
//                    }
//                }
//            }
//        }
//        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.clickable { serviceAgreement.value = !serviceAgreement.value }, verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = serviceAgreement.value, onCheckedChange = {serviceAgreement.value = it})
            Text(text = "?????? ????????? ???????????????. ")
            Text(text = "[???????????? ??????]", color = Color.Blue, modifier = Modifier.clickable { visibility.value =true })
        }
        OutlinedButton(onClick = {
            if (idError || pwdError || pwd2Error || emailError || nickError || !serviceAgreement.value) {
                Toast.makeText(
                    context,
                    "??? ????????? ????????? ?????? ??? ?????? ??????????????????.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                println(iconContent.value)
                signUpCall(context, id, pwd, email, nick, iconContent.value, routeAction)
            }
        }) {
            Text("????????????")
        }
        OutlinedButton(onClick = {
            id = ""; pwd = ""; pwd2 = "";email = ""; nick = ""
            idError = true;pwdError = true;pwd2Error = true;emailError = true;nickError = true
        }) {
            Text("?????????")
        }
    }
}
@Composable
fun TermOfServiceDialog(visibility: MutableState<Boolean>, agreement: MutableState<Boolean>) {
    Dialog(onDismissRequest = { visibility.value = false }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(Modifier.padding(20.dp)) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(text = TermOfService.t)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    OutlinedButton(onClick = { visibility.value = false }) {
                        Text(text = "??????")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedButton(onClick = {
                        agreement.value=true
                        visibility.value=false
                    }) {
                        Text(text = "??????")
                    }
                }
            }
        }
    }
}
fun signUpCall(
    context: Context,
    id: String,
    pwd: String,
    email: String,
    nick: String,
    icon: String,
    routeAction: RouteAction
) {
    val retrofitClass = RetrofitClass.api.register(SendBody(id, pwd, email, nick, icon))
    println(retrofitClass.request().toString())
    retrofitClass.enqueue(object : retrofit2.Callback<PostBody> {
        override fun onResponse(call: Call<PostBody>, response: Response<PostBody>) {
            when (val msg = response.body()?.msg) {
                "OK" -> {
                    println(msg)
                    routeAction.goBack()
                }
                "id reduplication" -> {
                    Toast.makeText(
                        context,
                        "????????? ID?????????. ?????? ID??? ??????????????????.",
                        Toast.LENGTH_LONG
                    ).show()
                    retrofitClass.cancel()
                }
                "nickname reduplication" -> {
                    Toast.makeText(
                        context,
                        "????????? ??????????????????. ?????? ???????????? ??????????????????.",
                        Toast.LENGTH_LONG
                    ).show()
                    retrofitClass.cancel()
                }
                else -> {
                    Toast.makeText(
                        context,
                        "????????? ??????????????????. ?????? ??? ??????????????????.",
                        Toast.LENGTH_LONG
                    ).show()
                    retrofitClass.cancel()
                }
            }
        }

        override fun onFailure(call: Call<PostBody>, t: Throwable) {
            t.printStackTrace()
        }

    })

}