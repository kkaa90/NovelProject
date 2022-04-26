package com.e.myapplication.retrofit

import com.e.myapplication.dataclass.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UserApi {
    // 로그인 및 회원가입
    @Headers("Content-Type: application/json")
    @POST("/login")
    fun getUser(
        @Body body: GetBody
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @PUT("/users/token")
    fun sendToken(
        @Header("Authorization") authorization: String?,
        @Body body : Token
    ): Call<CallMethod>

    @Headers("Content-Type: application/json")
    @POST("/join")
    fun register(
        @Body body: SendBody
    ): Call<PostBody>


    @Headers("Content-Type: application/json")
    @PUT("/users")
    fun changeProfile(
        @Body body: SendBody
    ): Call<PostBody>

    //포인트
    @Headers("Content-Type: application/json")
    @GET("/users/point")
    fun getPoint(
        @Header("Authorization") authorization: String?
    ): Call<Point>

    // 자유게시판 글쓰기 및 불러오기
    @Headers("Content-Type: application/json")
    @POST("/boards")
    fun writeBoard(
        @Header("Authorization") authorization: String?,
        @Body body: PostBoard
    ): Call<PostBoardResponse>

    @GET("/boards")
    fun getBoards(
        @Query("page") page: Int
    ): Call<Boards>

    @GET("/boards/{num}")
    fun getBoard(
        @Path("num") num: Int
    ): Call<Board>

    // 자유게시판 댓글 쓰기 및 불러오기
    @Headers("Content-Type: application/json")
    @POST("/boards/{num}/cmts")
    fun writeComment(
        @Header("Authorization") authorization: String?,
        @Body body: PostComments,
        @Path("num") num: Int
    ): Call<PostBoardResponse>

    @GET("/boards/{num}/cmts")
    fun getComment(
        @Path("num") num: Int
    ): Call<Comments>

    @GET("/boards/{num}/cmts")
    fun getComments(
        @Path("num") num: Int,
        @Query("page") page: Int
    ): Call<Comments>

    //이미지 업로드 및 주소받기
    @Multipart
    @POST("/upload")
    fun uploadImage(
        @Header("Authorization") authorization: String?,
        @Part images: MultipartBody.Part?
    ): Call<ImageUpload>

    @GET("/imgs/{num}")
    fun getImageUrl(
        @Path("num") num: Int
    ): Call<ImageUrl>

    //소설 커버 목록 받기 및 쓰기
    @GET("/novels")
    fun getNovels(
        @Query("sort") sort : String
    ): Call<Novels>

    @Headers("Content-Type: application/json")
    @POST("/novels")
    fun writeNCover(
        @Header("Authorization") authorization: String?,
        @Body body: SendNCover
    ): Call<SNCR>

    //각 소설 목록받기
    @GET("/novels/{num}")
    fun getNovelList(
        @Path("num") num: Int
    ): Call<NovelsInfo>

    //소설 게시글 보기 및 작성
    @GET("/novels/detail/{num}")
    fun getNovel(
        @Header("Authorization") authorization: String?,
        @Path("num") num: Int,
        @Query("nv-id") nv_id : Int
    ) : Call<NovelsDetail>

    @POST("/novels/detail/{num}")
    fun writeNovel(
        @Header("Authorization") authorization: String?,
        @Path("num") num: Int,
        @Body body: PostNovelsDetail
    ) : Call<SNCR>

    //각화 리뷰
    @Headers("Content-Type: application/json")
    @POST("/novels/review/{num}")
    fun sendReview(
        @Header("Authorization") authorization: String?,
        @Path("num") num: Int,
        @Body body: ReviewBody
    ): Call<CallMethod>

    //소설 구독
    @POST("/nvc")
    fun subscribe(
        @Header("Authorization") authorization: String?,
        @Body body : Nvc
    ) : Call<nvcr>

    @POST("/oauth2/authorization/google")
    fun test(
        @Body body : GoogleSignInAccount
    ) : Call<ResponseBody>

    @Headers("Accept: text/html")
    @GET("/oauth2/authorization/google")
    fun test2(

    ) : Call<ResponseBody>

    //검색
    @GET("/novels/search")
    fun searchNovel(
        @Query("keyword") keyword : String,
    ) : Call<Novels>

    @GET("/boards/search")
    fun searchBoard(
        @Query("srctype") srctype : String,
        @Query("keyword") keyword: String
    ) : Call<Boards>


    //좋아요 싫어요
    @POST("/boards/{num}/like")
    fun likeBoard(
        @Header("Authorization") authorization: String?,
        @Path("num") num : Int
    ) : Call<CallMethod>

    @POST("/boards/{num}/dislike")
    fun dislikeBoard(
        @Header("Authorization") authorization: String?,
        @Path("num") num : Int
    ) : Call<CallMethod>
}