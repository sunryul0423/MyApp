package com.srpark.myapp.home.model.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.srpark.myapp.base.BaseViewModel
import com.srpark.myapp.di.LottoRetrofit
import com.srpark.myapp.di.interfaces.LottoApiRequest
import com.srpark.myapp.home.model.data.LottoInfoRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LottoPlaceVM @Inject constructor(@LottoRetrofit private val lottoApiRequest: LottoApiRequest) : BaseViewModel() {

    private val winningPlaces = MutableLiveData<LottoInfoRes>()

    fun requestLottoApi(number: Long) {
        showProgress()
        addDisposable(
            lottoApiRequest.lottoWinPlace(number)
                .subscribeOn(Schedulers.io())
                .filter { res -> "success" == res.returnValue }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    winningPlaces.value = response
                    cancelProgress()
                }, {
                    onError(it)
                })
        )
    }

    fun getWinningPlaces(): LiveData<LottoInfoRes> {
        return winningPlaces
    }
}