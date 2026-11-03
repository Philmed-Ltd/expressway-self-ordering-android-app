package com.clicks.app.network

import android.content.Context
import com.clicks.app.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    lateinit var appContext: Context
        private set

    @Volatile
    private var initialized = false

    @Volatile
    private var retrofitInstance: Retrofit? = null

    fun initialize(context: Context, tokenProvider: () -> String?) {
        if (initialized) return

        synchronized(this) {
            if (initialized) return

            appContext = context.applicationContext

            val client = OkHttpClient.Builder()
                .addInterceptor(
                    AuthInterceptor(
                        tokenProvider = tokenProvider
                    )
                )
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            retrofitInstance = Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            initialized = true
        }
    }

    val retrofit: Retrofit
        get() = checkNotNull(retrofitInstance) {
            "ApiClient is not initialized. Call initialize(context) first."
        }

    fun <T> createService(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> createService(): T = createService(T::class.java)
}
