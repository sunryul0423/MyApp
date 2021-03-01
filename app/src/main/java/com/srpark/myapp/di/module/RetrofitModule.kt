package com.srpark.myapp.di.module

import com.srpark.myapp.di.*
import com.srpark.myapp.di.interfaces.*
import dagger.Module
import dagger.Provides
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class RetrofitModule {

    companion object {
        private const val CONNECT_TIMEOUT = 15L
        private const val WRITE_TIMEOUT = 15L
        private const val READ_TIMEOUT = 20L
        private const val LOTTO_BASE_URL = "https://www.dhlottery.co.kr/"
        private const val WEATHER_BASE_URL = "https://apis.openapi.sk.com/weather/"
        private const val NAVER_BASE_URL = "https://openapi.naver.com/"
        private const val MOVIE_BASE_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/"
        private const val EXCHANGE_BASE_URL = "https://quotation-api-cdn.dunamu.com/v1/"
    }

    @Provides
    @Singleton
    @LottoRetrofit
    fun lottoRetrofit(lottoRetrofit: Retrofit): LottoApiRequest {
        return lottoRetrofit.create(LottoApiRequest::class.java)
    }

    @Provides
    @Singleton
    fun setLottoRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(LOTTO_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @WeatherRetrofit
    fun weatherRetrofit(okHttpClient: OkHttpClient): WeatherApiRequest {
        return Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(WeatherApiRequest::class.java)
    }

    @Provides
    @Singleton
    @NaverRetrofit
    fun naverRetrofit(okHttpClient: OkHttpClient): NaverApiRequest {
        return Retrofit.Builder()
            .baseUrl(NAVER_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(NaverApiRequest::class.java)
    }

    @Provides
    @Singleton
    @MovieRetrofit
    fun movieRetrofit(okHttpClient: OkHttpClient): MovieApiRequest {
        return Retrofit.Builder()
            .baseUrl(MOVIE_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(MovieApiRequest::class.java)
    }

    @Provides
    @Singleton
    @ExchangeRetrofit
    fun exchangeRetrofit(okHttpClient: OkHttpClient): ExchangeApiRequest {
        return Retrofit.Builder()
            .baseUrl(EXCHANGE_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ExchangeApiRequest::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .cookieJar(CookieJar.NO_COOKIES)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
    }
}