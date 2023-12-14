package com.example.cinemaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cinemaapp.R
import com.google.android.material.imageview.ShapeableImageView

class ImageListAdapter(private val list: List<String>?): RecyclerView.Adapter<ImageListAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val img: ShapeableImageView = itemView.findViewById(R.id.itemImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_detail_images, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list?.count() ?: 0
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = list?.get(position)

        holder.img.load(currentItem)
    }
}