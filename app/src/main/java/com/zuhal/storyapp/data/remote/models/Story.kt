package com.zuhal.storyapp.data.remote.models

import com.google.gson.annotations.SerializedName

data class Story(
	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("lon")
	val lon: Any?,

	@field:SerializedName("lat")
	val lat: Any?
)
