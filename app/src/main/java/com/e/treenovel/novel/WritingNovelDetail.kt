package com.e.treenovel.novel

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.e.treenovel.RouteAction
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

@Composable
fun WritingNovelDetail(num: Int, state : Int, routeAction: RouteAction, viewModel: NovelViewModel) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val novelInfo = viewModel.d.collectAsState().value
    val dMenu: MutableList<String> = ArrayList()
    dMenu.add("첫화")
    if (novelInfo.isNotEmpty()) {
        if (viewModel.parent != 0) {
            for (i in novelInfo.indices) {
                dMenu.add(novelInfo[i].nvId.toString() + "화 : " + novelInfo[i].nvTitle)
            }
            dMenu.removeAt(0)
        }
    }
    var dMenuExpanded by remember { mutableStateOf(false) }
    var dMenuName: String by remember { mutableStateOf("선택") }
    var dMenuIndex: Int by remember { mutableStateOf(0) }
    var requestBody: RequestBody
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                viewModel.nDImageUri.add(uri)
                viewModel.nDBitmap.add(
                    if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    } else {
                        val source =
                            uri.let { ImageDecoder.createSource(context.contentResolver, it) }
                        source.let { ImageDecoder.decodeBitmap(it) }
                    }
                )
                viewModel.nDFiles.add(
                    bitmapToFile(
                        viewModel.nDBitmap[viewModel.nDBitmap.size - 1]!!,
                        viewModel.nDBitmap.size.toString() + ".png"
                    )
                )
                requestBody = RequestBody.create(
                    MediaType.parse("image/*"),
                    viewModel.nDFiles[viewModel.nDBitmap.size - 1]!!
                )
                viewModel.nDBody.add(
                    MultipartBody.Part.createFormData(
                        "images", viewModel.nDBitmap.size.toString() + ".png",
                        requestBody
                    )
                )
            }
        }
    Scaffold(topBar = {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { routeAction.goBack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
            TextButton(onClick = {
                if (viewModel.woe) {
                    viewModel.editNovelDetail(routeAction)
                } else {
                    val parent: String = when (dMenuName) {
                        "첫화" -> {
                            "0"
                        }
                        "선택" -> {
                            "-"
                        }
                        else -> {
                            novelInfo[dMenuIndex].nvId.toString()
                        }
                    }
                    if (parent == "-") {
                        Toast.makeText(
                            context,
                            "부모 화를 선택해야 합니다.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        viewModel.writeNovelDetail(routeAction, num, parent)
                    }
                }

            }) {
                Text(text = if (viewModel.woe) "수정" else "글쓰기")
            }
        }
    }) { p ->
        Box() {
            AnimatedVisibility(visible = viewModel.nDBackVisibility) {
                NDBackAskingDialog(viewModel = viewModel, routeAction = routeAction, state = state)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(p)
        ) {
            OutlinedTextField(
                value = viewModel.nDTitle,
                onValueChange = { viewModel.nDTitle = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                label = { Text(text = "제목") }
            )
            Divider(thickness = 1.dp, color = Color.Gray)
            OutlinedTextField(
                value = viewModel.nDContent,
                onValueChange = { viewModel.nDContent = it },
                label = { Text("내용") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                })
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (!viewModel.woe) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "포인트 : ")
                    OutlinedTextField(
                        value = if (viewModel.nDPoint == "0") "" else viewModel.nDPoint,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = {
                            val d = it.toIntOrNull()
                            if (d == null) {
                                viewModel.nDPoint = "0"
                            } else {
                                if (d > 50) {
                                    viewModel.nDPoint = "0"
                                    Toast.makeText(
                                        context,
                                        "포인트는 0부터 50까지 설정 가능합니다.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    viewModel.nDPoint = "0"

                                } else {
                                    viewModel.nDPoint = it
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )

                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, Color.Gray)
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "부모 화 선택 [")
                    Column {
                        Row(Modifier.clickable { dMenuExpanded = !dMenuExpanded }) {
                            Text(dMenuName)
                            Icon(imageVector = Icons.Filled.ArrowDropDown, "")
                        }
                        DropdownMenu(
                            expanded = dMenuExpanded,
                            onDismissRequest = { dMenuExpanded = false }) {
                            dMenu.forEachIndexed { index, dMenuItem ->
                                DropdownMenuItem(onClick = {
                                    dMenuExpanded = false; dMenuName = dMenuItem
                                    dMenuIndex = index
                                }) {
                                    Text(dMenuItem)
                                }
                            }

                        }
                    }
                    Text(text = "]")
                }
                Row {
                    for (i: Int in 0 until viewModel.nDBitmap.size) {
                        Image(
                            bitmap = viewModel.nDBitmap[i]!!.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            viewModel.nDImageUri.remove(viewModel.nDImageUri[i])
                                            viewModel.nDBitmap.remove(viewModel.nDBitmap[i])
                                            viewModel.nDFiles.remove(viewModel.nDFiles[i])
                                            viewModel.nDBody.remove(viewModel.nDBody[i])
                                        }
                                    )
                                }
                        )
                    }
                }
                Row {
                    TextButton(onClick = { launcher.launch("image/*") }) {
                        Text(text = "이미지 선택")
                    }
                    TextButton(onClick = { viewModel.uploadNDImages() }) {
                        Text(text = "이미지 업로드")
                    }
                }
            }
            if (state==1){
                Row {
                    for (i: Int in 0 until viewModel.nDBitmap.size) {
                        Image(
                            bitmap = viewModel.nDBitmap[i]!!.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            viewModel.nDImageUri.remove(viewModel.nDImageUri[i])
                                            viewModel.nDBitmap.remove(viewModel.nDBitmap[i])
                                            viewModel.nDFiles.remove(viewModel.nDFiles[i])
                                            viewModel.nDBody.remove(viewModel.nDBody[i])
                                        }
                                    )
                                }
                        )
                    }
                }
                Row {
                    TextButton(onClick = { launcher.launch("image/*") }) {
                        Text(text = "이미지 선택")
                    }
                    TextButton(onClick = { viewModel.uploadNDImages() }) {
                        Text(text = "이미지 업로드")
                    }
                }
            }
        }
    }
}

@Composable
fun NDBackAskingDialog(viewModel: NovelViewModel, routeAction: RouteAction, state: Int) {
    Dialog(onDismissRequest = { viewModel.nDBackVisibility = false }) {
        Surface(
            modifier = Modifier
                .wrapContentSize(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "소설 작성 취소", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "소설 작성을 취소하시겠습니까?\n입력한 내용은 삭제됩니다.")
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    Modifier
                        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        OutlinedButton(onClick = { viewModel.nDBackVisibility = false }) {
                            Text(text = "취소")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedButton(onClick = {
                            if(viewModel.woe){
                                if(state==1){
                                    viewModel.nDBackPressed(routeAction)
                                }
                                else {
                                    viewModel.nDBackPressed2(routeAction)
                                }
                            }
                            else {
                                viewModel.nDBackPressed(routeAction)
                            }
                        }) {
                            Text(text = "확인")
                        }
                    }
                }

            }
        }

    }
}