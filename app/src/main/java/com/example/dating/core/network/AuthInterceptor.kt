package com.example.dating.core.network

import com.example.dating.data.local.TokenDao
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds the Authorization header to requests if a token is available.
 */
class AuthInterceptor(private val tokenDao: TokenDao) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip auth for login and register endpoints
        val path = originalRequest.url.encodedPath
        if (path.contains("/auth/login") || path.contains("/auth/register")) {
            return chain.proceed(originalRequest)
        }

        // Get token from local database synchronously (ok to do in interceptor)
        val tokenEntity = runBlocking {
            tokenDao.getLatestToken()
        }

        return if (tokenEntity != null) {
            val token = if (tokenEntity.token.startsWith("Bearer ")) {
                tokenEntity.token
            } else {
                "Bearer ${tokenEntity.token}"
            }
            
            val authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", token)
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}
