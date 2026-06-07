package com.example.dating.core.auth

/**
 * Singleton object để lưu trữ thông tin User hiện tại trên toàn App.
 */
object UserManager {
    private var currentUserId: Int? = null

    fun setUserId(id: Int?) {
        currentUserId = id
    }

    fun getUserId(): Int? {
        return currentUserId
    }

    fun clearUserId() {
        currentUserId = null
    }
}