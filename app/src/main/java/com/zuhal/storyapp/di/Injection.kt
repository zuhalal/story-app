package com.zuhal.storyapp.di

import android.content.Context
import com.zuhal.storyapp.data.StoryUserRepository
import com.zuhal.storyapp.data.remote.retrofit.RetrofitConfig
import com.zuhal.storyapp.utils.AppExecutors

object Injection {
    fun provideStoryRepository(context: Context): StoryUserRepository {
        val apiService = RetrofitConfig.getStoryService()
        val appExecutors = AppExecutors()
        return StoryUserRepository.getInstance(apiService, appExecutors)
    }
}