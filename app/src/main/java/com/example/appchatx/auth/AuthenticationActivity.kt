package com.example.appchatx.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.appchatx.R
import com.example.appchatx.auth.LoginFragment
import com.example.appchatx.databinding.ActivityAuthenticationBinding

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        // Optional: Load LoginFragment by default if not already loaded
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.auth_fragmentContainer, LoginFragment())
                .commit()
        }
    }
}