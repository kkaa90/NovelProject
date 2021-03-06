package com.e.treenovel.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.e.treenovel.dataclass.User
import com.e.treenovel.ui.theme.MyApplicationTheme
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

var temp = User("", "", "", "", "", "", "", "", "")
var temp2 = mutableStateOf(false)

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val l2 = "https://treenovel.tk/oauth2/authorization/google"
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
    val repository = ProtoRepository(context = context)
    fun accountSave(user: User?) {
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
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            run()
                            {
                                if (temp2.value) {
                                    accountSave(temp)
                                    val intent = Intent().apply {
                                        putExtra("lToken", temp.authorization)
                                    }
                                    (context as Activity).setResult(Activity.RESULT_OK, intent)
                                    context.finish()
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
    }, modifier = Modifier.alpha(if (t) 0f else 1f))
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
            temp = User(
                jsonObject.get("memUserId").toString().replace("\"", ""),
                jsonObject.get("token").toString().replace("\"", ""),
                jsonObject.get("memIcon").toString().replace("\"", ""),
                jsonObject.get("memId").toString().replace("\"", ""),
                jsonObject.get("memNick").toString().replace("\"", ""),
                jsonObject.get("memPoint").toString().replace("\"", ""),
                jsonObject.get("memLastloginDatetime").toString().replace("\"", ""),
                jsonObject.get("RefreshToken").toString().replace("\"", ""),
                jsonObject.get("memEmail").toString().replace("\"", "")
            )
            temp2.value = true
        }
    }
}
