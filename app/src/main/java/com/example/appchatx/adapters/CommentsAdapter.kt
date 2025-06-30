package com.example.appchatx.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appchatx.models.Comment
import com.example.appchatx.R
import com.example.appchatx.util.TimeUtli

class CommentsAdapter(
    private val comments: List<Comment>
) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>(){

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.comment_text)
        val authorText: TextView = itemView.findViewById(R.id.comment_author)
        val timeText: TextView = itemView.findViewById(R.id.comment_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comments, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val model = comments[position]

        holder.commentText.text = model.text
        holder.authorText.text = model.author.name
        holder.timeText.text = TimeUtli.formatUnixTime(model.time)
    }

    override fun getItemCount(): Int = comments.size
}