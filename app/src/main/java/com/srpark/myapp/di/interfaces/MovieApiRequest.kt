package com.srpark.myapp.di.interfaces

import com.srpark.myapp.home.model.data.MovieDetailRes
import com.srpark.myapp.home.model.data.MovieRes
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface MovieApiRequest {

    companion object {
        private const val MOVIE_INFO_URL = "boxoffice/searchDailyBoxOfficeList.json"
        private const val MOVIE_DETAIL_URL = "movie/searchMovieInfo.json"
    }

    @GET(MOVIE_INFO_URL)
    fun movieRank(@QueryMap param: Map<String, String>): Single<MovieRes>

    @GET(MOVIE_DETAIL_URL)
    fun movieDetail(@QueryMap param: Map<String, String>): Single<MovieDetailRes>
}