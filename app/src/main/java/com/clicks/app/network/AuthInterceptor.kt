package com.clicks.app.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val path = request.url.encodedPath
        if (path in NO_AUTH_PATHS || path.startsWith(COMPLETE_LOGIN_PATH_PREFIX)) {
            return chain.proceed(request)
        }

        val token = tokenProvider()?.takeIf { it.isNotBlank() } ?: return chain.proceed(request)

        return chain.proceed(
            request.newBuilder()
                .header("Authorization", token)
                .build()
        )
    }

    private companion object {
        const val COMPLETE_LOGIN_PATH_PREFIX = "/customer/auth/complete-login/"

        val NO_AUTH_PATHS = setOf(
            "/customer/auth/init-login",
            "/customer/auth/register",
            "/customer/verification-code/request"
        )
    }
}
