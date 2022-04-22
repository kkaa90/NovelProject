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