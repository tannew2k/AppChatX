// FeedFragment.kt
package com.example.appchatx

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appchatx.adapters.FeedAdapter
import com.example.appchatx.databinding.FragmentFeedBinding
import com.example.appchatx.models.Post
import com.example.appchatx.models.User
import com.example.appchatx.util.UserUtil
import com.example.appchatx.util.api.ApiChatX
import com.example.appchatx.util.api.PostApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            startActivity(Intent(requireContext(), CreatePostActivity::class.java))
        }

        binding.feedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchPosts()
    }

    private fun fetchPosts() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = ApiChatX().apply { setToken(UserUtil.token.orEmpty()) }
                val response = api.createService(PostApi::class.java).listPost()

                if (response.isSuccessful && response.body() != null) {
                    val posts = response.body()!!.posts
                    withContext(Dispatchers.Main) {
                        adapter = FeedAdapter(requireContext(), posts)
                        binding.feedRecyclerView.adapter = adapter
                    }
                } else {
                    showError("Failed to load posts: ${response.code()}")
                }
            } catch (e: Exception) {
                showError("Error loading posts: ${e.message}")
            }
        }
    }


    private fun showError(message: String?) {
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), message ?: "Unknown error", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
