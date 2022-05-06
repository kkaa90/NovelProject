package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class ImageUpload(
    @SerializedName("msg") val msg: String
)
data class ImageUploadSingle(
    @SerializedName("imgUrl") val imgUrl: String,
    @SerializedName("msg") val msg: String
)

data class ImageUrl(
    @SerializedName("imgUrl")
    val imgUrl: String
)