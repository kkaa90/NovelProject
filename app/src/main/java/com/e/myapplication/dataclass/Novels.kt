package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class Novels(
    @SerializedName("content")
    val content: List<Content>,
    @SerializedName("numberOfElements")
    val numberOfElements: Int,
    @SerializedName("pageable")
    val pageable: Pageable,
    @SerializedName("size")
    val size: Int,
    @SerializedName("tags")
    val tags: List<List<String>>,
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("totalPages")
    val totalPages: Int
) {
    data class Content(
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

    data class Pageable(
        @SerializedName("offset")
        val offset: Int,
        @SerializedName("pageNumber")
        val pageNumber: Int,
        @SerializedName("pageSize")
        val pageSize: Int,
        @SerializedName("paged")
        val paged: Boolean,
        @SerializedName("sort")
        val sort: Sort,
        @SerializedName("unpaged")
        val unpaged: Boolean
    ) {
        data class Sort(
            @SerializedName("empty")
            val empty: Boolean,
            @SerializedName("sorted")
            val sorted: Boolean,
            @SerializedName("unsorted")
            val unsorted: Boolean
        )
    }
}

data class SendNCover(
    @SerializedName("novelCover")
    val novelCover: NovelCover,
    @SerializedName("tag")
    val tag: List<String>
) {
    data class NovelCover(
        @SerializedName("img_id")
        val imgId: String,
        @SerializedName("nv_id")
        val nvId: String,
        @SerializedName("nvc_content")
        val nvcContent: String,
        @SerializedName("nvc_title")
        val nvcTitle: String
    )
}

data class SNCR(
    @SerializedName("msg") val msg : String
)