package com.e.myapplication.dataclass

import com.google.gson.annotations.SerializedName
import retrofit2.http.Header
import retrofit2.http.Query

data class U(
    @SerializedName("status") val status : String
)

data class User(
    @SerializedName("mem_userid") val mem_userid : String,
    @SerializedName("Authorization") val authorization: String,
    @SerializedName("mem_icon") val memIcon: String,
    @SerializedName("mem_id") val mem_id: String,
    @SerializedName("mem_nick") val memNick: String,
    @SerializedName("attendance point") val aPoint : String,
    @SerializedName("mem_lastlogin_datetime") val lastLogin : String
)

data class PostBody(
    @SerializedName("msg") val msg: String
)

data class SendBody(
    @SerializedName("mem_userid") val mem_userid: String,
    @SerializedName("mem_password") val mem_password: String,
    @SerializedName("mem_email") val mem_email: String,
    @SerializedName("mem_nick") val mem_nick: String,
    @SerializedName("mem_icon") val mem_icon: String
)

data class GetBody(
    @SerializedName("mem_userid") val mem_userid: String,
    @SerializedName("mem_password") val mem_password: String
)

data class Point(
    @SerializedName("point") val point: String
)