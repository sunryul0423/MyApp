package com.srpark.myapp.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.srpark.myapp.R
import com.srpark.myapp.databinding.ItemLottoPlaceBinding
import com.srpark.myapp.home.activity.MapViewActivity
import com.srpark.myapp.home.model.data.LottoInfoRes
import com.srpark.myapp.utils.ActivityConstant.INTENT_MAP_DATA
import com.srpark.myapp.utils.onClick
import kotlinx.coroutines.Job

class RvLottoPlaceAdapter(private val lottoResult: List<LottoInfoRes>, private val job: Job) :
    RecyclerView.Adapter<RvLottoPlaceAdapter.LottoPlaceViewHolder>() {

    companion object {
        private const val AUTO = "AUTO"
        private const val SEMI_AUTO = "SEMI_AUTO"
        private const val MANUAL = "MANUAL"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LottoPlaceViewHolder {
        val binding = ItemLottoPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LottoPlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LottoPlaceViewHolder, position: Int) {
        holder.setData(lottoResult[position])
    }

    override fun getItemCount(): Int {
        return lottoResult.size
    }

    inner class LottoPlaceViewHolder(private val binding: ItemLottoPlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        internal fun setData(lottoResult: LottoInfoRes) {
            val context = binding.root.context
            //TODO 정보 수정
//            binding.tvName.text = lottoResult.shopName
//            binding.tvAddress.text = lottoResult.address
//            getType(context, binding.tvType, lottoResult.gameType)
            binding.ivLocation.onClick(job = job) {
                val intent = Intent(context, MapViewActivity::class.java).apply {
                    putExtra(INTENT_MAP_DATA, lottoResult)
                }
                context.startActivity(intent)
            }
        }
    }

    private fun getType(context: Context, textView: TextView, type: String) {
        when (type) {
            AUTO -> {
                textView.text = "자동"
                textView.setTextColor(ContextCompat.getColor(context, R.color.red))
            }
            SEMI_AUTO -> {
                textView.text = "반자동"
                textView.setTextColor(ContextCompat.getColor(context, R.color.btnConfirm))
            }
            MANUAL -> {
                textView.text = "수동"
                textView.setTextColor(ContextCompat.getColor(context, R.color.green))
            }
            else -> textView.text = "알수없음"
        }
    }
}