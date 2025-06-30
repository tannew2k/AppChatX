package com.example.appchatx.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appchatx.R
import com.example.appchatx.models.Chat
import com.example.appchatx.util.UserUtil

class ChatAdapter(
    private val chatList: MutableList<Chat>
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    companion object {
        private const val MSG_BY_SELF = 0
        private const val MSG_BY_OTHER = 1
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatText: TextView = itemView.findViewById(R.id.chat_text)
        val chatAuthor: TextView = itemView.findViewById(R.id.chat_author)
    }

    override fun getItemViewType(position: Int): Int {
        val myId = UserUtil.user?.id?.toDoubleOrNull()?.toInt()
        val authorId = chatList[position].author.id?.toDoubleOrNull()?.toInt()
        return if (myId != null && myId == authorId) MSG_BY_SELF else MSG_BY_OTHER
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutId = if (viewType == MSG_BY_SELF) {
            R.layout.item_chat_self
        } else {
            R.layout.item_chat_other
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.chatText.text = chat.text
        holder.chatAuthor.text = chat.author.name
    }

    override fun getItemCount(): Int = chatList.size
}
