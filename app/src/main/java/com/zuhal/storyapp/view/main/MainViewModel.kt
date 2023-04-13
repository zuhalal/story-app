package com.zuhal.storyapp.view.main

import androidx.lifecycle.ViewModel
import com.zuhal.storyapp.data.StoryUserRepository

class MainViewModel(private val repository: StoryUserRepository): ViewModel() {
    fun getListStories(token: String) = repository.getListStory(token)
}