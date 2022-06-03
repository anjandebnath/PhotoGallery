package com.assignment.photogallery.permission

interface PermissionListener {
    fun   shouldShowRationaleInfo()
    fun   isPermissionGranted(isGranted : Boolean)
}