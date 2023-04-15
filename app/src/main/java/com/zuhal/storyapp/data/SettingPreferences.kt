package com.zuhal.storyapp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    fun getTokenSetting(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    suspend fun saveTokenSetting(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    fun getUser(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[NAME_KEY] ?: "",
                preferences[USER_ID_KEY] ?: "",
            )
        }
    }

    suspend fun saveUser(userModel: UserModel) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = userModel.name
            preferences[USER_ID_KEY] = userModel.userId
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val NAME_KEY = stringPreferencesKey("name")
        private val USER_ID_KEY = stringPreferencesKey("userId")

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}