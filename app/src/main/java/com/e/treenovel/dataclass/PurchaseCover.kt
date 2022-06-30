package com.e.treenovel.dataclass


import com.google.gson.annotations.SerializedName

class PurchaseCover : ArrayList<PurchaseCover.PurchaseItem>(){
    data class PurchaseItem(
        @SerializedName("imgUrl")
        val imgUrl: String,
        @SerializedName("memId")
        val memId: Int,
        @SerializedName("nvCommentCount")
        val nvCommentCount: Int,
        @SerializedName("nvContents")
        val nvContents: Any,
        @SerializedName("nvDatetime")
        val nvDatetime: String,
        @SerializedName("nvHit")
        val nvHit: Int,
        @SerializedName("nvId")
        val nvId: Int,
        @SerializedName("nvPoint")
        val nvPoint: Int,
        @SerializedName("nvReviewcount")
        val nvReviewcount: Int,
        @SerializedName("nvReviewpoint")
        val nvReviewpoint: Int,
        @SerializedName("nvState")
        val nvState: Int,
        @SerializedName("nvTitle")
        val nvTitle: String,
        @SerializedName("nvUpdatetime")
        val nvUpdatetime: Any,
        @SerializedName("nvWriter")
        val nvWriter: String
    )
}