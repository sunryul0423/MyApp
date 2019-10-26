package com.srpark.myapp.home.adapter

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.srpark.myapp.databinding.ItemShoppingBinding
import com.srpark.myapp.home.model.data.Items
import com.srpark.myapp.utils.transDecimalWon

class RvShoppingAdapter : PagedListAdapter<Items, RvShoppingAdapter.ShoppingHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Items>() {
            override fun areItemsTheSame(oldItem: Items, newItem: Items): Boolean {
                return oldItem.productId == newItem.productId
            }

            override fun areContentsTheSame(oldItem: Items, newItem: Items): Boolean {
                return oldItem.productId == newItem.productId && oldItem.title == newItem.title
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingHolder {
        val binding = ItemShoppingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingHolder, position: Int) {
        getItem(position)?.let {
            holder.setData(it)
        }
    }

    inner class ShoppingHolder(private val binding: ItemShoppingBinding) : RecyclerView.ViewHolder(binding.root) {
        internal fun setData(item: Items) {
            binding.imgUrl = item.image
            binding.tvShop.text = item.mallName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.tvTitle.text = Html.fromHtml(item.title, FROM_HTML_MODE_LEGACY).toString()
            } else {
                binding.tvTitle.text = Html.fromHtml(item.title).toString()
            }
            binding.tvLowPrice.text = transDecimalWon(binding.root.context, item.lprice)
            binding.llItemView.setOnClickListener {
                binding.root.context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
                )
            }
        }
    }
}

