package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class NovelsDetail(
    @SerializedName("img_url")
    val imgUrl: Int,
    @SerializedName("mem_id")
    val memId: Int,
    @SerializedName("nv_comment_count")
    val nvCommentCount: Int,
    @SerializedName("nv_contents")
    val nvContents: String,
    @SerializedName("nv_datetime")
    val nvDatetime: String,
    @SerializedName("nv_hit")
    val nvHit: Int,
    @SerializedName("nv_id")
    val nvId: Int,
    @SerializedName("nv_reviewcount")
    val nvReviewcount: Int,
    @SerializedName("nv_reviewpoint")
    val nvReviewpoint: Int,
    @SerializedName("nv_state")
    val nvState: Int,
    @SerializedName("nv_title")
    val nvTitle: String,
    @SerializedName("nv_updatetime")
    val nvUpdatetime: Any,
    @SerializedName("nv_writer")
    val nvWriter: String,
    @SerializedName("msg")
    val msg: String
)