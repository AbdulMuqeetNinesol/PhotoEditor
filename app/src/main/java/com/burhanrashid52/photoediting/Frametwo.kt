package com.burhanrashid52.photoediting

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import com.burhanrashid52.photoediting.Ads.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.slowmac.autobackgroundremover.BackgroundRemover
import com.slowmac.autobackgroundremover.OnBackgroundChangeListener
import editphotos.backgroundremover.photobackgroundchanger.R
import editphotos.backgroundremover.photobackgroundchanger.databinding.ActivityFrametwoBinding
import ja.burhanrashid52.photoeditor.PhotoEditorView
import org.w3c.dom.Text
import java.io.File
import java.io.FileOutputStream

class Frametwo : AppCompatActivity() {
    private lateinit var mbitmap: Bitmap
    private val handler = Handler()
    private val imageResult =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let { uri ->
                _binding.imageView.setImageURI(uri)
            }
        }
    private val _binding : ActivityFrametwoBinding by lazy {
        ActivityFrametwoBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        val btn : Button = findViewById(R.id.next)
        val btn2 : Button = findViewById(R.id.back)
        val btn3 : Button = findViewById(R.id.download)
        val tx1 : TextView = findViewById(R.id.caution)
        btn.setOnClickListener(){
            if (btn.isVisible){
                btn.visibility=View.INVISIBLE
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Dexter.withContext(this)
                    .withPermissions(
                        Manifest.permission.READ_MEDIA_IMAGES,
                    ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            if (report.areAllPermissionsGranted()) {
                                imageResult.launch("image/*")
                            }
                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied) {
                                this@Frametwo.finish()
                                val intent= Intent(this@Frametwo,FrameActivity::class.java)
                                startActivity(intent)

                                // permission is denied permanently, we will show user a dialog message.

                            }



                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                            token: PermissionToken?,
                        ) {
                            token?.continuePermissionRequest();

                        }
                    }).check()
            } else {
                Dexter.withContext(this)
                    .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            if (report.areAllPermissionsGranted()) {
                                imageResult.launch("image/*")
                            }
                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied) {
                                this@Frametwo.finish()
                                val intent= Intent(this@Frametwo,FrameActivity::class.java)
                                startActivity(intent)

                                // permission is denied permanently, we will show user a dialog message.

                            }


                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                            token: PermissionToken?,
                        ) {
                            token?.continuePermissionRequest();

                        }
                    }).check()
            }


            if (btn2.isVisible){
                btn2.visibility=View.INVISIBLE
            }else{
                btn2.visibility=View.VISIBLE

            }
        }

        _binding.finishh.setOnClickListener {
            sendAnalytics("Frametwo","remove_fTwo_btn_click")
            finish()
        }

        _binding.download.setOnClickListener {
            sendAnalytics("Frametwo","stoarge_btn_click")
            if(mbitmap!=null){
                saveToStorage(mbitmap,this)
                Toast.makeText(this@Frametwo,"Phone saved",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,savePhoto::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this@Frametwo,"First remove background",Toast.LENGTH_SHORT).show()
            }
            finish()

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
        _binding.back.setOnClickListener {
            val mProgressDialog = CustomProgressDialog(this)
            mProgressDialog.start("")
            handler.postDelayed({ mProgressDialog.stop() }, 3000)
            btn2.visibility=View.INVISIBLE
            tx1.visibility=View.INVISIBLE
            sendAnalytics("Frametwo","remove_fTwo_btn_click")
            removeBg()
        }
    }


    private fun removeBg() {
        _binding.imageView.invalidate()
        BackgroundRemover.bitmapForProcessing(
            _binding.imageView.drawable.toBitmap(),
            true,
            object : OnBackgroundChangeListener {
                override fun onSuccess(bitmap: Bitmap) {
                    _binding.imageView.setImageBitmap(bitmap)
                    mbitmap=bitmap
                    _binding.download.visibility=View.VISIBLE
                }
                override fun onFailed(exception: Exception) {
                    Toast.makeText(this@Frametwo, "Error Occur", Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun showSettingsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(
            this,
            com.google.android.material.R.style.ThemeOverlay_Material3_Dialog_Alert
        )
        builder.setTitle(Html.fromHtml("<font color='#2D2D2D'>Need Permissions</font>"))
        builder.setMessage(Html.fromHtml("<font color='#2D2D2D'>This app needs permission to use this feature. You can grant them in app settings.</font>"))
        builder.setPositiveButton(Html.fromHtml("<font color='#2D2D2D'>SETTING</font>")) { dialog, which ->
            dialog.cancel()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", this.packageName, null)
            intent.data = uri
            startActivityForResult(intent, 101)
        }
        builder.setNegativeButton(Html.fromHtml("<font color='#2D2D2D'>CANCEL</font>")) { dialog, which ->
            // this method is called when user click on negative button.
            dialog.cancel()
        }

        builder.show()
    }

    companion object{
        lateinit var pUri: Uri
    }
}