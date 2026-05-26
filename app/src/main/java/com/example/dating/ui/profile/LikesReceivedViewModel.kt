package com.example.dating.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dating.core.network.RetrofitClient
import com.example.dating.data.model.User
import com.example.dating.data.remote.DiscoveryApiService
import com.example.dating.data.repository.DiscoveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LikesReceivedViewModel : ViewModel() {

    private val api by lazy {
        RetrofitClient.retrofitInstance.create(DiscoveryApiService::class.java)
    }

    private val repository by lazy {
        DiscoveryRepository(api)
    }

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _index = MutableStateFlow(0)
    val index: StateFlow<Int> = _index.asStateFlow()

    private var currentPage = 1
    private var isLoading = false
    private var hasMore = true

    init {
        loadNextPage()
    }

    fun nextUser() {
        val newIndex = _index.value + 1
        _index.value = newIndex

        if (_users.value.size - newIndex <= 2) {
            loadNextPage()
        }
    }

    fun loadNextPage() {
        if (isLoading || !hasMore) return

        viewModelScope.launch {
            try {
                isLoading = true

                // SỬ DỤNG ENDPOINT MỚI TẠI ĐÂY
                val response = repository.getReceivedRequests(
                    page = currentPage,
                    limit = 10
                )

                _users.value = _users.value + response.users

                hasMore = currentPage < response.pagination.totalPages
                currentPage++

            } catch (e: Exception) {
                Log.e("LikesReceivedVM", "loadNextPage error", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun likeUser(user: User) {
        sendInteraction(user, true)
    }

    fun passUser(user: User) {
        sendInteraction(user, false)
    }

    private fun sendInteraction(user: User, liked: Boolean) {
        viewModelScope.launch {
            try {
                val result = repository.createInteraction(
                    targetId = user.userId,
                    liked = liked
                )
                Log.d("LikesReceivedVM", "Match = ${result.match != null}")
                nextUser()
            } catch (e: Exception) {
                Log.e("LikesReceivedVM", "interaction error", e)
            }
        }
    }
}