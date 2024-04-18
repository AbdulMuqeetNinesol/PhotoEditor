package com.burhanrashid52.photoediting

import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.SensorPrivacyManager.Sensors.CAMERA
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.appbar.MaterialToolbar
import android.Manifest
import android.provider.Settings
import android.text.Html
import androidx.annotation.RequiresApi
import com.burhanrashid52.photoediting.Ads.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import editphotos.backgroundremover.photobackgroundchanger.R
import editphotos.backgroundremover.photobackgroundchanger.databinding.ActivityFrameBinding
import editphotos.backgroundremover.photobackgroundchanger.databinding.MyCustomDialogBinding
import editphotos.backgroundremover.photobackgroundchanger.databinding.PermissionDialogBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class FrameActivity : AppCompatActivity() {


    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var appBar: MaterialToolbar

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var selectImageLauncher: ActivityResultLauncher<String>


    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private var i : Int = 0
    private val cameraResultLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                createImageFile()?.let { photoFile ->
                    photoUri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, photoFile)
                    takePictureLauncher.launch(photoUri)
                }
            } else {
                showSettingsDialog()
            }
        }

    private val _binding: ActivityFrameBinding by lazy {
        ActivityFrameBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
         sendAnalytics("FrameActivity","OnCreate")
        InterstitialAdUpdated.getInstance().loadInterstitialAd(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        } else {

        }
        with(_binding) {
            if (isNetworkConnected()) {
                loadNativeAd(
                    frame.adContainer,
                    frame.ShimmerContainerSmall,
                    frame.FrameLayoutSmall,
                    R.layout.small_native_ad,
                    getString(R.string.native_ad_id_home)
                )
            } else {
                frame1.Gone()
            }
        }

        _binding.lineRemove.setOnClickListener {
            if (i%2!=0){
                showAdAndGo {
                    sendAnalytics("FrameActivity","background_Remove_btn_click")
                    val intent = Intent(this, Frametwo::class.java)
                    startActivity(intent)
                }

            }
            else{
                sendAnalytics("FrameActivity","background_Remove_btn_click")
                val intent = Intent(this, Frametwo::class.java)
                startActivity(intent)
            }
            i++
            /*sendAnalytics("FrameActivity","background_Remove_btn_click")
            val intent = Intent(this, Frametwo::class.java)
            startActivity(intent)*/
        }
        _binding.setting.setOnClickListener {
            if (i%2!=0){
                showAdAndGo {
                    sendAnalytics("FrameActivity","setting_btn_click")
                    val intent = Intent(this, setting::class.java)
                    startActivity(intent)
                }

            }
            else{
                sendAnalytics("FrameActivity","setting_btn_click")
                val intent = Intent(this, setting::class.java)
                startActivity(intent)
            }
            i++
            /*sendAnalytics("FrameActivity","setting_btn_click")
            val intent = Intent(this, setting::class.java)
            startActivity(intent)*/
        }
        _binding.photoEditor.setOnClickListener {
            sendAnalytics("FrameActivity","photo_Editor_btn_click")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Dexter.withContext(this)
                    .withPermissions(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.CAMERA
                    ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            if (report.areAllPermissionsGranted()) {
                                selectImageLauncher.launch("image/*")
                            }
                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied) {
                                showSettingsDialog()

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
            else {
                Dexter.withContext(this)
                    .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,

                        ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            if (report.areAllPermissionsGranted()) {
                                selectImageLauncher.launch("image/*")
                            }
                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied) {
                                showSettingsDialog()

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
        }

        // Set up the image picker result launcher
        selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    // Create an intent to the destination activity, attach the image URI, and start the activity
                    val intent = Intent(this, EditImageActivity::class.java)
                    photoUri = uri
                    intent.putExtra("imageUri", photoUri)
                    startActivity(intent)

                }
            }



        _binding.cameraBox.setOnClickListener {
            if(i%2!=0){
                showAdAndGo {
                    sendAnalytics("FrameActivity","camera_btn_click")
                    Dexter.withContext(this)
                        .withPermissions(
                            Manifest.permission.CAMERA,

                            ).withListener(object : MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                if (report.areAllPermissionsGranted()) {
                                    cameraResultLauncher.launch(Manifest.permission.CAMERA)
                                }
                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied) {
                                    showSettingsDialog()

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

            }
            else{
                sendAnalytics("FrameActivity","camera_btn_click")
                Dexter.withContext(this)
                    .withPermissions(
                        Manifest.permission.CAMERA,

                        ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            if (report.areAllPermissionsGranted()) {
                                cameraResultLauncher.launch(Manifest.permission.CAMERA)
                            }
                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied) {
                                showSettingsDialog()

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
            i++
//            sendAnalytics("FrameActivity","camera_btn_click")
//            Dexter.withContext(this)
//                .withPermissions(
//                    Manifest.permission.CAMERA,
//
//                    ).withListener(object : MultiplePermissionsListener {
//                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                        if (report.areAllPermissionsGranted()) {
//                            cameraResultLauncher.launch(Manifest.permission.CAMERA)
//                        }
//                        // check for permanent denial of any permission
//                        if (report.isAnyPermissionPermanentlyDenied) {
//                            showSettingsDialog()
//
//                            // permission is denied permanently, we will show user a dialog message.
//
//                        }
//
//
//                    }
//
//                    override fun onPermissionRationaleShouldBeShown(
//                        p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
//                        token: PermissionToken?,
//                    ) {
//                        token?.continuePermissionRequest();
//
//                    }
//                }).check()
        }

        _binding.saveGallery.setOnClickListener {
//            if (i%2!=0){
//                showAdAndGo {
//                    sendAnalytics("FrameActivity","save_Photo_btn_click")
//                    val intent = Intent(this, savePhoto::class.java)
//                    startActivity(intent)
//                }
//
//            }
//            else{
//                sendAnalytics("FrameActivity","save_Photo_btn_click")
//                val intent = Intent(this, savePhoto::class.java)
//                startActivity(intent)
//            }
//            i++
            sendAnalytics("FrameActivity","save_Photo_btn_click")
            val intent = Intent(this, savePhoto::class.java)
            startActivity(intent)
        }
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    // The picture was taken successfully, so you can send it to another activity
                    val intent = Intent(this, EditImageActivity::class.java)
                    setResult(Activity.RESULT_OK, intent)
                    startActivity(intent)
                }
            }
    }


    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        showSaveDialog()
    }

    @SuppressLint("MissingPermission")
    private fun showSaveDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val binding = MyCustomDialogBinding.inflate(LayoutInflater.from(this))
        builder.setView(binding.root)
        val alertDialog: AlertDialog = builder.create()
        binding.ok.setOnClickListener { super.onBackPressed() }
        binding.cancel.setOnClickListener { alertDialog.cancel() }
        alertDialog.show()
    }

    companion object {
        lateinit var photoUri: Uri
        const val FILE_PROVIDER_AUTHORITY = "editphotos.backgroundremover.photobackgroundchanger"
    }

    private fun showRationalDialog(
        title: String,
        message: String,
    ) {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
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

    private fun permissionRequired() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {

                    }
                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()

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

    private fun permissionLauncher() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val isGranted = permissions.all { it.value }
                Log.d("TAG", "entries: ${permissions.entries}")
                if (isGranted) {
//                    Log.d("TAG", "permissionLauncher: $isGranted")
                } else permissionDialog()
            }
    }

    private fun permissionDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val binding = PermissionDialogBinding.inflate(LayoutInflater.from(this))
        builder.setView(binding.root)

        val alertDialog: AlertDialog = builder.create()
        binding.allowPermission.setOnClickListener {
            requestPermission()
            alertDialog.dismiss()
        }
        binding.openSetting.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun requestPermission() {
        permissionLauncher.launch(
            arrayOf(
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
//                android.Manifest.permission.INTERNET,

            )
        )


    }

}