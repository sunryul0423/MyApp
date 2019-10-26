package com.srpark.myapp.home.model.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.srpark.myapp.base.BaseViewModel
import com.srpark.myapp.home.model.data.UserData
import com.srpark.myapp.home.activity.SplashActivity
import com.srpark.myapp.utils.ServiceConstant
import javax.inject.Inject

class UserViewModel @Inject constructor(private val context: Context) : BaseViewModel() {

    private val userData = MutableLiveData<UserData>()
    private val isUpdate = MutableLiveData<Boolean>()
    private val firebaseUser = FirebaseAuth.getInstance().currentUser

    fun setUserData() {
        userData.value = firebaseUser?.let { data ->
            UserData(data.photoUrl.toString(), data.email, data.displayName)
        }
    }

    fun getUserData(): LiveData<UserData> {
        return userData
    }

    fun isUpdate(): LiveData<Boolean> {
        return isUpdate
    }

    /**
     * Firebase 프로필 업데이트
     */
    fun updateProfile(userName: String, imgUrl: String) {
        showProgress()
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(userName)
            .setPhotoUri(Uri.parse(imgUrl))
            .build()
        firebaseUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            isUpdate.value = task.isSuccessful
            cancelProgress()
        }
    }

    fun signOut() {
        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                var intent = Intent(ServiceConstant.LOTTO_ACTION).apply {
                    setPackage(context.packageName)
                }
                val sender =
                    PendingIntent.getBroadcast(context, ServiceConstant.LOTTO_ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                am.cancel(sender)
                intent = Intent(context, SplashActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
            }
    }
}