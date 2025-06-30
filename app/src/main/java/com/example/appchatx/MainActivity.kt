package com.example.appchatx

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.appchatx.auth.AuthenticationActivity
import com.example.appchatx.databinding.ActivityMainBinding
import com.example.appchatx.util.SessionUtil
import com.example.appchatx.util.UserUtil
import com.example.appchatx.util.api.ApiChatX
import com.example.appchatx.util.api.AuthApi
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.getBooleanExtra("open_feed", false)) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FeedFragment())
                .commit()
        }

        checkSessionAndProceed()
    }

    private fun checkSessionAndProceed() {
        lifecycleScope.launch(Dispatchers.IO) {
            val token = SessionUtil.getToken(this@MainActivity)
            if (!token.isNullOrEmpty()) {
                val api = ApiChatX().apply { setToken(token) }
                try {
                    val response = api.createService(AuthApi::class.java).refresh()

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body() != null) {
                            val json: JsonObject? = response.body()
                            var token = json?.get("token")?.asString.toString()
                            SessionUtil.saveSession(this@MainActivity, token)
                            UserUtil.token = token
                            withContext(Dispatchers.IO) {
                                UserUtil().getCurrentUser(UserUtil.token)
                            }

                            setFragment(FeedFragment())

                            binding.navigationView.setOnItemSelectedListener {
                                when (it.itemId) {
                                    R.id.feed_item -> {
                                        setFragment(FeedFragment())
                                    }
                                    R.id.search_item -> {
                                        setFragment(SearchFragment())
                                    }
                                    R.id.chatroom_item -> {
                                        setFragment(ChatroomFragment())
                                    }
                                    R.id.profile_item -> {
                                        setFragment(ProfileFragment())
                                    }
                                }
                                true
                            }
                        } else {
                            navigateToLogin()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        navigateToLogin()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    navigateToLogin()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }



    private fun setFragmentWithBackStack(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, AuthenticationActivity::class.java))
        finish()
    }
}
