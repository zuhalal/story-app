package com.zuhal.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zuhal.storyapp.R
import com.zuhal.storyapp.adapter.ListStoryAdapter
import com.zuhal.storyapp.adapter.LoadingStateAdapter
import com.zuhal.storyapp.data.UserModel
import com.zuhal.storyapp.data.local.entity.StoryEntity
import com.zuhal.storyapp.databinding.ActivityMainBinding
import com.zuhal.storyapp.view.ViewModelFactory
import com.zuhal.storyapp.view.add.AddStoryActivity
import com.zuhal.storyapp.view.detail.StoryDetailActivity
import com.zuhal.storyapp.view.login.LoginActivity
import com.zuhal.storyapp.view.login.LoginViewModel
import com.zuhal.storyapp.view.maps.MapsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var user: UserModel
    private lateinit var rvUser: RecyclerView
    private lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        factory = ViewModelFactory.getInstance(this)

        val loginViewModel: LoginViewModel by viewModels { factory }
        val mainViewModel: MainViewModel by viewModels { factory }

        rvUser = binding.rvStory
        rvUser.setHasFixedSize(true)
        rvUser.layoutManager = LinearLayoutManager(this)

        loginViewModel.getToken().observe(this) { token ->
            showLoading(true)
            if (token == "") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                val adapter = ListStoryAdapter()
                rvUser.adapter = adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        adapter.retry()
                    }
                )

                mainViewModel.getListStories("${getString(R.string.bearer)} $token")
                    .observe(this) { result ->
                        if (result != null) {
                            adapter.submitData(lifecycle, result)

                            showLoading(false)

                            adapter.setOnItemClickCallback(object :
                                ListStoryAdapter.OnItemClickCallback {
                                override fun onItemClicked(
                                    data: StoryEntity,
                                    index: Int,
                                    sharedImageView: ImageView,
                                    sharedName: TextView,
                                    sharedDesc: TextView
                                ) {
                                    val intent =
                                        Intent(this@MainActivity, StoryDetailActivity::class.java)
                                    intent.putExtra(StoryDetailActivity.EXTRA_USER, data)

                                    val options: ActivityOptionsCompat =
                                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                            this@MainActivity,
                                            Pair(
                                                sharedImageView,
                                                getString(R.string.img_item_transition_name)
                                            ),
                                            Pair(
                                                sharedName,
                                                getString(R.string.name_transition_name)
                                            ),
                                            Pair(
                                                sharedDesc,
                                                getString(R.string.description_transition_name)
                                            )
                                        )
                                    startActivity(intent, options.toBundle())
                                }
                            })
                        }
                    }
            }
            setContentView(binding.root)
        }

        loginViewModel.getUser().observe(this) { user ->
            this.user = user
            "${getString(R.string.welcome_back)} ${this.user.name}".also {
                binding.username.text = it
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
//
//    private fun showNotFoundMessage(show: Boolean) {
//        binding.notFound.visibility = if (show) View.VISIBLE else View.GONE
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                val loginViewModel: LoginViewModel by viewModels { factory }
                loginViewModel.logout()
                return true
            }
            R.id.menu_add -> {
                val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                return true
            }
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            R.id.menu_maps -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                return true
            }
            else -> return true
        }
    }
}