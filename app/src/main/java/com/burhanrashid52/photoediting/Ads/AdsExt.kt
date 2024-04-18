package com.burhanrashid52.photoediting.Ads

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.facebook.shimmer.ShimmerFrameLayout
//import com.google.android.gms.common.internal.service.Common
import com.google.firebase.analytics.FirebaseAnalytics
import com.karumi.dexter.BuildConfig
import ja.burhanrashid52.photoeditor.ViewType
import java.io.File
import java.io.FileOutputStream

fun Context.isAlreadyPurchased(): Boolean {
    return TinyDB(applicationContext).getBoolean("KEY_PURCHASE")
}

fun Activity.showAdAndGo(afterAdWork: () -> Unit) {

    if (!isAlreadyPurchased()) {
        InterstitialAdUpdated.getInstance().showInterstitialAdNew(this){
            afterAdWork()
        }

    } else {
        afterAdWork()
    }
}
fun Context.isNetworkConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun View.Visible() {
    visibility = View.VISIBLE
}

fun View.Gone() {
    visibility = View.GONE
}

fun View.Invisible() {
    visibility = View.INVISIBLE
}
fun Context.loadNativeAd(adView: View,
                         shimmerViewContainer: ShimmerFrameLayout? = null,
                         frameLayout: FrameLayout,
                         layoutId: Int,
                         sdIs: String?) {
    if (!isAlreadyPurchased()) {
        if (isNetworkConnected()) {
            NativeAdsHelper(this).setNativeAd(shimmerViewContainer, frameLayout, layoutId, sdIs)
        } else {
            adView.Invisible()
        }
    } else {
        adView.Gone()
    }
}


fun Activity.saveToStorage(bitmap: Bitmap, context: Context){
    val directory = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES)

    if (!directory!!.exists()) {
        directory!!.mkdir()
    }
    // Create a unique filename
    val filename = "image_${System.currentTimeMillis()}.jpg"

    // Create the file object
    val file = File(directory, filename)

    // Create the output stream
    val outputStream = FileOutputStream(file)

    // Compress the bitmap and write it to the output stream
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

    // Flush and close the output stream
    outputStream.flush()
    outputStream.close()

    // Update the gallery with the new image
    MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null) { path, uri ->
        // Image saved successfully
        Log.d(ContentValues.TAG, "Image saved to $path")
    }
}

fun takeScreenshotOfView(view: View, height: Int, width: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val bgDrawable = view.background
    if (bgDrawable != null) {
        bgDrawable.draw(canvas)
    } else {
        canvas.drawColor(Color.WHITE)
    }
    view.draw(canvas)
    return bitmap
}

fun Context.shareIt() {
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_SUBJECT, "${BuildConfig.APPLICATION_ID}")
    intent.putExtra(
        Intent.EXTRA_TEXT,
        " https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
    )
    intent.type = "text/plain"
    startActivity(intent)
}

fun openAppOnPlayStore(context: Context) {
    val uri = Uri.parse("market://details?id=" + context.packageName)
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    try {
        context.startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
            )
        )
    }
}

fun String.getFilePAth(context: Context): String {
    val dir = File(context.getExternalFilesDir("MyOutputDirectory").toString())
    if (!dir.exists()) {
        dir.mkdirs()
    }
    var extension: String? = null
    when {
        TextUtils.equals(this, ViewType.IMAGE.toString()) -> {
            extension = "%03d.jpg"
        }

    }
    val dest = File(
        dir.path + File.separator + "MyOutputDirectory" + System.currentTimeMillis()
            .div(1000L) + extension
    )
    return dest.absolutePath

}
fun Context.sendAnalytics(screen:String,event:String) {
    val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    val bundle = Bundle()
    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, screen)
    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event)
    analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
}