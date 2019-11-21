package com.srpark.myapp.home.fragment

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.jakewharton.rxbinding2.view.RxView
import com.srpark.myapp.R
import com.srpark.myapp.base.BaseFragment
import com.srpark.myapp.databinding.FragmentMyappBinding
import com.srpark.myapp.home.activity.LottoDetailActivity
import com.srpark.myapp.home.activity.WebViewActivity
import com.srpark.myapp.home.model.view.MyAppViewModel
import com.srpark.myapp.sevice.GpsService
import com.srpark.myapp.utils.*
import com.srpark.myapp.utils.ActivityConstant.INTENT_LOTTO_DETAIL
import com.srpark.myapp.utils.ActivityConstant.INTENT_WEB_URL
import com.srpark.myapp.utils.RetrofitConstant.LOTTO_MY_PAGE_URL
import com.srpark.myapp.utils.RetrofitConstant.RANK_FIRST
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.Job
import srpark.rxactivity2.activity.RxActivityResult
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.roundToInt


@SuppressLint("StaticFieldLeak")
class MyAppFragment : BaseFragment<FragmentMyappBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.fragment_myapp

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var job: Job

    companion object {
        private val myAppFragment = MyAppFragment()
        private var CHECK_SERVICE = false
        fun getInstance(): MyAppFragment {
            return myAppFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val myAppVM = ViewModelProviders.of(this, viewModelFactory).get(MyAppViewModel::class.java)
        viewBinding.myAppVM = myAppVM
        viewBinding.lifecycleOwner = this
        liveDataObserver(myAppVM)
        rxEvent(myAppVM)

        if (isLocationEnabled(mContext)) {
            bindLocation(myAppVM)
        } else {
            myAppVM.requestWeatherApi()
        }
        viewBinding.cardWeather.btnUpdate.onClick(job = job) { checkLocation(myAppVM) }
        return view
    }

    private fun bindLocation(myAppVM: MyAppViewModel) {
        val gpsIntent = Intent(mContext, GpsService::class.java)
        val gpsServiceConnection: ServiceConnection = object : ServiceConnection {

            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val connect = this
                (service as GpsService.LocalBinder).getService().apply {
                    setGpsListener(object : GpsService.GpsListener {
                        override fun getLocation(location: Location) {
                            mContext.unbindService(connect)
                            viewBinding.cardWeather.ivSync.clearAnimation()
                            myAppVM.setLocation(location)
                            if (CHECK_SERVICE) {
                                mContext.stopService(Intent(mContext, GpsService::class.java))
                            }
                        }
                    }, false)
                }
            }

            override fun onServiceDisconnected(name: ComponentName) {
            }
        }
        ContextCompat.startForegroundService(mContext, Intent(mContext, GpsService::class.java))
        mContext.bindService(gpsIntent, gpsServiceConnection, Context.BIND_AUTO_CREATE)
        CHECK_SERVICE = true
    }

    private fun checkLocation(myAppVM: MyAppViewModel) {
        if (isLocationEnabled(mContext)) {
            val animation = AnimationUtils.loadAnimation(mContext, R.anim.rotation)
            viewBinding.cardWeather.ivSync.startAnimation(animation)
            bindLocation(myAppVM)
        } else {
            Snackbar.make(requireView(), getString(R.string.system_location_msg), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.setting)) {
                    val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    myAppVM.addDisposable(
                        RxActivityResult.from(mContext).activityForResult(myIntent)
                            .filter { isLocationEnabled(mContext) }
                            .subscribe { bindLocation(myAppVM) }
                    )
                }.show()
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun liveDataObserver(myAppVM: MyAppViewModel) {
        myAppVM.getLottoInfoResponse().observe(this, Observer { response ->
            viewBinding.cardLottoView.lottoView.tvLottoTitle.text =
                String.format(getString(R.string.lotto_item_title), response.drawNo, response.drawDate)

            setLottoValue(mContext, viewBinding.cardLottoView.lottoView.tvLottoNo1, response.num1)
            setLottoValue(mContext, viewBinding.cardLottoView.lottoView.tvLottoNo2, response.num2)
            setLottoValue(mContext, viewBinding.cardLottoView.lottoView.tvLottoNo3, response.num3)
            setLottoValue(mContext, viewBinding.cardLottoView.lottoView.tvLottoNo4, response.num4)
            setLottoValue(mContext, viewBinding.cardLottoView.lottoView.tvLottoNo5, response.num5)
            setLottoValue(mContext, viewBinding.cardLottoView.lottoView.tvLottoNo6, response.num6)
            setLottoValue(mContext, viewBinding.cardLottoView.lottoView.tvLottoBonus, response.bonusNum)

            response.lottoResult
                .filter { lottoResult -> RANK_FIRST == lottoResult.rank }
                .map { lottoResult ->
                    viewBinding.cardLottoView.tvWinnerCnt.text =
                        String.format(getString(R.string.lotto_first_winner_count), lottoResult.winningCnt)
                    viewBinding.cardLottoView.tvWinnerPrice.text =
                        String.format(
                            getString(R.string.lotto_first_winner_price),
                            transDecimalWon(mContext, lottoResult.winningPriceByRank)
                        )
                }
        })

        myAppVM.getWeatherResponse().observe(this, Observer { response ->
            viewBinding.cardWeather.tvAddress.text = response.station.name
            viewBinding.cardWeather.tvTemperature.text =
                String.format(getString(R.string.weather_temp_current), response.temperature.tc.toFloat().roundToInt())
            viewBinding.cardWeather.tvMinMax.text =
                String.format(
                    getString(R.string.weather_temp_min_max),
                    response.temperature.tmin.toFloat().roundToInt(),
                    response.temperature.tmax.toFloat().roundToInt()
                )
            viewBinding.cardWeather.ivStatus.background = getWeatherImage(response.sky.code)
        })

        myAppVM.getProgress().observe(this, Observer {
            if (it) {
                progressDialog.show()
            } else {
                progressDialog.cancel()
            }
        })
        myAppVM.getThrowableData().observe(this, Observer {
            showThrowableToast(mContext, it)
        })
    }

    private fun rxEvent(myAppVM: MyAppViewModel) {
        myAppVM.addDisposable(RxView.clicks(viewBinding.cardLottoView.btnLottoCheck)
            .throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe {
                val oItems = arrayOf<CharSequence>("QR 코드", "홈페이지")
                AlertDialog.Builder(mContext)
                    .setTitle("당첨결과 확인")
                    .setItems(oItems) { _, which ->
                        when (which) {
                            0 -> {
                                IntentIntegrator.forSupportFragment(this)
                                    .setBeepEnabled(false)
                                    .setOrientationLocked(false)
                                    .initiateScan()
                            }
                            1 -> {
                                val intent = Intent(mContext, WebViewActivity::class.java).apply {
                                    putExtra(INTENT_WEB_URL, LOTTO_MY_PAGE_URL)
                                }
                                startActivity(intent)
                            }
                        }
                    }
                    .setCancelable(true)
                    .show()
            }
        )

        myAppVM.addDisposable(RxView.clicks(viewBinding.cardLottoView.btnLottoDetail)
            .throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(mContext, LottoDetailActivity::class.java).apply {
                    putExtra(INTENT_LOTTO_DETAIL, myAppVM.getLottoInfoResponse().value)
                }
                startActivity(intent)
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        viewBinding.myAppVM?.addDisposable(
            Observable.just(result).filter {
                if (result == null) {
                    super.onActivityResult(requestCode, resultCode, data)
                    return@filter false
                } else {
                    return@filter !result.contents.isNullOrEmpty()
                }
            }.map {
                return@map Intent(mContext, WebViewActivity::class.java).apply {
                    putExtra(INTENT_WEB_URL, result.contents)
                }
            }.subscribe {
                startActivity(it)
            }
        )
    }

    private fun getWeatherImage(code: String): Drawable? {
        val skyArray =
            arrayOf(
                "SKY_A01", "SKY_A02", "SKY_A03", "SKY_A04", "SKY_A05", "SKY_A06", "SKY_A07",
                "SKY_A08", "SKY_A09", "SKY_A10", "SKY_A11", "SKY_A12", "SKY_A13", "SKY_A14"
            )
        return when (code) {
            skyArray[0] -> ContextCompat.getDrawable(mContext, R.drawable.icon_clear)
            skyArray[1], skyArray[6] -> ContextCompat.getDrawable(mContext, R.drawable.icon_light_cloudy)
            skyArray[2] -> ContextCompat.getDrawable(mContext, R.drawable.icon_heavy_cloud)
            skyArray[3], skyArray[7], skyArray[11] -> ContextCompat.getDrawable(mContext, R.drawable.icon_rain)
            skyArray[4], skyArray[8], skyArray[12] -> ContextCompat.getDrawable(mContext, R.drawable.icon_snow)
            skyArray[5], skyArray[9], skyArray[13] -> ContextCompat.getDrawable(mContext, R.drawable.icon_sleet)
            skyArray[10] -> ContextCompat.getDrawable(mContext, R.drawable.icon_storm)
            else -> ContextCompat.getDrawable(mContext, R.drawable.icon_clear)
        }
    }
}