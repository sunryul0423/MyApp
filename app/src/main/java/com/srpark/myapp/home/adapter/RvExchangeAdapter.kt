package com.srpark.myapp.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.srpark.myapp.R
import com.srpark.myapp.databinding.ItemExchangeBinding
import com.srpark.myapp.home.model.data.ExchangeRes
import com.srpark.myapp.utils.transDecimalWon
import java.math.RoundingMode

class RvExchangeAdapter(private val responseList: List<ExchangeRes>) :
    RecyclerView.Adapter<RvExchangeAdapter.ExchangeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeViewHolder {
        val binding = ItemExchangeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExchangeViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ExchangeViewHolder, position: Int) {
        holder.setData(responseList[position])
    }

    override fun getItemCount(): Int {
        return responseList.size
    }

    inner class ExchangeViewHolder(private val binding: ItemExchangeBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        internal fun setData(exchangeResponse: ExchangeRes) {
            binding.tvTargetNation.text = exchangeResponse.country.plus(" ").plus(exchangeResponse.currencyCode)
            when (exchangeResponse.currencyCode) {
                "USD" -> binding.tvTargetNation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_usa, 0, 0, 0)
                "JPY" -> binding.tvTargetNation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_japan, 0, 0, 0)
                "CNY" -> binding.tvTargetNation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_china, 0, 0, 0)
                "EUR" -> binding.tvTargetNation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_europe, 0, 0, 0)
            }
            binding.tvTargetPrice.text = exchangeResponse.currencyUnit.toString().plus(exchangeResponse.currencyName)
            binding.tvKoreaPrice.text = transDecimalWon(binding.root.context, exchangeResponse.basePrice)

            binding.tvChangePrice.apply {
                when {
                    exchangeResponse.signedChangePrice < 0 -> {
                        text = getChangePriceValue("▼", exchangeResponse)
                        setTextColor(ContextCompat.getColor(binding.root.context, R.color.blue))
                    }
                    0 < exchangeResponse.signedChangePrice -> {
                        text = getChangePriceValue("▲", exchangeResponse)
                        setTextColor(ContextCompat.getColor(binding.root.context, R.color.red))
                    }
                    else -> {
                        text = getChangePriceValue("-", exchangeResponse)
                        setTextColor(ContextCompat.getColor(binding.root.context, android.R.color.black))
                    }
                }
            }
        }

        private fun getChangePriceValue(plusString: String, exchangeResponse: ExchangeRes): String {
            return if ("-" == plusString) {
                binding.root.context.getString(
                    R.string.exchange_rate,
                    exchangeResponse.changePrice.toString(),
                    exchangeResponse.signedChangeRate.times(100).toBigDecimal()
                        .setScale(2, RoundingMode.HALF_UP).toString().plus("%")
                )
            } else {
                binding.root.context.getString(
                    R.string.exchange_rate,
                    plusString.plus(exchangeResponse.changePrice),
                    exchangeResponse.signedChangeRate.times(100).toBigDecimal()
                        .setScale(2, RoundingMode.HALF_UP).toString().plus("%")
                )
            }

        }
    }
}