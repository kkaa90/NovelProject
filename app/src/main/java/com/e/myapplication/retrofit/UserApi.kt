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
        @Body body : Boards
    ): Call<Int>

    @GET("/boards")
    fun getBoards(
        @Query("page") page : Int
    ): Call<List<Boards>>


}