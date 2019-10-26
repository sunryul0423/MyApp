package com.srpark.myapp.home.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.srpark.myapp.R
import com.srpark.myapp.base.BaseFragment
import com.srpark.myapp.databinding.FragmentMovieBinding
import com.srpark.myapp.home.activity.MovieDetailActivity
import com.srpark.myapp.home.adapter.RvMovieAdapter
import com.srpark.myapp.home.model.data.Items
import com.srpark.myapp.home.model.view.MovieViewModel
import com.srpark.myapp.utils.ActivityConstant.INTENT_MOVIE_DATA
import com.srpark.myapp.utils.showThrowableToast
import kotlinx.coroutines.Job
import javax.inject.Inject


@SuppressLint("StaticFieldLeak")
class MovieFragment : BaseFragment<FragmentMovieBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.fragment_movie

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var job: Job

    companion object {
        private val movieFragment = MovieFragment()

        fun getInstance(): MovieFragment {
            return movieFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val movieVM = ViewModelProviders.of(this, viewModelFactory).get(MovieViewModel::class.java)
        viewBinding.lifecycleOwner = this
        liveDataObserver(movieVM)
        movieVM.requestMovieRank()
        return view
    }

    private fun liveDataObserver(movieVM: MovieViewModel) {
        movieVM.getItemList().observe(this, Observer { itemList ->
            viewBinding.rvMovie.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(mContext).apply {
                    orientation = RecyclerView.VERTICAL
                }
                val itemClick: (item: Items, view: View) -> Unit = { item: Items, view: View ->
                    val intent = Intent(mContext, MovieDetailActivity::class.java).apply {
                        putExtra(INTENT_MOVIE_DATA, item)
                    }
                    val posterThumb = Pair.create(view, view.transitionName)
                    val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, posterThumb)
                    startActivity(intent, optionsCompat.toBundle())
                }
                adapter = RvMovieAdapter(itemList, itemClick, job)
            }
        })

        movieVM.getProgress().observe(this, Observer {
            if (it) {
                progressDialog.show()
            } else {
                progressDialog.cancel()
            }
        })

        movieVM.getThrowableData().observe(this, Observer {
            showThrowableToast(mContext, it)
        })
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}