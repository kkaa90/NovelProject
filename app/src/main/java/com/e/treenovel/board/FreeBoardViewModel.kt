package com.e.treenovel.board

import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.e.treenovel.AccountInfo
import com.e.treenovel.MyApplication
import com.e.treenovel.RouteAction
import com.e.treenovel.dataclass.*
import com.e.treenovel.retrofit.RetrofitClass
import com.e.treenovel.user.ProtoRepository
import com.e.treenovel.user.getAToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class FreeBoardViewModel : ViewModel() {
    //게시글 목록

    private val _boards = MutableStateFlow(mutableListOf<BoardList.BoardListItem>())
    val boards = _boards.asStateFlow()

    //게시글
    private val _board = MutableStateFlow(Boards(Boards.Board(), Boards.User()))
    val board = _board.asStateFlow()

    //댓글
    private val _comments = MutableStateFlow(mutableListOf<Comment>())
    val comments = _comments.asStateFlow()
    //대댓글
    private val _replys = MutableStateFlow(mutableMapOf<Int, MutableList<Comment>>())
    val replys = _replys.asStateFlow()

    var p by mutableStateOf(1)
    var pageNum by mutableStateOf(1)
    var check by mutableStateOf(false)

    //ShowFreeBoard
    var comment by mutableStateOf("")
    var comment2 by mutableStateOf("")
    var reportContent by mutableStateOf("")
    var currentCommentPosition by mutableStateOf(-1)
    var scrollState by mutableStateOf(LazyListState())
    var commentPage by mutableStateOf(1)
    var reportComment by mutableStateOf(0)
    var boardNum by mutableStateOf(0)
    var progress by mutableStateOf(false)

    //WritingBoard and editBoard
    var title by mutableStateOf("")
    var content by mutableStateOf("")
    var imageUri = mutableStateListOf<Uri?>()
    var bitmap = mutableStateListOf<Bitmap?>()
    var files = mutableStateListOf<File?>()
    var body = mutableStateListOf<MultipartBody.Part?>()
    var imageNum by mutableStateOf("1")
    var backVisibility by mutableStateOf(false)
    var woe by mutableStateOf(false)

    var a by mutableStateOf(read())

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
        if(p==1){
            _boards.value.clear()
        }
        val retrofitClass = RetrofitClass.api.getBoards(p)
        retrofitClass.enqueue(object : retrofit2.Callback<BoardList>{
            override fun onResponse(call: Call<BoardList>, response: Response<BoardList>) {
                _boards.value.addAll(response.body()!!.boards)
                pageNum=response.body()!!.pagenum
                progress = false
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
                check = false
            }
            override fun onFailure(call: Call<Boards>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    //댓글 받아오기
    fun updateComments(num : Int, page : Int){
        if(_comments.value.isNotEmpty()&&page==1){
            _comments.value.clear()
        }
        val retrofitClass = RetrofitClass.api.getComments(num,page)
        retrofitClass.enqueue(object : retrofit2.Callback<Comments>{
            override fun onResponse(call: Call<Comments>, response: Response<Comments>) {
                val r = response.body()
                if (r?.pagenum != 0) {

                    for (i: Int in 0 until r?.comments?.size!!) {
                        if(r.comments[i].size==1){
                            _comments.value.add(r.comments[i][0])
                        }
                        else {
                            _comments.value.add(r.comments[i][0])
                            val temp = mutableListOf<Comment>()
                            for(j : Int in 1 until r.comments[i].size){
                                temp.add(r.comments[i][j])
                            }
                            _replys.value[r.comments[i][0].brdCmtId] = temp
                        }
                    }
                    if(r.pagenum >page){
                        updateComments(num,page+1)
                    }
                    else {
                        progress=false
                    }
                }
                else {
                    progress = false
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

    //댓글 삭제
    fun deleteComment(){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.deleteComment(ac.authorization,boardNum, reportComment)
        retrofitClass.enqueue(object : retrofit2.Callback<CallMethod>{
            override fun onResponse(
                call: Call<CallMethod>,
                response: Response<CallMethod>
            ) {
                val result = response.body()?.msg
                println(result)
                if (result == "OK") {
                    updateComments(boardNum,1)
                } else {
                    getAToken(context)
                    retrofitClass.cancel()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            deleteComment()
                        }, 1000
                    )

                }
            }

            override fun onFailure(call: Call<CallMethod>, t: Throwable) {
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
                println(response.body()!!.msg)
                when (response.body()!!.msg) {
                    "OK" -> {
                        Toast.makeText(
                            context,
                            "추천을 누르셨습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                        updateBoard(num)
                    }
                    "cancel" -> {
                        Toast.makeText(
                            context,
                            "추천이 취소되었습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                        updateBoard(num)
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
                        updateBoard(num)
                    }
                    "cancel" -> {
                        Toast.makeText(
                            context,
                            "비추천이 취소되었습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                        updateBoard(num)
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

    fun reportingBoard(num: Int, reportState: ReportState){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.reportBoard(
            ac.authorization,
            num,
            ReportMethod(reportState.sendState, reportContent)
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
                                reportingBoard(num, reportState)
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

    //좋아요 누르기
    fun likeComment(bNum: Int,cNum: Int){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.likeBoardComment(ac.authorization, bNum, cNum)
        retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
            override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
                when (response.body()!!.msg) {
                    "OK" -> {
                        Toast.makeText(
                            context,
                            "추천을 누르셨습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                        updateComments(boardNum,1)
                    }
                    "cancel" -> {
                        Toast.makeText(
                            context,
                            "추천이 취소되었습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                        updateComments(boardNum,1)
                    }
                    else -> {
                        getAToken(context)
                        retrofitClass.cancel()
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                likeComment(bNum, cNum)
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
    fun dislikeComment(bNum: Int, cNum: Int){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.dislikeBoardComment(ac.authorization, bNum,cNum)
        retrofitClass.enqueue(object : retrofit2.Callback<CallMethod> {
            override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
                when (response.body()!!.msg) {
                    "OK" -> {
                        Toast.makeText(
                            context,
                            "비추천을 누르셨습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                        updateComments(boardNum,1)
                    }
                    "cancel" -> {
                        Toast.makeText(
                            context,
                            "비추천이 취소되었습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                        updateComments(boardNum,1)
                    }
                    else -> {
                        getAToken(context)
                        retrofitClass.cancel()
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                dislikeComment(bNum, cNum)
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


    //댓글 신고
    fun reportingComment(bNum: Int, reportState: ReportState){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.reportBoardComment(
            ac.authorization,
            bNum,
            reportComment,
            ReportMethod(reportState.sendState, reportContent)
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
                                reportingComment(bNum, reportState)
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
        val wb = RetrofitClass.api.writeBoard(
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

    fun editing(num: Int){
        woe=true
        boardNum=num
        content = Html.fromHtml(board.value.board.brdContents).toString()
            .replace("<br>", "\n")
        title = board.value.board.brdTitle
    }

    fun editBoard(i: String,routeAction: RouteAction){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val eb = RetrofitClass.api.editBoard(
            ac.authorization.toString(),
            boardNum,
            PostBoard(content.replace("\n","<br>"),"",if(imageNum=="1") "0" else "1",title,ac.memNick,imageNum,"0")
        )
        eb.enqueue(object : retrofit2.Callback<CallMethod>{
            override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
                val r = response.body()?.msg
                println(response.body().toString())
                println(r)
                when (r) {
                    "JWT expiration" -> {
                        println("토큰 만료")
                        getAToken(context)
                        eb.cancel()
                        Handler(Looper.getMainLooper()).postDelayed({ editBoard(i, routeAction) }, 1000)
                    }
                    "OK" -> {
                        println("글쓰기 성공")
                        backPressed2(routeAction)
                    }
                    else -> {
                        println("글쓰기 오류")
                    }
                }
                eb.cancel()
            }

            override fun onFailure(call: Call<CallMethod>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
    fun deleteBoard(routeAction: RouteAction){
        val context = MyApplication.ApplicationContext()
        val ac = read()
        val retrofitClass = RetrofitClass.api.deleteBoard(ac.authorization,boardNum)
        retrofitClass.enqueue(object : retrofit2.Callback<CallMethod>{
            override fun onResponse(call: Call<CallMethod>, response: Response<CallMethod>) {
                val r = response.body()?.msg
                println(response.body().toString())
                println(r)
                when (r) {
                    "JWT expiration" -> {
                        println("토큰 만료")
                        getAToken(context)
                        retrofitClass.cancel()
                        Handler(Looper.getMainLooper()).postDelayed({ deleteBoard(routeAction) }, 1000)
                    }
                    "OK" -> {
                        println("글쓰기 성공")
                        backPressed(routeAction)
                    }
                    else -> {
                        println("글쓰기 오류")
                    }
                }
            }

            override fun onFailure(call: Call<CallMethod>, t: Throwable) {
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
        woe=false
        boardNum=0
        imageUri.clear()
        bitmap.clear()
        files.clear()
        body.clear()
        routeAction.goBack()
    }
    //뒤로가기
    fun backPressed2(routeAction: RouteAction){
        backVisibility=false
        content=""
        title=""
        woe=false
        boardNum=0
        imageUri.clear()
        bitmap.clear()
        files.clear()
        body.clear()
        routeAction.goBack()
    }

    fun getBSearch(srcType: String, keyword: String) {
        _boards.value.clear()
        val t = if (srcType == "제목") "title" else if (srcType == "글쓴이") "" else ""
        val retrofitClass = RetrofitClass.api.searchBoard(t, keyword)
        retrofitClass.enqueue(object : retrofit2.Callback<BoardList> {
            override fun onResponse(call: Call<BoardList>, response: Response<BoardList>) {
                val r = response.body()!!.boards
//                list.addAll(r)
                println(r)
                _boards.value.addAll(r)
                progress = false
            }
            override fun onFailure(call: Call<BoardList>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}