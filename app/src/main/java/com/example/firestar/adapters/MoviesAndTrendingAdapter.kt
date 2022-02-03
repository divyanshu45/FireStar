package com.example.firestar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firestar.R
import com.example.firestar.model.Item
import kotlinx.android.synthetic.main.movie_and_trending.view.*

class MoviesAndTrendingAdapter(val urls: List<Item>) : RecyclerView.Adapter<MoviesAndTrendingAdapter.MovieAndTrendingViewHolder>(){

    private var onItemClickListener : ((Item) -> Unit)? = null

    fun setOnItemClickListener(listener: (Item) -> Unit){
        onItemClickListener = listener
    }

    inner class MovieAndTrendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAndTrendingViewHolder {
        return MovieAndTrendingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.movie_and_trending, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MovieAndTrendingViewHolder, position: Int) {
        val item = urls[position]
        val url = urls[position].imageUri?.toUri()
        Glide.with(holder.itemView)
            .load(url)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.itemView.iv_imageView)
        holder.itemView.setOnClickListener{
            onItemClickListener?.let {
                it(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return urls.size
    }
}