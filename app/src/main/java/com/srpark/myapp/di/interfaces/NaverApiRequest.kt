package com.srpark.myapp.di.interfaces

import com.srpark.myapp.home.model.data.NaverRes
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NaverApiRequest {

    companion object {
        private const val SEARCH_SHOP_URL = "v1/search/shop.json"
        private const val MOVIE_SEARCH_URL = "v1/search/movie.json"
        private const val CLIENT_ID = "hH22ZN5UG_2i8QFYtxA3"
        private const val CLIENT_SECRET = "_nkoAE1b5K"
        private const val QUERY = "query"
        private const val START = "start"
        private const val DISPLAY = "display"
        private const val SORT = "sort"
    }

    @Headers("X-Naver-Client-Id: $CLIENT_ID", "X-Naver-Client-Secret: $CLIENT_SECRET")
    @GET(SEARCH_SHOP_URL)
    fun searchShop(@Query(QUERY) text: String, @Query(START) start: Int, @Query(DISPLAY) display: Int, @Query(SORT) sort: String): Single<NaverRes>

    @Headers("X-Naver-Client-Id: $CLIENT_ID", "X-Naver-Client-Secret: $CLIENT_SECRET")
    @GET(MOVIE_SEARCH_URL)
    fun movieImage(@Query(QUERY) text: String, @Query(START) start: Int = 1, @Query(DISPLAY) display: Int = 1): Single<NaverRes>
}