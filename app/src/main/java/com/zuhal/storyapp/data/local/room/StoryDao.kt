package com.zuhal.storyapp.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zuhal.storyapp.data.local.entity.StoryEntity

@Dao
interface StoryDao {
    @Query("SELECT * FROM story")
    fun getStories(): List<StoryEntity>

    @Query("DELETE FROM story")
    fun deleteAllStories()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(stories: List<StoryEntity>)
}