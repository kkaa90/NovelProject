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
    private val _d = MutableStateFlow(emptyList<NovelsInfo.NovelInfo>())
    val d = _d.asStateFlow()
    //소설 커버
    private val _c = MutableStateFlow(NovelsInfo.NovelCover())
    val c = _c.asStateFlow()


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


    //소설 게시물 작성
    var nDTitle by mutableStateOf("")
    var nDContent by mutableStateOf("")
    var nDImageUri = mutableStateListOf<Uri?>()
    var nDBitmap = mutableStateListOf<Bitmap?>()
    var nDFiles = mutableStateListOf<File?>()
    var nDBody = mutableStateListOf<MultipartBody.Part?>()
    var nDImageNum by mutableStateOf("1")
    var nDPoint by mutableStateOf("0")




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
        val gNovels = RetrofitClass.api.getNovels("nvcId,ASC")
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

}