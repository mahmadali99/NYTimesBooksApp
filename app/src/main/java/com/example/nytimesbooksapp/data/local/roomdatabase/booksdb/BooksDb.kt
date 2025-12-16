package com.example.nytimesbooksapp.data.local.roomdatabase.booksdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nytimesbooksapp.data.local.roomdatabase.dao.BookDao
import com.example.nytimesbooksapp.data.local.roomdatabase.bookentity.BookEntity

@Database(entities = [BookEntity::class], version = 1, exportSchema = false)
abstract class BooksDb : RoomDatabase()
{

    abstract fun booksDao(): BookDao
}