package com.zuhal.storyapp.di

import android.content.Context
import com.zuhal.storyapp.data.StoryUserRepository
import com.zuhal.storyapp.data.remote.retrofit.RetrofitConfig

object Injection {
    fun provideStoryRepository(context: Context): StoryUserRepository {
        val apiService = RetrofitConfig.getStoryService()
        return StoryUserRepository.getInstance(apiService)
    }
}