package com.srpark.myapp.sevice

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.srpark.myapp.R
import com.srpark.myapp.di.interfaces.LottoApiRequest
import com.srpark.myapp.home.activity.SplashActivity
import com.srpark.myapp.preference.AppPreference
import com.srpark.myapp.utils.DatabaseConstant
import com.srpark.myapp.utils.RetrofitConstant
import com.srpark.myapp.utils.ServiceConstant.LOTTO_ACTION
import com.srpark.myapp.utils.ServiceConstant.LOTTO_ALARM_ID
import com.srpark.myapp.utils.strParse
import dagger.android.DaggerBroadcastReceiver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import java.util.*
import javax.inject.Inject

class LottoReceiver : DaggerBroadcastReceiver() {

    private val databaseRef = FirebaseDatabase.getInstance().reference

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var appPreference: AppPreference

    @Inject
    lateinit var lottoRetrofit: Retrofit

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == LOTTO_ACTION || intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            databaseRef.child(DatabaseConstant.LOTTO).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var lastDate = ""
                    var lastNo = 0L
                    dataSnapshot.children.map {
                        when (it.value) {
                            is String -> lastDate = it.value as String
                            is Long -> lastNo = it.value as Long
                        }
                    }

                    val nowCalendar = Calendar.getInstance().apply {
                        this.set(Calendar.SECOND, 0)
                        this.set(Calendar.MILLISECOND, 0)
                    }
                    val drwNoDate = strParse(lastDate, RetrofitConstant.DATE_FORMAT)
                    calculateDay(drwNoDate, lastNo, nowCalendar)
                }
            })
        }
    }

    private tailrec fun calculateDay(drwNoDate: Date, drwNo: Long, nowCalendar: Calendar) {
        val nextCalendar = Calendar.getInstance().apply {
            this.time = drwNoDate
            this.set(Calendar.DATE, this.get(Calendar.DATE) + 7)
            this.set(Calendar.HOUR_OF_DAY, 21)
            this.set(Calendar.MINUTE, 0)
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
        }

        if (nextCalendar.timeInMillis < nowCalendar.timeInMillis || nextCalendar.timeInMillis == nowCalendar.timeInMillis) {
            calculateDay(nextCalendar.time, drwNo + 1, nowCalendar)
        } else {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(LOTTO_ACTION).apply {
                setPackage(context.packageName)
            }
            val sender = PendingIntent.getBroadcast(context, LOTTO_ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextCalendar.timeInMillis, sender)
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, nextCalendar.timeInMillis, sender)
            }

            if (appPreference.drwNo != drwNo) {
                lottoRetrofit.create(LottoApiRequest::class.java).lottoWinPlace(drwNo)
                    .subscribeOn(Schedulers.io())
                    .filter { res -> "success" == res.returnValue }
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { res ->
                        databaseRef.child(DatabaseConstant.LOTTO).child(DatabaseConstant.LAST_DATE).setValue(res.drwNoDate)
                        databaseRef.child(DatabaseConstant.LOTTO).child(DatabaseConstant.LAST_NO).setValue(res.drwNo)
                            .addOnSuccessListener {
                                appPreference.drwNo = res.drwNo
                                showNotification(res.drwNo)
                            }
                    }
                    .subscribe()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun showNotification(drwNo: Long) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder: Notification.Builder
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val channel =
                    NotificationChannel(
                        LOTTO_ALARM_ID.toString(),
                        LOTTO_ALARM_ID.toString(),
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        setShowBadge(false)
                    }
                notificationManager.createNotificationChannel(channel)
                builder = Notification.Builder(context, LOTTO_ALARM_ID.toString())
            }
            else -> {
                builder = Notification.Builder(context).apply {
                    setPriority(Notification.PRIORITY_MAX)
                }
            }
        }
        builder.setOnlyAlertOnce(true)
            .setContentTitle(String.format(context.getString(R.string.lotto_notification_title), drwNo))
            .setSmallIcon(R.mipmap.app_icon)
            .setShowWhen(false)
            .setAutoCancel(true)
            .setVisibility(Notification.VISIBILITY_SECRET)
            .setContentText(context.getString(R.string.lotto_notification_content))
            .setContentIntent(pendingIntent)
        notificationManager.notify(LOTTO_ALARM_ID, builder.build())
    }
}