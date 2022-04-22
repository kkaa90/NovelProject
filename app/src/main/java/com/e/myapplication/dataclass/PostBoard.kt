package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class PostBoard(
    @SerializedName("brdContents")
    val brdContents: String,
    @SerializedName("brdFile")
    val brdFile: String,
    @SerializedName("brdImg")
    val brdImg: String,
    @SerializedName("brdTitle")
    val brdTitle: String,
    @SerializedName("memNickname")
    val memNickname: String,
    @SerializedName("imgUrl")
    val imgUrl : String,
    @SerializedName("brdState")
    val brdState : String
)

data class PostBoardResponse(
    @SerializedName("msg") val msg : String
)