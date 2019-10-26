package com.srpark.myapp.home.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.srpark.myapp.R
import com.srpark.myapp.base.BaseActivity
import com.srpark.myapp.databinding.ActivitySplashBinding
import com.srpark.myapp.utils.getLoginIntent
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.*
import srpark.rxactivity2.activity.RxActivityResult
import srpark.rxactivity2.permission.RxPermission
import kotlin.coroutines.CoroutineContext

class SplashActivity : BaseActivity<ActivitySplashBinding>(), CoroutineScope {
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override val layoutResourceId: Int
        get() = R.layout.activity_splash

    private lateinit var alertDialog: AlertDialog

    private val firebaseUser = FirebaseAuth.getInstance().currentUser
    private var isGranted: Boolean = false
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        alertDialog = AlertDialog.Builder(this@SplashActivity)
            .setPositiveButton(getString(R.string.setting)) { _, _ ->
                var intent: Intent? = null
                try {
                    intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        this.data = Uri.parse("package:$packageName")
                    }
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                } finally {
                    startActivity(intent)
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> finish() }
            .setCancelable(false)
            .create()
    }

    override fun onResume() {
        super.onResume()
        if (!alertDialog.isShowing && !isGranted) {
            requestPermission()
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun requestPermission() {
        addDisposable(
            RxPermission.from(this).request(permissions)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    isGranted = result.isGranted
                    if (isGranted) {
                        launch {
                            delay(1000)
                            if (firebaseUser != null) {
                                startTargetActivity(MainActivity::class.java)
                                finish()
                            } else {
                                addDisposable(RxActivityResult.from(this@SplashActivity)
                                    .activityForResult(getLoginIntent())
                                    .subscribe {
                                        if (it.resultCode == RESULT_OK) {
                                            startTargetActivity(MainActivity::class.java)
                                        }
                                        finish()
                                    })
                            }
                        }
                    } else {
                        alertDialog.setMessage(result.permission)
                        alertDialog.show()
                    }
                }
        )
    }
}