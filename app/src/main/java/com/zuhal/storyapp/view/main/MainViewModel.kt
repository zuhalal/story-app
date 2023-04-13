package com.zuhal.storyapp.view.main

import androidx.lifecycle.ViewModel
import com.zuhal.storyapp.data.StoryUserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val repository: StoryUserRepository): ViewModel() {
    fun getListStories(token: String) = repository.getListStory(token)
    fun postStory(image: MultipartBody.Part, description: RequestBody, token: String) = repository.postStory(image, description, token)
}