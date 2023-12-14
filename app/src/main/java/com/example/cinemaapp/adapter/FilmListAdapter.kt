package com.example.cinemaapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cinemaapp.R
import com.example.cinemaapp.activity.DetailActivity
import com.example.cinemaapp.domain.Data


class FilmListAdapter(private val list: List<Data>?, private val userId: String): RecyclerView.Adapter<FilmListAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val filmImg: ImageView = itemView.findViewById(R.id.pic)
        val scoreTxt: TextView = itemView.findViewById(R.id.scoreTxt)
        val yearTxt: TextView = itemView.findViewById(R.id.yearTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_film, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = list?.get(position)
        val context = holder.itemView.context

        holder.filmImg.load(currentItem?.poster)
        holder.scoreTxt.text = currentItem?.imdbRating ?: ""
        holder.yearTxt.text = currentItem?.year ?: ""

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("filmId", currentItem?.id)
            intent.putExtra("userId", userId)
            context.startActivity(intent)
        }
    }
}