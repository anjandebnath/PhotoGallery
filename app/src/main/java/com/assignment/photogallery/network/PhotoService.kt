package com.assignment.photogallery.network

import com.assignment.photogallery.BuildConfig
import com.assignment.photogallery.model.PhotosResponseApi
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PhotoService {

    @GET("photos?client_id="+ BuildConfig.ACCESS_KEY +"")
    suspend fun getPhotoList(
        @Query("page") page:Int
    ): Response<PhotosResponseApi>

}