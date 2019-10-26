package com.srpark.myapp.home.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.srpark.myapp.R
import com.srpark.myapp.base.BaseActivity
import com.srpark.myapp.databinding.ActivityMainBinding
import com.srpark.myapp.databinding.ViewDrawerHeaderBinding
import com.srpark.myapp.home.adapter.VpMainAdapter
import com.srpark.myapp.home.fragment.ShoppingFragment
import com.srpark.myapp.home.model.data.UserData
import com.srpark.myapp.home.model.view.UserViewModel
import com.srpark.myapp.slide.activity.ExchangeActivity
import com.srpark.myapp.slide.activity.LocationActivity
import com.srpark.myapp.slide.activity.ProfileActivity
import com.srpark.myapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import srpark.rxactivity2.activity.RxActivityResult
import srpark.rxactivity2.permission.RxPermission
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding>(), NavigationView.OnNavigationItemSelectedListener {

    override val layoutResourceId: Int
        get() = R.layout.activity_main

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var abToggle: ActionBarDrawerToggle
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private var backPressedTime = 0L
    private val intervalTime: Int = 2000

    private lateinit var inputManager: InputMethodManager
    private lateinit var headerBinding: ViewDrawerHeaderBinding
    private var userData: UserData? = null
    private var searchText = ""

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        headerBinding.userVM?.setUserData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(viewBinding.toolbar)
        inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        headerBinding = ViewDrawerHeaderBinding.inflate(LayoutInflater.from(this))
        val userVM = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel::class.java)
        headerBinding.userVM = userVM
        viewBinding.lifecycleOwner = this

        abToggle =
            ActionBarDrawerToggle(this, viewBinding.dlView, R.string.drawer_open, R.string.drawer_close)
        viewBinding.dlView.addDrawerListener(abToggle)
        viewBinding.dlView.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        viewBinding.navigationView.setNavigationItemSelectedListener(this)

        userVM.getUserData().observe(this, Observer { setDrawerHeader(it) })
        userVM.setUserData()
        setViewPager()

        addDisposable(RxView.clicks(viewBinding.actionSearch)
            .throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe {
                searchAction()
            }
        )

        addDisposable(RxView.clicks(viewBinding.actionRecord)
            .throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe {
                val permissionsList = arrayOf(Manifest.permission.RECORD_AUDIO)
                RxPermission.from(this).request(permissionsList)
                    .subscribe { result ->
                        if (result.isGranted) {
                            val intent = Intent(this, RecordActivity::class.java)
                            RxActivityResult.from(this).activityForResult(intent)
                                .filter { it.resultCode == Activity.RESULT_OK && !it.data.getStringExtra(ActivityConstant.INTENT_RECORD_DATA).isNullOrEmpty() }
                                .subscribe { activityResult ->
                                    val text = activityResult.data.getStringExtra(ActivityConstant.INTENT_RECORD_DATA)
                                    viewBinding.etSearchItem.setText(text)
                                    searchAction()
                                }
                        } else {
                            showPermissionSnackbar(window.decorView.rootView, result.permission)
                        }
                    }
            }
        )

        addDisposable(RxView.clicks(headerBinding.btnLogout)
            .throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe { userVM.signOut() }
        )

        addDisposable(RxTextView.editorActions(viewBinding.etSearchItem)
            .filter { actionId ->
                val editText = viewBinding.etSearchItem.text.toString()
                editText.isNotEmpty() && actionId == EditorInfo.IME_ACTION_SEARCH && searchText != editText
            }
            .map { return@map viewBinding.etSearchItem.text.toString() }
            .subscribe { editText ->
                searchText = editText
                ShoppingFragment.getInstance().callRequest(editText)
                closeInput()
            }
        )
    }


    private fun setDrawerHeader(it: UserData?) {
        viewBinding.navigationView.removeHeaderView(headerBinding.root)
        userData = it
        setCircleImageUrl(headerBinding.ivUserImg, userData?.userImage)
        headerBinding.tvUserName.text = userData?.userName
        headerBinding.tvUserEmail.text = userData?.userEmail
        viewBinding.navigationView.addHeaderView(headerBinding.root)
    }

    private fun setViewPager() {
        val mainAdapter = VpMainAdapter(this)
        viewBinding.mainViewPager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = mainAdapter
        }

        val stayListener = object : TabLayoutMediator.StayListener {
            override fun onSelectPosition(position: Int) {
                when (position) {
                    0 -> {
                        viewBinding.tvHeaderTitle.visibility = View.VISIBLE
                        viewBinding.searchView.visibility = View.GONE
                        closeInput()
                    }
                    1 -> {
                        viewBinding.tvHeaderTitle.visibility = View.GONE
                        viewBinding.searchView.visibility = View.VISIBLE
                    }
                    2 -> {
                        viewBinding.tvHeaderTitle.visibility = View.VISIBLE
                        viewBinding.searchView.visibility = View.GONE
                        closeInput()
                    }
                }
            }

            override fun onCreateTab(tab: TabLayout.Tab, position: Int) {
                when (position) {
                    0 -> tab.text = getString(R.string.tab_item_one)
                    1 -> tab.text = getString(R.string.tab_item_two)
                    2 -> tab.text = getString(R.string.tab_item_three)
                }
            }
        }

        tabLayoutMediator = TabLayoutMediator(viewBinding.tabLayout, viewBinding.mainViewPager, stayListener).also {
            it.attach()
        }
    }

    override fun onDestroy() {
        tabLayoutMediator.detach()
        super.onDestroy()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        abToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        abToggle.onConfigurationChanged(newConfig)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_profile -> {
                startTargetActivity(ProfileActivity::class.java)
                viewBinding.dlView.closeDrawer(GravityCompat.START)
            }
            R.id.item_exchange -> {
                startTargetActivity(ExchangeActivity::class.java)
                viewBinding.dlView.closeDrawer(GravityCompat.START)
            }
            R.id.item_location -> {
                if (isLocationEnabled(this)) {
                    startTargetActivity(LocationActivity::class.java)
                    viewBinding.dlView.closeDrawer(GravityCompat.START)
                } else {
                    Snackbar.make(window.decorView.rootView, getString(R.string.system_location_msg), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.setting)) {
                            val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            addDisposable(RxActivityResult.from(this).activityForResult(myIntent)
                                .filter { isLocationEnabled(this) }
                                .subscribe {
                                    startTargetActivity(LocationActivity::class.java)
                                    viewBinding.dlView.closeDrawer(GravityCompat.START)
                                }
                            )
                        }.show()
                }
            }
        }
        return false
    }

    /**
     * 검색 키보드 내리기
     */
    private fun closeInput() {
        viewBinding.etSearchItem.clearFocus()
        inputManager.hideSoftInputFromWindow(viewBinding.etSearchItem.windowToken, 0)
    }

    override fun onBackPressed() {
        if (viewBinding.dlView.isDrawerOpen(GravityCompat.START)) {
            viewBinding.dlView.closeDrawer(GravityCompat.START)
        } else {
            val currentTime = System.currentTimeMillis()
            val intervalTime = currentTime - backPressedTime
            if (intervalTime in 0..this.intervalTime) {
                super.onBackPressed()
            } else {
                backPressedTime = currentTime
                showToast(this, getString(R.string.app_finish_msg))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewBinding.dlView.openDrawer(GravityCompat.START)
                closeInput()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun searchAction() {
        val editText = viewBinding.etSearchItem.text.toString()
        if (editText.isNotEmpty() && searchText != editText) {
            searchText = editText
            ShoppingFragment.getInstance().callRequest(editText)
            closeInput()
        }
    }
}
