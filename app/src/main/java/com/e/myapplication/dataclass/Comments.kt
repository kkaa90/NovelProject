package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class Comments(
    @SerializedName("comments")
    val comments: List<List<Comment>>,
    @SerializedName("pagenum")
    val pagenum: Int
)