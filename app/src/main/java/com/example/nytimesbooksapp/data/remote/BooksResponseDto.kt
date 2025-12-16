package com.example.nytimesbooksapp.data.remote

import com.google.gson.annotations.SerializedName

data class BooksResponseDto(

    val results: BooksResultDto? // nullable in case API changes
)

data class BooksResultDto(
    val published_date: String?,
    val lists: List<BookListDto>?
)

data class BookListDto(
//    val display_name: String?,
    val books: List<BookDto>?
)

data class BookDto(
    val title: String?,
    val author: String?,
    val description: String?,
    val book_image: String?,
    val publisher: String?,
    @SerializedName("primary_isbn13")
    val primaryIsbn13:String,
    val rank : String
)
