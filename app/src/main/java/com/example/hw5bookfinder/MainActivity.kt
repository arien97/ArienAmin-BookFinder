package com.example.hw5bookfinder

import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.hw5bookfinder.api.BookViewModel
import com.example.hw5bookfinder.api.Book
import com.example.hw5bookfinder.api.BookRepository
import com.example.hw5bookfinder.api.BookViewModelFactory
import com.example.hw5bookfinder.api.createGoogleBooksApi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val api = createGoogleBooksApi()
            val repository = BookRepository(api)
            val viewModel: BookViewModel by viewModels { BookViewModelFactory(repository) }
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "book_search") {
                composable("book_search") {
                    BookSearchScreen(viewModel = viewModel, navController = navController)
                }
                composable("book_detail/{bookId}") { backStackEntry ->
                    val bookId = backStackEntry.arguments?.getString("bookId") ?: return@composable
                    BookDetailScreen(bookId = bookId, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun BookSearchScreen(viewModel: BookViewModel, navController: NavHostController) {
    val books by viewModel.bookList.collectAsState()
    var query by remember { mutableStateOf("") }

    Column {
        // Search bar
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            TextField(
                value = query,
                onValueChange = { newQuery -> query = newQuery },
                label = { Text("Search for books...") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    if (query.isNotEmpty()) {
                        viewModel.searchBooks(query)
                    } else {
                        viewModel.loadSuggestedBooks()
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Search")
            }
        }

        // Book list
        LazyColumn {
            items(books) { book ->
                BookItem(book = book, onClick = {
                    navController.navigate("book_detail/${book.id}")
                })
            }
        }
    }
}

@Composable
fun BookItem(book: Book, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(book.volumeInfo.imageLinks?.thumbnail),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = book.volumeInfo.title)
            Text(text = book.volumeInfo.authors?.joinToString(", ") ?: "Unknown")
        }
    }
}

@Composable
fun BookDetailScreen(bookId: String, viewModel: BookViewModel) {
    val books by viewModel.bookList.collectAsState()
    val book = books.find { it.id == bookId }

    Column(modifier = Modifier.padding(16.dp)) {
        if (book != null) {
            Text(text = "Title: ${book.volumeInfo.title}")
            Text(text = "Authors: ${book.volumeInfo.authors?.joinToString(", ") ?: "Unknown"}")
        }
    }
}