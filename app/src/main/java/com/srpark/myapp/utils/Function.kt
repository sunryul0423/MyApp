package com.srpark.myapp.utils

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AnyRes
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.srpark.myapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


fun showToast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun showThrowableToast(context: Context, throwable: Throwable) {
    when (throwable) {
        is HttpException -> {
            when (throwable.code()) {
                403 -> showToast(context, context.getString(R.string.throwable_http_code_403))
            }
        }
        is SocketTimeoutException -> showToast(context, context.getString(R.string.throwable_socket_timeout))
        is IOException -> showToast(context, context.getString(R.string.throwable_network_error))
        else -> throwable.message?.let { message -> showToast(context, message) }
    }
}

fun showSnackbar(view: View, msg: String) {
    Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
}

fun showPermissionSnackbar(view: View, msg: String) {
    val context = view.context
    Snackbar.make(view, getPermissionMsg(context, msg), Snackbar.LENGTH_LONG)
        .setAction(context.getString(R.string.setting)) {
            var intent: Intent? = null
            try {
                intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    this.data = Uri.parse("package:" + context.packageName)
                }
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
            } finally {
                context.startActivity(intent)
            }
        }.show()
}

fun getPermissionMsg(context: Context, permission: String): String {
    return when (permission) {
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
            context.resources.getString(R.string.permission_read_msg)
        }
        Manifest.permission.RECORD_AUDIO -> {
            context.resources.getString(R.string.permission_audio_msg)
        }
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION -> {
            context.resources.getString(R.string.permission_location_msg)
        }
        else -> {
            ""
        }
    }
}

/**
 * 자신의 폰번호 가져오기
 */
//@SuppressLint("HardwareIds")
//fun getPhoneNumber(context: Context): String {
//    val phoneMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
//    return if (ActivityCompat.checkSelfPermission(
//            context,
//            Manifest.permission.READ_PHONE_STATE
//        ) != PackageManager.PERMISSION_GRANTED ||
//        ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
//        phoneMgr == null
//    ) {
//        ""
//    } else {
//        phoneMgr.line1Number
//    }
//}

/**
 * date 객체를 format 형식의 날짜로 변환
 * @param date 객체 (Date(), System.currentTimeMillis()...)
 * @param format 날짜 format (ex)yyyyMMddHHmm)
 * @return 날짜 format 형식의 String
 */
fun strFormat(date: Any, format: String): String {
    return SimpleDateFormat(format, Locale.getDefault()).format(date)
}

/**
 * format 형식의 String 값을 Date 형식으로 변환
 * @param date String 날짜
 * @param format 날짜 format (ex)yyyyMMddHHmm)
 * @return 날짜 format 형식의 Date()
 */
fun strParse(date: String, format: String): Date {
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    return SimpleDateFormat(format, Locale.getDefault()).parse(date)
}

/**
 * 화페단위(원) 적용
 * @param number 숫자
 * @return ex)45,000원
 */
fun transDecimalWon(context: Context, number: Any): String {
    return DecimalFormat("#,###").format(number).plus(context.getString(R.string.won))
}

/**
 * 로또 결과 설정
 */
fun setLottoValue(context: Context, textView: TextView, number: Int) {
    textView.apply {
        text = number.toString()
        val resource: Pair<Int, Drawable?>
        when (number) {
            in 1..10 -> resource = Pair(
                ContextCompat.getColor(context, android.R.color.black),
                ContextCompat.getDrawable(context, R.drawable.lotto_yellow_value_background)
            )
            in 11..20 -> resource = Pair(
                ContextCompat.getColor(context, android.R.color.white),
                ContextCompat.getDrawable(context, R.drawable.lotto_blue_value_background)
            )
            in 21..30 -> resource = Pair(
                ContextCompat.getColor(context, android.R.color.white),
                ContextCompat.getDrawable(context, R.drawable.lotto_red_value_background)
            )
            in 31..40 -> resource = Pair(
                ContextCompat.getColor(context, android.R.color.black),
                ContextCompat.getDrawable(context, R.drawable.lotto_gray_value_background)
            )
            else -> resource = Pair(
                ContextCompat.getColor(context, android.R.color.white),
                ContextCompat.getDrawable(context, R.drawable.lotto_green_value_background)
            )
        }
        setTextColor(resource.first)
        background = resource.second
    }
}

/**
 * Resource 위치를 Uri 주소로 가져오기
 */
fun getUriToResource(context: Context, @AnyRes resId: Int): String {
    val res = context.resources
    return Uri.parse(
        ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(resId)
                + '/'.toString() + res.getResourceTypeName(resId)
                + '/'.toString() + res.getResourceEntryName(resId)
    ).toString()
}

fun getMetaData(context: Context, name: String): String {
    val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
    return appInfo.metaData.getString(name) ?: ""
}

fun getLoginIntent(): Intent {
    return AuthUI.getInstance().createSignInIntentBuilder().apply {
        setAvailableProviders(
            arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.FacebookBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )
        )
        setTheme(R.style.LoginAppTheme)
        setLogo(R.drawable.icon_app)
        setIsSmartLockEnabled(false)
    }.build()
}

/**
 * 위치 정보 사용 가능 여부
 *
 * @param context context
 * @return 위치 정보 사용 가능 여부
 */
fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

/**
 * 코루틴 Onclick Method
 */
@UseExperimental(ObsoleteCoroutinesApi::class)
fun View.onClick(job: Job, action: suspend (View) -> Unit) {
    val event = CoroutineScope(Dispatchers.Main + job).actor<View> {
        for (event in channel)
            action(event)
    }
    setOnClickListener {
        event.offer(it)
    }
}