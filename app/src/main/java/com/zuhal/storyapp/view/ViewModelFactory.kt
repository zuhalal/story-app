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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ViewModelFactory private constructor(
    private val storyUserRepository: StoryUserRepository,
    private val pref: SettingPreferences
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
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