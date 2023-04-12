package com.zuhal.storyapp.data

import com.zuhal.storyapp.data.remote.retrofit.StoryApiService
import com.zuhal.storyapp.utils.AppExecutors

class StoryUserRepository private constructor(
    private val apiService: StoryApiService,
    private val appExecutors: AppExecutors
) {

    companion object {
        @Volatile
        private var instance: StoryUserRepository? = null
        fun getInstance(
            apiService: StoryApiService,
            appExecutors: AppExecutors
        ): StoryUserRepository =
            instance ?: synchronized(this) {
                instance ?: StoryUserRepository(apiService, appExecutors)
            }.also { instance = it }
    }
}