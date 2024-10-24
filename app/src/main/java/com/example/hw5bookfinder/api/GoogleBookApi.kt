package com.example.hw5bookfinder.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun getBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String,
        @Query("maxResults") maxResults: Int = 20
    ): BookResponse
}

class BookRepository(private val api: GoogleBooksApi) {

    suspend fun getSuggestedBooks(apiKey: String): List<Book> {
        val response = api.getBooks(query = "best+books", apiKey = apiKey)
        return response.items ?: emptyList()
    }

    suspend fun searchBooks(query: String, apiKey: String): List<Book> {
        val response = api.getBooks(query = query, apiKey = apiKey)
        return response.items ?: emptyList()
    }
}

fun createGoogleBooksApi(): GoogleBooksApi {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/v1/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    return retrofit.create(GoogleBooksApi::class.java)
}

// Data classes for JSON response
data class BookResponse(val items: List<Book>?)
data class Book(val id: String, val volumeInfo: VolumeInfo)
data class VolumeInfo(val title: String, val authors: List<String>?, val imageLinks: ImageLinks?)
data class ImageLinks(val thumbnail: String?)