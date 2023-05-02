package com.zuhal.storyapp.view.maps

import androidx.lifecycle.ViewModel
import com.zuhal.storyapp.data.StoryUserRepository

class MapsViewModel(private val repository: StoryUserRepository) : ViewModel() {
    fun getListStoriesLocation(token: String) = repository.getListStoryLocation(token)
}