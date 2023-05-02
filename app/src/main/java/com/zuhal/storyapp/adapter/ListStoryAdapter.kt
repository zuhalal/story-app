@file:Suppress("DEPRECATION")

package com.zuhal.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zuhal.storyapp.data.local.entity.StoryEntity
import com.zuhal.storyapp.extensions.loadImageCenterCrop
import com.zuhal.storyapp.databinding.ItemRowStoryBinding

class ListStoryAdapter :
    PagingDataAdapter<StoryEntity, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(
            data: StoryEntity,
            index: Int,
            sharedImageView: ImageView,
            sharedName: TextView,
            sharedDesc: TextView
        )
    }

    inner class ListViewHolder(itemView: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var tvName: TextView = itemView.tvName
        var tvDescription: TextView = itemView.tvDescription
        var imgPhoto: ImageView = itemView.imgItemPhoto
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = getItem(position)

        if (story != null) {
            holder.tvName.text = story.name
            holder.tvDescription.text = story.description
            holder.imgPhoto.loadImageCenterCrop(story.photoUrl)

            holder.itemView.setOnClickListener {
                getItem(holder.adapterPosition)?.let { it1 ->
                    onItemClickCallback.onItemClicked(
                        it1,
                        holder.adapterPosition,
                        holder.imgPhoto,
                        holder.tvName,
                        holder.tvDescription
                    )
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}