package com.zuhal.storyapp.data.remote.models

import com.google.gson.annotations.SerializedName

data class GetAllStoryResponse(

    @field:SerializedName("listStory")
    val listStory: List<Story>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

