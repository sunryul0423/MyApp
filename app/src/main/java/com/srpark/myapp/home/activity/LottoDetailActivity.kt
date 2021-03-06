package com.srpark.myapp.home.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.srpark.myapp.R
import com.srpark.myapp.base.BaseActivity
import com.srpark.myapp.databinding.ActivityLottoDetailBinding
import com.srpark.myapp.home.adapter.RvLottoDetailAdapter
import com.srpark.myapp.home.adapter.RvLottoPlaceAdapter
import com.srpark.myapp.home.model.data.LottoInfoRes
import com.srpark.myapp.home.model.view.LottoPlaceVM
import com.srpark.myapp.utils.ActivityConstant.INTENT_LOTTO_DETAIL
import com.srpark.myapp.utils.setLottoValue
import com.srpark.myapp.utils.showThrowableToast
import kotlinx.coroutines.Job
import javax.inject.Inject

class LottoDetailActivity : BaseActivity<ActivityLottoDetailBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_lotto_detail

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        setToolbar(viewBinding.toolbar.toolbar)
        viewBinding.toolbar.toolbarTitle.text = getString(R.string.title_lotto_detail)
        val lottoPlaceVM = ViewModelProviders.of(this, viewModelFactory).get(LottoPlaceVM::class.java)
        liveDataObserver(lottoPlaceVM)
        viewBinding.lifecycleOwner = this

        val response = intent.getSerializableExtra(INTENT_LOTTO_DETAIL) as LottoInfoRes?
        if (response != null) {
            viewBinding.rvLottoDetail.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@LottoDetailActivity).apply {
                    orientation = RecyclerView.VERTICAL
                }
                // TODO 정보 수정
//                adapter = RvLottoDetailAdapter(response)
            }

            viewBinding.lottoView.tvLottoTitle.apply {
                text = String.format(getString(R.string.lotto_item_title), response.drwNo, response.drwNoDate)
                setTextColor(ContextCompat.getColor(this@LottoDetailActivity, android.R.color.white))
            }

            viewBinding.lottoView.ivPlus.background = ContextCompat.getDrawable(this, R.drawable.ic_add_white)

            setLottoValue(this, viewBinding.lottoView.tvLottoNo1, response.drwtNo1)
            setLottoValue(this, viewBinding.lottoView.tvLottoNo2, response.drwtNo2)
            setLottoValue(this, viewBinding.lottoView.tvLottoNo3, response.drwtNo3)
            setLottoValue(this, viewBinding.lottoView.tvLottoNo4, response.drwtNo4)
            setLottoValue(this, viewBinding.lottoView.tvLottoNo5, response.drwtNo5)
            setLottoValue(this, viewBinding.lottoView.tvLottoNo6, response.drwtNo6)
            setLottoValue(this, viewBinding.lottoView.tvLottoBonus, response.bnusNo)

            viewBinding.rvLottoPlace.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@LottoDetailActivity).apply {
                    orientation = RecyclerView.VERTICAL
                }
            }
            viewBinding.lottoPlaceVM = lottoPlaceVM.apply {
                requestLottoApi(response.drwNo)
            }
        }
    }

    private fun liveDataObserver(lottoPlaceVM: LottoPlaceVM) {
        lottoPlaceVM.getWinningPlaces().observe(this, Observer {
            //TODO 정보 수정
//            viewBinding.rvLottoPlace.adapter = RvLottoPlaceAdapter(it, job)
        })

        lottoPlaceVM.getProgress().observe(this, Observer {
            if (it) {
                progressDialog.show()
            } else {
                progressDialog.cancel()
            }
        })
        lottoPlaceVM.getThrowableData().observe(this, Observer {
            showThrowableToast(this, it)
        })
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}