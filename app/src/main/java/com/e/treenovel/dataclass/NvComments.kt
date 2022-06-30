package com.e.treenovel.dataclass


import com.google.gson.annotations.SerializedName

data class NvComments(
    @SerializedName("comments")
    val comments: List<List<Comment>>,
    @SerializedName("pagenum")
    val pagenum: Int
) {
    data class Comment(
        @SerializedName("memId")
        val memId: Int,
        @SerializedName("memNickname")
        val memNickname: String,
        @SerializedName("nvCmtBlame")
        val nvCmtBlame: Int,
        @SerializedName("nvCmtContents")
        val nvCmtContents: String,
        @SerializedName("nvCmtDatetime")
        val nvCmtDatetime: String,
        @SerializedName("nvCmtDislike")
        val nvCmtDislike: Int,
        @SerializedName("nvCmtId")
        val nvCmtId: Int,
        @SerializedName("nvCmtLike")
        val nvCmtLike: Int,
        @SerializedName("nvCmtReply")
        val nvCmtReply: Int,
        @SerializedName("nvCmtReplynum")
        val nvCmtReplynum: Int,
        @SerializedName("nvCmtState")
        val nvCmtState: Int,
        @SerializedName("nvCmtUpdatetime")
        val nvCmtUpdatetime: String,
        @SerializedName("nvId")
        val nvId: Int
    )
}