package com.example.nytimesbooksapp.data.local.mapper

import com.example.nytimesbooksapp.data.local.roomdatabase.bookentity.BookEntity
import com.example.nytimesbooksapp.domain.model.Book

fun BookEntity.toDomain(): Book = Book( // Database to Domain
    title = title,
    author = author,
    description = description,
    imageUrl = imageUrl,
    publisher = publisher,
    publishedDate = publishedDate,
    primaryIsbn13 = primaryIsbn13,
    rank = rank
)

fun Book.toEntity(): BookEntity = BookEntity( // Domain model to Room
    title = title,
    author = author,
    description = description,
    imageUrl = imageUrl,
    publisher = publisher,
    publishedDate = publishedDate,
    primaryIsbn13 = primaryIsbn13,
    rank = rank
)
