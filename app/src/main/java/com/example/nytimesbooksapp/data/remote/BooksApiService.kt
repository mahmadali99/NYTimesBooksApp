package com.example.nytimesbooksapp.data.remote

import com.example.nytimesbooksapp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
// This is my Retrofit Interface
interface BooksApiService {
        @GET("lists/overview.json")
        suspend fun getBooksOverview(
            @Query("published_date") publishedDate: String? = null,
            @Query("api-key") apiKey: String = BuildConfig.API_KEY
        ): BooksResponseDto


    }

