package com.srpark.myapp.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.srpark.myapp.home.fragment.MovieFragment
import com.srpark.myapp.home.fragment.MyAppFragment
import com.srpark.myapp.home.fragment.ShoppingFragment

class VpMainAdapter(context: FragmentActivity) : FragmentStateAdapter(context) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyAppFragment.getInstance()
            1 -> ShoppingFragment.getInstance()
            2 -> MovieFragment.getInstance()
            else -> Fragment()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}