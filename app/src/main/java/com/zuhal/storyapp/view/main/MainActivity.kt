package com.zuhal.storyapp.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zuhal.storyapp.R
import com.zuhal.storyapp.adapter.ListStoryAdapter
import com.zuhal.storyapp.data.UserModel
import com.zuhal.storyapp.databinding.ActivityMainBinding
import com.zuhal.storyapp.view.ViewModelFactory
import com.zuhal.storyapp.view.login.LoginActivity
import com.zuhal.storyapp.view.login.LoginViewModel
import com.zuhal.storyapp.data.Result
import com.zuhal.storyapp.data.remote.models.Story

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var user: UserModel
    private lateinit var rvUser: RecyclerView
    private lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)
        val loginViewModel: LoginViewModel by viewModels { factory }
        val mainViewModel: MainViewModel by viewModels { factory }

        loginViewModel.getUser().observe(this) { user ->
            this.user = user
            binding.username.text = this.user.name
        }

        loginViewModel.getToken().observe(this) { token ->
            if (token == "") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                rvUser = binding.rvStory
                mainViewModel.getListStories("Bearer $token").observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                showLoading(true)
                            }
                            is Result.Success -> {
                                showLoading(false)
                                val data = result.data

                                setListUserData(data)
                            }
                            is Result.Error -> {
                                showLoading(false)
//                                Toast.makeText(
//                                    this,
//                                    "${getString(R.string.mistake)} ${result.error}",
//                                    Toast.LENGTH_SHORT
//                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setListUserData(listUser: List<Story>) {
        rvUser.setHasFixedSize(true)
        rvUser.layoutManager = LinearLayoutManager(this)
        rvUser.setHasFixedSize(true)

        val listUserAdapter = ListStoryAdapter()
        listUserAdapter.setListUser(listUser)

        rvUser.adapter = listUserAdapter

        listUserAdapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Story, index: Int) {
//                showSelectedUser(data)
//                val intent = Intent(this@MainActivity, UserDetailActivity::class.java)
//                intent.putExtra(UserDetailActivity.EXTRA_USER, data)
//                startActivity(intent)
            }
        })
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                val loginViewModel: LoginViewModel by viewModels { factory }
                loginViewModel.logout()
                return true
            }
            else -> return true
        }
    }
}