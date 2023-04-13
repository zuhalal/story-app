package com.zuhal.storyapp.helper

import androidx.recyclerview.widget.DiffUtil
import com.zuhal.storyapp.data.remote.models.Story

class StoryDiffCallback(
    private val mOldStoryList: List<Story>,
    private val mNewStoryList: List<Story>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return mOldStoryList.size
    }

    override fun getNewListSize(): Int {
        return mNewStoryList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldStoryList[oldItemPosition].id == mNewStoryList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldStory = mOldStoryList[oldItemPosition]
        val newStory = mNewStoryList[newItemPosition]
        return oldStory.name == newStory.name && oldStory.description == newStory.description && oldStory.photoUrl == newStory.photoUrl
    }
}