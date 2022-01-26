package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class Boards(
    @SerializedName("boards")
    val boards: List<Board>,
    @SerializedName("pagenum")
    val pagenum: Int
)