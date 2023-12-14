package com.example.cinemaapp.adapter

import android.graphics.ColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.RecyclerView
import com.example.cinemaapp.R
import com.example.cinemaapp.activity.MainActivity
import com.example.cinemaapp.domain.Genres

class GenresListAdapter(private val list: List<Genres>): RecyclerView.Adapter<GenresListAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val genreTv : TextView = itemView.findViewById(R.id.genreTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.genres_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = list[position]
        holder.genreTv.text = currentItem.name
        holder.itemView.setBackgroundResource(R.drawable.genre_background)

        holder.itemView.setOnClickListener {
            currentItem.isClicked = !currentItem.isClicked

            if (currentItem.isClicked) {
                holder.itemView.setBackgroundResource(R.drawable.active_genre_background)
            } else
                holder.itemView.setBackgroundResource(R.drawable.genre_background)

            currentItem.name?.let {
                (holder.itemView.context as? MainActivity)?.showFilmsByCategory(
                    it,
                    currentItem.isClicked
                )
            }
        }
    }
}