package com.e.myapplication

import com.e.myapplication.dataclass.Novel
import com.e.myapplication.dataclass.NovelTree
import com.e.myapplication.dataclass.Posting

object SampleData {
    val postingList = listOf(
        Posting(
            0,
            1,
            "1화 테스트",
            "테스트중인 소설",
            15,
            null,
            2
        ),
        Posting(
            1,
            2,
            "2화 테스트",
            "테스트중인 소설",
            10,
            1,
            3

        ),
        Posting(
            2,
            3,
            "3화 테스트",
            "테스트중인 소설",
            20,
            2,
            4
        ),
        Posting(
            3,
            4,
            "4화 테스트",
            "테스트중인 소설",
            5,
            3,
            5
        ),
        Posting(
            4,
            5,
            "5화 테스트",
            "테스트중인 소설",
            7,
            4,
            null
        ),
        Posting(5,2,"2a화 테스트","테스트중", 4, 2,null),
    )


    val novelList = listOf(
        Novel(0,1,"스타크래프트","블리자드","스타1 설명",1),
        Novel(1,1,"워크래프트","블리자드","워크 설명",2),
        Novel(2,1,"메이플스토리","넥슨","메이플 설명",3),
        Novel(3,2,"KBS","국영방송","KBS 설명",4),
        Novel(4,2,"유튜브","구글","유튜브 설명",5),
        Novel(5,2,"트위치","트위치","트위치 설명",6),
    )

    val novel1 = NovelTree(1, listOf(0,1,2,3,4))
    val novel2 = NovelTree(2,listOf(0,1,5))
}