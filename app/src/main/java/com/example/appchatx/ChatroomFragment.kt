package com.example.appchatx

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appchatx.adapters.ChatroomAdapter
import com.example.appchatx.databinding.FragmentChatroomBinding
import com.example.appchatx.util.UserUtil
import com.example.appchatx.util.api.ApiChatX
import com.example.appchatx.util.api.ChatroomApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatroomFragment : Fragment() {

    private lateinit var chatroomAdapter: ChatroomAdapter
    private lateinit var chatroomRecyclerView: RecyclerView
    private var _binding: FragmentChatroomBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatroomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatroomRecyclerView = binding.chatroomRecyclerView
        chatroomRecyclerView.layoutManager = LinearLayoutManager(context)

        loadChatrooms() // Load existing chatrooms

        binding.createChatroom.setOnClickListener {
            showCreateChatroomDialog()
        }
    }

    private fun showCreateChatroomDialog() {
        val alertDialog = AlertDialog.Builder(context)
        val editText = EditText(context)
        alertDialog.setTitle("Create Chatroom")
        alertDialog.setMessage("Enter the name of the new chatroom")
        alertDialog.setView(editText)

        alertDialog.setPositiveButton("Create") { dialog, _ ->
            val name = editText.text.toString().trim()
            if (name.isNotEmpty()) {
                createChatroom(name)
            }
        }

        alertDialog.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    private fun createChatroom(name: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = ApiChatX().apply { setToken(UserUtil.token.orEmpty()) }
                val response = api.createService(ChatroomApi::class.java).createChatroom(name)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(context, "Chatroom created", Toast.LENGTH_SHORT).show()
                        loadChatrooms()
                    } else {
                        Toast.makeText(context, "Failed to create chatroom", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadChatrooms() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = ApiChatX().apply { setToken(UserUtil.token.orEmpty()) }
                val response = api.createService(ChatroomApi::class.java).getChatrooms()

                if (response.isSuccessful && response.body() != null) {
                    val chatrooms = response.body()!!.chatrooms

                    withContext(Dispatchers.Main) {
                        chatroomAdapter = ChatroomAdapter(requireContext(), chatrooms)
                        chatroomRecyclerView.adapter = chatroomAdapter
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error loading chatrooms", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
