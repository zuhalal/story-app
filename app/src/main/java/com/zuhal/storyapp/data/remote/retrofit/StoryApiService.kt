package com.zuhal.storyapp.data.remote.retrofit

import com.zuhal.storyapp.data.remote.models.DetailStoryResponse
import com.zuhal.storyapp.data.remote.models.GetAllStoryResponse
import com.zuhal.storyapp.data.remote.models.LoginResponse
import com.zuhal.storyapp.data.remote.models.RegisterResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface StoryApiService {
    @FormUrlEncoded
    @POST("/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<LoginResponse>

    @Multipart
    @Headers("Content-Type: multipart/form-data")
    @POST("/stories")
    fun postStories(
        @Field("description") description: String,
        @Part file: MultipartBody.Part,
        @Header("Authorization") authorization: String
    ): Call<LoginResponse>

    @GET("/stories")
    fun getAllStories(
        @Header("Authorization") authorization: String
    ): Call<GetAllStoryResponse>

    @GET("/stories/{id}")
    fun getDetailStory(@Path("id") username: String): Call<List<DetailStoryResponse>>
}