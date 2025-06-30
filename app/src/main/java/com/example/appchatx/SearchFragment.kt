// SearchFragment.kt
package com.example.appchatx

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appchatx.adapters.SearchAdapter
import com.example.appchatx.databinding.FragmentSearchBinding
import com.example.appchatx.models.User
import com.example.appchatx.util.UserUtil
import com.example.appchatx.util.api.ApiChatX
import com.example.appchatx.util.api.UserApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {

    private lateinit var adapter: SearchAdapter
    private lateinit var recyclerView: RecyclerView

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchToolbar.title = "Search Users"
        (activity as? MainActivity)?.setSupportActionBar(binding.searchToolbar)
        setHasOptionsMenu(true)

        recyclerView = binding.searchRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)

        val searchView = SearchView(context)
        menu.findItem(R.id.action_search).actionView = searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchUsers(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    private fun searchUsers(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = ApiChatX().apply { setToken(UserUtil.token.orEmpty()) }
                val userApi = api.createService(UserApi::class.java)
                val response = userApi.searchUsers(query)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val usersJsonArray = response.body()?.getAsJsonArray("users")
                        val users = usersJsonArray?.map { jsonElement ->
                            val obj = jsonElement.asJsonObject
                            User(
                                id = obj["id"].asString,
                                name = obj["name"].asString,
                                email = obj["email"].asString,
                                bio = obj["bio"].asString,
                                imageUrl = obj["image_url"].asString
                            )
                        } ?: emptyList()

                        adapter = SearchAdapter(requireContext(), users)
                        recyclerView.adapter = adapter
                        recyclerView.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
