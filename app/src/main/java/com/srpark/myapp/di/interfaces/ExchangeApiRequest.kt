package com.srpark.myapp.di.interfaces

import com.srpark.myapp.home.model.data.ExchangeRes
import io.reactivex.Single
import retrofit2.http.GET

interface ExchangeApiRequest {

    companion object {
        private const val EXCHANGE_INFO_URL = "forex/recent?codes=FRX.KRWUSD,FRX.KRWJPY,FRX.KRWCNY,FRX.KRWEUR"
    }

    @GET(EXCHANGE_INFO_URL)
    fun exchangeInfo(): Single<List<ExchangeRes>>
}