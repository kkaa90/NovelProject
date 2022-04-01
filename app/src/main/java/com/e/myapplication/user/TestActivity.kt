package com.e.myapplication.user

import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.e.myapplication.ui.theme.MyApplicationTheme
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val l = intent.getStringExtra("url")
        println("주소 : $l")
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
fun WebPageView(url : String){
    AndroidView(factory = {
        val apply = WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = object : WebViewClient(){
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    super.shouldOverrideUrlLoading(view, request)
                    val okHttpClient = OkHttpClient.Builder().build()
                    val okRequest : Request = Request.Builder().url(request!!.url.toString()).build()
                    Thread(){
                        try {
                            val okResponse : Response = okHttpClient.newCall(okRequest).execute()
                            println(okResponse.headers().toString())
                        } catch (e:IOException){
                            e.printStackTrace()
                        }
                    }.start()
                    //println("링크 : +${request!!.url.toString()}")
                    return false
                }
            }
            settings.javaScriptEnabled = true
            settings.userAgentString = "Mozilla/5.0 AppleWebKit/535.19 Chrome/56.0.0 Mobile Safari/535.19"
            loadUrl(url)
        }
        apply
    }, update = {
        it.loadUrl(url)
    })
}
