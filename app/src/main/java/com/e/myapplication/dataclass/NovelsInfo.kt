package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class NovelsInfo(
    @SerializedName("episode")
    val episode: Map<Int,List<Int>>,
    @SerializedName("NovelCover")
    val novelCover: NovelCover,
    @SerializedName("NovelInfo")
    val novelInfo: List<NovelInfo>
) {
    data class NovelCover(
        @SerializedName("imgUrl")
        val imgUrl: String,
        @SerializedName("nvcContents")
        val nvcContents: String,
        @SerializedName("nvcHit")
        val nvcHit: Int,
        @SerializedName("nvcReviewcount")
        val nvcReviewcount: Int,
        @SerializedName("nvcReviewpoint")
        val nvcReviewpoint: Int,
        @SerializedName("nvcTitle")
        val nvcTitle: String,
        @SerializedName("nvcId")
        val nvcId: Int,
        @SerializedName("nvId")
        val nvId: Int
    )
    data class NovelInfo(
        @SerializedName("imgUrl")
        val imgUrl: String,
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
}