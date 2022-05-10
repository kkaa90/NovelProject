package com.e.myapplication.dataclass


import com.google.gson.annotations.SerializedName

data class Boards(
    @SerializedName("board")
    var board: Board,
    @SerializedName("user")
    var user: User
) {
    data class Board(
        @SerializedName("brdCommentCount")
        var brdCommentCount: Int=0,
        @SerializedName("brdContents")
        var brdContents: String="",
        @SerializedName("brdDatetime")
        var brdDatetime: String="",
        @SerializedName("brdDislike")
        var brdDislike: Int=0,
        @SerializedName("brdFile")
        var brdFile: Int=0,
        @SerializedName("brdHit")
        var brdHit: Int=0,
        @SerializedName("brdId")
        var brdId: Int=0,
        @SerializedName("brdImg")
        var brdImg: Int=0,
        @SerializedName("brdLike")
        var brdLike: Int=0,
        @SerializedName("brdNotice")
        var brdNotice: Int=0,
        @SerializedName("brdState")
        var brdState: Int=0,
        @SerializedName("brdTitle")
        var brdTitle: String="",
        @SerializedName("brdUpdatetime")
        var brdUpdatetime: String="",
        @SerializedName("imgUrls")
        var imgUrls: List<String> = emptyList(),
        @SerializedName("memId")
        var memId: Int=0,
        @SerializedName("memNickname")
        var memNickname: String=""
    )

    data class User(
        @SerializedName("memEmail")
        var memEmail: String="",
        @SerializedName("memIcon")
        var memIcon: String="",
        @SerializedName("memNick")
        var memNick: String=""
    )
}