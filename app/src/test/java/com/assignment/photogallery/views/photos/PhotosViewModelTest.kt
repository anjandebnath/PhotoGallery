package com.assignment.photogallery.views.photos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.assignment.photogallery.network.PhotoService
import com.assignment.photogallery.paging.PhotoPagingSource
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import util.MockResponseFileReader

@RunWith(JUnit4::class)
class PhotosViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    lateinit var photListService: PhotoService
    private val server = MockWebServer()
    private lateinit var mockedResponse: String
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    @Before
    fun setUp() {

        server.start(8000)
        var BASE_URL = server.url("/").toString()
        val okHttpClient = OkHttpClient
            .Builder()
            .build()

        val service = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build().create(PhotoService::class.java)

        photListService = service
    }

    @Test
    fun testMockApi(){

        mockedResponse = MockResponseFileReader("DetailsResponseApi/success.json").content
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockedResponse)
        )
        val response = runBlocking {
            Pager(PagingConfig(pageSize = 1)){
                PhotoPagingSource(photListService)
            }
        }

        Assert.assertNotNull(response)

    }
}