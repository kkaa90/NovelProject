package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class PostComments(
    @SerializedName("brdCmtContents")
    val brdCmtContents: String,
    @SerializedName("brdCmtReply")
    val brdCmtReply: String,
    @SerializedName("brdCmtState")
    val brdCmtState: String,
    @SerializedName("memNickname")
    val memNickname: String
)

data class PostNvComments(
    @SerializedName("memNickname")
    val memNickname: String,
    @SerializedName("nvCmtContents")
    val nvCmtContents: String,
    @SerializedName("nvCmtReply")
    val nvCmtReply: Int,
    @SerializedName("nvCmtState")
    val nvCmtState: Int
)