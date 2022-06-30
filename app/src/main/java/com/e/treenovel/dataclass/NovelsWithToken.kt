package com.e.treenovel.dataclass


import com.google.gson.annotations.SerializedName

data class NovelsWithToken(
    @SerializedName("content")
    val content: List<Content>,
    @SerializedName("numberOfElements")
    val numberOfElements: Int,
    @SerializedName("pageable")
    val pageable: Pageable,
    @SerializedName("size")
    val size: Int,
    @SerializedName("subscribe")
    val subscribe: List<String>,
    @SerializedName("tags")
    val tags: List<List<String>>,
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("totalPages")
    val totalPages: Int
) {
    data class Content(
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