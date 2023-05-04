package com.zuhal.storyapp.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.zuhal.storyapp.data.StoryUserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val repository: StoryUserRepository) : ViewModel() {
    fun getListStories(token: String) = repository.getListStory(token).cachedIn(viewModelScope)
    fun postStory(
        image: MultipartBody.Part,
        description: RequestBody,
        token: String,
        long: RequestBody? = null,
        lat: RequestBody? = null
    ) =
        repository.postStory(image, description, token, long, lat)
}