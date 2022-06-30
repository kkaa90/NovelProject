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
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
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
                        "ID에 Google을 포함할 수 없습니다."
                    } else {
                        "ID는 6~16 길이의 영문 및 숫자로 이루어져야 합니다."
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
                label = { Text("패스워드") },
                isError = pwdError,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Text(
                text = if (pwdError && pwd.isNotEmpty()) "비밀번호는 8~16 길이의 영문 및 숫자로 이루어져야 합니다." else "",
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
                label = { Text("패스워드 확인") },
                isError = pwd2Error,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Text(
                text = if (pwd2Error && pwd2.isNotEmpty()) "비밀번호가 같지 않습니다." else "",
                color = Color.Red,
                fontSize = 12.sp
            )
        }
        Column() {
            OutlinedTextField(value = email, onValueChange = {
                emailError = !Patterns.EMAIL_ADDRESS.matcher(it).matches()
                email = it
            }, label = { Text("이메일") }, isError = emailError, singleLine = true)
            Text(
                text = if (emailError && email.isNotEmpty()) "이메일 형식에 맞지 않습니다." else "",
                color = Color.Red,
                fontSize = 12.sp
            )
        }
        Column() {
            OutlinedTextField(
                value = nick,
                onValueChange = {
                    nickError = !Pattern.matches("^[a-zA-z0-9가-힣]{2,10}\$", it)
                    nick = it
                },
                label = { Text("닉네임") },
                isError = nickError,
                singleLine = true
            )
            Text(
                text = if (nickError && nick.isNotEmpty()) "닉네임은 특수문자를 제외한 2~10 길이의 문자로 이루어져야합니다." else "",
                color = Color.Red,
                fontSize = 10.sp
            )
        }
//        Row(modifier = Modifier.width(200.dp).height(50.dp), verticalAlignment = Alignment.CenterVertically){
//            Text(text = "이모티콘 선택 ",modifier = Modifier.clickable { launcher.launch("image/*")})
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
//                        Text(text = "업로드")
//                    }
//                }
//            }
//        }
//        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = {
            if (idError || pwdError || pwd2Error || emailError || nickError) {
                Toast.makeText(
                    context,
                    "각 항목의 조건을 확인 후 다시 시도해주세요.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                println(iconContent.value)
                signUpCall(context, id, pwd, email, nick, iconContent.value, routeAction)
            }
        }) {
            Text("회원가입")
        }
        OutlinedButton(onClick = {
            id = ""; pwd = ""; pwd2 = "";email = ""; nick = ""
            idError = true;pwdError = true;pwd2Error = true;emailError = true;nickError = true
        }) {
            Text("비우기")
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
                        "중복된 ID입니다. 다른 ID를 사용해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                    retrofitClass.cancel()
                }
                "nickname reduplication" -> {
                    Toast.makeText(
                        context,
                        "중복된 닉네임입니다. 다른 닉네임을 사용해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                    retrofitClass.cancel()
                }
                else -> {
                    Toast.makeText(
                        context,
                        "오류가 발생했습니다. 잠시 후 시도해주세요.",
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