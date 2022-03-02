package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class PostNovelsDetail(
    @SerializedName("novel")
    val novel: Novel,
    @SerializedName("parent")
    val parent: String
) {
    data class Novel(
        @SerializedName("img_url")
        val imgUrl: String,
        @SerializedName("mem_id")
        val memId: String,
        @SerializedName("nv_contents")
        val nvContents: String,
        @SerializedName("nv_state")
        val nvState: String,
        @SerializedName("nv_title")
        val nvTitle: String,
        @SerializedName("nv_writer")
        val nvWriter: String,
        @SerializedName("nv_point")
        val nvPoint: String
    )
}