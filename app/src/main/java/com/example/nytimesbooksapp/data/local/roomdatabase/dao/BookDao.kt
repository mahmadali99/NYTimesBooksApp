package com.example.nytimesbooksapp.data.local.roomdatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nytimesbooksapp.data.local.roomdatabase.bookentity.BookEntity

@Dao
interface BookDao {

    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<BookEntity>
    @Query("SELECT * FROM Books WHERE primaryIsbn13 = :isbn LIMIT 1")
    suspend fun getBookByIsbn(isbn:String): BookEntity?

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%'")
    suspend fun searchBooksByTitle(query: String): List<BookEntity>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Query("DELETE FROM books")
    suspend fun clearBooks()
}
