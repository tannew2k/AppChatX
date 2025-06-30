package com.example.appchatx

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appchatx.adapters.CommentsAdapter
import com.example.appchatx.databinding.ActivityCommentsBinding
import com.example.appchatx.models.Comment
import com.example.appchatx.models.User
import com.example.appchatx.util.TimeUtli
import com.example.appchatx.util.UserUtil
import com.example.appchatx.util.api.ApiChatX
import com.example.appchatx.util.api.CommnetApi
import com.example.appchatx.util.api.PostApi
import com.example.appchatx.util.api.request.CreateCommentRequest
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentsBinding
    private val commentList = mutableListOf<Comment>()
    private lateinit var commentsAdapter: CommentsAdapter
    private var postId: Int? = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postId = intent.getIntExtra("postId", -1)
        if (postId == -1) {
            showToast("Error")
            finish()
            return
        }

        setupRecyclerView()
        setupSendButton()
        fetchComments()
    }

    private fun setupRecyclerView() {
        commentsAdapter = CommentsAdapter(commentList)
        binding.commentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CommentsActivity)
            adapter = commentsAdapter
        }
    }

    private fun setupSendButton() {
        binding.commentSendIcon.setOnClickListener {
            val content = binding.commentEditText.text.toString().trim()
            if (content.isEmpty()) {
                showToast("Comment cannot be empty")
            } else {
                sendComment(content)
                binding.commentEditText.text.clear()
            }
        }
    }

    private fun fetchComments() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = ApiChatX().apply { setToken(UserUtil.token.orEmpty()) }
                val postApi = api.createService(PostApi::class.java)
                val response = postApi.getPostById(postId!!)

                if (response.isSuccessful) {
                    val json = response.body()
                    val post = json?.getAsJsonObject("post")
                    val commentsJson = post?.getAsJsonArray("comments")

                    val comments = mutableListOf<Comment>()

                    commentsJson?.forEach { commentElement ->
                        val commentObj = commentElement.asJsonObject
                        val authorObj = commentObj.getAsJsonObject("author")

                        val comment = Comment(
                            text = commentObj.get("content").asString,
                            postId = commentObj.get("post_id").asInt,
                            time = TimeUtli.parseDateToUnix(commentObj.get("created_at").asString),
                            author = com.example.appchatx.models.User(
                                id = authorObj.get("id").asString,
                                name = authorObj.get("name").asString,
                                email = authorObj.get("email").asString,
                                imageUrl = authorObj.get("image_url").asString,
                                bio = authorObj.get("bio").asString,
                                following = mutableListOf()
                            )
                        )
                        comments.add(comment)
                    }

                    withContext(Dispatchers.Main) {
                        commentList.clear()
                        commentList.addAll(comments)
                        commentsAdapter.notifyDataSetChanged()
                    }
                } else {
                    showToast("Failed to fetch post or comments")
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }


    private fun sendComment(content: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = ApiChatX().apply { setToken(UserUtil.token.orEmpty()) }
                val commentApi = api.createService(CommnetApi::class.java)
                val request = CreateCommentRequest(content, postId.toString())
                val response = commentApi.createComment(request)

                if (response.isSuccessful) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val commentJson = body?.getAsJsonObject("comment")

                        val newComment = Comment(
                            text = commentJson?.get("content")?.asString.orEmpty(),
                            postId = commentJson?.get("post_id")?.asString?.toIntOrNull() ?: -1,
                            time = TimeUtli.parseDateToUnix(commentJson?.get("created_at")?.asString.orEmpty()),
                            author = UserUtil.user ?: User( // fallback to current user
                                id = "0",
                                name = "Unknown",
                                email = "",
                                imageUrl = "",
                                bio = "",
                                following = mutableListOf()
                            )
                        )

                        withContext(Dispatchers.Main) {
                            commentList.add(newComment)
                            commentsAdapter.notifyItemInserted(commentList.size - 1)
                            binding.commentsRecyclerView.scrollToPosition(commentList.size - 1)
                        }
                    }
                } else {
                    showToast("Failed to post comment")
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }

    private fun showToast(msg: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(this@CommentsActivity, msg, Toast.LENGTH_SHORT).show()
        }
    }
}
