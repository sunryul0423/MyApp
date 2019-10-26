package com.srpark.myapp.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srpark.myapp.di.ViewModelKey
import com.srpark.myapp.home.model.view.*
import com.srpark.myapp.slide.model.ExchangeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    abstract fun bindUserViewModel(userViewModel: UserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyAppViewModel::class)
    abstract fun bindMyAppViewModel(myAppViewModel: MyAppViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LottoPlaceVM::class)
    abstract fun bindLottoPlaceVM(lottoPlaceVM: LottoPlaceVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShoppingViewModel::class)
    abstract fun bindShoppingViewModel(shoppingViewModel: ShoppingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MovieViewModel::class)
    abstract fun bindMovieViewModel(movieViewModel: MovieViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailVM::class)
    abstract fun bindMovieDetailVM(movieDetailVM: MovieDetailVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ExchangeViewModel::class)
    abstract fun bindExchangeViewModel(exchangeViewModel: ExchangeViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@Singleton
class ViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator: Provider<out ViewModel>? = creators[modelClass]

        return if (creator == null) {
            for ((key, value) in creators) {
                if (modelClass.isAssignableFrom(key)) {
                    return value.get() as T
                }
            }
            throw IllegalArgumentException("Unknown model class $modelClass")
        } else {
            creator.get() as T
        }
    }
}