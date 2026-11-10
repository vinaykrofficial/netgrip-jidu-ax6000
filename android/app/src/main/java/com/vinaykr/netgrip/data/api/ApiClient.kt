package com.vinaykr.netgrip.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    val cookieJar = SessionCookieJar()

    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private var currentBaseUrl: String = "http://192.168.2.1:8090/"
    private var cachedService: NetGripApiService? = null

    fun getService(baseUrl: String = currentBaseUrl): NetGripApiService {
        val sanitized = formatBaseUrl(baseUrl)
        if (cachedService != null && sanitized == currentBaseUrl) {
            return cachedService!!
        }

        currentBaseUrl = sanitized
        val retrofit = Retrofit.Builder()
            .baseUrl(currentBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(NetGripApiService::class.java)
        cachedService = service
        return service
    }

    fun formatBaseUrl(input: String): String {
        var url = input.trim()
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }
        if (!url.endsWith("/")) {
            url = "$url/"
        }
        return url
    }
}
