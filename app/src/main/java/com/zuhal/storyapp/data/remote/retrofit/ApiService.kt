package com.zuhal.storyapp.data.remote.retrofit

import com.zuhal.storyapp.data.remote.models.GetAllStoryResponse
import com.zuhal.storyapp.data.remote.models.LoginResponse
import com.zuhal.storyapp.data.remote.models.CommonResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<CommonResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

    @Multipart
    @POST("stories")
    fun postStories(
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part,
        @Header("Authorization") authorization: String,
        @Part("lon") lon: RequestBody? = null,
        @Part("lat") lat: RequestBody? = null,
    ): Call<CommonResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): GetAllStoryResponse

    @GET("stories?location=1")
    fun getAllStoriesWithLocation(
        @Header("Authorization") authorization: String
    ): Call<GetAllStoryResponse>
}