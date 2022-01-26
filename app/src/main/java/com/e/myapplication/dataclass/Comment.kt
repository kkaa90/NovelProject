package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("brd_cmt_blame")
    val brdCmtBlame: Int,
    @SerializedName("brd_cmt_contents")
    val brdCmtContents: String,
    @SerializedName("brd_cmt_datetime")
    val brdCmtDatetime: String,
    @SerializedName("brd_cmt_dislike")
    val brdCmtDislike: Int,
    @SerializedName("brd_cmt_id")
    val brdCmtId: Int,
    @SerializedName("brd_cmt_like")
    val brdCmtLike: Int,
    @SerializedName("brd_cmt_reply")
    val brdCmtReply: Int,
    @SerializedName("brd_cmt_replynum")
    val brdCmtReplynum: Int,
    @SerializedName("brd_cmt_state")
    val brdCmtState: Int,
    @SerializedName("brd_cmt_updatetime")
    val brdCmtUpdatetime: Any,
    @SerializedName("brd_id")
    val brdId: Int,
    @SerializedName("mem_id")
    val memId: Int,
    @SerializedName("mem_nickname")
    val memNickname: String
)