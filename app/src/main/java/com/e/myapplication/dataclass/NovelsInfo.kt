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
        @SerializedName("img_url")
        val imgUrl: String,
        @SerializedName("nvc_contents")
        val nvcContents: String,
        @SerializedName("nvc_hit")
        val nvcHit: Int,
        @SerializedName("nvc_reviewcount")
        val nvcReviewcount: Int,
        @SerializedName("nvc_reviewpoint")
        val nvcReviewpoint: Int,
        @SerializedName("nvc_title")
        val nvcTitle: String,
        @SerializedName("nvcid")
        val nvcid: Int,
        @SerializedName("nvid")
        val nvid: Int
    )

    data class NovelInfo(
        @SerializedName("img_url")
        val imgUrl: String,
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
        @SerializedName("nv_point")
        val nvPoint: Int,
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
        val nvWriter: String
    )
}