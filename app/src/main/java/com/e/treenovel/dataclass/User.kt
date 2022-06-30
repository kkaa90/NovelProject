package com.e.treenovel.dataclass

import com.google.gson.annotations.SerializedName

data class U(
    @SerializedName("status") val status : String
)

data class User(
    @SerializedName("memUserId") val memUserId : String,
    @SerializedName("Authorization") val authorization: String,
    @SerializedName("memIcon") val memIcon: String,
    @SerializedName("memId") val memId: String,
    @SerializedName("memNick") val memNick: String,
    @SerializedName("attendancePoint") val aPoint : String,
    @SerializedName("memLastloginDatetime") val lastLogin : String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("email") val email: String
)

data class PostBody(
    @SerializedName("msg") val msg: String
)

data class SendBody(
    @SerializedName("memUserId") val memUserId: String,
    @SerializedName("memPassword") val memPassword: String,
    @SerializedName("memEmail") val memEmail: String,
    @SerializedName("memNick") val memNick: String,
    @SerializedName("memIcon") val memIcon: String
)

data class LoginBody(
    @SerializedName("memUserId") val memUserId: String,
    @SerializedName("memPassword") val memPassword: String
)

data class Point(
    @SerializedName("point") val point: String
)

data class Token(
    @SerializedName("token") val token : String
)

data class ChkLogin(
    val chkIdSave : Boolean,
    val chkAutoLogin : Boolean,
    val id : String,
    val pwd : String
)