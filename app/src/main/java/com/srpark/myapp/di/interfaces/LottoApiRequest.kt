package com.srpark.myapp.di.interfaces

import com.srpark.myapp.home.model.data.LottoInfoRes
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface LottoApiRequest {

    companion object {
        private const val LOTTO_PRIZE_URL = "lotto"
        private const val LOTTO_PLACE_URL = "lotto/winning/places"
        private const val DRAW_NO = "drawNo"
    }

    @GET(LOTTO_PRIZE_URL)
    fun lottoPrizeInfo(@Query(DRAW_NO) number: Long): Single<LottoInfoRes>

    @GET(LOTTO_PLACE_URL)
    fun lottoWinPlace(@Query(DRAW_NO) number: Long) : Single<LottoInfoRes>
}