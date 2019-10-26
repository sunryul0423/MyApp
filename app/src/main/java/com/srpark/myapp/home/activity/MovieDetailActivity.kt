package com.srpark.myapp.home.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.srpark.myapp.R
import com.srpark.myapp.base.BaseActivity
import com.srpark.myapp.databinding.ActivityMovieDetailBinding
import com.srpark.myapp.home.model.data.Items
import com.srpark.myapp.home.model.view.MovieDetailVM
import com.srpark.myapp.utils.ActivityConstant.INTENT_MOVIE_DATA
import com.srpark.myapp.utils.setImageUrl
import com.srpark.myapp.utils.showThrowableToast
import javax.inject.Inject

class MovieDetailActivity : BaseActivity<ActivityMovieDetailBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_movie_detail

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(viewBinding.collapseToolbar.toolbar)
        val movieDetailVM = ViewModelProviders.of(this, viewModelFactory).get(MovieDetailVM::class.java)
        viewBinding.lifecycleOwner = this
        val item = intent.getSerializableExtra(INTENT_MOVIE_DATA) as Items
        setImageUrl(viewBinding.collapseToolbar.ivMovie, item.image)
        viewBinding.collapseToolbar.tvRating.text = item.userRating.toString()
        viewBinding.collapseToolbar.tvOpenDt.text = String.format(getString(R.string.movie_open_date), item.pubDate)
        liveDataObserver(movieDetailVM, item)
    }

    private fun liveDataObserver(movieDetailVM: MovieDetailVM, item: Items) {
        movieDetailVM.getMovieInfo().observe(this, Observer { movieInfo ->
            viewBinding.collapseToolbar.collapseBar.title = movieInfo.movieNm
            viewBinding.collapseToolbar.tvSubTitle.text = movieInfo.movieNmEn
        })
        movieDetailVM.getProgress().observe(this, Observer {
            if (it) {
                progressDialog.show()
            } else {
                progressDialog.cancel()
            }
        })
        movieDetailVM.getThrowableData().observe(this, Observer {
            showThrowableToast(this, it)
        })
        
        movieDetailVM.requestMovieDetail(item.movieCd)
    }
}