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
        val imgUrl: String = "",
        @SerializedName("nvcContents")
        val nvcContents: String = "",
        @SerializedName("nvcHit")
        val nvcHit: Int = 0,
        @SerializedName("nvcReviewcount")
        val nvcReviewcount: Int = 0,
        @SerializedName("nvcReviewpoint")
        val nvcReviewpoint: Int = 0,
        @SerializedName("nvcTitle")
        val nvcTitle: String = "",
        @SerializedName("nvcId")
        val nvcId: Int = 0,
        @SerializedName("nvId")
        val nvId: Int = 0,
        @SerializedName("nvcSubscribeCount")
        val nvcSubscribeCount : Int =0
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