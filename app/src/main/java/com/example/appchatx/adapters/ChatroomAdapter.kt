package com.example.appchatx.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appchatx.ChatFragment
import com.example.appchatx.MainActivity
import com.example.appchatx.models.Chatroom
import com.example.appchatx.R

class ChatroomAdapter(
    private val context: Context,
    private val chatrooms: List<Chatroom>
) : RecyclerView.Adapter<ChatroomAdapter.ChatroomViewHolder>() {

    class ChatroomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val chatroomName: TextView = itemView.findViewById(R.id.chatroom_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chatroom, parent, false)
        return ChatroomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatroomViewHolder, position: Int) {
        val model = chatrooms[position]
        holder.chatroomName.text = model.name

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("chatroomId", model.id)

            val chatFragment = ChatFragment()
            chatFragment.arguments = bundle

            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, chatFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int = chatrooms.size
}