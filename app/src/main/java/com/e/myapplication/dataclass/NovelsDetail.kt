package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class NovelsDetail(
    @SerializedName("imgUrl")
    val imgUrl: Int,
    @SerializedName("memId")
    val memId: Int,
    @SerializedName("nvCommentCount")
    val nvCommentCount: Int,
    @SerializedName("nvContents")
    val nvContents: String,
    @SerializedName("nvDatetime")
    val nvDatetime: String,
    @SerializedName("nvHit")
    val nvHit: Int,
    @SerializedName("nvId")
    val nvId: Int,
    @SerializedName("nvReviewcount")
    val nvReviewcount: Int,
    @SerializedName("nvReviewpoint")
    val nvReviewpoint: Int,
    @SerializedName("nvState")
    val nvState: Int,
    @SerializedName("nvTitle")
    val nvTitle: String,
    @SerializedName("nvUpdatetime")
    val nvUpdatetime: Any,
    @SerializedName("nvWriter")
    val nvWriter: String,
    @SerializedName("msg")
    val msg: String
)