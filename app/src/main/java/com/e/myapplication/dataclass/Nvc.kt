package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class Nvc(
    @SerializedName("memid")
    val memid: String,
    @SerializedName("nvcid")
    val nvcid: String,
    @SerializedName("token")
    val token: String
)

data class nvcr(
    @SerializedName("msg")
    val msg : String
)