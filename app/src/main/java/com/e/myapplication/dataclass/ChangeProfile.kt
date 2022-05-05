package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class ChangeProfile(
    @SerializedName("memEmail")
    val memEmail: String,
    @SerializedName("memNick")
    val memNick: String,
    @SerializedName("memIcon")
    val memIcon: String
)

data class ChangePwd(
    @SerializedName("memPassword")
    val memPassword : String
)