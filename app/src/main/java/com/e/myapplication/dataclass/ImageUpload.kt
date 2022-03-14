package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class ImageUpload(
    @SerializedName("msg") val msg: String
)

data class ImageUrl(
    @SerializedName("img_url")
    val imgUrl: String
)