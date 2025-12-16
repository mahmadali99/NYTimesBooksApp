package com.example.nytimesbooksapp.domain.repository

import com.example.nytimesbooksapp.data.local.roomdatabase.dao.BookDao
import com.example.nytimesbooksapp.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun getBooks( forceRefresh: Boolean = false): Flow<List<Book>>
    suspend fun getCachedBooks(): List<Book>
    suspend fun getBookByIsbn(isbn: String): Book?
    suspend fun getBooksByDate(date:String): List<Book>



}