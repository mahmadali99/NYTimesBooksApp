package com.example.nytimesbooksapp.domain.model

data class Book(
    val title: String?,
    val author: String?,
    val description: String?,
    val imageUrl: String?,
    val publisher: String?,
    val publishedDate: String?,
    val primaryIsbn13:String?,
    val rank: String?

    )