package com.e.myapplication.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.myapplication.*
import com.e.myapplication.R
import com.e.myapplication.dataclass.*
import com.e.myapplication.menu.point
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.net.URLDecoder

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestId()
//            .requestIdToken(getString(R.string.gms_id))
//            .requestEmail().build()
//        val gsc = GoogleSignIn.getClient(this, gso)
//        println("테스트중 : ${getString(R.string.gms_id)}")
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme

                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {
                        //Login()
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Login(routeAction: RouteAction) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val repository = ProtoRepository(context = context)
    val repository2 = LoginRepository(context)
    fun accountSave(user: User?) {
        runBlocking(Dispatchers.IO) {
            if (user != null) {
                repository.writeAccountInfo(user)
            }
        }
        return
    }

    fun loginSave(chkLogin: ChkLogin?){
        runBlocking(Dispatchers.IO){
            if(chkLogin!=null){
                repository2.writeLoginInfo(chkLogin)
            }
        }
        return
    }

    fun readLoginInfo() : LoginInfo{
        var loginInfo : LoginInfo
        runBlocking(Dispatchers.IO) {
            loginInfo = repository2.readLoginInfo()
        }
        return loginInfo
    }

    val l = readLoginInfo()

    var id by remember { mutableStateOf(l.id) }
    var pwd by rememberSaveable { mutableStateOf(l.pwd) }
    var check1 by remember { mutableStateOf(l.chkIdSave) }
    var check2 by remember { mutableStateOf(l.chkAccSave) }

    val t = remember {
        mutableStateOf("")
    }
    com.e.myapplication.novel.getToken(t)
    /*val startForResult = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK)
        {
            val intent = result.data
            if (result.data != null){
                val task : Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(intent)
                handleSignInResult(task)
            }
        }
    }*/
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID") },
            singleLine = true
        )
        OutlinedTextField(value = pwd,
            onValueChange = { pwd = it },
            label = { Text("패스워드") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Row(Modifier.clickable {
                check1 = !check1
                if (!check1)
                    check2 = false
            }) {
                Text("아이디 저장")
                Spacer(modifier = Modifier.height(4.0.dp))
                Checkbox(checked = check1, onCheckedChange = null)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(Modifier.clickable {
                check2 = !check2
                if (check2) {
                    check1 = true
                }
            }) {
                Text("자동 로그인")
                Spacer(modifier = Modifier.height(4.0.dp))
                Checkbox(checked = check2, onCheckedChange = null)
            }
        }
        OutlinedButton(onClick = {
            loginFun(context, id, pwd, check1, check2, t, routeAction)
            keyboardController?.hide()
        }) {
            Text(text = "로그인")
        }
        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val lToken = result.data?.getStringExtra("lToken")
                    sendT(lToken!!, t.value)
                    getPoint(lToken)
                    routeAction.goBack()
                }
            }
        OutlinedButton(onClick = {
            routeAction.navTo(NAVROUTE.REGISTER)
        }) {
            Text(text = "회원가입")
        }
        /*Button(
            onClick = {
                startForResult.launch(gsc.signInIntent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_login_24),
                contentDescription = ""
            )
            Text(text = "Sign in with Google", modifier = Modifier.padding(6.dp))
        }*/

        Button(
            onClick = {
                //testFun2(context)
                val intent = Intent(context, TestActivity::class.java)
                launcher.launch(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_login_24),
                contentDescription = ""
            )
            Text(text = "Sign in with Google", modifier = Modifier.padding(6.dp))
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview4() {
    MyApplicationTheme {
        //Login()
    }
}

/*
private fun handleSignInResult(completeTask: Task<GoogleSignInAccount>){
    try {
        val account = completeTask.getResult(ApiException::class.java)
        val email = account?.email.toString()
        val familyName = account?.familyName.toString()
        val givenName = account?.givenName.toString()
        val displayName = account?.displayName.toString()
        val idToken = account?.idToken
        testFun(account)
        Log.d("성공","token : $idToken")
        println("==================")
        println(account.id.toString())
    } catch (e: ApiException){
        Log.w("실패", "SignInResult:Failed Code = ${e.statusCode}")
    }
}

fun testFun(account: GoogleSignInAccount){
    val retrofitClass = RetrofitClass.api.test(account)
    retrofitClass.enqueue(object :retrofit2.Callback<ResponseBody>{
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            println(response.headers().toString())
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun testFun2(context: Context){
    val retrofitClass = RetrofitClass.api.test2()
    retrofitClass.enqueue(object : retrofit2.Callback<ResponseBody>{
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            println(response.raw().isRedirect)
            println("===========")
            //println(response.body()!!.string())
            println("===========")
            val l = response.raw().request().url()
            println(response.code())
            val intent = Intent(context,TestActivity::class.java)
            intent.putExtra("url", l.toString())
            context.startActivity(intent)
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            t.printStackTrace()
        }

    })
}
*/
fun getPoint(token: String) {
    val retrofitClass = RetrofitClass.api.getPoint(token)
    retrofitClass.enqueue(object : retrofit2.Callback<Point> {
        override fun onResponse(call: Call<Point>, response: Response<Point>) {
            val r = response.body()!!.point.toInt()
            if (r == -1) {

            } else {
                point = r
                lCheck = true
            }
        }

        override fun onFailure(call: Call<Point>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun sendT(lToken: String, fToken: String) {
    val retrofitClass = RetrofitClass.api.sendToken(lToken, Token(fToken))
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {

        }

        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })
}

fun getAToken(context: Context){
    val repository = ProtoRepository(context = context)
    fun accountSave(user: User?) {
        runBlocking(Dispatchers.IO) {
            if (user != null) {
                repository.writeAccountInfo(user)
            }
        }
        return
    }
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()
        }
        return accountInfo
    }
    val ac = read()
    val retrofitClass = RetrofitClass.api.getAccessToken(ac.refreshToken)
    retrofitClass.enqueue(object : retrofit2.Callback<CallMethod>{
        override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
            if(response.body()!!.msg=="OK"){
                val r= response.headers()
                val user = User(ac.memUserid,r.get("Authorization")!!,ac.memIcon,ac.memId,ac.memNick,"0","",ac.refreshToken, ac.email)
                accountSave(user)
            }
            else {
                lCheck = false
                val user = User("","","","","","","","", "")
                accountSave(user)
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
                Toast.makeText(
                    context,
                    "토큰이 만료되었습니다.\n 다시 로그인 해주세요.",
                    Toast.LENGTH_LONG
                ).show()
            }
            getPoint(response.headers().get("Authorization")!!)
        }
        override fun onFailure(call: Call<CallMethod>, t: Throwable) {
            t.printStackTrace()
        }

    })

}

fun loginFun(context: Context, id: String, pwd: String, check1: Boolean, check2:Boolean, t : MutableState<String>, routeAction: RouteAction){
    val repository = ProtoRepository(context = context)
    val repository2 = LoginRepository(context)
    fun accountSave(user: User?) {
        runBlocking(Dispatchers.IO) {
            if (user != null) {
                repository.writeAccountInfo(user)
            }
        }
        return
    }

    fun loginSave(chkLogin: ChkLogin?){
        runBlocking(Dispatchers.IO){
            if(chkLogin!=null){
                repository2.writeLoginInfo(chkLogin)
            }
        }
        return
    }

    val login = RetrofitClass
    val service = login.api
    val getResult = service.getUser(GetBody(id, pwd))
    println(getResult.request().url())
    println(getResult.request().toString())
    val chkLogin = ChkLogin(check1,check2,if (check1) id else "", if(check2) pwd else "")
    getResult.enqueue(object : retrofit2.Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            t.printStackTrace()
        }

        override fun onResponse(
            call: Call<ResponseBody>,
            response: Response<ResponseBody>
        ) {
            val r = response.code()
            val u = response.headers()
            val d = u.values("Set-Cookie")

            println(d)
            if (r == 200) {
                val nick = URLDecoder.decode(d[0].split("=")[1], "UTF-8")
                val id = d[1].split("=")[1]
                val userid = d[2].split("=")[1]
                val memicon = d[3].split("=")[1]
                val mempoint = d[5].split("=")[1]
                val ll = URLDecoder.decode(d[6].split("=")[1], "UTF-8")
                val email = URLDecoder.decode(d[4].split("=")[1], "UTF-8")
                val user = User(
                    userid, u.get("Authorization")!!, memicon,
                    id, nick, mempoint, ll, u.get("RefreshToken")!!,
                    email
                )
                println(nick)
                println(ll)
                accountSave(user)
                loginSave(chkLogin)
                sendT(u.get("Authorization")!!, t.value)
                getPoint(u.get("Authorization")!!)
                routeAction.goBack()
            } else {
                Toast.makeText(
                    context,
                    "존재하지 않는 ID 이거나\n비밀번호가 다릅니다.",
                    Toast.LENGTH_LONG
                ).show()
            }
            getResult.cancel()
        }
    })
}
