package com.assignment.photogallery.views.photo_detail

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.assignment.photogallery.R
import com.assignment.photogallery.databinding.FragmentDetailsBinding
import com.assignment.photogallery.permission.PermissionHelper
import com.assignment.photogallery.permission.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors

@AndroidEntryPoint
class PhotoDetailsFragment : Fragment() , PermissionListener {
    private val viewModel: PhotoDetailsViewModel by viewModels()
    private val args: PhotoDetailsFragmentArgs by navArgs()

    private var _binding: FragmentDetailsBinding? = null
    private var imgSave: ImageView? = null
    private val binding get() = _binding!!

    lateinit var permissionHelper: PermissionHelper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_details, container, false
        )

        binding.viewModel = viewModel
        imgSave = binding.imgSave
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionHelper =  PermissionHelper(this, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.photoUrl = args.photoId

        imgSave?.setOnClickListener {
            permissionHelper.checkForMultiplePermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

    }

    private fun storageSave(){
        // Declaring a Bitmap local
        var mImage: Bitmap?
        // Declaring and initializing an Executor and a Handler
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())

        myExecutor.execute {
            mImage = viewModel.mLoad(args.photoId)
            myHandler.post {
                if (mImage != null) {
                    mSaveMediaToStorage(mImage)
                }
            }
        }
    }

    override fun shouldShowRationaleInfo() {
        println("shouldShowRationaleInfo...................")
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // set message of alert dialog
        dialogBuilder.setMessage("Read/Write Storage permission is Required")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("OK") { dialog, id ->
                permissionHelper.launchPermissionDialogForMultiplePermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                dialog.cancel()
            }
            // negative button text and action
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("AlertDialogExample")
        // show alert dialog
        alert.show()
    }

    override fun isPermissionGranted(isGranted: Boolean) {
        if(isGranted){
            Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
            storageSave()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun mSaveMediaToStorage(bitmap: Bitmap?) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context?.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(context, "Saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    }

}