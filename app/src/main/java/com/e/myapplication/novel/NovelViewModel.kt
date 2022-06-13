package com.e.myapplication.novel

import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import com.e.myapplication.AccountInfo
import com.e.myapplication.MyApplication
import com.e.myapplication.RouteAction
import com.e.myapplication.dataclass.*
import com.e.myapplication.retrofit.RetrofitClass
import com.e.myapplication.user.ProtoRepository
import com.e.myapplication.user.getAToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class NovelViewModel : ViewModel(){
    //소설 목록
    private val _n = MutableStateFlow(emptyList<Novels.Content>())
    val n = _n.asStateFlow()
    //태그 목록
    private val _t = MutableStateFlow(emptyList<List<String>>())
    val t = _t.asStateFlow()
    //소설 게시물 목록
    private val _d = MutableStateFlow(mutableListOf<NovelsInfo.NovelInfo>())
    val d = _d.asStateFlow()
    //에피소드
    private val _e = MutableStateFlow(mutableMapOf<Int, List<Int>>())
    val e = _e.asStateFlow()
    //소설 커버
    private val _c = MutableStateFlow(NovelsInfo.NovelCover())
    val c = _c.asStateFlow()
    //소설 트리
    private val _tree = MutableStateFlow(mutableMapOf<Int, List<NovelsInfo.NovelInfo>>())
    val tree = _tree.asStateFlow()
    //소설 조회순
    private val _h = MutableStateFlow(mutableListOf<NovelsInfo.NovelInfo>())
    val h = _h.asStateFlow()
    //소설 키

    var back by mutableStateOf("")

    //소설 커버 작성
    var nCTitle by mutableStateOf("")
    var nCContent by mutableStateOf("")
    var nCImageUri by mutableStateOf<Uri?>(null)
    var nCBitmap by mutableStateOf<Bitmap?>(null)
    var tag by mutableStateOf("")
    var tags = mutableStateListOf<String>()
    var nCFile by mutableStateOf<File?>(null)
    var nCBody by mutableStateOf<MultipartBody.Part?>(null)
    var nCImageNum by mutableStateOf("0")
    var nCBackVisibility by mutableStateOf(false)


    //소설 게시물 작성 및 수정
    var nDTitle by mutableStateOf("")
    var nDContent by mutableStateOf("")
    var nDImageUri = mutableStateListOf<Uri?>()
    var nDBitmap = mutableStateListOf<Bitmap?>()
    var nDFiles = mutableStateListOf<File?>()
    var nDBody = mutableStateListOf<MultipartBody.Part?>()
    var nDImageNum by mutableStateOf("1")
    var nDPoint by mutableStateOf("0")
    var parent by mutableStateOf(-1)
    var woe by mutableStateOf(false)
    var novelNum by mutableStateOf(0)

    //소설 게시물
    var detailNow by mutableStateOf(-1)
    var reportContent by mutableStateOf("")
    var reportComment by mutableStateOf(0)

    var a by mutableStateOf(read())

    //소설 커버 목록
    class NCSort(val present: String, val v: String)
    val sList = listOf(NCSort("최신 순", "nvcId"),
        NCSort("조회수 순","nvcHit"), NCSort("구독자 순","nvcSubscribeCount")
    )
    var sNow by mutableStateOf(sList[0])
    var asc by mutableStateOf("DESC")

    //계정 정보 읽기
    fun read(): AccountInfo {
        val context = MyApplication.ApplicationContext()
        val repository = ProtoRepository(context)
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()
        }
        return accountInfo
    }

    //소설 커버 목록 받아오기
    fun updateNovels(){
        val gNovels = RetrofitClass.api.getNovels("${sNow.v},$asc")
        gNovels.enqueue(object :retrofit2.Callback<Novels>{
            override fun onResponse(call: Call<Novels>, response: Response<Novels>) {
                _n.value = response.body()!!.content
                _t.value = response.body()!!.tags
            }

            override fun onFailure(call: Call<Novels>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
    //이미지 업로드
    fun uploadNCImage(){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.uploadImage(ac.authorization.toString(),nCBody)
        retrofitClass.enqueue(object : retrofit2.Callback<ImageUploadSingle>{
            override fun onResponse(
                call: Call<ImageUploadSingle>,
                response: Response<ImageUploadSingle>
            ) {
                val r= response.body()!!.msg
                if(r=="JWT expiration"){
                    getAToken(context)
                    retrofitClass.cancel()
                    Handler(Looper.getMainLooper()).postDelayed({uploadNCImage()},1000)
                }
                else {
                    nCImageNum=response.body()!!.imgUrl
                    Toast.makeText(
                        context,
                        "이미지가 업로드 되었습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ImageUploadSingle>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
    //커버 작성
    fun writeNCover(routeAction: RouteAction){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val nc = SendNCover(SendNCover.NovelCover(nCImageNum, "1", nCContent, nCTitle), tags)

        val retrofitClass = RetrofitClass.api.writeNCover(ac.authorization,nc)
        println(retrofitClass.request().toString())
        retrofitClass.enqueue(object : retrofit2.Callback<SNCR>{
            override fun onResponse(call: Call<SNCR>, response: Response<SNCR>) {
                val r = response.body()?.msg
                if(r == "JWT expiration"){
                    getAToken(context)
                    retrofitClass.cancel()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {writeNCover(routeAction)},
                        1000
                    )
                }
                else {
                    println(r)
                    Toast.makeText(
                        context,
                        "커버 작성 완료",
                        Toast.LENGTH_LONG
                    ).show()
                    nCBackPressed(routeAction)
                }
            }

            override fun onFailure(call: Call<SNCR>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
    //커버 작성 뒤로가기
    fun nCBackPressed(routeAction: RouteAction){
        nCTitle = ""
        nCContent = ""
        nCImageNum = "0"
        nCImageUri = null
        nCBitmap = null
        nCFile = null
        nCBody = null
        routeAction.goBack()
    }

    //이미지 업로드
    fun uploadNDImages(){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.uploadImageTest(ac.authorization.toString(), nDBody)
        retrofitClass.enqueue(object : retrofit2.Callback<ImageUpload> {
            override fun onResponse(
                call: Call<ImageUpload>,
                response: Response<ImageUpload>
            ) {
                nDImageNum = response.body()?.msg.toString()
                println(nDImageNum)
                if (nDImageNum == "JWT expiration") {
                    getAToken(context)
                    retrofitClass.cancel()
                    Handler(Looper.getMainLooper()).postDelayed({ uploadNDImages() }, 1000)
                }
                else {
                    Toast.makeText(
                        context,
                        "이미지가 업로드 되었습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                println("이미지 : " + response.body()!!.msg)
            }

            override fun onFailure(call: Call<ImageUpload>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    //소설 게시물 작성
    fun writeNovelDetail(routeAction: RouteAction, num : Int, parent : String){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.writeNovel(ac.authorization, num,
            PostNovelsDetail(PostNovelsDetail.Novel(nDImageNum, nDContent, "0", nDTitle,ac.memNick,nDPoint), parent)
        )

        retrofitClass.enqueue(object : retrofit2.Callback<SNCR> {
            override fun onResponse(
                call: Call<SNCR>,
                response: Response<SNCR>
            ) {
                val r = response.body()?.msg
                println(response.body().toString())
                println(r)
                when (r) {
                    "JWT expiration" -> {
                        println("토큰 만료")
                        getAToken(context)
                        retrofitClass.cancel()
                        Handler(Looper.getMainLooper()).postDelayed({ writeNovelDetail(routeAction, num, parent) }, 1000)
                    }
                    "OK" -> {
                        println("글쓰기 성공")
                        nDBackPressed(routeAction)
                    }
                    else -> {
                        println("글쓰기 오류")
                    }
                }
                retrofitClass.cancel()
            }

            override fun onFailure(call: Call<SNCR>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun nDBackPressed(routeAction: RouteAction){
        nDTitle=""
        nDContent=""
        nDImageUri.clear()
        nDBitmap.clear()
        nDFiles.clear()
        nDBody.clear()
        nDPoint="0"
        nDImageNum = "1"
        parent = -1
        woe = false
        routeAction.goBack()
    }

    fun nDBackPressed2(routeAction: RouteAction){
        nDTitle=""
        nDContent=""
        nDImageUri.clear()
        nDBitmap.clear()
        nDFiles.clear()
        nDBody.clear()
        nDPoint="0"
        woe = false
        routeAction.goBack()
    }

    fun getNovelsList(
        num: Int
    ){
        _d.value.clear()
        _e.value.clear()
        _tree.value.clear()
        _h.value.clear()
        val retrofitClass = RetrofitClass.api.getNovelList(num)
        retrofitClass.enqueue(object : retrofit2.Callback<NovelsInfo>{
            override fun onResponse(call: Call<NovelsInfo>, response: Response<NovelsInfo>) {
                val r = response.body()
                _d.value.addAll(r!!.novelInfo)
                _e.value.putAll(r.episode)
                if(_d.value.size!=0){
                    for(key in _e.value.keys){
                        val epList = mutableListOf<NovelsInfo.NovelInfo>()
                        epList.add(_d.value.find { it.nvId==key }!!)
                        val ep = _e.value[key]
                        if(ep!!.isNotEmpty()){
                            for(i in ep.indices){
                                _d.value.find { it.nvId==ep[i] }.let {
                                    epList.add(it!!)
                                }
                            }
                        }
                        _tree.value[key] = epList
                    }
                }
                _c.value = r.novelCover
                _d.value.sortBy {
                    it.nvId
                }
                sortList()
            }

            override fun onFailure(call: Call<NovelsInfo>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
    fun sortList(){
        _h.value.clear()
        val temp = mutableListOf<NovelsInfo.NovelInfo>()
        val temp2 = mutableListOf<NovelsInfo.NovelInfo>()
        if(_d.value.isNotEmpty()){
            temp2.add(_d.value.find { it.nvId== _e.value.keys.first()}!!)
            while (true){
                val ep = _e.value[temp2[temp2.size-1].nvId]!!
                if (ep.isNotEmpty()){
                    temp.clear()
                    for (i in ep.indices){
                        _d.value.find { it.nvId == ep[i] }.let {
                            temp.add(it!!)
                        }
                    }
                    temp2.add(temp.sortedByDescending { it.nvHit }[0])
                }
                else {
                    break
                }
            }
            _h.value.addAll(temp2)
        }
    }

    fun reportingNovel(num: Int, reportState: ReportState){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.reportNovel(ac.authorization,num,
            ReportMethod(reportState.sendState, reportContent)
        )
        println(retrofitClass.request().toString())
        retrofitClass.enqueue(object : retrofit2.Callback<CallMethod>{
            override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
                when (response.body()!!.msg) {
                    "OK" -> {
                        Toast.makeText(
                            context,
                            "신고가 완료되었습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                        reportContent = ""
                    }
                    "reduplication" -> {
                        Toast.makeText(
                            context,
                            "이미 신고한 게시물입니다.",
                            Toast.LENGTH_LONG
                        ).show()
                        reportContent = ""
                    }
                    else -> {
                        getAToken(context)
                        retrofitClass.cancel()
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                reportingNovel(num, reportState)
                            }, 1000
                        )
                    }
                }
            }
            override fun onFailure(call: Call<CallMethod>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun reportingNComment(num: Int, reportState: ReportState){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.reportNovelComment(ac.authorization,num,reportComment,ReportMethod(reportState.sendState,reportContent))
        println(retrofitClass.request().toString())
        retrofitClass.enqueue(object : retrofit2.Callback<CallMethod>{
            override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
                when (response.body()!!.msg) {
                    "OK" -> {
                        Toast.makeText(
                            context,
                            "신고가 완료되었습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                        reportContent = ""
                    }
                    "reduplication" -> {
                        Toast.makeText(
                            context,
                            "이미 신고한 댓글입니다.",
                            Toast.LENGTH_LONG
                        ).show()
                        reportContent = ""
                    }
                    else -> {
                        getAToken(context)
                        retrofitClass.cancel()
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                reportingNComment(num, reportState)
                            }, 1000
                        )
                    }
                }
            }

            override fun onFailure(call: Call<CallMethod>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
    fun getParent(num: Int){
        parent = if(num==c.value.nvId){
            0
        } else {
            e.value.entries.find { it.value.contains(num) }?.key!!
        }
    }

    fun editing(novel : NovelsDetail.Novel){
        woe = true
        nDTitle = novel.nvTitle
        nDContent = novel.nvContents
        nDPoint = novel.nvPoint.toString()
        novelNum = novel.nvId
    }
    fun editNovelDetail(routeAction: RouteAction){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.editNovel(ac.authorization, c.value.nvcId , novelNum, EditingNovelDetail(nDImageNum,nDContent,"0",nDTitle,ac.memNick))
        retrofitClass.enqueue(object : retrofit2.Callback<CallMethod>{
            override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
                val r =response.body()!!.msg
                when (r) {
                    "JWT expiration" -> {
                        println("토큰 만료")
                        getAToken(context)
                        retrofitClass.cancel()
                        Handler(Looper.getMainLooper()).postDelayed({ editNovelDetail(routeAction) }, 1000)
                    }
                    "OK" -> {
                        println("글쓰기 성공")
                        nDBackPressed2(routeAction)
                    }
                    else -> {
                        println("글쓰기 오류")
                    }
                }
                retrofitClass.cancel()
            }

            override fun onFailure(call: Call<CallMethod>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
}