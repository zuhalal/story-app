package com.zuhal.storyapp.view.login

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.zuhal.storyapp.databinding.ActivityLoginBinding
import com.zuhal.storyapp.view.ViewModelFactory
import com.zuhal.storyapp.view.main.MainActivity
import com.zuhal.storyapp.data.Result

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val loginViewModel: LoginViewModel by viewModels { factory }

        binding.loginButton.setOnClickListener{
            run {
                if (binding.edLoginEmail.error == null && binding.edLoginPassword.error == null) {
                    loginViewModel
                        .postLogin(binding.edLoginEmail.text.toString(), binding.edLoginPassword.text.toString())
                        .observe(this) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        showLoading(true)
                                    }
                                    is Result.Success -> {
                                        showLoading(false)
                                        AlertDialog.Builder(this).apply {
                                            setTitle("Sukses")
                                            setMessage("Anda berhasil login.")
                                            setPositiveButton("Lanjut") { _, _ ->
                                                val intent = Intent(context, MainActivity::class.java)
                                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                startActivity(intent)
                                                finish()
                                            }
                                            create()
                                            show()
                                        }
                                    }
                                    is Result.Error -> {
                                        showLoading(false)
                                        AlertDialog.Builder(this).apply {
                                            setTitle("Gagal")
                                            setMessage("Anda gagal login.")
                                            setPositiveButton("Lanjut") { _, _ -> }
                                            create()
                                            show()
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}