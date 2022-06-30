package com.e.treenovel.dataclass


import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("msg")
    val msg : String,
    @SerializedName("items")
    val items: Items
) {
    data class Items(
        @SerializedName("content")
        val content: List<Content>,
        @SerializedName("empty")
        val empty: Boolean,
        @SerializedName("first")
        val first: Boolean,
        @SerializedName("last")
        val last: Boolean,
        @SerializedName("number")
        val number: Int,
        @SerializedName("numberOfElements")
        val numberOfElements: Int,
        @SerializedName("pageable")
        val pageable: Pageable,
        @SerializedName("size")
        val size: Int,
        @SerializedName("sort")
        val sort: Sort,
        @SerializedName("totalElements")
        val totalElements: Int,
        @SerializedName("totalPages")
        val totalPages: Int
    ) {
        data class Content(
            @SerializedName("content")
            val content: String,
            @SerializedName("datetime")
            val datetime: String,
            @SerializedName("isRead")
            val isRead: Int,
            @SerializedName("msgId")
            val msgId: Int,
            @SerializedName("receiverDelete")
            val receiverDelete: Int,
            @SerializedName("receiverId")
            val receiverId: Int,
            @SerializedName("receiverNickname")
            val receiverNickname: String,
            @SerializedName("senderDelete")
            val senderDelete: Int,
            @SerializedName("senderId")
            val senderId: Int,
            @SerializedName("senderNickname")
            val senderNickname: String,
            @SerializedName("title")
            val title: String
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

data class SingleMessage(
    @SerializedName("msg")
    val msg : String,
    @SerializedName("items")
    val items: Message.Items.Content
)