package com.zuhal.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.zuhal.storyapp.data.remote.models.Story
import com.zuhal.storyapp.data.remote.retrofit.StoryApiService
import com.zuhal.storyapp.data.remote.models.GetAllStoryResponse
import com.zuhal.storyapp.data.remote.models.LoginResponse
import com.zuhal.storyapp.data.remote.models.CommonResponse
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.GlobalScope
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryUserRepository private constructor(
    private val apiService: StoryApiService,
) {
    private val apiResult = MediatorLiveData<Result<List<Story>>>()
    private val message = MediatorLiveData<Result<String>>()

    @OptIn(DelicateCoroutinesApi::class)
    fun postLogin(email: String, password: String, pref: SettingPreferences): LiveData<Result<String>> {
        message.value = Result.Loading
        val client = apiService.login(email, password)
        client.enqueue(object: Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.error == false) {
                        val user = UserModel(body.loginResult.name, body.loginResult.userId)

                        GlobalScope.launch {
                            pref.saveTokenSetting(body.loginResult.token)
                            pref.saveUser(user)
                        }

                        val msg = response.body()?.message ?: ""
                        message.value =  Result.Success(msg)
                    } else {
                        val msg = response.body()?.message ?: ""
                        message.value =  Result.Error(msg)
                    }
                } else {
                    val msg = response.body()?.message ?: ""
                    message.value =  Result.Error(msg)
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                message.value = Result.Error(t.message.toString())
            }
        })
        return message
    }

    fun postRegister(email: String, password: String, name: String): LiveData<Result<String>> {
        message.value = Result.Loading
        Log.e("email", email)
        Log.e("name", name)
        val client = apiService.register(name, email, password)
        client.enqueue(object: Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.error == false) {

                        val msg = response.body()?.message ?: ""
                        message.value =  Result.Success(msg)
                    } else {
                        val msg = response.body()?.message ?: ""
                        message.value =  Result.Error(msg)
                    }
                } else {
                    val msg = response.body()?.message ?: ""
                    message.value =  Result.Error(msg)
                }
            }
            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                message.value = Result.Error(t.message.toString())
            }
        })
        return message
    }

    fun getListStory(token: String): LiveData<Result<List<Story>>> {
        apiResult.value = Result.Loading
        val client = apiService.getAllStories(token)
        client.enqueue(object : Callback<GetAllStoryResponse> {
            override fun onResponse(
                call: Call<GetAllStoryResponse>,
                response: Response<GetAllStoryResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val listStory = response.body()?.listStory as List<Story>
                        apiResult.value = Result.Success(listStory)
                    }
                }
            }

            override fun onFailure(call: Call<GetAllStoryResponse>, t: Throwable) {
                apiResult.value = Result.Error(t.message.toString())
            }
        })

        return apiResult
    }

    fun postStory(image: MultipartBody.Part, description: RequestBody, token: String): LiveData<Result<String>> {
        message.value = Result.Loading
        val service = apiService.postStories(description, image, token)
        service.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.error == false) {

                        val msg = response.body()?.message ?: ""
                        message.value =  Result.Success(msg)
                    } else {
                        val msg = response.body()?.message ?: ""
                        message.value =  Result.Error(msg)
                    }
                } else {
                    val msg = response.body()?.message ?: ""
                    message.value =  Result.Error(msg)
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
            pref.saveUser(UserModel(name="", userId = ""))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryUserRepository? = null
        fun getInstance(
            apiService: StoryApiService,
        ): StoryUserRepository =
            instance ?: synchronized(this) {
                instance ?: StoryUserRepository(apiService)
            }.also { instance = it }
    }
}