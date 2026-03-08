package com.example.network.interceptor

import com.example.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.TMDB_ACCESS_TOKEN}")
            .addHeader("accept", "application/json")
            .method(originalRequest.method, originalRequest.body)

        return chain.proceed(requestBuilder.build())
    }
}