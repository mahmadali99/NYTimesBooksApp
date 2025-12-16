package com.example.nytimesbooksapp

import com.example.nytimesbooksapp.data.local.repository.BookRepositoryImpl
import com.example.nytimesbooksapp.data.local.roomdatabase.bookentity.BookEntity
import com.example.nytimesbooksapp.data.local.roomdatabase.dao.BookDao
import com.example.nytimesbooksapp.data.remote.BookDto
import com.example.nytimesbooksapp.data.remote.BookListDto
import com.example.nytimesbooksapp.data.remote.BooksApiService
import com.example.nytimesbooksapp.data.remote.BooksResponseDto
import com.example.nytimesbooksapp.data.remote.BooksResultDto
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookRepositoryImplTest {

    private lateinit var api: BooksApiService
    private lateinit var dao: BookDao
    private lateinit var repo: BookRepositoryImpl

    @Before
    fun setUp() {
        api = mockk()
        dao = mockk()
        repo = BookRepositoryImpl(api, dao)
    }

    @Test
    fun `getBooks returns cached when not empty`() = runTest {
        val cached = listOf(
            BookEntity(
                id = 1,
                title = "Test",
                author = "A",
                imageUrl = null,
                description = null,
                publisher = null,
                publishedDate = null,
                primaryIsbn13 = "123",
                rank = "1"
            )
        )

        coEvery { dao.getAllBooks() } returns cached

        val result = repo.getBooks(forceRefresh = false).first()

        assert(result.isNotEmpty())
        assert(result[0].title == "Test")
    }

    @Test
    fun `getBooks fetches from API when forceRefresh true`() = runTest {
        coEvery { dao.getAllBooks() } returns emptyList()

        val dto = BooksResponseDto(
            results = BooksResultDto(
                published_date = "2024-01-01",
                lists = listOf(
                    BookListDto(
                        books = listOf(
                            BookDto(
                                title = "Network Book",
                                author = "Author",
                                description = "desc",
                                book_image = "",
                                publisher = "Pub",
                                primaryIsbn13 = "12345",
                                rank = "2"
                            )
                        )
                    )
                )
            )
        )

        coEvery { api.getBooksOverview(any()) } returns dto
        coEvery { dao.insertBooks(any()) } just Runs
        coEvery { dao.clearBooks() } just Runs

        val result = repo.getBooks(forceRefresh = true).first()

        assert(result.isNotEmpty())
        assert(result[0].title == "Network Book")
    }

    @Test
    fun `searchBooks should combine local and remote results`() = runTest {
        // local
        coEvery { dao.searchBooksByTitle("Harry") } returns listOf(
            BookEntity(
                id = 1,
                title = "Harry Local",
                author = "X",
                imageUrl = null,
                description = "",
                publisher = "",
                publishedDate = "",
                primaryIsbn13 = "1111",
                rank = "2"
            )
        )

        // remote
        coEvery { api.getBooksOverview(any()) } returns BooksResponseDto(
            BooksResultDto(
                published_date = "2024-01-01",
                lists = listOf(
                    BookListDto(
                        books = listOf(
                            BookDto(
                                title = "Harry Remote",
                                author = "Y",
                                description = "",
                                book_image = "",
                                publisher = "",
                                primaryIsbn13 = "2222",
                                rank = "4"
                            )
                        )
                    )
                )
            )
        )

        coEvery { dao.insertBooks(any()) } just Runs

        val result = repo.searchBooks("Harry")

        assert(result.size == 2)
    }
}
