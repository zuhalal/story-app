package com.zuhal.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.google.gson.Gson
import com.zuhal.storyapp.data.local.entity.StoryEntity
import com.zuhal.storyapp.data.local.room.StoryDao
import com.zuhal.storyapp.data.local.room.StoryDatabase
import com.zuhal.storyapp.data.remote.models.CommonResponse
import com.zuhal.storyapp.data.remote.models.Story
import com.zuhal.storyapp.data.remote.retrofit.ApiService
import com.zuhal.storyapp.utils.AppExecutors
import com.zuhal.storyapp.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class StoryUserRepository private constructor(
    private val apiService: ApiService,
    private val storyDao: StoryDao,
    private val appExecutors: AppExecutors,
    private val database: StoryDatabase
) {
    private val message = MediatorLiveData<Result<String>>()

    private fun convertErrorResponse(stringRes: String?): CommonResponse {
        return Gson().fromJson(stringRes, CommonResponse::class.java)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun postLogin(
        email: String,
        password: String,
        pref: SettingPreferences
    ): LiveData<Result<String>> = liveData {
        emit(Result.Loading)

        wrapEspressoIdlingResource {
            try {
                val response = apiService.login(email, password)

                if (!response.error) {
                    val user = UserModel(response.loginResult.name, response.loginResult.userId)

                    GlobalScope.launch {
                        pref.saveTokenSetting(response.loginResult.token)
                        pref.saveUser(user)
                    }

                    val msg = response.message
                    emit(Result.Success(msg))
                } else {
                    val msg = response.message
                    emit(Result.Error(msg))
                }
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        val jsonRes = convertErrorResponse(e.response()?.errorBody()?.string())
                        val msg = jsonRes.message
                        emit(Result.Error(msg))
                    }
                    else -> {
                        emit(Result.Error(e.message.toString()))
                    }
                }
            }
        }
    }

    fun postRegister(email: String, password: String, name: String): LiveData<Result<String>> {
        message.value = Result.Loading
        val client = apiService.register(name, email, password)
        client.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.error == false) {

                        val msg = response.body()?.message ?: ""
                        message.value = Result.Success(msg)
                    } else {
                        val msg = response.body()?.message ?: ""
                        message.value = Result.Error(msg)
                    }
                } else {
                    try {
                        val jsonRes = convertErrorResponse(response.errorBody()?.string())
                        val msg = jsonRes.message
                        message.value = Result.Error(msg)
                    } catch (e: Exception) {
                        val msg = response.message()
                        message.value = Result.Error(msg)
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                message.value = Result.Error(t.message.toString())
            }
        })
        return message
    }

    fun getListStory(token: String):
            LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(database, apiService, token),
            pagingSourceFactory = {
                storyDao.getStories()
            }
        ).liveData
    }

    fun getListStoryLocation(token: String, location: Int = 0): LiveData<Result<List<Story>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAllStories(token, location)
            if (!response.error) {
                val listStory = response.listStory
                emit(Result.Success(listStory))
            } else {
                emit(Result.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun postStory(
        image: MultipartBody.Part,
        description: RequestBody,
        token: String,
        long: RequestBody?,
        lat: RequestBody?
    ): LiveData<Result<String>> {
        message.value = Result.Loading
        val service = if (long != null && lat != null) apiService.postStories(
            description,
            image,
            token,
            long,
            lat
        ) else apiService.postStories(description, image, token)
        service.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.error == false) {

                        val msg = response.body()?.message ?: ""
                        message.value = Result.Success(msg)
                    } else {
                        val msg = response.body()?.message ?: ""
                        message.value = Result.Error(msg)
                    }
                } else {
                    val msg = response.message()
                    message.value = Result.Error(msg)
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                message.value = Result.Error(t.message.toString())
            }
        })

        return message
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun logout(pref: SettingPreferences) {
        GlobalScope.launch {
            pref.saveTokenSetting("")
            pref.saveUser(UserModel(name = "", userId = ""))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryUserRepository? = null
        fun getInstance(
            apiService: ApiService,
            dao: StoryDao,
            appExecutors: AppExecutors,
            database: StoryDatabase
        ): StoryUserRepository =
            instance ?: synchronized(this) {
                instance ?: StoryUserRepository(apiService, dao, appExecutors, database)
            }.also { instance = it }
    }
}