package com.srpark.myapp.home.model.data

data class NaverRes(
    val lastBuildDate: String,
    val total: Int,
    val start: Int,
    val display: Int,
    val items: List<Items>
)