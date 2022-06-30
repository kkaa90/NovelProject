package com.e.treenovel.dataclass


import com.google.gson.annotations.SerializedName

data class CallMethod(
    @SerializedName("msg")
    val msg: String
)