package com.assignment.photogallery.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.assignment.photogallery.model.PhotosResponseApiItem
import com.assignment.photogallery.network.PhotoService
import java.lang.Exception

class PhotoPagingSource (private val photoService: PhotoService):
    PagingSource<Int, PhotosResponseApiItem>() {

    override fun getRefreshKey(state: PagingState<Int, PhotosResponseApiItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotosResponseApiItem> {
        return try {
            val currentPage = params.key ?: 1
            val response = photoService.getPhotoList(currentPage)

            val data = response.body() ?: emptyList()
            val responseData = mutableListOf<PhotosResponseApiItem>()
            responseData.addAll(data)

            LoadResult.Page(
                data= responseData,
                prevKey = if (currentPage == 1) null else -1,
                nextKey = currentPage.plus(1)
            )

        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }
}