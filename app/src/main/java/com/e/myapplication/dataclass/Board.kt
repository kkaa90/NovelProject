package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class Board(
    @SerializedName("brdCommentCount")
    val brdCommentCount: Int,
    @SerializedName("brdContents")
    val brdContents: String,
    @SerializedName("brdDatetime")
    val brdDatetime: String,
    @SerializedName("brdDislike")
    var brdDislike: Int,
    @SerializedName("brdFile")
    val brdFile: Int,
    @SerializedName("brdHit")
    val brdHit: Int,
    @SerializedName("brdId")
    val brdId: Int,
    @SerializedName("brdImg")
    val brdImg: Int,
    @SerializedName("brdLike")
    var brdLike: Int,
    @SerializedName("brdNotice")
    val brdNotice: Int,
    @SerializedName("brdState")
    val brdState: Int,
    @SerializedName("brdTitle")
    val brdTitle: String,
    @SerializedName("brdUpdatetime")
    val brdUpdatetime: Any,
    @SerializedName("imgUrls")
    val imgUrls: List<String>,
    @SerializedName("memId")
    val memId: Int,
    @SerializedName("memNickname")
    val memNickname: String
)