package com.srpark.myapp.di.module

import com.srpark.myapp.sevice.GpsService
import com.srpark.myapp.sevice.LottoReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract fun bindGpsService(): GpsService

    @ContributesAndroidInjector
    abstract fun bindLottoReceiver(): LottoReceiver
}