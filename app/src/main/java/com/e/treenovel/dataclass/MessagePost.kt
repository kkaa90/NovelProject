package com.e.treenovel.dataclass


import com.google.gson.annotations.SerializedName

data class MessagePost(
    @SerializedName("content")
    val content: String,
    @SerializedName("receiverId")
    val receiverId: String,
    @SerializedName("title")
    val title: String
)