package com.example.cinemaapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.cinemaapp.R
import com.example.cinemaapp.activity.DetailActivity
import com.example.cinemaapp.domain.SliderItems

class SliderAdapter(private val list: MutableList<SliderItems>, private val viewPager2: ViewPager2, private val userId: String): RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {
    class SliderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageSlider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.slider_item_container, parent, false)
        return SliderViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val currentItem = list[position]
        val context = holder.itemView.context

        val transformation = RoundedCornersTransformation(60f)
        holder.imageView.load(currentItem.film.poster) {
            transformations(transformation)
        }

        if(position == list.size - 1){
            viewPager2.post(runnable)
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("filmId", currentItem.film.id)
            context.startActivity(intent)
        }
    }

    private val runnable = Runnable {
        list.addAll(list)
        notifyDataSetChanged()
    }
}