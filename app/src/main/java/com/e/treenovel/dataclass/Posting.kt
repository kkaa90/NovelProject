package com.e.treenovel.dataclass

data class Posting(
    val num: Int, val novelNum: Int, val title: String, val body: String, val like: Int,
    val parent: Int?, val child: Int?
)