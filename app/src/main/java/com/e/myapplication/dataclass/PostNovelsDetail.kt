package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class PostNovelsDetail(
    @SerializedName("novel")
    val novel: Novel,
    @SerializedName("parent")
    val parent: String
) {
    data class Novel(
        @SerializedName("imgUrl")
        val imgUrl: String,
        @SerializedName("nvContents")
        val nvContents: String,
        @SerializedName("nvState")
        val nvState: String,
        @SerializedName("nvTitle")
        val nvTitle: String,
        @SerializedName("nvWriter")
        val nvWriter: String,
        @SerializedName("nvPoint")
        val nvPoint: String
    )
}