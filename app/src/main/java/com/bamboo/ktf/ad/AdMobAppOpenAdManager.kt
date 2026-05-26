package com.bamboo.ktf.ad

import android.app.Activity
import android.app.Application
import android.util.Log
import com.bamboo.ktf.BuildConfig
import com.bamboo.ktf.dataeye.AdLoadSession
import com.bamboo.ktf.dataeye.DataEyeAdConstants
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.appopen.AppOpenAd

class AdMobAppOpenAdManager(
    private val application: Application,
    private val adUnitId: String,
    private val scene: String = "app_foreground",
) {

    private var appOpenAd: AppOpenAd? = null
    private var isShowingAd: Boolean = false
    private var currentLoadSession: AdLoadSession? = null

    init {
        loadAd()
    }

    private fun loadAd() {
        if (adUnitId.isBlank() || appOpenAd != null) return

        val loadSession = AdLoadSession(
            context = application,
            adType = DataEyeAdConstants.AD_TYPE_SPLASH,
            placementId = adUnitId,
            scene = scene,
        )
        currentLoadSession = loadSession
        loadSession.request()

        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            application,
            adUnitId,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    Log.d(TAG, "AppOpenAd loaded.")
                    loadSession.updateNetworkFirmId(ad.responseInfo.loadedAdapterResponseInfo?.adSourceName)
                    loadSession.inventory()
                    ad.onPaidEventListener = OnPaidEventListener { adValue ->
                        loadSession.onPaid(adValue)
                        if (BuildConfig.DEBUG) {
                            Log.d(
                                TAG,
                                "OnPaid valueMicros=${adValue.valueMicros}, currencyCode=${adValue.currencyCode}, precisionType=${adValue.precisionType}",
                            )
                        }
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    appOpenAd = null
                    Log.w(TAG, "AppOpenAd failed to load: $loadAdError")
                }
            },
        )
    }

    fun showIfAvailable(activity: Activity, onComplete: () -> Unit = {}) {
        if (isShowingAd) return

        val ad = appOpenAd ?: run {
            loadAd()
            onComplete()
            return
        }
        val loadSession = currentLoadSession ?: return

        isShowingAd = true
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed fullscreen content.")
                appOpenAd = null
                isShowingAd = false
                loadAd()
                onComplete()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d(TAG, "error ${adError.message}")
                appOpenAd = null
                isShowingAd = false
                loadAd()
                onComplete()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
                appOpenAd = null
            }

            override fun onAdImpression() {
                Log.d(TAG, "The ad recorded an impression.")
            }

            override fun onAdClicked() {
                Log.d(TAG, "The ad was clicked.")
                loadSession.click()
            }
        }
        ad.show(activity)
    }

    companion object {
        private const val TAG = "AdMobAppOpenAdManager"
    }
}
