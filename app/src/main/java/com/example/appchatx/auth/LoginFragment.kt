package com.example.appchatx.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.appchatx.MainActivity
import com.example.appchatx.R
import com.example.appchatx.databinding.FragmentLoginBinding
import com.example.appchatx.util.SessionUtil
import com.example.appchatx.util.UserUtil
import com.example.appchatx.util.api.ApiChatX
import com.example.appchatx.util.api.AuthApi
import com.example.appchatx.util.api.request.LoginRequest
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    companion object {
        const val TAG = "LoginFragment"
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signupTextView.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.auth_fragmentContainer, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.emailText.editText?.text.toString().trim()
            val password = binding.passwordText.editText?.text.toString()

            if (TextUtils.isEmpty(email)) {
                binding.emailText.error = "Email is required."
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailText.error = "Enter a valid email address"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                binding.passwordText.error = "Password is required."
                return@setOnClickListener
            }

            binding.loginProgressBar.visibility = View.VISIBLE
            binding.btnLogin.isEnabled = false

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val api = ApiChatX().createService(AuthApi::class.java)
                    val response = api.login(LoginRequest(email, password))

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body() != null) {
                            val json: JsonObject? = response.body()
                            var token = json?.get("token")?.asString.toString()
                            SessionUtil.saveSession(requireContext(), token)
                            UserUtil.token = token
                            withContext(Dispatchers.IO) {
                                UserUtil().getCurrentUser(UserUtil.token)
                            }
                            Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()

                            // Navigate to main screen
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), "Invalid email or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        binding.loginProgressBar.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
