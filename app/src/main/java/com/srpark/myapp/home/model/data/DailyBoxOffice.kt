package com.srpark.myapp.home.model.data

data class DailyBoxOffice(
    val rank: String, // 해당일자의 박스오피스 순위를 출력합니다
    val movieCd: String, // 영화의 대표코드를 출력합니다.
    val movieNm: String, // 영화명(국문)을 출력합니다.
    val openDt: String // 영화의 개봉일을 출력합니다.
)