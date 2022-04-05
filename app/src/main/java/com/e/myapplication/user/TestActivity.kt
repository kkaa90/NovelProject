package com.e.myapplication.user

import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.e.myapplication.dataclass.User
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

var temp = User("", "", "", "", "", "", "")
var temp2 = mutableStateOf(false)

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val l = intent.getStringExtra("url")
        //println("주소 : $l")
        val l2 = "https://treenovel.tk:8080/oauth2/authorization/google"
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    //Greeting11(l.toString())
                    Greeting11(url = l2)
                }
            }
        }
    }
}

@Composable
fun Greeting11(url: String) {
    WebPageView(url = url)

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview13() {
    MyApplicationTheme {
        Greeting11("Google.com")

    }
}

@Composable
fun WebPageView(url: String) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val repository = ProtoRepository(context = context)
    fun AccountSave(user: User?) {
        runBlocking(Dispatchers.IO) {
            if (user != null) {
                repository.writeAccountInfo(user)
            }
        }
        return
    }

    val t by remember { temp2 }

    AndroidView(factory = {
        val v = MyJavascriptInterface()
        println(url)
        val apply = WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    //view?.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);")
                    view?.loadUrl("javascript:window.Android.getHtml(document.documentElement.innerHTML);")

                    Handler().postDelayed(
                        Runnable()
                        {
                            run()
                            {
                                if (temp2.value) {
                                    AccountSave(temp)
                                    (context as Activity).finish()
                                }
                            }
                        }, 2000
                    )

                }
            }
            settings.javaScriptEnabled = true
            settings.userAgentString =
                "Mozilla/5.0 AppleWebKit/535.19 Chrome/56.0.0 Mobile Safari/535.19"
            addJavascriptInterface(v, "Android")
            loadUrl(url)
        }
        apply
    }, update = {
        it.loadUrl(url)
    }, modifier = Modifier.alpha(if(t) 0f else 1f))
}

class MyJavascriptInterface {
    @JavascriptInterface
    fun getHtml(html: String) {
        Log.d("Test", "html: $html")
        temp2.value = false
        if (html.contains("break-word; white-space: pre-wrap;")) {
            val d = html.split(";\">")[1].split("</")[0]
//            val dd = d.split(",")
//            val ddd = dd.associate { it.split(":")[0] to it.split(":")[1] }
//            println(ddd.entries)
            val jsonParser = JsonParser()
            val obj = jsonParser.parse(d)
            val jsonObject = obj.asJsonObject
            Log.d("Test2", d)
            println(jsonObject)
            temp = User(jsonObject.get("mem_userid").toString().replace("\"",""),
                jsonObject.get("token").toString().replace("\"",""),
                jsonObject.get("mem_icon").toString().replace("\"",""),
                jsonObject.get("mem_id").toString().replace("\"",""),
                jsonObject.get("mem_nick").toString().replace("\"",""),
                jsonObject.get("mem_point").toString().replace("\"",""),
                jsonObject.get("mem_lastlogin_datetime").toString().replace("\"",""))
            temp2.value = true
        }
    }
}
