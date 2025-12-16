package com.example.nytimesbooksapp.data.remote

import com.example.nytimesbooksapp.domain.model.Book

fun BooksResponseDto.toDomainBooks(): List<Book>
{
    val allBooks = mutableListOf<Book>() // empty list where we will store all converted books objects
    val result = results ?: return emptyList()


    result.lists?.forEach { list ->
        list.books?.forEach { dto ->

            allBooks.add(
                Book(
                    title = dto.title,
                    author = dto.author,
                    description = dto.description,
                    imageUrl = dto.book_image,
                    publisher = dto.publisher,
                    publishedDate = result.published_date,
                    primaryIsbn13 = dto.primaryIsbn13,
                    rank = dto.rank
                )
            )
        }
    }

    return allBooks
}