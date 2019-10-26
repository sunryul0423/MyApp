package com.srpark.myapp.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srpark.myapp.R
import com.srpark.myapp.databinding.ItemMovieBinding
import com.srpark.myapp.home.model.data.Items
import com.srpark.myapp.utils.onClick
import com.srpark.myapp.utils.setImageUrl
import kotlinx.coroutines.Job


class RvMovieAdapter(
    private val itemList: List<Items>,
    private val itemClick: (item: Items, view: View) -> Unit,
    private val job: Job
) :
    RecyclerView.Adapter<RvMovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.setData(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal fun setData(item: Items) {
            val context = binding.root.context
            binding.tvRank.text = item.rank
            binding.tvMovieTitle.text = item.title
            binding.tvOpenDt.text = String.format(context.getString(R.string.movie_open_date), item.pubDate)
            binding.tvRating.text = item.userRating.toString()
            setImageUrl(binding.ivMovie, item.image)
            binding.clRoot.onClick(job = job) { itemClick(item, binding.ivMovie) }
        }
    }
}