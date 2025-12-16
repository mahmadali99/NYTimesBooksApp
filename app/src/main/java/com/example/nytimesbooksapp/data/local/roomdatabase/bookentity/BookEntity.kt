package com.example.nytimesbooksapp.data.local.roomdatabase.bookentity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String?,
    val author: String?,
    val imageUrl: String?,
    val description: String?,
    val publisher: String?,
    val publishedDate: String?,
    val primaryIsbn13:String?,
    val rank: String?
)