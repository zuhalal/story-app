package com.zuhal.storyapp.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "story")
@Parcelize
data class StoryEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String = "",
    val photoUrl: String,
    val createdAt: String,
    val name: String,
    val description: String,
    val lat: Double,
    val lon: Double,
) : Parcelable