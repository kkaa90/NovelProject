package com.e.treenovel.dataclass


import com.google.gson.annotations.SerializedName

data class Nvc(
    @SerializedName("nvcId")
    val nvcId: String,
    @SerializedName("token")
    val token: String
)

data class nvcr(
    @SerializedName("msg")
    val msg : String
)