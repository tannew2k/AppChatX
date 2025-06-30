package com.example.appchatx.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appchatx.R
import com.example.appchatx.models.User
import com.example.appchatx.util.UserUtil
import com.example.appchatx.util.api.ApiChatX
import com.example.appchatx.util.api.UserApi
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchAdapter(
    private val context: Context,
    private val userList: List<User>,
    private val onFollowToggled: ((updatedFollowing: List<String>) -> Unit)? = null
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage: CircleImageView = itemView.findViewById(R.id.profile_image)
        val nameText: TextView = itemView.findViewById(R.id.user_name)
        val followButton: Button = itemView.findViewById(R.id.follow_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = userList[position]

        holder.nameText.text = user.name
        Glide.with(context).load(user.imageUrl).into(holder.userImage)

        val isFollowing = UserUtil.user?.following?.contains(user.id) == true
        holder.followButton.text = if (isFollowing) {
            context.getString(R.string.following)
        } else {
            context.getString(R.string.follow)
        }

        holder.followButton.setOnClickListener {
            toggleFollow(user, holder)
        }
    }

    private fun toggleFollow(user: User, holder: SearchViewHolder) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = ApiChatX().apply { setToken(UserUtil.token.orEmpty()) }
                val userApi = api.createService(UserApi::class.java)
                val response = userApi.toggleFollow(user.id)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val followingArray = response.body()?.getAsJsonArray("following")
                        val updatedFollowing = followingArray?.map { it.asString } ?: listOf()
                        UserUtil.user?.following = updatedFollowing.toMutableList()

                        // Update button text
                        val isNowFollowing = updatedFollowing.contains(user.id)
                        holder.followButton.text = if (isNowFollowing) {
                            context.getString(R.string.following)
                        } else {
                            context.getString(R.string.follow)
                        }

                        // Notify parent (optional)
                        onFollowToggled?.invoke(updatedFollowing)
                    } else {
                        Toast.makeText(context, "Action failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = userList.size
}
