package com.assignment.photogallery.views.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.assignment.photogallery.network.PhotoService
import com.assignment.photogallery.paging.PhotoPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor( // Primary constructor
    private val photListService: PhotoService
) : ViewModel() {

    val listData = Pager(PagingConfig(pageSize = 1)){
        PhotoPagingSource(photListService)

    }.flow.cachedIn(viewModelScope) // flow allows continous stream of data


}