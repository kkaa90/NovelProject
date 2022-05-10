package com.e.myapplication

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import coil.compose.rememberImagePainter
import com.e.myapplication.board.FreeBoardViewModel
import com.e.myapplication.board.ShowBoard
import com.e.myapplication.board.ShowFreeBoardList
import com.e.myapplication.board.WritingBoard
import com.e.myapplication.dataclass.Novels
import com.e.myapplication.menu.Drawer
import com.e.myapplication.notification.NotificationsView
import com.e.myapplication.notification.NotifyDB
import com.e.myapplication.novel.*
import com.e.myapplication.search.SearchView
import com.e.myapplication.ui.theme.MyApplicationTheme
import com.e.myapplication.ui.theme.gray
import com.e.myapplication.user.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

lateinit var notifyDB : NotifyDB
var lCheck = false
class MainActivity : ComponentActivity() {
    private val multiplePermissionsCode = 100
    private val requiredPermissions =
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    private fun checkPermissions() {
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        val rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if (rejectedPermissionList.isNotEmpty()) {
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(
                this,
                rejectedPermissionList.toArray(array),
                multiplePermissionsCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            multiplePermissionsCode -> {
                if (grantResults.isNotEmpty()) {
                    for ((i, permission) in permissions.withIndex()) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.i("TAG", "The user has denied to $permission")
                            Log.i("TAG", "I can't work for you anymore then. ByeBye!")
                        }
                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val repository = ProtoRepository(this)
        val repository2 = LoginRepository(this)
        fun read(): AccountInfo {
            var accountInfo: AccountInfo
            runBlocking(Dispatchers.IO) {
                accountInfo = repository.readAccountInfo()

            }
            return accountInfo
        }
        fun readLoginInfo() : LoginInfo{
            var loginInfo : LoginInfo
            runBlocking(Dispatchers.IO) {
                loginInfo = repository2.readLoginInfo()
            }
            return loginInfo
        }

        val l = readLoginInfo()
        if(l.chkAccSave){
            getAToken(this)
        }

        super.onCreate(savedInstanceState)
        checkPermissions()
        notifyDB = Room.databaseBuilder(applicationContext, NotifyDB::class.java,"notifyDB").build()

        setContent {
            MyApplicationTheme {
                Surface(color = MaterialTheme.colors.background) {
                    //ShowNovelList(vmn, vmt)
                    NavigationGraph()
                }
            }
        }
    }

}

enum class NAVROUTE(val routeName: String, val description: String){
    MAIN("main","메인"),
    LOGIN("login","로그인"),
    REGISTER("register","회원가입"),
    PROFILE("profile","회원정보"),
    BOARD("board","자유게시판 목록"),
    BOARDDETAIL("boardDetail","자유게시판"),
    WRITINGBOARD("writingBoard","자유게시판 글쓰기"),
    WRITINGNOVELCOVER("writingNovelCover","소설 커버 작성"),
    NOVELCOVERLIST("novelCoverList","소설 커버 목록"),
    NOVELDETAILSLIST("novelDetailsList","소설 세부 목록"),
    NOVELDETAIL("novelDetail","소설 보기"),
    WRITINGNOVELDETAIL("writingNovelDetail","소설 쓰기"),
    SEARCH("search","검색"),
    NOTIFICATION("notification","알림")
}

// 네비게이션 라우트 액션
class RouteAction(navHostController: NavHostController){
    // 특정 화면으로 이동
    val navTo: (NAVROUTE) -> Unit = {route ->
        navHostController.navigate(route.routeName)
    }
    // 특정 화수, 소설로 이동
    val navWithNum: (String) -> Unit = {route ->
        navHostController.navigate(route)
    }
    // 뒤로 가기
    val goBack: () -> Unit = {
        navHostController.popBackStack()
    }
}

@Composable
fun NavigationGraph(starting: String = NAVROUTE.MAIN.routeName){
    // 내비게이션 컨트롤러
    val navController = rememberNavController()

    // 내비게이션 라우트 액션
    val routeAction = remember(navController) { RouteAction(navController) }
    val mainViewModel : MainActivityViewModel = viewModel()
    val novelViewModel : NovelViewModel = viewModel()
    val boardViewModel : FreeBoardViewModel = viewModel()

    // NavHost로 내비게이션 결정
    // 내비게이션 연결 설정
    NavHost(navController, starting) {
        composable(NAVROUTE.MAIN.routeName){
            ShowNovelList(routeAction,mainViewModel)
        }
        composable(NAVROUTE.LOGIN.routeName){
            Login(routeAction)
        }
        composable(NAVROUTE.REGISTER.routeName){
            Register(routeAction)
        }
        composable(NAVROUTE.PROFILE.routeName){
            ProfileView(routeAction)
        }
        composable(NAVROUTE.BOARD.routeName){
            ShowFreeBoardList(boardViewModel, routeAction)
        }
        composable(NAVROUTE.BOARDDETAIL.routeName+"/{num}"){ nav ->
            val num = nav.arguments?.getString("num")!!.toInt()
            ShowBoard(boardViewModel, routeAction, num)
        }
        composable(NAVROUTE.WRITINGBOARD.routeName){
            WritingBoard(boardViewModel, routeAction)
        }
        composable(NAVROUTE.WRITINGNOVELCOVER.routeName){
            WritingNCover(novelViewModel, routeAction)
        }
        composable(NAVROUTE.NOVELCOVERLIST.routeName){
            NovelCovers(routeAction, novelViewModel)
        }
        composable(NAVROUTE.NOVELDETAILSLIST.routeName+"/{num}"){ nav ->
            val num = nav.arguments?.getString("num")!!.toInt()
            ShowPostList(routeAction, num)
        }
        composable(NAVROUTE.NOVELDETAIL.routeName+"?nNum={nNum}&bNum={bNum}",
            arguments = listOf(
                navArgument("nNum"){
                    defaultValue = "0"
                    type = NavType.StringType
                }, navArgument("bNum"){
                    defaultValue = "0"
                    type = NavType.StringType
                }
            )){ nav ->
            val nNum = nav.arguments?.getString("nNum")!!.toInt()
            val bNum = nav.arguments?.getString("bNum")!!.toInt()
            NovelDetailView(nNum, bNum, routeAction)
        }
        composable(NAVROUTE.WRITINGNOVELDETAIL.routeName+"/{num}"){nav ->
            val num = nav.arguments?.getString("num")!!.toInt()
            WritingNovelDetail(num = num, routeAction = routeAction)
        }
        composable(NAVROUTE.SEARCH.routeName){
            SearchView(routeAction)
        }
        composable(NAVROUTE.NOTIFICATION.routeName){
            NotificationsView(routeAction)
        }
    }
}

@Composable
fun ShowNovelList(routeAction: RouteAction,viewModel: MainActivityViewModel) {
    println("ShowNovelList")
    val context = LocalContext.current
    val repository = ProtoRepository(context = context)
    fun read(): AccountInfo {
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()

        }
        return accountInfo
    }
    LaunchedEffect(true){
        viewModel.updateNovels()
    }
    val novels = viewModel.n.collectAsState()
    val tags = viewModel.t.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = { TopMenu(scaffoldState, scope, routeAction) },
        scaffoldState = scaffoldState,
        drawerContent = { Drawer(routeAction,scaffoldState) },
        drawerGesturesEnabled = true
    ) {
        BackHandler {
            if (scaffoldState.drawerState.isClosed) (context as Activity).finish()
            else {
                scope.launch {
                    scaffoldState.drawerState.apply {
                        close()
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxHeight()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberImagePainter(""), contentDescription = "", modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clickable(onClick = {
                            val ac = read()
                            println("토큰 : ${ac.authorization}")
                            println("리프레시 : ${ac.refreshToken}")
                            println("아이콘 : ${ac.memIcon}")
                            getToken()
                        })
                )
                Spacer(modifier = Modifier.height(8.0.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("실시간 랭킹", fontSize = 32.sp, modifier = Modifier.padding(4.0.dp))
                        Text("좋아요 순", fontSize = 18.sp, modifier = Modifier.padding(4.0.dp))
                    }
                    Text(
                        text = "더보기 ", fontSize = 14.sp, modifier = Modifier
                            .clickable(onClick = {
                                routeAction.navTo(NAVROUTE.NOVELCOVERLIST)
                            })
                            .padding(4.0.dp)
                    )

                }
                LazyColumn {
                    itemsIndexed(novels.value) { index, novel ->
                        Spacer(modifier = Modifier.padding(8.dp))
                        NovelCoverListItem(novel, tags.value[index],routeAction)
                    }

                }
            }
        }
    }


}

@Composable
fun TopMenu(scaffoldState: ScaffoldState, scope: CoroutineScope, routeAction: RouteAction) {
    println("TopMenu")
    Row(
        horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
            .fillMaxWidth()
            .background(gray)
    ) {
        IconButton(onClick = {
            scope.launch {
                scaffoldState.drawerState.apply {
                    if (isClosed) open() else close()
                }
            }
        }) {
            Icon(
                Icons.Default.Menu,
                contentDescription = null
            )
        }
        Row {
            IconButton(onClick = {
                routeAction.navTo(NAVROUTE.NOTIFICATION)
            }) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null
                )
            }
            IconButton(onClick = {
                routeAction.navTo(NAVROUTE.SEARCH)
            }) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun NovelCoverListItem(novel: Novels.Content, tag: List<String>, routeAction: RouteAction) {
    var t = ""
    if(tag.isNotEmpty()){
        t+=tag[0]
        for(i in 1 until tag.size){
            t+=", "
            t+=tag[i]
        }
    }

    val context = LocalContext.current
    Row(
        verticalAlignment = CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                routeAction.navWithNum(NAVROUTE.NOVELDETAILSLIST.routeName + "/${novel.nvcId}")
            })
    ) {
        Text("-", modifier = Modifier.padding(16.dp), fontSize = 24.sp)
        if(novel.imgUrl=="1"||novel.imgUrl=="23"){
            Image(
                painter = painterResource(R.drawable.schumi), contentDescription = "schumi",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RectangleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondary, RectangleShape),

                )
        }
        else {
            Image(
                painter = rememberImagePainter(novel.imgUrl), contentDescription = "schumi",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RectangleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondary, RectangleShape),

                )
        }
        Spacer(modifier = Modifier.width(16.0.dp))
        Column {
            Text(novel.nvcTitle)
            Text(novel.nvcId.toString())
            Spacer(modifier = Modifier.height(4.0.dp))
            Text("태그 : $t")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview3() {
    MyApplicationTheme {

    }
}
fun getToken(){
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task->
        if(!task.isSuccessful){
            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }

        val token = task.result
        println(token)
    })
}