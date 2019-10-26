package com.srpark.myapp.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


open class BaseViewModel : ViewModel() {

    private var compositeDisposable = CompositeDisposable()
    protected val throwableData = MutableLiveData<Throwable>()
    private val progress = MutableLiveData<Boolean>().apply { value = false }

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun getThrowableData(): LiveData<Throwable> {
        return throwableData
    }

    fun getProgress(): LiveData<Boolean> {
        return progress
    }

    fun showProgress() {
        progress.value?.let { isShow ->
            addDisposable(Observable.just(isShow)
                .subscribeOn(Schedulers.io())
                .filter { !it }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { progress.value = true }
            )
        }
    }

    fun cancelProgress() {
        progress.value?.let { isShow ->
            addDisposable(Observable.just(isShow)
                .subscribeOn(Schedulers.io())
                .filter { it }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { progress.value = false }
            )
        }
    }

    fun onError(throwable: Throwable) {
        cancelProgress()
        throwableData.value = throwable
    }
}