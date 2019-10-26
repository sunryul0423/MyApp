package com.srpark.myapp.home.model.data

data class WeatherResponse(
    val weather: Weather,
    val common: Common,
    val result: Result
) {
    data class Weather(
        val minutely: List<Minutely>
    ) {
        data class Minutely(
            val station: Station, // 관측소 정보
            val wind: Wind, // 바람정보
            val precipitation: Precipitation, // 강수정보
            val sky: Sky, // 하늘상태 정보
            val rain: Rain, // 강우정보
            val temperature: Temperature, // 기온정보
            val humidity: String, // 상대습도(%)
            val pressure: Pressure, // 기압정보
            val lightning: String, // 낙뢰유무
            val timeObservation: String // 관측시간
        ) {
            data class Station(
                val longitude: String,
                val latitude: String,
                var name: String,
                val id: String,
                val type: String
            )

            data class Wind(
                val wdir: String,
                val wspd: String
            )

            data class Precipitation(
                val sinceOntime: String,
                val type: String
            )

            data class Sky(
                val code: String,
                val name: String
            )

            data class Rain(
                val sinceOntime: String,
                val sinceMidnight: String,
                val last10min: String,
                val last15min: String,
                val last30min: String,
                val last1hour: String,
                val last6hour: String,
                val last12hour: String,
                val last24hour: String
            )

            data class Temperature(
                val tc: String, // 현재기온
                val tmax: String, // 오늘의 최고기온
                val tmin: String // 오늘의 최저기온
            )

            data class Pressure(
                val surface: String,
                val seaLevel: String
            )
        }
    }

    data class Common(
        val alertYn: String,
        val stormYn: String
    )

    data class Result(
        val code: Int,
        val requestUrl: String,
        val message: String
    )
}