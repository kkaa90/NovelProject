package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class BoardList(
    @SerializedName("boards")
    var boards: List<BoardListItem>,
    @SerializedName("pagenum")
    var pagenum: Int
) {
    data class BoardListItem(
        @SerializedName("brdCommentCount")
        var brdCommentCount: Int,
        @SerializedName("brdContents")
        var brdContents: String,
        @SerializedName("brdDatetime")
        var brdDatetime: String,
        @SerializedName("brdDislike")
        var brdDislike: Int,
        @SerializedName("brdFile")
        var brdFile: Int,
        @SerializedName("brdHit")
        var brdHit: Int,
        @SerializedName("brdId")
        var brdId: Int,
        @SerializedName("brdImg")
        var brdImg: Int,
        @SerializedName("brdLike")
        var brdLike: Int,
        @SerializedName("brdNotice")
        var brdNotice: Int,
        @SerializedName("brdState")
        var brdState: Int,
        @SerializedName("brdTitle")
        var brdTitle: String,
        @SerializedName("brdUpdatetime")
        var brdUpdatetime: String,
        @SerializedName("imgUrl")
        var imgUrl: String,
        @SerializedName("memId")
        var memId: Int,
        @SerializedName("memNickname")
        var memNickname: String
    )
}