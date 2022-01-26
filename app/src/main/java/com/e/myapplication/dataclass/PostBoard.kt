package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class PostBoard(
    @SerializedName("brd_contents")
    val brdContents: String,
    @SerializedName("brd_file")
    val brdFile: String,
    @SerializedName("brd_img")
    val brdImg: String,
    @SerializedName("brd_title")
    val brdTitle: String,
    @SerializedName("mem_id")
    val memId: String,
    @SerializedName("mem_nickname")
    val memNickname: String
)

data class PostBoardResponse(
    @SerializedName("msg") val msg : String
)