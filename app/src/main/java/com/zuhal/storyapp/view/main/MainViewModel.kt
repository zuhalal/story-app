package com.zuhal.storyapp.view.main

import com.zuhal.storyapp.data.StoryUserRepository

class MainViewModel(private val repository: StoryUserRepository) {
//    fun postLogin(email: String, password: String) =>repos
    fun getListStories(token: String) = repository.getListStory(token)
}