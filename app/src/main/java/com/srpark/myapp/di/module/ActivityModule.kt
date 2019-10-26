package com.srpark.myapp.di.module

import com.srpark.myapp.home.activity.*
import com.srpark.myapp.slide.activity.ProfileActivity
import com.srpark.myapp.slide.activity.ExchangeActivity
import com.srpark.myapp.slide.activity.LocationActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun bindProfileActivity(): ProfileActivity

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindLottoDetailActivity(): LottoDetailActivity

    @ContributesAndroidInjector
    abstract fun bindMovieDetailActivity(): MovieDetailActivity

    @ContributesAndroidInjector
    abstract fun bindMapViewActivity(): MapViewActivity

    @ContributesAndroidInjector
    abstract fun bindRecordActivity(): RecordActivity

    @ContributesAndroidInjector
    abstract fun bindWebViewActivity(): WebViewActivity

    @ContributesAndroidInjector
    abstract fun bindExchangeActivity(): ExchangeActivity

    @ContributesAndroidInjector
    abstract fun bindLocationActivity(): LocationActivity
}