package com.example.appchatx

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appchatx.adapters.ChatAdapter
import com.example.appchatx.databinding.FragmentChatBinding
import com.example.appchatx.models.Chat
import com.example.appchatx.models.User
import com.example.appchatx.util.SocketUtil
import com.example.appchatx.util.UserUtil
import com.example.appchatx.util.api.ApiChatX
import com.example.appchatx.util.api.ChatApi
import com.example.appchatx.util.api.request.CreateChatRequest
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private var chatroomId: Int? = null
    private var channelName: String? = null
    private lateinit var chatAdapter: ChatAdapter
    private val chatList: MutableList<Chat> = mutableListOf()
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatroomId = arguments?.getString("chatroomId")?.toIntOrNull()
        if (chatroomId == null) return

        channelName = "chatroom.$chatroomId"
        setupRecyclerView()
        fetchMessages()

        binding.chatSendIcon.setOnClickListener {
            val text = binding.chatEditText.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
                binding.chatEditText.setText("") // Clear input safely
            }
        }

        SocketUtil.connect {
            listenForMessages()
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(chatList)
        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
    }

    private fun fetchMessages() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val roomId = chatroomId ?: return@launch
                val api = ApiChatX().apply { setToken(UserUtil.token.orEmpty()) }
                val service = api.createService(ChatApi::class.java)
                val messages = service.getChatsByRoom(roomId)

                launch(Dispatchers.Main) {
                    chatList.clear()
                    chatList.addAll(messages)
                    chatAdapter.notifyDataSetChanged()
                    binding.chatRecyclerView.scrollToPosition(chatList.size - 1)
                }
            } catch (e: Exception) {
                Log.e("ChatFragment", "Error loading messages: ${e.message}")
                showError("Could not load messages.")
            }
        }
    }

    private fun listenForMessages() {
        SocketUtil.subscribe(channelName ?: return, "MessagePosted") { data ->
            try {
                val json = Gson().fromJson(data, Map::class.java)
                val authorMap = json["author"] as? Map<*, *>
                val chat = Chat(
                    id = (json["chat_id"] as? Double)?.toInt() ?: 0,
                    text = json["text"] as? String ?: "",
                    time = (json["timestamp"] as? Double)?.toLong() ?: System.currentTimeMillis(),
                    author = User(
                        id = authorMap?.get("id")?.toString() ?: "",
                        name = authorMap?.get("name") as? String ?: "",
                        imageUrl = authorMap?.get("image_url") as? String ?: ""
                    )
                )

                requireActivity().runOnUiThread {
                    if (chatList.none { it.id == chat.id }) {
                        chatList.add(chat)
                        chatAdapter.notifyItemInserted(chatList.size - 1)
                        binding.chatRecyclerView.scrollToPosition(chatList.size - 1)
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatFragment", "Error parsing socket message: ${e.message}")
            }
        }
    }

    private fun sendMessage(text: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val userId = UserUtil.user?.id?.toIntOrNull()
                val roomId = chatroomId
                if (userId == null || roomId == null) {
                    showError("Invalid user or chatroom ID")
                    return@launch
                }

                val payload = CreateChatRequest(
                    text = text,
                    author_id = userId,
                    chatroom_id = roomId,
                    timestamp = System.currentTimeMillis()
                )

                val api = ApiChatX().apply { setToken(UserUtil.token.orEmpty()) }
                val service = api.createService(ChatApi::class.java)
                service.createChat(payload)

                // NOTE: Do not update UI here — wait for socket update
            } catch (e: Exception) {
                Log.e("ChatFragment", "Send failed: ${e.message}")
                showError("Error sending message: ${e.message}")
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
        SocketUtil.unsubscribe(channelName ?: return)
        SocketUtil.disconnect()
        _binding = null
    }
}
