package com.zuhal.storyapp.utils

import com.zuhal.storyapp.data.local.entity.StoryEntity
import java.text.SimpleDateFormat
import java.util.*

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val story = StoryEntity(
                i.toString(),
                "https://story-api.dicoding.dev/images/stories/photos-1683105455902_HCD0gMcd.jpg",
                SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm'Z'", Locale.ENGLISH
                ).toString(),
                "Story + $i",
                "description + $i",
                i * 10.4,
                i * 10.5
            )
            items.add(story)
        }
        return items
    }
}