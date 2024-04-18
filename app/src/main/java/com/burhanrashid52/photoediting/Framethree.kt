package com.burhanrashid52.photoediting


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.VisibleForTesting
import com.bumptech.glide.Glide
import com.burhanrashid52.photoediting.Ads.Gone
import com.burhanrashid52.photoediting.Ads.isNetworkConnected
import com.burhanrashid52.photoediting.Ads.loadNativeAd
//import com.burhanrashid52.photoediting.Ads.loadNativeAd
import com.burhanrashid52.photoediting.Ads.sendAnalytics
import com.google.android.material.snackbar.Snackbar
import editphotos.backgroundremover.photobackgroundchanger.R
import editphotos.backgroundremover.photobackgroundchanger.databinding.ActivityFramethreeBinding
import java.io.File

class Framethree : AppCompatActivity() {
    private lateinit var imageResult: ActivityResultLauncher<String>
    private val _binding : ActivityFramethreeBinding by lazy {
        ActivityFramethreeBinding.inflate(layoutInflater)
    }
    @VisibleForTesting
    var mSaveImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

//        _binding.imageView.setImageURI(Frametwo.pUri)
//
//        val imageUri = intent.getStringExtra("fromFrame2")
//        Glide.with(this)
//            .load(Uri.parse(imageUri))
//            .into(_binding.imageView)
//        imageResult.launch(imageUri)

        val imageView = findViewById<ImageView>(R.id.image_view)

        // Get the image uri from the intent extra
        val imageUri = intent.getStringExtra("imageUri")

        // Set the clicked image to the ImageView in this activity
        imageView.setImageURI(Uri.parse(imageUri))

        _binding.next.setOnClickListener {
            sendAnalytics("Framethree","delete_btn_click")
            val imageUri = intent.getStringExtra("imageUri")
            val fileToDelete = getFileFromUri(Uri.parse(imageUri))
            if (fileToDelete.exists()) {
                fileToDelete.delete()
            }
            setResult(RESULT_OK)
            finish()
        }
        _binding.sharee.setOnClickListener {
            sendAnalytics("Framethree","share_btn_click")
            shareImage()
        }
        with(_binding){
            if(isNetworkConnected()) {
                loadNativeAd(
                    frame.adContainer,
                    frame.ShimmerContainerSmall,
                    frame.FrameLayoutSmall,
                    R.layout.small_native_ad,
                    getString(R.string.native_ad_id_home)
                )
            }else{
                frame1.Gone()
            }
        }
        _binding.arrowback.setOnClickListener {
            finish()
        }

    }
    private fun shareImage() {
        val saveImageUri = intent.getStringExtra("imageUri")
        if (saveImageUri == null) {
            showSnackbar(getString(R.string.msg_save_image_to_share))
            return
        }

        // Create an Intent with the ACTION_SEND action and set the image URI as the data
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_STREAM, saveImageUri)
        startActivity(Intent.createChooser(intent, "Share Image"))
    }


    protected fun showSnackbar(message: String) {
        val view = findViewById<View>(android.R.id.content)
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun getFileFromUri(uri: Uri): File {
        return File(uri.path!!)
    }

}