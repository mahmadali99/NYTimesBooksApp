package com.example.nytimesbooksapp.domain.usecases

import com.example.nytimesbooksapp.data.local.repository.BookRepositoryImpl
import com.example.nytimesbooksapp.domain.model.Book
import com.example.nytimesbooksapp.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
class GetBooksUseCase @Inject constructor(private val repo: BookRepositoryImpl) {

    suspend operator fun invoke(forceRefresh: Boolean) = repo.getBooks(forceRefresh)

    suspend fun searchBooks(query: String) = repo.searchBooks(query)
    suspend fun getCachedBooks() = repo.getCachedBooks()
    suspend fun getBookByIsbn(isbn: String): Book? = repo.getBookByIsbn(isbn)
    suspend fun getBooksByDate(date: String): List<Book> = repo.getBooksByDate(date)




}
