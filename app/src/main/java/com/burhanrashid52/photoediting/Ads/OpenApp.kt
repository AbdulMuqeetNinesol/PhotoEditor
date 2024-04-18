package com.burhanrashid52.photoediting.Ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.burhanrashid52.photoediting.PhotoApp
import com.burhanrashid52.photoediting.rateUs
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import editphotos.backgroundremover.photobackgroundchanger.R

class OpenApp(globalClass: PhotoApp) : Application.ActivityLifecycleCallbacks,
    LifecycleObserver {
    companion object {
        private const val LOG_TAG = "AppOpenManager"
        private lateinit var AD_UNIT_ID: String
        var isShowingAd = true
        var isInterstitialShown = false
    }

    private var appOpenAd: AppOpenAd? = null
    private lateinit var loadCallback: AppOpenAdLoadCallback
    private var myApplication: PhotoApp = globalClass
    private var currentActivity: Activity? = null
    private var appOpenLoading: AppOpenLoading? = null

    init {

        AD_UNIT_ID = globalClass.getString(R.string.app_open)

        myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {

        Handler(Looper.getMainLooper()).postDelayed({
            showAdIfAvailable()
            Log.d(LOG_TAG, "onStart")
        }, 100)
    }

    private fun showAdIfAvailable() {
        if (isShowingAd && isAdAvailable()  && currentActivity !is rateUs) {
            Log.d(LOG_TAG, "Will show ad.")
            val fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    appOpenLoading?.dismiss()
                    super.onAdFailedToShowFullScreenContent(p0)
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAd = false
                }

                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = true
                    fetchAd()
                    appOpenLoading?.dismiss()
                }
            }
            appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
            currentActivity?.let {
                showLoading()
                if (isShowingAd) {
                    isShowingAd = false
                    appOpenAd?.show(it)
                }
            }

        } else {
            Log.d(LOG_TAG, "Can not show ad.")
            appOpenLoading?.dismiss()
            fetchAd()
        }
    }

    private fun showLoading() {
        currentActivity?.let {
            if (!it.isFinishing) {
                appOpenLoading = AppOpenLoading(it)
                appOpenLoading?.show()
            }
        }
    }

    fun fetchAd() {
        if (isAdAvailable()) {
            return
        }
        loadCallback = object : AppOpenAdLoadCallback() {
            override fun onAdLoaded(p0: AppOpenAd) {
                appOpenAd = p0
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                appOpenLoading?.dismiss()
                super.onAdFailedToLoad(p0)
            }
        }
        val request = getAdRequest()
        AppOpenAd.load(
            myApplication, AD_UNIT_ID, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
        )
    }

    private fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null

    }
}