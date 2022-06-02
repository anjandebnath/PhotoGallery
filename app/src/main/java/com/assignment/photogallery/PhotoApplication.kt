package com.assignment.photogallery

import android.app.Application
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PhotoApplication: Application() {

    override fun onCreate(){
        super.onCreate()

        UnsplashPhotoPicker.init(
            this, // application
            BuildConfig.ACCESS_KEY,
            BuildConfig.SECRET_KEY,
            20
        )
    }

}