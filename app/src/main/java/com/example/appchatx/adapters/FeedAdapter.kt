package com.example.appchatx.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appchatx.CommentsActivity
import com.example.appchatx.R
import com.example.appchatx.models.Post
import com.example.appchatx.util.TimeUtli
import com.example.appchatx.util.UserUtil
import com.example.appchatx.util.api.ApiChatX
import com.example.appchatx.util.api.PostApi
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class FeedAdapter(
    private val context: Context,
    private val posts: List<Post>
) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.feed_post_image)
        val likeIcon: ImageView = itemView.findViewById(R.id.post_like_btn)
        val commentIcon: ImageView = itemView.findViewById(R.id.post_comment_btn)
        val postLikeCount: TextView = itemView.findViewById(R.id.like_count)
        val postCommentCount: TextView = itemView.findViewById(R.id.comment_count)
        val authorText: TextView = itemView.findViewById(R.id.post_author)
        val timeText: TextView = itemView.findViewById(R.id.post_time)
        val postText: TextView = itemView.findViewById(R.id.post_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val model = posts[position]
        val currentUserId = UserUtil.user?.id ?: ""

        // Bind text
        holder.postText.text = model.text
        holder.authorText.text = model.user.name
        holder.timeText.text = TimeUtli.formatUnixTime(model.time)
        holder.postCommentCount.text = model.commentCount.toString()

        // Bind likes
        holder.postLikeCount.text = model.likeList.size.toString()
        updateLikeIcon(holder.likeIcon, model.likeList.contains(currentUserId))

        // Image
        if (!model.imageUrl.isNullOrEmpty()) {
            holder.postImage.visibility = View.VISIBLE
            Glide.with(context)
                .load(model.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(holder.postImage)
        } else {
            holder.postImage.visibility = View.GONE
        }

        // Like button click
        holder.likeIcon.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val api = ApiChatX().apply { setToken(UserUtil.token.orEmpty()) }
                    val response = api.createService(PostApi::class.java).likePost(model.id)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            // Toggle like in local list
                            if (model.likeList.contains(currentUserId)) {
                                model.likeList.remove(currentUserId)
                            } else {
                                model.likeList.add(currentUserId)
                            }

                            // Update UI
                            holder.postLikeCount.text = model.likeList.size.toString()
                            updateLikeIcon(holder.likeIcon, model.likeList.contains(currentUserId))
                        } else {
                            Toast.makeText(context, "Failed to like post", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Comment click
        holder.commentIcon.setOnClickListener {
            val intent = Intent(context, CommentsActivity::class.java)
            intent.putExtra("postId", posts[position].id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = posts.size

    private fun updateLikeIcon(icon: ImageView, liked: Boolean) {
        val drawableRes = if (liked) R.drawable.icon_like_fill else R.drawable.like_icon_outline
        icon.setImageDrawable(ContextCompat.getDrawable(context, drawableRes))
    }
}
