package com.srpark.myapp.di.module

import android.content.Context
import com.srpark.myapp.MyApplication
import com.srpark.myapp.preference.AppPreference
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PreferenceModule(private val context: MyApplication) {

    @Provides
    @Singleton
    fun providePreference(): AppPreference {
        return AppPreference.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideContext(): Context {
        return context
    }
}