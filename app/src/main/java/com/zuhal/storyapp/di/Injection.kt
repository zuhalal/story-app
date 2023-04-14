package com.zuhal.storyapp.di

import com.zuhal.storyapp.data.StoryUserRepository
import com.zuhal.storyapp.data.remote.retrofit.RetrofitConfig

object Injection {
    fun provideStoryRepository(): StoryUserRepository {
        val apiService = RetrofitConfig.getStoryService()
        return StoryUserRepository.getInstance(apiService)
    }
}