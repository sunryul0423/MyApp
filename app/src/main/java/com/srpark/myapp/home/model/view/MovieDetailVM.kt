package com.srpark.myapp.home.model.view

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.srpark.myapp.base.BaseViewModel
import com.srpark.myapp.di.MovieRetrofit
import com.srpark.myapp.di.interfaces.MovieApiRequest
import com.srpark.myapp.home.model.data.MovieDetailRes
import com.srpark.myapp.utils.getMetaData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieDetailVM @Inject constructor(
    private val context: Context,
    @MovieRetrofit private val movieApiRequest: MovieApiRequest
) : BaseViewModel() {

    companion object {
        private const val KEY = "key"
        private const val MOVIE_CD = "movieCd"
    }

    private val movieInfo = MutableLiveData<MovieDetailRes.MovieInfoResult.MovieInfo>()

    fun requestMovieDetail(movieCd: String) {
        showProgress()
        val param = hashMapOf(
            KEY to getMetaData(context, "movieApiKey"),
            MOVIE_CD to movieCd
        )
        addDisposable(
            movieApiRequest.movieDetail(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    movieInfo.value = it.movieInfoResult.movieInfo
                    cancelProgress()
                }, {
                    onError(it)
                })
        )
    }

    fun getMovieInfo(): LiveData<MovieDetailRes.MovieInfoResult.MovieInfo> {
        return movieInfo
    }
}