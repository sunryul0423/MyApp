package com.srpark.myapp.utils

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.*
import com.google.android.material.tabs.TabLayout
import java.lang.ref.WeakReference


class TabLayoutMediator(
    private val tabLayout: TabLayout,
    private val viewPager: ViewPager2,
    private val stayListener: StayListener
) {
    private var adapter: RecyclerView.Adapter<*>? = null
    private var attached: Boolean = false
    private var onPageChangeCallback: OnPageChangedListener? = null
    private var onTabSelectedListener: TabLayout.OnTabSelectedListener? = null

    interface StayListener {
        fun onCreateTab(tab: TabLayout.Tab, position: Int)
        fun onSelectPosition(position: Int)
    }

    fun attach() {
        check(!attached) { "TabLayoutMediator is already attached" }
        adapter = checkNotNull(viewPager.adapter) { "TabLayoutMediator attached before ViewPager2 has an " + "adapter" }
        onPageChangeCallback = OnPageChangedListener(tabLayout).also {
            viewPager.registerOnPageChangeCallback(it)
        }
        onTabSelectedListener = OnTebSelectedListener(viewPager, stayListener).also {
            tabLayout.addOnTabSelectedListener(it)
        }
        populateTabsFromPagerAdapter()
        tabLayout.setScrollPosition(viewPager.currentItem, 0f, true)
        attached = true
    }

    fun detach() {
        onTabSelectedListener?.let {
            tabLayout.removeOnTabSelectedListener(it)
            null
        }
        onPageChangeCallback?.let {
            viewPager.unregisterOnPageChangeCallback(it)
            null
        }
        adapter = null
        attached = false
    }

    private fun populateTabsFromPagerAdapter() {
        adapter?.let {
            val adapterCount = it.itemCount
            for (position in 0 until adapterCount) {
                tabLayout.newTab().also { tab ->
                    stayListener.onCreateTab(tab, position)
                    tabLayout.addTab(tab, false)
                }
            }
            if (adapterCount > 0) {
                val currItem = viewPager.currentItem
                if (currItem != tabLayout.selectedTabPosition) {
                    tabLayout.getTabAt(currItem)?.select()
                }
            }
        }
    }

    private class OnPageChangedListener(tabLayout: TabLayout) :
        ViewPager2.OnPageChangeCallback() {

        private val tabLayoutRef: WeakReference<TabLayout> = WeakReference(tabLayout)
        private var previousScrollState: Int = 0
        private var scrollState: Int = 0

        init {
            reset()
        }

        override fun onPageScrollStateChanged(state: Int) {
            previousScrollState = scrollState
            scrollState = state
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            tabLayoutRef.get()?.let { tabLayout ->
                val isUpdateText = scrollState != SCROLL_STATE_SETTLING || previousScrollState == SCROLL_STATE_DRAGGING
                tabLayout.setScrollPosition(position, positionOffset, isUpdateText)
            }
        }

        override fun onPageSelected(position: Int) {
            tabLayoutRef.get()?.let { tabLayout ->
                if (tabLayout.selectedTabPosition != position && position < tabLayout.tabCount) {
                    tabLayout.getTabAt(position)?.select()
                }
            }
        }

        internal fun reset() {
            scrollState = SCROLL_STATE_IDLE
            previousScrollState = scrollState
        }
    }

    private class OnTebSelectedListener(private val viewPager: ViewPager2, private val stayListener: StayListener) :
        TabLayout.OnTabSelectedListener {

        override fun onTabSelected(tab: TabLayout.Tab) {
            viewPager.setCurrentItem(tab.position, true)
            stayListener.onSelectPosition(tab.position)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
        }
    }
}