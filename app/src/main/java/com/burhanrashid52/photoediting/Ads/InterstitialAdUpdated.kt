package com.burhanrashid52.photoediting.Ads

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.burhanrashid52.photoediting.Ads.OpenApp.Companion.isInterstitialShown
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import editphotos.backgroundremover.photobackgroundchanger.R


open class InterstitialAdUpdated {

    var mInterstitialAd: InterstitialAd? = null
        private set

    companion object {
        @Volatile
        private var instance: InterstitialAdUpdated? = null
        var onCloseCallback: (() -> Unit)? = null
        var counter = 0
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: InterstitialAdUpdated().also {
                instance = it
            }
        }
    }

    fun loadInterstitialAd(context: Context) {
        if (!context.isAlreadyPurchased()) {
            if (context.isNetworkConnected()) {
                context.let {
                    val interId = it.getString(R.string.Interstisial_id_splash)
                    InterstitialAd.load(it,
                        interId,
                        AdRequest.Builder().build(),
                        object : InterstitialAdLoadCallback() {

                            override fun onAdFailedToLoad(ad: LoadAdError) {

                                isInterstitialShown=false
                                if (counter == 2) {
                                    onCloseCallback?.invoke()
                                } else {
                                    counter++
                                    onCloseCallback?.invoke()
                                    loadInterstitialAd(context)
                                    Log.d("loaded_interstitial", "onAdFailedToLoad")
                                }
                            }

                            override fun onAdLoaded(ad: InterstitialAd) {
                                mInterstitialAd = ad
                                mInterstitialAd?.fullScreenContentCallback =
                                    object : FullScreenContentCallback() {
                                        override fun onAdDismissedFullScreenContent() {
                                            onCloseCallback?.invoke()
                                            isInterstitialShown=false

                                            Log.e("InterstiatialReload", "Reloaded___")
                                        }

                                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                            isInterstitialShown=false
                                            super.onAdFailedToShowFullScreenContent(p0)
                                            onCloseCallback?.invoke()
                                        }

                                        override fun onAdShowedFullScreenContent() {
                                            isInterstitialShown=true
                                            super.onAdShowedFullScreenContent()
                                            loadInterstitialAd(context)
                                            Log.d(
                                                "loaded_interstitial",
                                                "onAdShowedFullScreenContent: "
                                            )
                                        }
                                    }
                                Log.e("Interstitial____", "AdLoaded____")
                            }
                        })
                }
            }
        }
    }

    // to show Interstitial Ad Activity reference must be given
    fun showInterstitialAdNew(activity: Activity, onAction: (() -> Unit)? = null) {
        if (mInterstitialAd != null) {
            activity.let {
                mInterstitialAd?.show(it)
            }
            onCloseCallback = {
                onAction?.invoke()
                onCloseCallback = null
            }

        } else {
            loadInterstitialAd(activity)
            Log.d("loaded_interstitial", "showInterstitialAdNew: ")
            onAction?.invoke()
        }
    }

}