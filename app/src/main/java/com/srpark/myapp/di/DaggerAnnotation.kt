package com.srpark.myapp.di

import androidx.lifecycle.ViewModel
import dagger.MapKey
import javax.inject.Qualifier
import javax.inject.Scope
import kotlin.reflect.KClass

@MustBeDocumented
@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LottoRetrofit

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class WeatherRetrofit

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class NaverRetrofit

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MovieRetrofit

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ExchangeRetrofit