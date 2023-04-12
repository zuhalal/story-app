package com.zuhal.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.zuhal.storyapp.data.SettingPreferences
import com.zuhal.storyapp.data.StoryUserRepository
import com.zuhal.storyapp.data.UserModel

class LoginViewModel(private val repository: StoryUserRepository, private val pref: SettingPreferences) {
    fun postLogin(email: String, password: String) = repository.postLogin(email, password, pref)
    fun postRegister(email: String, password: String, name: String) = repository.postRegister(email, password, name)
    fun logout() = repository.logout(pref)
    fun getToken(): LiveData<String> = pref.getTokenSetting().asLiveData()
    fun getUser(): LiveData<UserModel> = pref.getUser().asLiveData()
}