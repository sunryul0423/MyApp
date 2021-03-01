package com.srpark.myapp.di.interfaces

import com.srpark.myapp.home.model.data.LottoInfoRes
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface LottoApiRequest {

    companion object {
        private const val LOTTO_PLACE_URL = "common.do?method=getLottoNumber"
        private const val DRW_NO = "drwNo"
    }

    @GET(LOTTO_PLACE_URL)
    fun lottoWinPlace(@Query(DRW_NO) number: Long) : Single<LottoInfoRes>
}