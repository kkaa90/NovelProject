package com.e.treenovel.dataclass


import com.google.gson.annotations.SerializedName

data class ReportMethod(
    @SerializedName("reportState")
    val reportState: String,
    @SerializedName("content")
    val content: String

)

data class ReportState(
    val sendState: String,
    val presentState: String
)

val reportState = listOf(
    ReportState("ADVERTIS", "광고"),
    ReportState("PAPERING", "도배"),
    ReportState("PORNOGRAPHY", "성인물"),
    ReportState("ABUSE", "어뷰징"),
    ReportState("PRIVACY", "사생활 침해"),
    ReportState("COPYRIGHT", "저작권"),
    ReportState("ETC", "기타")
)