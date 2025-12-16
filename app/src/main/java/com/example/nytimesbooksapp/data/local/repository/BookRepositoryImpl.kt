package com.example.nytimesbooksapp.data.local.repository

import com.example.nytimesbooksapp.data.local.roomdatabase.dao.BookDao
import com.example.nytimesbooksapp.data.local.mapper.toDomain
import com.example.nytimesbooksapp.data.local.mapper.toEntity
import com.example.nytimesbooksapp.data.remote.BooksApiService
import com.example.nytimesbooksapp.data.remote.toDomainBooks
import com.example.nytimesbooksapp.domain.model.Book
import com.example.nytimesbooksapp.domain.repository.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val apiService: BooksApiService,
    private val bookDao: BookDao,
) : BookRepository {
    override suspend fun getBooks(forceRefresh: Boolean): Flow<List<Book>> = flow {
        val cachedBooks = bookDao.getAllBooks().map { it.toDomain() }
        if (cachedBooks.isNotEmpty()) emit(cachedBooks)

        if (forceRefresh || cachedBooks.isEmpty()) {
            try {
                val response = apiService.getBooksOverview()
                val networkBooks = response.toDomainBooks()

                bookDao.clearBooks()
                bookDao.insertBooks(networkBooks.map { it.toEntity() })

                emit(networkBooks)
            } catch (e: Exception) {
                emit(cachedBooks)
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getCachedBooks(): List<Book> {
        return bookDao.getAllBooks().map { it.toDomain() }

    }
    suspend fun searchBooks(query: String): List<Book> {
        //  Search cached books
        val localResults = bookDao.searchBooksByTitle(query).map { it.toDomain() }

        // Search API
        val remoteResults = try {
            val response = apiService.getBooksOverview()
            response.toDomainBooks()
                .filter { it.title?.contains(query, ignoreCase = true) == true }
                .onEach { bookDao.insertBooks(listOf(it.toEntity())) } // update cache
        } catch (e: Exception) {
            emptyList()
        }

        return (localResults + remoteResults).distinctBy { it.title }
    }

    // added later
     override suspend fun getBookByIsbn(isbn: String): Book?{
         val local = bookDao.getBookByIsbn(isbn)//?.toDomain()
        if (local!= null ) return local.toDomain()

     return try {
         val network = apiService.getBooksOverview().toDomainBooks()
         val found = network.find { it.primaryIsbn13 == isbn }
         found?.let { bookDao.insertBooks(listOf(it.toEntity())) }
         found
     } catch (e: Exception)
     {
         null
     }
     }
    override suspend fun getBooksByDate(date: String): List<Book> {
        return try {


            val response = apiService.getBooksOverview(date)
            val networkBooks = response.toDomainBooks()
            bookDao.clearBooks()
            bookDao.insertBooks(networkBooks.map { it.toEntity() })
            networkBooks
        }
        catch (e: Exception)
        {
            bookDao.getAllBooks().map { it.toDomain() }
        }
    }

}
