package com.srpark.myapp.home.model.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.srpark.myapp.base.BaseViewModel
import com.srpark.myapp.di.NaverRetrofit
import com.srpark.myapp.di.interfaces.NaverApiRequest
import com.srpark.myapp.home.model.data.Items
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject


class ShoppingViewModel @Inject constructor(@NaverRetrofit private val naverApiRequest: NaverApiRequest) : BaseViewModel() {

    private val dataEmpty = MutableLiveData<Boolean>().apply { value = true }

    fun getDataEmpty(): LiveData<Boolean> {
        return dataEmpty
    }

    fun getPageLiveData(searchText: String, sort: String): LiveData<PagedList<Items>> {
        val pageConfig = PagedList.Config.Builder()
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .setPrefetchDistance(10)
            .setEnablePlaceholders(true)
            .build()

        val dataSourceListener = object : DataSource.Factory<Int, Items>() {
            override fun create(): DataSource<Int, Items> {
                return ShopPageKeyedDataSource(searchText, sort)
            }
        }
        return LivePagedListBuilder(dataSourceListener, pageConfig).build()
    }

    inner class ShopPageKeyedDataSource(private val searchText: String, private val sort: String, private val next: Int = 20) :
        PageKeyedDataSource<Int, Items>() {
        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Items>) {
            showProgress()
            val start = 1
            addDisposable(
                naverApiRequest.searchShop(searchText, start, next, sort)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        if (response.items.isNullOrEmpty()) {
                            dataEmpty.value = true
                        } else {
                            dataEmpty.value = false
                            if (response.items.size < 20) {
                                callback.onResult(response.items, null, null)
                            } else {
                                callback.onResult(response.items, null, start + next)
                            }
                        }
                        cancelProgress()
                    }, {
                        if (it is HttpException && it.code() == 400) {
                            dataEmpty.value = true
                            cancelProgress()
                        } else {
                            onError(it)
                        }
                    })
            )
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Items>) {
            addDisposable(Single.just(params.key)
                .subscribeOn(Schedulers.io())
                .filter { key -> key <= 1000 }
                .flatMap { key ->
                    showProgress()
                    naverApiRequest.searchShop(searchText, key, next, sort)
                        .filter { !it.items.isNullOrEmpty() }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response.items.size < 20) {
                        callback.onResult(response.items, null)
                    } else {
                        callback.onResult(response.items, params.key + next)
                    }
                    cancelProgress()
                }, {
                    if (it is HttpException && it.code() == 400) {
                        dataEmpty.value = true
                        cancelProgress()
                    } else {
                        onError(it)
                    }
                })
            )
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Items>) {
        }
    }
}