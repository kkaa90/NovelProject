package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class PostComments(
    @SerializedName("brd_cmt_contents")
    val brdCmtContents: String,
    @SerializedName("brd_cmt_reply")
    val brdCmtReply: String,
    @SerializedName("brd_cmt_state")
    val brdCmtState: String,
    @SerializedName("brd_id")
    val brdId: String,
    @SerializedName("mem_id")
    val memId: String,
    @SerializedName("mem_nickname")
    val memNickname: String
)