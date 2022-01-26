package com.e.myapplication.retrofit

import com.e.myapplication.dataclass.*
import retrofit2.Call
import retrofit2.http.*

interface UserApi {
    @Headers("Content-Type: application/json")
    @POST("/login")
    fun getUser(
        @Body body : GetBody
    ): Call<User>


    @Headers("Content-Type: application/json")
    @POST("/join")
    fun register(
        @Body body : SendBody
    ): Call<PostBody>

    @Headers("Content-Type: application/json")
    @POST("/boards")
    fun writeBoard(
        @Header("Authorization") authorization : String?,
        @Body body : PostBoard
    ): Call<PostBoardResponse>

    @GET("/boards")
    fun getBoards(
        @Query("page") page : Int
    ): Call<Boards>

    @GET("/boards/{num}")
    fun getBoard(
        @Path("num") num : Int
    ): Call<Board>

    @Headers("Content-Type: application/json")
    @POST("/boards/{num}/cmts")
    fun writeComment(
        @Header("Authorization") authorization : String?,
        @Body body : PostComments,
        @Path("num") num : Int
    ): Call<PostBoardResponse>

    @GET("/boards/{num}/cmts")
    fun getComment(
        @Path("num") num : Int
    ): Call<Comments>

    @GET("/boards/{num}/cmts")
    fun getComments(
        @Path("num") num : Int,
        @Query("page") page : Int
    ): Call<Comments>

}