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
        @SerializedName("imgUrl")
        val imgUrl: String,
        @SerializedName("nvId")
        val nvId: String,
        @SerializedName("nvcContents")
        val nvcContents: String,
        @SerializedName("nvcTitle")
        val nvcTitle: String
    )
}

data class SNCR(
    @SerializedName("msg") val msg : String
)