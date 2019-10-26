package com.srpark.myapp.slide.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.srpark.myapp.R
import com.srpark.myapp.base.BaseActivity
import com.srpark.myapp.databinding.ActivityProfileBinding
import com.srpark.myapp.home.activity.MainActivity
import com.srpark.myapp.home.model.data.UserData
import com.srpark.myapp.home.model.view.UserViewModel
import com.srpark.myapp.utils.setCircleImageUrl
import com.srpark.myapp.utils.showPermissionSnackbar
import com.srpark.myapp.utils.showSnackbar
import com.srpark.myapp.utils.showThrowableToast
import io.reactivex.android.schedulers.AndroidSchedulers
import srpark.rxactivity2.activity.RxActivityResult
import srpark.rxactivity2.permission.RxPermission
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ProfileActivity : BaseActivity<ActivityProfileBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_profile

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var inputManager: InputMethodManager

    private var imgUrl: String? = ""
    private var isProfileUpdate = false
    private var userData: UserData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(viewBinding.toolbar.toolbar)
        viewBinding.toolbar.toolbarTitle.text = getString(R.string.title_profile)

        val userVM = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel::class.java)
        viewBinding.userVM = userVM
        inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        userVM.getUserData().observe(this, Observer { _userData ->
            userData = _userData
            imgUrl = _userData.userImage
            setCircleImageUrl(viewBinding.ivUserImg, imgUrl)
            viewBinding.etUserName.setText(_userData.userName)
            viewBinding.tvUserEmail.text = _userData.userEmail
        })
        userVM.setUserData()

        userVM.isUpdate().observe(this, Observer {
            isProfileUpdate = it
            if (it) showSnackbar(window.decorView.rootView, "프로필 수정 완료")
            else showSnackbar(window.decorView.rootView, "프로필 수정 실패")
        })

        userVM.getProgress().observe(this, Observer {
            if (it) progressDialog.show()
            else progressDialog.cancel()
        })

        userVM.getThrowableData().observe(this, Observer {
            showThrowableToast(this, it)
        })

        addDisposable(RxView.clicks(viewBinding.flUserImg)
            .throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe { checkImagePermission() }
        )

        addDisposable(RxTextView.textChanges(viewBinding.etUserName)
            .subscribe { invalidateData() }
        )

        addDisposable(RxView.clicks(viewBinding.btnUpdate)
            .throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe {
                val userName = viewBinding.etUserName.text.toString()
                userData = UserData(imgUrl, userData?.userEmail, userName)
                userVM.updateProfile(userName, imgUrl ?: "")
            }
        )
    }

    /**
     * 갤러리 접근 권한 확인
     */
    private fun checkImagePermission() {
        val permissionsList = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        addDisposable(RxPermission.from(this).request(permissionsList).subscribe { result ->
            if (result.isGranted) {
                val intent = Intent(Intent.ACTION_PICK).apply {
                    type = MediaStore.Images.Media.CONTENT_TYPE
                }
                RxActivityResult.from(this)
                    .activityForResult(intent)
                    .filter { it.resultCode == RESULT_OK }
                    .subscribe { activityResult ->
                        imgUrl = activityResult.data.data.toString()
                        setCircleImageUrl(viewBinding.ivUserImg, imgUrl)
                        invalidateData()
                    }
            } else {
                showPermissionSnackbar(window.decorView.rootView, result.permission)
            }
        })
    }

    private fun invalidateData() {
        val text = viewBinding.etUserName.text.toString()
        viewBinding.btnUpdate.isEnabled = !(text.isEmpty() || (text == userData?.userName && imgUrl == userData?.userImage))
    }

    override fun onBackPressed() {
        if (isProfileUpdate) {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        } else {
            super.onBackPressed()
        }
    }
}