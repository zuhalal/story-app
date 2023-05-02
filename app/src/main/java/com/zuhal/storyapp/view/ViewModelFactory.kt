package com.zuhal.storyapp.view

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zuhal.storyapp.data.SettingPreferences
import com.zuhal.storyapp.data.StoryUserRepository
import com.zuhal.storyapp.di.Injection
import com.zuhal.storyapp.view.login.LoginViewModel
import com.zuhal.storyapp.view.main.MainViewModel
import com.zuhal.storyapp.view.maps.MapsViewModel
import com.zuhal.storyapp.view.register.RegisterViewModel

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ViewModelFactory private constructor(
    private val storyUserRepository: StoryUserRepository,
    private val pref: SettingPreferences
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(storyUserRepository) as T
        }

        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(storyUserRepository) as T
        }

        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(storyUserRepository, pref) as T
        }

        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(storyUserRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideStoryRepository(context),
                    SettingPreferences.getInstance(context.dataStore)
                )
            }.also { instance = it }
    }
}