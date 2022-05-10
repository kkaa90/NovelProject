package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class NovelsDetail(
    @SerializedName("novel")
    val novel: Novel,
    @SerializedName("user")
    val user: User,
    @SerializedName("msg")
    val msg: String
) {
    data class Novel(
        @SerializedName("imgUrls")
        val imgUrls: List<Any>,
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
        @SerializedName("nvPoint")
        val nvPoint: Int,
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
        val nvWriter: String
    )

    data class User(
        @SerializedName("memEmail")
        val memEmail: String,
        @SerializedName("memIcon")
        val memIcon: String,
        @SerializedName("memNick")
        val memNick: String
    )
}