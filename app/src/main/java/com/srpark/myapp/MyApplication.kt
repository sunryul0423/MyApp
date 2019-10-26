package com.srpark.myapp

import com.srpark.myapp.di.interfaces.DaggerApplicationComponent
import com.srpark.myapp.di.module.PreferenceModule
import com.srpark.myapp.utils.GlideApp
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication


class MyApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.builder()
            .preferenceModule(PreferenceModule(this))
            .build()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        GlideApp.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        GlideApp.get(this).trimMemory(level)
    }
}