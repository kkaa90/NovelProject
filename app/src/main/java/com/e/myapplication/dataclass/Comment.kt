package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("brdCmtBlame")
    val brdCmtBlame: Int,
    @SerializedName("brdCmtContents")
    val brdCmtContents: String,
    @SerializedName("brdCmtDatetime")
    val brdCmtDatetime: String,
    @SerializedName("brdCmtDislike")
    val brdCmtDislike: Int,
    @SerializedName("brdCmtId")
    val brdCmtId: Int,
    @SerializedName("brdCmtLike")
    val brdCmtLike: Int,
    @SerializedName("brdCmtReply")
    val brdCmtReply: Int,
    @SerializedName("brdCmtReplynum")
    val brdCmtReplynum: Int,
    @SerializedName("brdCmtState")
    val brdCmtState: Int,
    @SerializedName("brdCmtUpdatetime")
    val brdCmtUpdatetime: Any,
    @SerializedName("brdId")
    val brdId: Int,
    @SerializedName("memId")
    val memId: Int,
    @SerializedName("memNickname")
    val memNickname: String
)