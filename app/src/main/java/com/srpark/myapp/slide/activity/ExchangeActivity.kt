package com.srpark.myapp.slide.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.srpark.myapp.R
import com.srpark.myapp.base.BaseActivity
import com.srpark.myapp.databinding.ActivityExchangeBinding
import com.srpark.myapp.home.adapter.RvExchangeAdapter
import com.srpark.myapp.slide.model.ExchangeViewModel
import javax.inject.Inject

class ExchangeActivity : BaseActivity<ActivityExchangeBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_exchange

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(viewBinding.toolbar.toolbar)
        viewBinding.toolbar.toolbarTitle.text = getString(R.string.title_exchange)

        val exchangeVM = ViewModelProviders.of(this, viewModelFactory).get(ExchangeViewModel::class.java)
        viewBinding.lifecycleOwner = this

        exchangeVM.getExchangeResponse().observe(this, Observer { responseList ->
            viewBinding.rvExchange.apply {
                val dividerItemDecoration =
                    DividerItemDecoration(this@ExchangeActivity, LinearLayoutManager(this@ExchangeActivity).orientation)
                val drawable = ContextCompat.getDrawable(this@ExchangeActivity, R.drawable.exchange_divider_drawable)
                drawable?.let { divider ->
                    dividerItemDecoration.setDrawable(divider)
                }
                addItemDecoration(dividerItemDecoration)
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@ExchangeActivity).apply {
                    orientation = RecyclerView.VERTICAL
                }
                adapter = RvExchangeAdapter(responseList)
            }
        })
    }
}