package com.example.nytimesbooksapp.data.controlpanel

import com.example.nytimesbooksapp.BuildConfig


// Class Description:

// |----------------------------------------------------------------------------------|
// |     This class acts as a centralized, type-safe place for all my constants.    |
// | it  is like as my app’s -> control panel. I’ll use it for URLs, keys,timeouts,|
// |  and similar configuration items.                                                |
// |                                                                                  |
// |                                                                                  |
// |----------------------------------------------------------------------------------|

sealed class AppConfig {
    companion object{

        const val  TIMEOUT = 30L
        const val  DATABASE_NAME = "Books_db"

        val BASE_URL = BuildConfig.BASE_URL
        val API_KEY = BuildConfig.API_KEY
    }
}