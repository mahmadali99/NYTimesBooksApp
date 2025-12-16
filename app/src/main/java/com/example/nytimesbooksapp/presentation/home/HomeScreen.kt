package com.example.nytimesbooksapp.presentation.home
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.nytimesbooksapp.R
import com.example.nytimesbooksapp.domain.model.Book
import com.example.nytimesbooksapp.presentation.navigation.NavRoutes
import com.example.nytimesbooksapp.presentation.viewmodel.BookViewModel
import com.example.nytimesbooksapp.presentation.viewmodel.ThemeViewModel
import java.time.LocalDate
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun HomeScreen(
    navController: NavHostController,
    viewModel: BookViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val pullToRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.dispatch(HomeIntent.Refresh ) },
        state = pullToRefreshState,
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()
            ) {
            HomeTopBar()
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {viewModel.dispatch(HomeIntent.Search(it))},
                label = { Text("Search by title") },
                singleLine = true,

                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),

                keyboardActions = KeyboardActions(
                    onDone = { }
                ),

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp, vertical = 1.dp),
                shape = RoundedCornerShape(50), // added later
                trailingIcon = {Icon(Icons.Default.Search, contentDescription = null)}, // added later

                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary

                )
            )
            Text(
                text ="    Last sync: ${viewModel.lastSyncTime.collectAsState().value}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                )
            when (uiState) {
                is HomeUiState.Loading -> LoadingView()
                is HomeUiState.Error ->{
                    val message = (uiState as HomeUiState.Error).message
                if (message.contains("offline", ignoreCase = true)) OfflineView()
                    else ErrorView((uiState as HomeUiState.Error).message) {
                        viewModel.loadBooks(forceRefresh = true)
                    }
                }
                is HomeUiState.Success -> {
                    val books = (uiState as HomeUiState.Success).books
                    if (books.isNotEmpty()) {
                        BookList(
                            books = books,
                            isRefreshing = false,
                            onRefresh = { viewModel.dispatch(HomeIntent.Refresh) },
                            onBookClick = { isbn->
                                navController.navigate(NavRoutes.Details.createRoute(isbn))
                                          },

                        )
                    } else EmptyView()
                }
                HomeUiState.Empty -> EmptyView()
            }
        }
        FloatingMenu(viewModel, themeViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    TopAppBar(
        title = {
            Text(
                "NYTimes Books",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun showDatePickerDialog(context: Context, onDateSelected: (String) -> Unit) {
    val today = LocalDate.now()

    val dialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            val formatted = "%04d-%02d-%02d".format(year, month + 1, day)
            onDateSelected(formatted)
        },
        today.year,
        today.monthValue - 1,
        today.dayOfMonth
    )
    dialog.show()
}
@Composable
fun EmptyView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("No Books Found", style = MaterialTheme.typography.bodyLarge)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookList(
    books: List<Book>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onBookClick: (String)-> Unit,

) {
    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state,
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {
            items(books) { book ->
                BookItem(book= book, onBookClick = onBookClick)//onBookClick } )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FloatingMenu(viewModel: BookViewModel, themeViewModel: ThemeViewModel) {
    var menu by remember { mutableStateOf(false ) }

    val isDark by themeViewModel.isDark.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (menu) {
                val context = LocalContext.current
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.offset(x= 10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        SmallFloatingActionButton(
                            onClick = {
                                viewModel.syncNow()
                                menu = false
                            },
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.img),
                                contentDescription = "Sync Now",
                                modifier = Modifier.size(20.dp),
                                tint = Color.Unspecified
                            )
                        }
                        Text(
                            "Sync Now",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        SmallFloatingActionButton(
                            onClick = {

                                themeViewModel.setTheme(!isDark)

                                menu = false
                            },
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme",
                                modifier = Modifier.size(20.dp),
                                tint = Color.Unspecified
                            )
                        }
                        Text(
                            "Theme",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        SmallFloatingActionButton(
                            onClick = {

                                showDatePickerDialog(context) { selectedDate ->
                                    viewModel.dispatch(HomeIntent.LoadWithDate(selectedDate))//pickDateAndLoad(selectedDate)
                                }

                                menu = false
                            },
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.img_2),
                                contentDescription = "Filter Date",
                                modifier = Modifier.size(20.dp),
                                tint = Color.Unspecified
                            )
                        }

                        Text(
                            "Select by Date",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
            }

            FloatingActionButton(
                onClick = { menu = !menu },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (menu) Icons.Default.Close else Icons.Default.Menu,
                        contentDescription = "Menu"
                    )
                }
            }
        }
    }
}


@Composable
fun OfflineView(){

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                painter = painterResource(id = R.drawable.img_1),
                contentDescription = "wifi-Lost",
                tint = Color.Gray,
                modifier = Modifier.size(50.dp)
            )
        }
        Text("You're Offline", style = MaterialTheme.typography.titleMedium)
    }
    Text("Showing Saved Books", color = Color.Gray, style = MaterialTheme.typography.bodySmall)

}

@Composable
fun BookItem(book: Book, onBookClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { book.primaryIsbn13?.let { onBookClick(it) } },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = rememberAsyncImagePainter(book.imageUrl),
                contentDescription = book.title,
                modifier = Modifier
                    .size(90.dp)
                    .padding(end = 8.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title ?: "Untitled",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = book.description ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

            }
        }
    }
}
@Composable
fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.searching))
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever
        )
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier.size(150.dp)
        )
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cat))
            val progress by animateLottieCompositionAsState(
                composition,
                iterations = LottieConstants.IterateForever
            )
            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Error: $message",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}




