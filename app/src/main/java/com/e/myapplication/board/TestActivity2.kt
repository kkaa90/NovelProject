package com.e.myapplication.board

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.pointlessapps.rt_editor.model.RichTextValue
import com.pointlessapps.rt_editor.ui.RichText
import com.pointlessapps.rt_editor.ui.RichTextEditor
import com.pointlessapps.rt_editor.ui.defaultRichTextFieldStyle
import com.pointlessapps.rt_editor.ui.defaultRichTextStyle

class TestActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting14("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting14(name: String) {
    var value by remember { mutableStateOf(RichTextValue.get()) }
    var t by remember {
        mutableStateOf(true)
    }
    Column() {
        Button(onClick = { t = !t }) {
            Text("테스트중")
        }
        if(t){
            RichTextEditor(
                modifier = Modifier.fillMaxSize().border(1.dp,Color.Black),
                value = value,
                onValueChange = { value = it },
                textFieldStyle = defaultRichTextFieldStyle().copy(
                    placeholder = "Test",
                    textColor = Color.Black,
                    placeholderColor = Color.Blue,

                ),

            )
        }
        else {
            RichText(
                modifier = Modifier.fillMaxSize().border(1.dp,Color.Black),
                value = value,
                textStyle = defaultRichTextStyle().copy(
                    textColor = Color.Black)
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview16() {
    MyApplicationTheme {
        Greeting14("Android")
    }
}