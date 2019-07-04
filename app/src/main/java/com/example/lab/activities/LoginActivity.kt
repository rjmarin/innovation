package com.example.lab.activities

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.lab.CredentialsManager
import com.example.lab.EmailValidator

import com.example.lab.R
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setListeners()
    }

    private fun setListeners() {
        loginButton.setOnClickListener {
            val userEmail = emailEditText.text.toString()
            val userPassword = passwordEditText.text.toString()
            when {
                // Check Email
                !EmailValidator.isValidEmail(userEmail) ->
                    Toast.makeText(this, R.string.emailError, Toast.LENGTH_LONG).show()
                // Check Password
                userPassword.isNullOrEmpty() ->
                    Toast.makeText(this, R.string.passwordError, Toast.LENGTH_LONG).show()
                // Go to MainActivity
                else -> {
                    // Add data to invoking intent
                    intent.apply {
                        putExtra("EMAIL", userEmail)
                        putExtra("PASSWORD", userPassword)
                    }
                    // Set response
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    // EXTRA
    override fun onBackPressed() {
        // Do nothing as we don't want it to go back to MainActivity after SingOut
    }
}
