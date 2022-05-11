package com.e.myapplication.board

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class FreeBoardViewModel : ViewModel() {
    //게시글 목록
    private val _boards = MutableStateFlow(emptyList<BoardList.BoardListItem>())
    val boards = _boards.asStateFlow()

    //게시글
    private val _board = MutableStateFlow(Boards(Boards.Board(), Boards.User()))
    val board = _board.asStateFlow()

    //댓글
    private val _comments = MutableStateFlow(mutableListOf<Comment>())
    val comments = _comments.asStateFlow()

    //ShowFreeBoard
    var comment by mutableStateOf("")
    var comment2 by mutableStateOf("")
    var reportContent by mutableStateOf("")
    var currentCommentPosition by mutableStateOf(-1)
    var scrollState by mutableStateOf(LazyListState())
    var commentPage by mutableStateOf(1)

    //WritingBoard
    var title by mutableStateOf("")
    var content by mutableStateOf("")
    var imageUri = mutableStateListOf<Uri?>()
    var bitmap = mutableStateListOf<Bitmap?>()
    var files = mutableStateListOf<File?>()
    var body = mutableStateListOf<MultipartBody.Part?>()
    var imageNum by mutableStateOf("1")
    var backVisibility by mutableStateOf(false)

    //계정 정보 읽어오기
    fun read(): AccountInfo {
        val context = MyApplication.ApplicationContext()
        val repository = ProtoRepository(context)
        var accountInfo: AccountInfo
        runBlocking(Dispatchers.IO) {
            accountInfo = repository.readAccountInfo()
        }
        return accountInfo
    }

    //게시글 목록 받아오기
    fun updateBoardList(){
        val retrofitClass = RetrofitClass.api.getBoards(1)
        retrofitClass.enqueue(object : retrofit2.Callback<BoardList>{
            override fun onResponse(call: Call<BoardList>, response: Response<BoardList>) {
                _boards.value = response.body()!!.boards
            }
            override fun onFailure(call: Call<BoardList>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    //게시글 받아오기
    fun updateBoard(num : Int){
        val retrofitClass = RetrofitClass.api.getBoard(num)
        retrofitClass.enqueue(object : retrofit2.Callback<Boards>{
            override fun onResponse(call: Call<Boards>, response: Response<Boards>) {
                _board.value = response.body()!!
            }
            override fun onFailure(call: Call<Boards>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    //댓글 받아오기
    fun updateComments(num : Int, page : Int){
        if(_comments.value.isNotEmpty()&&page==1){
            _comments.value.removeAll(_comments.value)
        }
        val retrofitClass = RetrofitClass.api.getComments(num,page)
        retrofitClass.enqueue(object : retrofit2.Callback<Comments>{
            override fun onResponse(call: Call<Comments>, response: Response<Comments>) {
                val r = response.body()
                if (r?.pagenum != 0) {
                    for (i: Int in 0 until response.body()?.comments?.size!!) {
                        let { _comments.value.addAll(response.body()?.comments!![i]) }
                    }
                    if(r?.pagenum!! >page){
                        updateComments(num,page+1)
                    }
                }
            }
            override fun onFailure(call: Call<Comments>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    //댓글 쓰기
    fun sendComment(content: String, replyNum : String,num: Int){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.writeComment(ac.authorization,
            PostComments(content,replyNum,"0",ac.memNick),num)
        retrofitClass.enqueue(object : retrofit2.Callback<PostBoardResponse>{
            override fun onResponse(
                call: Call<PostBoardResponse>,
                response: Response<PostBoardResponse>
            ) {
                val result = response.body()?.msg
                println(result)
                if (result == "OK") {
                    updateComments(num,1)
                } else {
                    getAToken(context)
                    retrofitClass.cancel()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            sendComment(content, replyNum, num)
                        }, 1000
                    )

                }
            }

            override fun onFailure(call: Call<PostBoardResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    //좋아요 누르기
    fun likeClick(num: Int){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.likeBoard(ac.authorization, num)
        retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
            override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
                when (response.body()!!.msg) {
                    "OK" -> {
                        Toast.makeText(
                            context,
                            "추천을 누르셨습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    "reduplication" -> {
                        Toast.makeText(
                            context,
                            "이미 추천을 누르셨습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        getAToken(context)
                        retrofitClass.cancel()
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                likeClick(num)
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

    //싫어요 누르기
    fun dislikeClick(num: Int){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.dislikeBoard(ac.authorization, num)
        retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
            override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
                when (response.body()!!.msg) {
                    "OK" -> {
                        Toast.makeText(
                            context,
                            "비추천을 누르셨습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    "reduplication" -> {
                        Toast.makeText(
                            context,
                            "이미 비추천을 누르셨습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        getAToken(context)
                        retrofitClass.cancel()
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                dislikeClick(num)
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

    fun reportingBoard(num: Int, reportState: ReportState ,content : String){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.reportBoard(
            ac.authorization,
            num,
            ReportMethod(reportState.sendState, content)
        )
        retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
            override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
                when (response.body()!!.msg) {
                    "OK" -> {
                        Toast.makeText(
                            context,
                            "신고가 완료되었습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    "reduplication" -> {
                        Toast.makeText(
                            context,
                            "이미 신고한 게시물입니다.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        getAToken(context)
                        retrofitClass.cancel()
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                reportingBoard(num, reportState, content)
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

    //이미지 업로드
    fun uploadImage(){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.uploadImageTest(ac.authorization.toString(), body)
        retrofitClass.enqueue(object : retrofit2.Callback<ImageUpload> {
            override fun onResponse(
                call: Call<ImageUpload>,
                response: Response<ImageUpload>
            ) {
                imageNum = response.body()?.msg.toString()
                println(imageNum)
                if (imageNum == "JWT expiration") {
                    getAToken(context)
                    retrofitClass.cancel()
                    Handler(Looper.getMainLooper()).postDelayed({ uploadImage() }, 1000)
                }
                else {
                    Toast.makeText(
                        context,
                        "이미지가 업로드 되었습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                println("이미지 : " + response.body()!!.msg)
                println(imageNum)
            }

            override fun onFailure(call: Call<ImageUpload>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    //글쓰기
    fun writeBoard(i : String,routeAction: RouteAction){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val writeBoard = RetrofitClass
        val service = writeBoard.api
        val wb = service.writeBoard(
            ac.authorization.toString(),
            PostBoard(content.replace("\n","<br>"),"",i,title,ac.memNick,imageNum,"0")
        )

        wb.enqueue(object : retrofit2.Callback<PostBoardResponse> {
            override fun onResponse(
                call: Call<PostBoardResponse>,
                response: Response<PostBoardResponse>
            ) {
                val r = response.body()?.msg
                println(response.body().toString())
                println(r)
                when (r) {
                    "JWT expiration" -> {
                        println("토큰 만료")
                        getAToken(context)
                        wb.cancel()
                        Handler(Looper.getMainLooper()).postDelayed({ writeBoard(i, routeAction) }, 1000)
                    }
                    "OK" -> {
                        println("글쓰기 성공")
                        backPressed(routeAction)
                    }
                    else -> {
                        println("글쓰기 오류")
                    }
                }
                wb.cancel()
            }

            override fun onFailure(call: Call<PostBoardResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    //뒤로가기
    fun backPressed(routeAction: RouteAction){
        backVisibility=false
        content=""
        title=""
        imageNum="1"
        imageUri.clear()
        bitmap.clear()
        files.clear()
        body.clear()
        routeAction.goBack()
    }
}