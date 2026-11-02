package com.clicks.app.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.url.encodedPath in NO_AUTH_PATHS) {
            return chain.proceed(request)
        }

        val token = tokenProvider() ?: return chain.proceed(request)

        return chain.proceed(
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        )
    }
    private companion object {
        val NO_AUTH_PATHS = setOf(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/forgot-password",
            "/api/auth/verify-reset-code",
            "/api/auth/reset-password"
        )
    }
}
