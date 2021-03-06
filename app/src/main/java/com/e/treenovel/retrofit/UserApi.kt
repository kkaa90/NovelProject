package com.e.treenovel.retrofit

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.e.treenovel.dataclass.*
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
        @Body body: LoginBody
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @PUT("/users/token")
    fun sendToken(
        @Header("Authorization") authorization: String?,
        @Body body : Token
    ): Call<CallMethod>

    @Headers("Content-Type: application/json")
    @PUT("/users/refresh")
    fun getAccessToken(
        @Header("Authorization") authorization: String?
    ): Call<CallMethod>

    @Headers("Content-Type: application/json")
    @POST("/join")
    fun register(
        @Body body: SendBody
    ): Call<PostBody>

    //회원 탈퇴
    @DELETE("/users")
    fun deleteUser(
        @Header("Authorization") authorization: String?
    ) : Call<CallMethod>

    //회원 정보 변경
    @Headers("Content-Type: application/json")
    @PUT("/users")
    fun changeProfile(
        @Header("Authorization") authorization: String?,
        @Body body: ChangeProfile
    ): Call<CallMethod>

    @Headers("Content-Type: application/json")
    @PUT("/users/pwd")
    fun changePassword(
        @Header("Authorization") authorization: String?,
        @Body body: ChangePwd
    ): Call<CallMethod>

    //포인트
    @Headers("Content-Type: application/json")
    @GET("/users/point")
    fun getPoint(
        @Header("Authorization") authorization: String?
    ): Call<Point>

    // 자유게시판
    @Headers("Content-Type: application/json")
    @POST("/boards")
    fun writeBoard(
        @Header("Authorization") authorization: String?,
        @Body body: PostBoard
    ): Call<PostBoardResponse>

    @GET("/boards")
    fun getBoards(
        @Query("page") page: Int
    ): Call<BoardList>

    @GET("/boards/{num}")
    fun getBoard(
        @Path("num") num: Int
    ): Call<Boards>

    @PUT("/boards/{num}")
    fun editBoard(
        @Header("Authorization") authorization: String?,
        @Path("num") num: Int,
        @Body body: PostBoard
    ): Call<CallMethod>

    @DELETE("/boards/{num}")
    fun deleteBoard(
        @Header("Authorization") authorization: String?,
        @Path("num") num: Int
    ): Call<CallMethod>
    //자유 게시판 신고
    @POST("/boards/{num}/report")
    fun reportBoard(
        @Header("Authorization") authorization: String?,
        @Path("num") num: Int,
        @Body body: ReportMethod
    ): Call<CallMethod>

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

    @DELETE("/boards/{bNum}/cmts/{cNum}")
    fun deleteComment(
        @Header("Authorization") authorization: String?,
        @Path("bNum") bNum : Int,
        @Path("cNum") cNum : Int
    ): Call<CallMethod>

    //자유게시판 댓글 좋아요 싫어요
    @POST("/boards/{bNum}/cmts/{cNum}/like")
    fun likeBoardComment(
        @Header("Authorization") authorization: String?,
        @Path("bNum") bNum : Int,
        @Path("cNum") cNum : Int
    ) : Call<CallMethod>

    @POST("/boards/{bNum}/cmts/{cNum}/dislike")
    fun dislikeBoardComment(
        @Header("Authorization") authorization: String?,
        @Path("bNum") bNum : Int,
        @Path("cNum") cNum : Int
    ) : Call<CallMethod>

    //자유 게시판 댓글 신고
    @POST("/boards/{bNum}/cmts/{cNum}/report")
    fun reportBoardComment(
        @Header("Authorization") authorization: String?,
        @Path("bNum") bNum : Int,
        @Path("cNum") cNum : Int,
        @Body body: ReportMethod
    ): Call<CallMethod>

    //이미지 업로드 및 주소받기
    @Multipart
    @POST("/upload")
    fun uploadImage(
        @Header("Authorization") authorization: String?,
        @Part images: MultipartBody.Part?
    ): Call<ImageUploadSingle>

    @Multipart
    @POST("/upload")
    fun uploadImageTest(
        @Header("Authorization") authorization: String?,
        @Part images: SnapshotStateList<MultipartBody.Part?>
    ): Call<ImageUpload>

    @GET("/imgs/{num}")
    fun getImageUrl(
        @Path("num") num: Int
    ): Call<ImageUrl>

    //소설 커버 목록 받기 및 쓰기
    @GET("/novels")
    fun getNovels(
        @Query("sort") sort : String,
        @Query("page") page : Int
    ): Call<Novels>

    @POST("/novels/{num}/reportcover")
    fun reportNovelCover(
        @Header("Authorization") authorization: String?,
        @Path("num") num: Int,
        @Body body: ReportMethod
    ): Call<CallMethod>

    @GET("/novels")
    fun getNovelsWithToken(
        @Header("Authorization") authorization: String?,
        @Query("sort") sort : String
    ): Call<NovelsWithToken>

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

    @PUT("/novels/detail/{nNum}")
    fun editNovel(
        @Header("Authorization") authorization: String?,
        @Path("nNum") num: Int,
        @Query("nv-id") nv_id: Int,
        @Body body : EditingNovelDetail
    ) : Call<CallMethod>


    @DELETE("/novels/detail/{nNum}")
    fun deleteNovel(
        @Header("Authorization") authorization: String?,
        @Path("nNum") nNum: Int,
        @Query("nv-id") nv_id: Int
    ): Call<CallMethod>

    //소설 신고
    @POST("/novels/{num}/report")
    fun reportNovel(
        @Header("Authorization") authorization: String?,
        @Path("num") num: Int,
        @Body body: ReportMethod
    ): Call<CallMethod>

    //소설 댓글
    @POST("/novels/detail/{num}/cmts")
    fun sendNComment(
        @Header("Authorization") authorization: String?,
        @Path("num") num: Int,
        @Body body: PostNvComments
    ) : Call<CallMethod>

    @GET("/novels/detail/{num}/cmts")
    fun getNComment(
        @Path("num") num : Int,
        @Query("nv-id") nv_id :Int,
        @Query("pagenum") pagenum : Int
    ) : Call<NvComments>

    @DELETE("/novels/detail/{nNum}/cmts")
    fun deleteNComment(
        @Header("Authorization") authorization: String?,
        @Path("nNum") nNum: Int,
        @Query("nv-cmt-id") nv_cmt_id : Int
    ): Call<CallMethod>

    //소설 댓글 좋아요 싫어요
    @POST("/novels/{nNum}/cmts/{cNum}/like")
    fun likeNovelComment(
        @Header("Authorization") authorization: String?,
        @Path("nNum") nNum : Int,
        @Path("cNum") cNum : Int
    ) : Call<CallMethod>

    @POST("/novels/{nNum}/cmts/{cNum}/dislike")
    fun dislikeNovelComment(
        @Header("Authorization") authorization: String?,
        @Path("nNum") nNum : Int,
        @Path("cNum") cNum : Int
    ) : Call<CallMethod>

    //소설 댓글 신고
    @POST("/novels/{nNum}/cmts/{cNum}/report")
    fun reportNovelComment(
        @Header("Authorization") authorization: String?,
        @Path("nNum") nNum : Int,
        @Path("cNum") cNum : Int,
        @Body body: ReportMethod
    ): Call<CallMethod>

    //각화 리뷰
    @Headers("Content-Type: application/json")
    @POST("/novels/{num}/review")
    fun sendReview(
        @Header("Authorization") authorization: String?,
        @Path("num") num: Int,
        @Body body: ReviewBody
    ): Call<CallMethod>

    //소설 구독
    @GET("/nvc")
    fun getSub(
        @Header("Authorization") authorization: String?,
    ): Call<NvcList>

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
    ) : Call<BoardList>

    //쪽지
    @GET("/messages/receive")
    fun getReceiveMessages(
        @Header("Authorization") authorization: String?,
    ) : Call<Message>

    @GET("/messages/send")
    fun getSendMessages(
        @Header("Authorization") authorization: String?,
    ) : Call<Message>

    @GET("/messages/{num}")
    fun getMessage(
        @Header("Authorization") authorization: String?,
        @Path("num") num: Int
    ) : Call<SingleMessage>
    @POST("/messages")
    fun sendMessage(
        @Header("Authorization") authorization: String?,
        @Body body : MessagePost
    ) : Call<CallMethod>
    @DELETE("messages/{num}")
    fun deleteMessage(
        @Header("Authorization") authorization: String?,
        @Path("num") num: Int
    ) : Call<CallMethod>

    //열람 목록
    @GET("/users/purchase/")
    fun getPurchaseCover(
        @Header("Authorization") authorization: String?
    ) : Call<PurchaseCover>
    @GET("/users/purchase/{num}")
    fun getPurchase(
        @Header("Authorization") authorization: String?,
        @Path("num") num: Int
    ) : Call<PurchaseNovel>
}