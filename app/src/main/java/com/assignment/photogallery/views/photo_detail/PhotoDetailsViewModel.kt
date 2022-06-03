package com.assignment.photogallery.views.photo_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var photoUrl: String? = null


    fun fetchMovieDetails(_photoUrl: String){
        photoUrl?.let { _photoUrl }
    }

}