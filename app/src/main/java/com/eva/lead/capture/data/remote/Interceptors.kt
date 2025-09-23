package com.eva.lead.capture.data.remote

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.jvm.Throws
import kotlin.text.isNullOrEmpty

class HostSelectionInterceptor : Interceptor {
    @Volatile
    var host: String? = null
    @Volatile
    var scheme: String = "https"

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        val newHost = host
        val newScheme = scheme

        if (!newHost.isNullOrEmpty()) {
            val newUrl: HttpUrl = request.url.newBuilder()
                .scheme(newScheme)
                .host(newHost)
                .build()
            request = request.newBuilder().url(newUrl).build()
        }

        return chain.proceed(request)
    }
}

/**
 * Interceptor to add Authorization Bearer token dynamically.
 */
class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenProvider()

        val requestBuilder = original.newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}