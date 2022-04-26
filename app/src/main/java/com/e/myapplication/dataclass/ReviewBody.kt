package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class ReviewBody(
    @SerializedName("rvPoint")
    val rvPoint: String
)