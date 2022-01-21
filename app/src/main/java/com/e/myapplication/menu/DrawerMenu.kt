package com.e.myapplication.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.myapplication.R


sealed class DrawerMenu(val route : String, val title: String) {
    object Home : DrawerMenu("home","Home")
    object Account : DrawerMenu("account","Account")
    object Board : DrawerMenu("board","Board")
}

@Composable
fun Drawer(){
    val drawers = listOf(
        DrawerMenu.Home,
        DrawerMenu.Account,
        DrawerMenu.Board
    )
    Column() {
        Image(painter = painterResource(id = R.drawable.schumi), contentDescription = "", 
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .padding(10.dp))
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(5.dp))

    }
}

@Preview
@Composable
fun previewDrawer(){
    Drawer()
}