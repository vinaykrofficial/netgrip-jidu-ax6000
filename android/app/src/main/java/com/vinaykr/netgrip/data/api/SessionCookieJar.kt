package com.vinaykr.netgrip.data.api

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

class SessionCookieJar : CookieJar {
    private val cookieStore = ConcurrentHashMap<String, MutableList<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        val currentCookies = cookieStore.getOrPut(host) { mutableListOf() }
        cookies.forEach { newCookie ->
            currentCookies.removeAll { it.name == newCookie.name }
            currentCookies.add(newCookie)
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host
        val cookies = cookieStore[host] ?: return emptyList()
        val now = System.currentTimeMillis()
        // Filter out expired cookies
        return cookies.filter { it.expiresAt > now }
    }

    fun clear() {
        cookieStore.clear()
    }

    fun hasSession(): Boolean {
        return cookieStore.values.flatten().any { it.name.startsWith("netgrip_") }
    }
}
