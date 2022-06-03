package com.assignment.photogallery.views.photos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.assignment.photogallery.databinding.GridPhotoListBinding
import com.assignment.photogallery.model.PhotosResponseApiItem
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class PhotosPagedAdapter @Inject constructor(val clickListener: ClickListener):
    PagingDataAdapter<PhotosResponseApiItem, PhotosPagedAdapter.MyViewHolder>(UsersListDiffCallback()) {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = getItem(position)
        // this method is from Paging
        currentItem?.let { holder.bind(it, clickListener) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }


    class MyViewHolder private constructor(private val binding: GridPhotoListBinding) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: PhotosResponseApiItem, clickListener: ClickListener) {
            binding.data = item
            binding.executePendingBindings()
            binding.clickListener = clickListener
        }

        /**
         * companion object is equivalent to Static.
         * A class that has a companion object, the members of the class can easily be accessed
         * without creating an object to access them.
         */
        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GridPhotoListBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

}

class UsersListDiffCallback : DiffUtil.ItemCallback<PhotosResponseApiItem>() {

    override fun areItemsTheSame(oldItem: PhotosResponseApiItem, newItem: PhotosResponseApiItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PhotosResponseApiItem, newItem: PhotosResponseApiItem): Boolean {
        return oldItem == newItem
    }

}

class ClickListener @Inject constructor() {

    var onItemClick: ((PhotosResponseApiItem) -> Unit)? = null

    fun onClick(data: PhotosResponseApiItem) {
        onItemClick?.invoke(data)
    }
}