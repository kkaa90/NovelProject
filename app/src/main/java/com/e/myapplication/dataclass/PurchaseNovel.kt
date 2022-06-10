package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

class PurchaseNovel : ArrayList<PurchaseNovel.PurchaseNovelItem>(){
    data class PurchaseNovelItem(
        @SerializedName("imgUrl")
        val imgUrl: String,
        @SerializedName("nvId")
        val nvId: Int,
        @SerializedName("nvcContents")
        val nvcContents: String,
        @SerializedName("nvcHit")
        val nvcHit: Int,
        @SerializedName("nvcId")
        val nvcId: Int,
        @SerializedName("nvcReviewcount")
        val nvcReviewcount: Int,
        @SerializedName("nvcReviewpoint")
        val nvcReviewpoint: Int,
        @SerializedName("nvcSubscribeCount")
        val nvcSubscribeCount: Int,
        @SerializedName("nvcTitle")
        val nvcTitle: String
    )
}