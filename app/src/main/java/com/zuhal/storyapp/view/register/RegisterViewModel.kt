package com.zuhal.storyapp.view.register

import androidx.lifecycle.ViewModel
import com.zuhal.storyapp.data.StoryUserRepository

class RegisterViewModel(private val repository: StoryUserRepository): ViewModel()  {
    fun postRegister(email: String, password: String, name: String) = repository.postRegister(email, password, name)
}