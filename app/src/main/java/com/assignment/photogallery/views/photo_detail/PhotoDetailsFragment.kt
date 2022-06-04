package com.assignment.photogallery.views.photo_detail

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.assignment.photogallery.R
import com.assignment.photogallery.databinding.FragmentDetailsBinding
import com.assignment.photogallery.permission.PermissionHelper
import com.assignment.photogallery.permission.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.*
import java.util.concurrent.Executors


@AndroidEntryPoint
class PhotoDetailsFragment : Fragment() , PermissionListener {
    private val viewModel: PhotoDetailsViewModel by viewModels()
    private val args: PhotoDetailsFragmentArgs by navArgs()

    private var _binding: FragmentDetailsBinding? = null
    private var imgSave: ImageView? = null
    private var imgShare: ImageView? = null
    private val binding get() = _binding!!

    lateinit var permissionHelper: PermissionHelper
    var isSave :Boolean?= false
    var isShare :Boolean?= false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_details, container, false
        )

        binding.viewModel = viewModel
//        imgSave = binding.imgSave
//        imgShare = binding.imgShare
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        permissionHelper =  PermissionHelper(this, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.photoUrl = args.photoId

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.action_save) {
            isSave = true
            isShare = false
            permissionHelper.checkForMultiplePermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
            return true
        }
        if (id == R.id.action_share) {
            isSave = false
            isShare = true
            permissionHelper.checkForMultiplePermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    companion object {
        const val SHARED_FILE_NAME = "share_screenshot.png"
        private const val CACHE_DIRECTORY = "our_screenshots/"
    }

    private val shareResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // Optional - called as soon as the user selects an option from the system share dialog
    }


    private fun shareBitmap(bitmap: Bitmap) {

        val cachePath = File(requireActivity().externalCacheDir, CACHE_DIRECTORY)
        cachePath.mkdirs()

        val screenshotFile = File(cachePath, SHARED_FILE_NAME).also { file ->
            FileOutputStream(file).use { fileOutputStream -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream) }
        }.apply {
            deleteOnExit()
        }

        val shareImageFileUri: Uri = FileProvider.getUriForFile(requireActivity(), requireContext().applicationContext.packageName + ".provider", screenshotFile)
        val shareMessage: String = "Unsplash Image to be shared"

        // Create the intent
        val intent = Intent(Intent.ACTION_SEND).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, shareImageFileUri)
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "image/png"
        }


        // Initialize the share chooser
        val chooserTitle: String = "Share your image!"
        val chooser = Intent.createChooser(intent, chooserTitle)
        val resInfoList: List<ResolveInfo> = requireContext().packageManager.queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName: String = resolveInfo.activityInfo.packageName
            requireContext().grantUriPermission(packageName, shareImageFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        shareResult.launch(chooser)
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
            binding.progressBar.visibility = View.VISIBLE
            if (isSave!!){
                storageSave()
            }

            if(isShare!!){
                lifecycleScope.launch {

                    val loader = ImageLoader(requireContext())
                    val request = ImageRequest.Builder(requireContext())
                        .data(args.photoId)
                        .allowHardware(false) // Disable hardware bitmaps.
                        .build()

                    val result = (loader.execute(request) as SuccessResult).drawable
                    val bitmap = (result as BitmapDrawable).bitmap
                    //Log.e("Bitmap", bitmap.toString())
                    binding.progressBar.visibility = View.GONE
                    shareBitmap(bitmap)
                }
            }

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
            binding.progressBar.visibility = View.GONE
            Toast.makeText(context, "Saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    }

}