package com.example.cinemaapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cinemaapp.R
import com.example.cinemaapp.activity.DetailActivity
import com.example.cinemaapp.db.AppDatabase
import com.example.cinemaapp.domain.FilmItem

class LikedMoviesAdapter(var list: List<FilmItem>, val userId: String ): RecyclerView.Adapter<LikedMoviesAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val filmImg: ImageView = itemView.findViewById(R.id.pic)
        val deleteBtn: ImageView = itemView.findViewById(R.id.deleteMovieBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.liked_film_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = list[position]
        val context = holder.itemView.context

        holder.filmImg.load(currentItem.poster)

        holder.itemView.setOnClickListener {
            currentItem.userId = userId

            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("filmId", currentItem.id)
            intent.putExtra("userId", userId)
            context.startActivity(intent)
        }

        holder.deleteBtn.setOnClickListener{
            val db = AppDatabase.initDb(context)
            val filmItemDao = db.filmItemDao()

            filmItemDao.deleteFilm(currentItem.id, userId)

            list = list.toMutableList().apply {
                removeAt(position)
            }

            notifyItemRemoved(position)
            notifyItemRangeChanged(position, list.size)
        }
    }
}