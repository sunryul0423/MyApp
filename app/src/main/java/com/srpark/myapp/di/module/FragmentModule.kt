package com.srpark.myapp.di.module

import com.srpark.myapp.home.fragment.MovieFragment
import com.srpark.myapp.home.fragment.MyAppFragment
import com.srpark.myapp.home.fragment.ShoppingFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun bindMyAppFragment(): MyAppFragment

    @ContributesAndroidInjector
    abstract fun bindShoppingFragment(): ShoppingFragment

    @ContributesAndroidInjector
    abstract fun bindMovieFragment(): MovieFragment
}