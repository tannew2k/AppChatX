package com.example.appchatx.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.appchatx.auth.LoginFragment
import com.example.appchatx.MainActivity
import com.example.appchatx.R
import com.example.appchatx.models.User
import com.example.appchatx.databinding.FragmentRegisterBinding
import com.example.appchatx.util.api.ApiChatX
import com.example.appchatx.util.api.AuthApi
import com.example.appchatx.util.api.request.RegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterFragment : Fragment() {

    companion object {
        const val TAG = "RegisterFragment"
    }

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val passwordRegex =
        Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginTextView.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.auth_fragmentContainer, LoginFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.confirmPasswordText.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = binding.passwordText.editText?.text.toString()
                val confirmPassword = s.toString()

                if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                    binding.confirmPasswordText.error = "Password do not match"
                } else {
                    binding.confirmPasswordText.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnSignup.setOnClickListener {
            val email = binding.emailText.editText?.text.toString()
            val name = binding.nameText.editText?.text.toString()
            val password = binding.passwordText.editText?.text.toString()
            val confirmPassword = binding.confirmPasswordText.editText?.text.toString()

            binding.emailText.error = if (email.isEmpty()) "Email is required" else null
            binding.nameText.error = if (name.isEmpty()) "Name is required" else null
            binding.passwordText.error = if (password.isEmpty()) "Password is required" else null
            binding.confirmPasswordText.error =
                if (confirmPassword.isEmpty()) "Confirm password is required" else null

            if (TextUtils.isEmpty(email)) {
                binding.emailText.error = "Email is required."
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailText.error = "Enter a valid email address"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(name)) {
                binding.nameText.error = "Name is required."
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                binding.passwordText.error = "Password is required."
                return@setOnClickListener
            }

            if (!password.matches(passwordRegex)) {
                binding.passwordText.error =
                    "Password must be at least 8 characters, contain 1 uppercase, 1 lowercase, 1 digit, and 1 special character"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                binding.confirmPasswordText.error = "Confirm Password is required"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.confirmPasswordText.error = "Password do not match"
                return@setOnClickListener
            }

            binding.signupProgressBar.visibility = View.VISIBLE
            binding.btnSignup.isEnabled = false

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val api = ApiChatX().createService(AuthApi::class.java)
                    val response = api.register(RegisterRequest(name, email, password, confirmPassword))

                    withContext(Dispatchers.Main) {
                        binding.signupProgressBar.visibility = View.GONE
                        binding.btnSignup.isEnabled = true

                        if (response.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Register successful. Please login.",
                                Toast.LENGTH_SHORT
                            ).show()
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.auth_fragmentContainer, LoginFragment())
                                .addToBackStack(null)
                                .commit()
                        } else {
                            Toast.makeText(requireContext(), "Register failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.signupProgressBar.visibility = View.GONE
                        binding.btnSignup.isEnabled = true
                        Toast.makeText(
                            requireContext(),
                            "Register failed: ${e.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
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