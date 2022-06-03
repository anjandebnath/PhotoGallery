package com.assignment.photogallery

import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main)

// THis code for android sdk usage
/*
class MainActivity : AppCompatActivity() {
    private lateinit var mAdapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // result adapter
        // recycler view configuration
        main_recycler_view.setHasFixedSize(true)
        main_recycler_view.itemAnimator = null
        main_recycler_view.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        mAdapter = PhotoAdapter(this)
        main_recycler_view.adapter = mAdapter
        // on the pick button click, we start the library picker activity
        // we are expecting a result from it so we start it for result
        main_pick_button.setOnClickListener {
            startActivityForResult(
                UnsplashPickerActivity.getStartingIntent(
                    this,
//                    !main_single_radio_button.isChecked
                false
                ), REQUEST_CODE
            )
        }
    }

    // here we are receiving the result from the picker activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            // getting the photos
            val photos: ArrayList<UnsplashPhoto>? = data?.getParcelableArrayListExtra(UnsplashPickerActivity.EXTRA_PHOTOS)
            // showing the preview
            mAdapter.setListOfPhotos(photos)
            // telling the user how many have been selected
            Toast.makeText(this, "number of selected photos: " + photos?.size, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        // dummy request code to identify the request
        private const val REQUEST_CODE = 123
    }
}*/
