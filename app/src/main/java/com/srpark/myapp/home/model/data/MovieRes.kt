package com.srpark.myapp.home.model.data

import com.google.gson.annotations.SerializedName

data class MovieRes(
    val boxOfficeResult: BoxOfficeResult
) {
    data class BoxOfficeResult(
        @SerializedName("boxofficeType")
        val boxOfficeType: String,
        val showRange: String,
        val dailyBoxOfficeList: List<DailyBoxOffice>
    )
}