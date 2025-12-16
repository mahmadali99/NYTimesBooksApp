package com.example.nytimesbooksapp.presentation.details

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.nytimesbooksapp.presentation.home.HomeUiState
import com.example.nytimesbooksapp.presentation.viewmodel.BookViewModel
import com.example.nytimesbooksapp.presentation.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookDetailsScreen(
    isbn: String,
    navController: NavHostController,
    themeViewModel: ThemeViewModel = hiltViewModel(),
    viewModel: BookViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedBook by viewModel.selectedBook.collectAsState()

    val isDarkTheme by themeViewModel.isDark.collectAsState()

    LaunchedEffect(isbn) {
        viewModel.getBookByIsbn(isbn)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { themeViewModel.setTheme(!isDarkTheme) }) {
                        Icon(
                            imageVector = if (isDarkTheme)
                                Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )

            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = Color(0xFF08D8F3))
            ) {
                when {
                    uiState is HomeUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }

                    uiState is HomeUiState.Error -> {
                        val message = (uiState as HomeUiState.Error).message
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error loading book details: $message",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    selectedBook != null -> {
                        val book = selectedBook!!
                        Box(
                            modifier = Modifier
                                .fillMaxSize()

                                .verticalScroll(rememberScrollState())
                                .background(color = Color(0xFF08D8F3))
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(book.imageUrl),
                                contentDescription = book.title,
                                modifier = Modifier

                                    .height(300.dp)
                                    .fillMaxWidth()

                                    .blur(8.dp), contentScale = ContentScale.Crop
                            )
                        }
                            Column(modifier = Modifier.fillMaxSize().padding(4.dp))
                            {
                                Image(
                                painter = rememberAsyncImagePainter(book.imageUrl),
                                contentDescription = book.title,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(bottom = 20.dp, top = 30.dp)
                            )
                            Text(
                                text = book.title ?: "Untitled",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "By ${book.author ?: "Unknown"}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Publisher: ${book.publisher ?: "N/A"}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Rank: ${book.rank ?: "Unknown"}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(16.dp))

                            Text(
                                text = book.description ?: "No description available.",
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                                color = Color.Blue
                            )

                            Spacer(Modifier.height(24.dp))

                            val context = LocalContext.current
                            Button(
                                onClick = {
                                    book.primaryIsbn13?.let {
                                        val url = "https://www.amazon.com/s?k=$it"
                                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                        context.startActivity(intent)
                                    }
                                }, modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            {
                                Text("Buy Now")
                            }
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Book not found", color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
            }
        }
    )
}
