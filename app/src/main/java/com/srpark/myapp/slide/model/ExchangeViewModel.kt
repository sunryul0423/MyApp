package com.srpark.myapp.slide.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.srpark.myapp.base.BaseViewModel
import com.srpark.myapp.di.ExchangeRetrofit
import com.srpark.myapp.di.interfaces.ExchangeApiRequest
import com.srpark.myapp.home.model.data.ExchangeRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ExchangeViewModel @Inject constructor(
    @ExchangeRetrofit private val exchangeApiRequest: ExchangeApiRequest
) : BaseViewModel() {

    private val exchangeResponse = MutableLiveData<List<ExchangeRes>>()

    init {
        requestExchangeApi()
    }

    private fun requestExchangeApi() {
        addDisposable(
            exchangeApiRequest.exchangeInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    exchangeResponse.value = response
                }, {
                    onError(it)
                })
        )
    }

    fun getExchangeResponse(): LiveData<List<ExchangeRes>> {
        return exchangeResponse
    }

}