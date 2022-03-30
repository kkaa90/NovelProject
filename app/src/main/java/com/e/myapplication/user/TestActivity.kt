package com.e.myapplication.user

import android.os.Bundle
import android.view.ViewGroup
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

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val l = intent.getStringExtra("url")
        println("주소 : $l")
        val l2 = "https://accounts.google.com/o/oauth2/v2/auth/oauthchooseaccount?response_type=code&client_id=1042421259759-kfl3kh6j5vdml7n9iu3fj5nrhmfb6huj.apps.googleusercontent.com&scope=email%20profile&state=9Rl1oV85YIBpSDnpQtkfajQSwPTPlxqI9Acw8H8SdnQ%3D&redirect_uri=https%3A%2F%2Ftreenovel.tk%3A8080%2Flogin%2Foauth2%2Fcode%2Fgoogle&flowName=GeneralOAuthFlow"
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
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.userAgentString = "Mozilla/5.0 AppleWebKit/535.19 Chrome/56.0.0 Mobile Safari/535.19"
            loadUrl(url)
        }
        apply
    }, update = {
        it.loadUrl(url)
    })
}