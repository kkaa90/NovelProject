package com.e.treenovel.dataclass


import com.google.gson.annotations.SerializedName

data class NvcList(
    @SerializedName("novelCovers")
    val novelCovers: List<NovelCover>,
    @SerializedName("msg")
    val msg : String
) {
    data class NovelCover(
        @SerializedName("imgUrl")
        val imgUrl: String,
        @SerializedName("nvId")
        val nvId: Int,
        @SerializedName("nvcContents")
        val nvcContents: String,
        @SerializedName("nvcHit")
        val nvcHit: Int,
        @SerializedName("nvcId")
        val nvcId: Int,
        @SerializedName("nvcReviewcount")
        val nvcReviewcount: Int,
        @SerializedName("nvcReviewpoint")
        val nvcReviewpoint: Int,
        @SerializedName("nvcSubscribeCount")
        val nvcSubscribeCount: Int,
        @SerializedName("nvcTitle")
        val nvcTitle: String
    )
}