package com.example.appchatx

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.appchatx.auth.AuthenticationActivity
import com.example.appchatx.databinding.FragmentProfileBinding
import com.example.appchatx.models.User
import com.example.appchatx.util.SessionUtil
import com.example.appchatx.util.UserUtil
import com.example.appchatx.util.api.ApiChatX
import com.example.appchatx.util.api.AuthApi
import com.example.appchatx.util.api.UserApi
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null
    private var user: User? = null
    private var originalName: String = ""
    private var originalBio: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserProfile()
        setupListeners()
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                UserUtil().getCurrentUser(UserUtil.token)
            }

            if (result != null) {
                user = result
                originalName = result.name
                originalBio = result.bio

                binding.userName.setText(result.name)
                binding.userBio.setText(result.bio)

                Glide.with(requireContext())
                    .load(result.imageUrl)
                    .placeholder(R.drawable.person_icon_black)
                    .error(R.drawable.person_icon_black)
                    .centerCrop()
                    .into(binding.userImage)
            } else {
                Toast.makeText(requireContext(), "Failed to load user info", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.userImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        binding.btnSave.setOnClickListener {
            if (hasChanges()) saveProfile()
            else Toast.makeText(requireContext(), "No changes to save", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun hasChanges(): Boolean {
        val currentName = binding.userName.text.toString().trim()
        val currentBio = binding.userBio.text.toString().trim()
        return imageUri != null || currentName != originalName || currentBio != originalBio
    }

    private fun saveProfile() {
        val name = binding.userName.text.toString().trim()
        val bio = binding.userBio.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = ApiChatX().apply { setToken(UserUtil.token.orEmpty()) }
                val service = api.createService(UserApi::class.java)

                val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val bioPart = bio.toRequestBody("text/plain".toMediaTypeOrNull())
                val followingJson = Gson().toJson(user?.following ?: emptyList<String>())
                val followingPart = followingJson.toRequestBody("application/json".toMediaTypeOrNull())

                val imagePart = imageUri?.path?.let {
                    val file = File(it)
                    val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image_url", file.name, reqFile)
                }

                val response = service.updateUser(namePart, bioPart, followingPart, imagePart)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val json: JsonObject? = response.body()
                        val userJson = json?.getAsJsonObject("user")

                        val updated = User(
                            id = userJson?.get("id")?.asString ?: "",
                            name = userJson?.get("name")?.asString ?: "",
                            email = userJson?.get("email")?.asString ?: "",
                            imageUrl = userJson?.get("image_url")?.asString ?: "",
                            bio = userJson?.get("bio")?.asString ?: "",
                            following = mutableListOf()
                        )

                        UserUtil.user = updated
                        user = updated
                        originalName = updated.name
                        originalBio = updated.bio
                        imageUri = null

                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()

                        // Reload image only if updated
                        Glide.with(requireContext())
                            .load(updated.imageUrl)
                            .placeholder(R.drawable.person_icon_black)
                            .error(R.drawable.person_icon_black)
                            .centerCrop()
                            .into(binding.userImage)
                    } else {
                        Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun logoutUser() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = ApiChatX().apply { setToken(UserUtil.token.orEmpty()) }
                val response = api.createService(AuthApi::class.java).logout()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        SessionUtil.clearSession(requireContext())
                        startActivity(Intent(requireContext(), AuthenticationActivity::class.java))
                        activity?.finish()
                        Toast.makeText(requireContext(), "Logout successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Logout failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Logout failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            binding.userImage.setImageURI(imageUri)
        } else {
            Toast.makeText(context, "Image selection cancelled or error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
