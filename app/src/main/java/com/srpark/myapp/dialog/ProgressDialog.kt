package com.srpark.myapp.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.srpark.myapp.databinding.DialogProgressBinding

class ProgressDialog(mContext: Context) : Dialog(mContext) {

    private val binding: DialogProgressBinding by lazy {
        DialogProgressBinding.inflate(LayoutInflater.from(mContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val manager = WindowManager.LayoutParams()
        manager.gravity = Gravity.CENTER
        manager.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        manager.dimAmount = 0.4f
        window?.attributes = manager
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        setContentView(binding.root)

        binding.progressBar.show()
    }
}