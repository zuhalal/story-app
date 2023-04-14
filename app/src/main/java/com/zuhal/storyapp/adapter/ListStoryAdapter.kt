package com.zuhal.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zuhal.storyapp.extensions.loadImageCenterCrop
import com.zuhal.storyapp.data.remote.models.Story
import com.zuhal.storyapp.databinding.ItemRowStoryBinding
import com.zuhal.storyapp.helper.StoryDiffCallback

class ListStoryAdapter:
    RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {
    private val listStory = ArrayList<Story>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setListUser(listStory: List<Story>) {
        val diffCallback = StoryDiffCallback(this.listStory, listStory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listStory.clear()
        this.listStory.addAll(listStory)
        diffResult.dispatchUpdatesTo(this)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Story, index: Int, sharedImageView: ImageView)
    }

    class ListViewHolder(itemView: ItemRowStoryBinding) : RecyclerView.ViewHolder(itemView.root) {
        var tvName: TextView = itemView.tvName
        var tvDescription: TextView = itemView.tvDescription
        var imgPhoto: ImageView = itemView.imgItemPhoto
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = listStory[position]

        holder.tvName.text = story.name
        holder.tvDescription.text = story.description
        holder.imgPhoto.loadImageCenterCrop(story.photoUrl)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(
                listStory[holder.adapterPosition],
                holder.adapterPosition,
                holder.imgPhoto
            )
        }
    }

    override fun getItemCount(): Int {
        return listStory.size
    }
}