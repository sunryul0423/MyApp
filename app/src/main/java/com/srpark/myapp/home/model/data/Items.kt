package com.srpark.myapp.home.model.data

import java.io.Serializable

data class Items(
    var title: String,
    val link: String,
    val image: String,

    // Shopping
    val lprice: Int,
    val hprice: Int,
    val mallName: String,
    val productId: Long,
    val productType: Int,

    // Movie
    val subtitle: String, // 영화 영문 제목
    var pubDate: String,
    val director: String, // 감독
    val actor: String, // 출연배우
    val userRating: Double, //유저들의 평점
    var movieCd: String,
    var rank: String
) : Serializable