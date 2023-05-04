package com.zuhal.storyapp.view.detail

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.zuhal.storyapp.extensions.loadImageCenterCrop
import com.zuhal.storyapp.R
import com.zuhal.storyapp.data.local.entity.StoryEntity
import com.zuhal.storyapp.databinding.ActivityStoryDetailBinding
import com.zuhal.storyapp.view.ViewModelFactory
import com.zuhal.storyapp.view.add.AddStoryActivity
import com.zuhal.storyapp.view.login.LoginActivity
import com.zuhal.storyapp.view.login.LoginViewModel
import com.zuhal.storyapp.view.maps.MapsActivity

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding
    private lateinit var factory: ViewModelFactory
    private val loginViewModel: LoginViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        loginViewModel.getToken().observe(this) { token ->
            if (token == "") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }

        val story = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_USER, StoryEntity::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_USER)
        }

        binding.apply {
            imgItemPhoto.loadImageCenterCrop(story?.photoUrl)
            name.text = story?.name ?: ""
            description.text = story?.description ?: ""
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                loginViewModel.logout()
                return true
            }
            R.id.menu_add -> {
                val intent = Intent(this@StoryDetailActivity, AddStoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                return true
            }
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            R.id.menu_maps -> {
                val intent = Intent(this@StoryDetailActivity, MapsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                return true
            }
            else -> return true
        }
    }

    companion object {
        const val EXTRA_USER = "extra_user"
    }
}