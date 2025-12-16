package com.example.nytimesbooksapp
import com.example.nytimesbooksapp.data.local.repository.BookRepositoryImpl
import com.example.nytimesbooksapp.domain.model.Book
import com.example.nytimesbooksapp.domain.usecases.GetBooksUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetBooksUseCaseTest {

    private lateinit var useCase: GetBooksUseCase
    private val repo: BookRepositoryImpl = mockk()

    private val sampleBooks = listOf(
        Book(
            title = "Book One",
            author = "Author A",
            description = "Test Desc",
            imageUrl = "",
            publisher = "Pub1",
            publishedDate = "2024-01-01",
            primaryIsbn13 = "1234567890",
            rank = "1"
        ),
        Book(
            title = "Book Two",
            author = "Author B",
            description = "Desc 2",
            imageUrl = "",
            publisher = "Pub2",
            publishedDate = "2024-01-02",
            primaryIsbn13 = "0987654321",
            rank = "3"
        )
    )

    @Before
    fun setup() {
        useCase = GetBooksUseCase(repo)
    }

    @Test
    fun `invoke returns list of books from repository`() = runTest {
        // GIVEN
        coEvery { repo.getBooks(forceRefresh = true) } returns flowOf(sampleBooks)

        // WHEN
        val result = useCase(true)

        // THEN
        result.collect { list ->
            assertEquals(sampleBooks, list)
        }
    }

    @Test
    fun `searchBooks delegates the call to repository`() = runTest {
        coEvery { repo.searchBooks("Book") } returns sampleBooks

        val result = useCase.searchBooks("Book")

        assertEquals(sampleBooks, result)
        coVerify { repo.searchBooks("Book") }
    }

    @Test
    fun `getCachedBooks delegates to repository`() = runTest {
        coEvery { repo.getCachedBooks() } returns sampleBooks

        val result = useCase.getCachedBooks()

        assertEquals(sampleBooks, result)
        coVerify { repo.getCachedBooks() }
    }

    @Test
    fun `getBookByIsbn delegates to repository`() = runTest {
        coEvery { repo.getBookByIsbn("123") } returns sampleBooks.first()

        val result = useCase.getBookByIsbn("123")

        assertEquals(sampleBooks.first(), result)
        coVerify { repo.getBookByIsbn("123") }
    }
}
