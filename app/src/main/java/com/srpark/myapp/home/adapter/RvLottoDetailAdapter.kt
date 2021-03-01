package com.srpark.myapp.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.srpark.myapp.R
import com.srpark.myapp.databinding.ItemLottoDetailBinding
import com.srpark.myapp.home.model.data.LottoInfoRes
import com.srpark.myapp.utils.RetrofitConstant.RANK_FIFTH
import com.srpark.myapp.utils.RetrofitConstant.RANK_FIRST
import com.srpark.myapp.utils.RetrofitConstant.RANK_FOURTH
import com.srpark.myapp.utils.RetrofitConstant.RANK_SECOND
import com.srpark.myapp.utils.RetrofitConstant.RANK_THIRD
import com.srpark.myapp.utils.transDecimalWon

class RvLottoDetailAdapter(private val lottoResult: List<LottoInfoRes>) :
    RecyclerView.Adapter<RvLottoDetailAdapter.LottoDetailViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LottoDetailViewHolder {
        val binding = ItemLottoDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LottoDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LottoDetailViewHolder, position: Int) {
        holder.setData(lottoResult[position])
    }

    override fun getItemCount(): Int {
        return lottoResult.size
    }

    inner class LottoDetailViewHolder(private val binding: ItemLottoDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        internal fun setData(lottoResult: LottoInfoRes) {
            val context = binding.root.context
            //TODO 정보 수정
//            binding.tvRank.apply {
//                text = String.format(context.getString(R.string.lotto_winner_rank), getRanking(lottoResult.rank))
//                setTextColor(ContextCompat.getColor(context, android.R.color.white))
//            }
//            binding.tvWinnerCnt.apply {
//                text = String.format(context.getString(R.string.lotto_winner_count), lottoResult.winningCnt)
//                setTextColor(ContextCompat.getColor(context, android.R.color.white))
//            }
//            binding.tvWinnerPrice.apply {
//                text = transDecimalWon(context, lottoResult.winningPriceByRank)
//                setTextColor(ContextCompat.getColor(context, android.R.color.white))
//            }
        }
    }

    private fun getRanking(rank: String): Int {
        return when (rank) {
            RANK_FIRST -> 1
            RANK_SECOND -> 2
            RANK_THIRD -> 3
            RANK_FOURTH -> 4
            RANK_FIFTH -> 5
            else -> 0
        }
    }
}