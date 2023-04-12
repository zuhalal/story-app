package com.zuhal.storyapp.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.zuhal.storyapp.data.UserModel
import com.zuhal.storyapp.databinding.ActivityMainBinding
import com.zuhal.storyapp.view.ViewModelFactory
import com.zuhal.storyapp.view.login.LoginViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val loginViewModel: LoginViewModel by viewModels { factory }

        loginViewModel.getUser().observe(this) { user ->
            this.user = user
            binding.username.text = this.user.name
        }
    }
}