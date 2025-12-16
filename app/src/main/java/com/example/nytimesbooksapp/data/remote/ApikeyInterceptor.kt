package com.example.nytimesbooksapp.data.remote

import com.example.nytimesbooksapp.data.controlpanel.AppConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApikeyInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("api-key", AppConfig.API_KEY)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }

}


