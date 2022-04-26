package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class ReviewBody(
    @SerializedName("nvId")
    val nvId: String,
    @SerializedName("rvPoint")
    val rvPoint: String
)