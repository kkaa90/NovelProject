package com.e.myapplication.dataclass

import com.google.gson.annotations.SerializedName

data class Boards(
    @SerializedName("brd_comment_count")
    val brdCommentCount: Int,
    @SerializedName("brd_contents")
    val brdContents: String,
    @SerializedName("brd_datetime")
    val brdDatetime: String,
    @SerializedName("brd_dislike")
    val brdDislike: Int,
    @SerializedName("brd_file")
    val brdFile: Int,
    @SerializedName("brd_hit")
    val brdHit: Int,
    @SerializedName("brd_id")
    val brdId: Int,
    @SerializedName("brd_img")
    val brdImg: Int,
    @SerializedName("brd_like")
    val brdLike: Int,
    @SerializedName("brd_notice")
    val brdNotice: Int,
    @SerializedName("brd_state")
    val brdState: Int,
    @SerializedName("brd_title")
    val brdTitle: String,
    @SerializedName("brd_updatetime")
    val brdUpdatetime: Any,
    @SerializedName("img_id")
    val imgId: Int,
    @SerializedName("mem_id")
    val memId: Int,
    @SerializedName("mem_nickname")
    val memNickname: String
)