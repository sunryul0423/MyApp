package com.srpark.myapp.di.interfaces

import com.srpark.myapp.home.model.data.WeatherResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface WeatherApiRequest {

    companion object {
        private const val CURRENT_WEATHER_URL = "current/minutely"

    }

    @GET(CURRENT_WEATHER_URL)
    fun weatherInfo(@QueryMap param: Map<String, String>): Single<WeatherResponse>
}