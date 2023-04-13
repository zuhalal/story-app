package com.zuhal.storyapp.view.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.githubusers.extensions.loadImageCenterCrop
import com.zuhal.storyapp.data.remote.models.Story
import com.zuhal.storyapp.databinding.ActivityStoryDetailBinding

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<Story>(EXTRA_USER)

        binding.apply {
            imgItemPhoto.loadImageCenterCrop(story?.photoUrl)
            name.text = story?.name ?: ""
            description.text = story?.description ?: ""
        }
    }

    companion object {
        const val EXTRA_USER = "extra_user"
    }
}