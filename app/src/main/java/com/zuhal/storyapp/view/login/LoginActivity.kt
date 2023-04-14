package com.zuhal.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.zuhal.storyapp.R
import com.zuhal.storyapp.databinding.ActivityLoginBinding
import com.zuhal.storyapp.view.ViewModelFactory
import com.zuhal.storyapp.view.main.MainActivity
import com.zuhal.storyapp.data.Result
import com.zuhal.storyapp.view.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val loginViewModel: LoginViewModel by viewModels { factory }

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            run {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)

                if (binding.edLoginEmail.text.toString()
                        .isEmpty() || binding.edLoginPassword.text.toString().isEmpty()
                ) {
                    showFailedDialog(getString(R.string.invalid_input))
                } else if (binding.edLoginEmail.error == null && binding.edLoginPassword.error == null) {
                    loginViewModel
                        .postLogin(
                            binding.edLoginEmail.text.toString(),
                            binding.edLoginPassword.text.toString()
                        )
                        .observe(this) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        showLoading(true)
                                    }
                                    is Result.Success -> {
                                        showLoading(false)
                                        AlertDialog.Builder(this).apply {
                                            setTitle(getString(R.string.success))
                                            setMessage(getString(R.string.login_successful_message))
                                            setPositiveButton(getString(R.string.next)) { _, _ ->
                                                val intent =
                                                    Intent(context, MainActivity::class.java)
                                                intent.flags =
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                startActivity(intent)
                                                finish()
                                            }
                                            create()
                                            show()
                                        }
                                    }
                                    is Result.Error -> {
                                        showFailedDialog(getString(R.string.login_failed_message))
                                    }
                                }
                            }
                        }
                }
            }
        }
    }

    private fun showFailedDialog(message: String) {
        showLoading(false)
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.failed))
            setMessage(message)
            setPositiveButton(getString(R.string.next)) { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)

        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)

        val password =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)

        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val or = ObjectAnimator.ofFloat(binding.orText, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)

        val emailTogether = AnimatorSet().apply {
            playTogether(email, emailLayout)
        }

        val passwordTogether = AnimatorSet().apply {
            playTogether(password, passwordLayout)
        }

        AnimatorSet().apply {
            playSequentially(title, emailTogether, passwordTogether, login, or, signup)
            start()
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