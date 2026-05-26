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

class LikesSentViewModel : ViewModel() {

    private val api by lazy { RetrofitClient.retrofitInstance.create(DiscoveryApiService::class.java) }
    private val repository by lazy { DiscoveryRepository(api) }

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
        val currentList = _users.value
        if (currentList.isEmpty()) return

        val newIndex = _index.value + 1

        // LOGIC VÒNG LẶP TIẾN: Nếu vượt quá phần tử cuối cùng, reset index về 0 để quay lại từ đầu
        if (newIndex >= currentList.size) {
            _index.value = 0
            loadNextPage()
        } else {
            _index.value = newIndex
            if (currentList.size - newIndex <= 2) {
                loadNextPage()
            }
        }
    }

    // THÊM HÀM NÀY: Xử lý vuốt lùi
    fun previousUser() {
        val currentList = _users.value
        if (currentList.isEmpty()) return

        val newIndex = _index.value - 1

        // LOGIC VÒNG LẶP LÙI: Nếu lùi qua người đầu tiên, vòng về người cuối cùng của danh sách
        if (newIndex < 0) {
            _index.value = currentList.size - 1
        } else {
            _index.value = newIndex
        }
    }

    fun loadNextPage() {
        if (!hasMore) {
            currentPage = 1
            hasMore = true
        }

        if (isLoading || !hasMore) return

        viewModelScope.launch {
            try {
                isLoading = true
                val response = repository.getSentRequests(page = currentPage, limit = 10)

                if (currentPage == 1) {
                    _users.value = response.users
                } else {
                    _users.value = _users.value + response.users
                }

                hasMore = currentPage < response.pagination.totalPages
                if (hasMore) currentPage++
            } catch (e: Exception) {
                Log.e("LikesSentVM", "loadNextPage error", e)
            } finally {
                isLoading = false
            }
        }
    }
}