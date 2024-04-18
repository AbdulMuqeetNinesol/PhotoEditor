package com.burhanrashid52.photoediting

import android.app.Application
import com.burhanrashid52.photoediting.Ads.InterstitialAdUpdated
import com.burhanrashid52.photoediting.Ads.OpenApp

/**
 * Created by Burhanuddin Rashid on 1/23/2018.
 */
class PhotoApp : Application() {
    var appOpen: OpenApp? =null
    override fun onCreate() {
        super.onCreate()
        photoApp = this
        appOpen = OpenApp(this)
        InterstitialAdUpdated.getInstance().loadInterstitialAd(this)
        myapp =this
        instance =this
    }

    companion object {
        var photoApp: PhotoApp? = null
            private set
        private val TAG = PhotoApp::class.java.simpleName
        var myapp: PhotoApp?=null
            private set
        lateinit var instance: PhotoApp
            private set
    }
}