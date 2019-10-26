package com.srpark.myapp.home.model.view

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.srpark.myapp.base.BaseViewModel
import com.srpark.myapp.di.MovieRetrofit
import com.srpark.myapp.di.NaverRetrofit
import com.srpark.myapp.di.interfaces.MovieApiRequest
import com.srpark.myapp.di.interfaces.NaverApiRequest
import com.srpark.myapp.home.model.data.DailyBoxOffice
import com.srpark.myapp.home.model.data.Items
import com.srpark.myapp.home.model.data.NaverRes
import com.srpark.myapp.utils.getMetaData
import com.srpark.myapp.utils.strFormat
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieViewModel @Inject constructor(
    private val context: Context,
    @MovieRetrofit private val movieApiRequest: MovieApiRequest,
    @NaverRetrofit private val naverApiRequest: NaverApiRequest
) : BaseViewModel() {

    companion object {
        private const val KEY = "key"
        private const val TARGET_DT = "targetDt"
        private const val ONE_DAY = 1000 * 60 * 60 * 24
        private const val MOVIE_DATE_FORMAT = "yyyyMMdd"
    }

    private val itemList = MutableLiveData<List<Items>>()

    fun requestMovieRank() {
        showProgress()
        val param = hashMapOf(
            KEY to getMetaData(context, "movieApiKey"),
            TARGET_DT to strFormat(System.currentTimeMillis() - ONE_DAY, MOVIE_DATE_FORMAT)
        )
        val addItemList: MutableList<Items> = mutableListOf()
        val singleList: MutableList<Single<NaverRes>> = mutableListOf()
        val dailyBoxOfficeList: MutableList<DailyBoxOffice> = mutableListOf()
        var position = 0
        addDisposable(movieApiRequest.movieRank(param)
            .subscribeOn(Schedulers.io())
            .flatMapPublisher { response ->
                dailyBoxOfficeList.addAll(response.boxOfficeResult.dailyBoxOfficeList)
                dailyBoxOfficeList.map {
                    singleList.add(naverApiRequest.movieImage(it.movieNm))
                }
                Single.concat(singleList)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                response.items[0].rank = dailyBoxOfficeList[position].rank
                response.items[0].title = dailyBoxOfficeList[position].movieNm
                response.items[0].pubDate = dailyBoxOfficeList[position].openDt
                response.items[0].movieCd = dailyBoxOfficeList[position].movieCd
                position++
                addItemList.add(response.items[0])
                if (singleList.size == addItemList.size) {
                    itemList.value = addItemList
                    cancelProgress()
                }
            }, {
                onError(it)
            })
        )
    }

    fun getItemList(): LiveData<List<Items>> {
        return itemList
    }
}