package com.zuhal.storyapp.di

import android.content.Context
import com.zuhal.storyapp.data.StoryUserRepository
import com.zuhal.storyapp.data.local.room.StoryDatabase
import com.zuhal.storyapp.data.remote.retrofit.RetrofitConfig
import com.zuhal.storyapp.utils.AppExecutors

object Injection {
    fun provideStoryRepository(context: Context): StoryUserRepository {
        val apiService = RetrofitConfig.getApiService()
        val database = StoryDatabase.getInstance(context)
        val dao = database.storyDao()
        val appExecutors = AppExecutors()
        return StoryUserRepository.getInstance(apiService, dao, appExecutors, database)
    }
}