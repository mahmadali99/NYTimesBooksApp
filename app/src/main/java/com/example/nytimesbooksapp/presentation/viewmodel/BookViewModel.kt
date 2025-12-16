package com.example.nytimesbooksapp.presentation.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nytimesbooksapp.data.datastore.UserPreferencesDataStore
import com.example.nytimesbooksapp.data.local.repository.BookRepositoryImpl
import com.example.nytimesbooksapp.domain.model.Book
import com.example.nytimesbooksapp.domain.usecases.GetBooksUseCase
import com.example.nytimesbooksapp.presentation.home.HomeEffect
import com.example.nytimesbooksapp.presentation.home.HomeIntent
import com.example.nytimesbooksapp.presentation.home.HomeUiState
import com.example.nytimesbooksapp.presentation.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class BookViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val prefs : UserPreferencesDataStore,
    private val repo : BookRepositoryImpl,
    application: Application
) : AndroidViewModel(application){

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook

    private var allBooks: List<Book> = emptyList()
    private var isInitialLoadDone = false

    private val _lastSyncTime = MutableStateFlow<String>("never")
    val  lastSyncTime = _lastSyncTime.asStateFlow()

    private val _selectedDate = MutableStateFlow("")
            val selectedDate : StateFlow<String> = _selectedDate.asStateFlow()

    private val _effects = MutableSharedFlow<HomeEffect>()
    val effects: SharedFlow<HomeEffect> = _effects.asSharedFlow()
    val formatter = java.text.SimpleDateFormat("hh:mm a, dd MMM", java.util.Locale.getDefault())

    init {// _________________________________________________________________________________________________________
                loadBooks(forceRefresh = false) // 1st Time API Calling


                viewModelScope.launch {
        prefs.lastSyncTime.collect { last ->

            _lastSyncTime.value = formatter.format(last)
        }

        prefs.selectedDate.collect { date ->
            _selectedDate.value = date
        }
    }
} // _________________________________________________________________________________________________________




    fun syncNow() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val now = System.currentTimeMillis()

            loadBooks(forceRefresh = true, isInitial = false)

            prefs.updateLastSyncTime(now)

            _lastSyncTime.value = formatter.format(java.util.Date(now))
        }
    }



    fun dispatch(intent : HomeIntent) {
        when (intent) {

            HomeIntent.ClearSearch -> TODO()

            HomeIntent.LoadBooks -> loadBooks()

            is HomeIntent.LoadBooksByForce -> loadBooks(forceRefresh = true, intent.isInitial)

            is HomeIntent.LoadWithDate -> loadBooksByDate(intent.date)

            HomeIntent.Refresh -> loadBooks(forceRefresh = true)


            is HomeIntent.Search -> {
                _searchQuery.value = intent.query
                if (intent.query.isBlank()) {
                    _uiState.value =
                        if (allBooks.isEmpty()) HomeUiState.Empty else HomeUiState.Success(allBooks)
                } else {
                    onSearch(intent.query)
                }
            }


            is HomeIntent.ShowDetails -> {
                getBookByIsbn(intent.isbn)
                viewModelScope.launch { _effects.emit(HomeEffect.NavigateToDetails(intent.isbn)) }
            }

        }
    } // _________________________________________________________________________________________________________




    fun loadBooks(forceRefresh: Boolean = false, isInitial: Boolean = false) {
        viewModelScope.launch @androidx.annotation.RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE) {
            val context = getApplication<Application>().applicationContext
            val isOnline = NetworkUtils.isNetworkAvailable(context)
            if (!isOnline){
                val cached = getBooksUseCase.getCachedBooks()
                if (cached.isEmpty()){
                    _uiState.value= HomeUiState.Error("You're Offline. No cached Data is Available")
                }
                else
                {
                    _uiState.value = HomeUiState.Success(cached)
                }
                return@launch

            }
            getBooksUseCase(forceRefresh)
                .onStart {
                    if (isInitial){
                        _isRefreshing.value = true
                    _uiState.value = HomeUiState.Loading
                }
                    else
                    {
                        _uiState.value = HomeUiState.Loading
                    }
                }
                .catch { e ->
                    _isRefreshing.value = false
                    _uiState.value = HomeUiState.Error(
                        e.localizedMessage ?: "Unexpected error occurred"
                    )
                }
                .collect { books ->
                    delay(2000L)
                    _isRefreshing.value = false
                    allBooks = books
                    _uiState.value = when {books.isEmpty() ->HomeUiState.Empty
                     else -> HomeUiState.Success(books)
                    }
                    if (forceRefresh){
                        val currentTimeMillis = System.currentTimeMillis()
                        prefs.updateLastSyncTime(currentTimeMillis)
                    }

                    isInitialLoadDone = true

                }
        }
    } // _________________________________________________________________________________________________________





fun onSearch(query: String) {
    Log.e("search", "$searchQuery", )

    _searchQuery.value = query
    viewModelScope.launch {
        _uiState.value = HomeUiState.Loading
        try {
            val results = getBooksUseCase.searchBooks(query)
            _uiState.value = if (results.isEmpty()) HomeUiState.Empty
            else HomeUiState.Success(results)
        } catch (e: Exception) {
            _uiState.value = HomeUiState.Error(e.localizedMessage ?: "Unexpected error")
        }
    }

}

    fun getBookByIsbn(isbn: String) {
        viewModelScope.launch { _uiState.value = HomeUiState.Loading

            try {
                val book = getBooksUseCase.getBookByIsbn(isbn)

                if (book != null)
                {
                    _selectedBook.value = book
                    _uiState.value = HomeUiState.Success(listOf(book))
                }
                else
                {
                    _uiState.value = HomeUiState.Error("Book Not Found")
                }
            }catch (e: Exception)
            {
                _uiState.value = HomeUiState.Error(e.localizedMessage?:"Error Loading Books")
            }
        }
    } // _________________________________________________________________________________________________________







    fun loadBooksByDate(date: String){
        viewModelScope.launch { _uiState.value = HomeUiState.Loading
        try {
                    val  books = getBooksUseCase.getBooksByDate(date)
            _uiState.value = if (books.isEmpty()) HomeUiState.Error("No books Found For $date")
            else HomeUiState.Success(books)
        }
        catch (e: Exception)
        {
                    _uiState.value  = HomeUiState.Error(e.localizedMessage?: "Error Fetching Books For $date ")

        }

        }
    }


}
