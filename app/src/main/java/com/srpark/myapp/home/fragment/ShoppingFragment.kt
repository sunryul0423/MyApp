package com.srpark.myapp.home.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.srpark.myapp.R
import com.srpark.myapp.base.BaseFragment
import com.srpark.myapp.databinding.FragmentShoppingBinding
import com.srpark.myapp.home.adapter.RvShoppingAdapter
import com.srpark.myapp.home.model.view.ShoppingViewModel
import com.srpark.myapp.utils.RetrofitConstant
import com.srpark.myapp.utils.showThrowableToast
import javax.inject.Inject


@SuppressLint("StaticFieldLeak")
class ShoppingFragment : BaseFragment<FragmentShoppingBinding>() {

    companion object {
        private var shoppingFragment = ShoppingFragment()
        fun getInstance(): ShoppingFragment {
            return shoppingFragment
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_shopping

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val shoppingAdapter = RvShoppingAdapter()
    private var searchText = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val shoppingVM = ViewModelProviders.of(this, viewModelFactory).get(ShoppingViewModel::class.java)
        viewBinding.shoppingVM = shoppingVM
        viewBinding.lifecycleOwner = this

        viewBinding.rvShopping.apply {
            val dividerItemDecoration = DividerItemDecoration(mContext, LinearLayoutManager(mContext).orientation)
            val drawable = ContextCompat.getDrawable(mContext, R.drawable.shopping_divider_drawable)
            drawable?.let { divider ->
                dividerItemDecoration.setDrawable(divider)
            }
            addItemDecoration(dividerItemDecoration)
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(mContext).apply {
                orientation = RecyclerView.VERTICAL
            }
            adapter = shoppingAdapter
            val inputManager = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            setOnClickListener {
                inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            }
        }

        viewBinding.shopSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                callRequest(searchText, position)
            }
        }

        shoppingVM.getProgress().observe(this, Observer {
            if (it) {
                progressDialog.show()
            } else {
                progressDialog.cancel()
            }
        })
        shoppingVM.getThrowableData().observe(this, Observer {
            showThrowableToast(mContext, it)
        })

        return view
    }

    fun callRequest(text: String, position: Int = viewBinding.shopSpinner.selectedItemPosition) {
        searchText = text
        when (position) {
            0 -> shoppingRequest(text, RetrofitConstant.SIM)
            1 -> shoppingRequest(text, RetrofitConstant.DATE)
            2 -> shoppingRequest(text, RetrofitConstant.DSC)
            3 -> shoppingRequest(text, RetrofitConstant.ASC)
        }
    }

    private fun shoppingRequest(text: String, sort: String) {
        viewBinding.shoppingVM?.getPageLiveData(text, sort)?.observe(this, Observer {
            shoppingAdapter.submitList(it)
        })
    }
}