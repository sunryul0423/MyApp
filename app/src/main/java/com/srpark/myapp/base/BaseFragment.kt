package com.srpark.myapp.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.srpark.myapp.dialog.ProgressDialog
import dagger.android.support.DaggerFragment

abstract class BaseFragment<T : ViewDataBinding> : DaggerFragment() {

    protected lateinit var viewBinding: T
    protected abstract val layoutResourceId: Int
    protected lateinit var mContext: Context
    protected lateinit var progressDialog: ProgressDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)
        progressDialog = ProgressDialog(mContext)
        return viewBinding.root
    }
}