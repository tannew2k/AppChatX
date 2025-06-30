package com.example.appchatx

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appchatx.databinding.ActivityCreatePostBinding
import com.example.appchatx.util.UserUtil
import com.example.appchatx.util.api.ApiChatX
import com.example.appchatx.util.api.PostApi
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.postImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        binding.btnPost.setOnClickListener {
            val text = binding.postText.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Description cannot be empty.", Toast.LENGTH_SHORT).show()
            } else {
                createPost(text)
            }
        }
    }

    private fun createPost(text: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = ApiChatX().apply { setToken(UserUtil.token.orEmpty()) }
                val service = api.createService(PostApi::class.java)

                val textBody = RequestBody.create("text/plain".toMediaTypeOrNull(), text)
                val imagePart = imageUri?.let {
                    val file = File(it.path ?: return@let null)
                    val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    MultipartBody.Part.createFormData("image_url", file.name, reqFile)
                }

                val response = service.createPost(textBody, imagePart)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CreatePostActivity, "Post created!", Toast.LENGTH_SHORT).show()
                        navigateToFeedFragment()
                    } else {
                        Toast.makeText(this@CreatePostActivity, "Failed: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreatePostActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigateToFeedFragment() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("open_feed", true)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                imageUri = data?.data
                binding.postImage.setImageURI(imageUri)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
