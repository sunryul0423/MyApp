package com.srpark.myapp.di.interfaces

import com.srpark.myapp.MyApplication
import com.srpark.myapp.di.module.*
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ActivityModule::class, FragmentModule::class, ViewModelModule::class, ServiceModule::class,
        RetrofitModule::class, PreferenceModule::class, AndroidInjectionModule::class]
)
interface ApplicationComponent : AndroidInjector<MyApplication>