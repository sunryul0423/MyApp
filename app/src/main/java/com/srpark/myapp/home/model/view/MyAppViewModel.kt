package com.srpark.myapp.home.model.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.srpark.myapp.base.BaseViewModel
import com.srpark.myapp.di.LottoRetrofit
import com.srpark.myapp.di.WeatherRetrofit
import com.srpark.myapp.di.interfaces.LottoApiRequest
import com.srpark.myapp.di.interfaces.WeatherApiRequest
import com.srpark.myapp.home.model.data.LottoInfoRes
import com.srpark.myapp.home.model.data.WeatherResponse
import com.srpark.myapp.preference.AppPreference
import com.srpark.myapp.utils.*
import com.srpark.myapp.utils.ServiceConstant.LOTTO_ACTION
import com.srpark.myapp.utils.ServiceConstant.LOTTO_ALARM_ID
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class MyAppViewModel @Inject constructor(
    private val context: Context,
    @LottoRetrofit private val lottoApiRequest: LottoApiRequest,
    @WeatherRetrofit private val weatherApiRequest: WeatherApiRequest,
    private val appPreference: AppPreference
) : BaseViewModel() {

    companion object {
        private const val APP_KEY = "appKey"
        private const val LAT = "lat"
        private const val LON = "lon"
    }

    private val lottoInfoResponse = MutableLiveData<LottoInfoRes>()
    private val weatherResponse = MutableLiveData<WeatherResponse.Weather.Minutely>()
    private val databaseRef = FirebaseDatabase.getInstance().reference

    init {
        databaseRef.child(DatabaseConstant.LOTTO)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    throwableData.value = databaseError.toException()
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
                    calculateDay(strParse(lastDate, RetrofitConstant.DATE_FORMAT), lastNo, nowCalendar)
                }
            })
    }

    fun requestWeatherApi() {
        showProgress()
        val locationList: List<String> = appPreference.location.split(",")
        val param = hashMapOf(
            APP_KEY to getMetaData(context, "weatherApiKey"),
            LAT to locationList[0],
            LON to locationList[1]
        )
        addDisposable(weatherApiRequest.weatherInfo(param)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { it.result.code == 9200 }
            .subscribe({
                val addressName = getCurrentAddress(locationList[0].toDouble(), locationList[1].toDouble())
                if (addressName.isNotEmpty()) {
                    it.weather.minutely[0].station.name = addressName
                }
                weatherResponse.value = it.weather.minutely[0]
                cancelProgress()
            }, {
                onError(it)
            })
        )
    }

    @Suppress("NO_TAIL_CALLS_FOUND", "NON_TAIL_RECURSIVE_CALL")
    private tailrec fun requestLottoApi(number: Long) {
        showProgress()
        addDisposable(
            lottoApiRequest.lottoWinPlace(number)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response.returnValue == "fail") {
                        requestLottoApi(number.minus(1))
                    } else {
                        lottoInfoResponse.value = response
                        cancelProgress()
                    }
                }, {
                    onError(it)
                })
        )
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
            val intent = Intent(LOTTO_ACTION).apply {
                setPackage(context.packageName)
            }
            var sender = PendingIntent.getBroadcast(context, LOTTO_ALARM_ID, intent, PendingIntent.FLAG_NO_CREATE)
            if (sender == null) {
                val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                sender = PendingIntent.getBroadcast(context, LOTTO_ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextCalendar.timeInMillis, sender)
                } else {
                    am.setExact(AlarmManager.RTC_WAKEUP, nextCalendar.timeInMillis, sender)
                }
            }

            if (appPreference.drwNo != drwNo) {
                databaseRef.child(DatabaseConstant.LOTTO).child(DatabaseConstant.LAST_DATE)
                    .setValue(strFormat(drwNoDate, RetrofitConstant.DATE_FORMAT))
                databaseRef.child(DatabaseConstant.LOTTO).child(DatabaseConstant.LAST_NO).setValue(drwNo).addOnSuccessListener {
                    appPreference.drwNo = drwNo
                    requestLottoApi(drwNo)
                }
            } else {
                requestLottoApi(drwNo)
            }
        }
    }

    fun setLocation(location: Location) {
        val nowLocation = "${location.latitude},${location.longitude}"
        if (appPreference.location != nowLocation) {
            appPreference.location = nowLocation
        }
        requestWeatherApi()
    }

    private fun getCurrentAddress(latitude: Double, longitude: Double): String {
        val geoCoder = Geocoder(context, Locale.getDefault())
        val addrList = geoCoder.getFromLocation(latitude, longitude, 1)
        return if (addrList.isNullOrEmpty()) {
            ""
        } else {
            val addr = addrList[0]
            if (addr.locality.isNullOrEmpty()) {
                addr.adminArea + " " + addr.subLocality
            } else {
                addr.locality + " " + addr.subLocality
            }
        }
    }

    fun getLottoInfoResponse(): LiveData<LottoInfoRes> {
        return lottoInfoResponse
    }

    fun getWeatherResponse(): LiveData<WeatherResponse.Weather.Minutely> {
        return weatherResponse
    }
}