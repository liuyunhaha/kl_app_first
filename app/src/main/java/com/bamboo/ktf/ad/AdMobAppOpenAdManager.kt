package com.bamboo.ktf.ad

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.bamboo.ktf.BuildConfig
import com.bamboo.ktf.dataeye.AdMobDataEyeReporter
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
) : Application.ActivityLifecycleCallbacks {

    private var currentActivity: Activity? = null
    private var appOpenAd: AppOpenAd? = null
    private var isShowingAd: Boolean = false
    private val reporter = AdMobDataEyeReporter(
        context = application,
        adType = DataEyeAdConstants.AD_TYPE_SPLASH,
        placementId = adUnitId,
        scene = scene,
    )

    private var startedActivityCount: Int = 0
    private var isChangingConfigurations: Boolean = false

    init {
        application.registerActivityLifecycleCallbacks(this)
        loadAd()
    }

    private fun loadAd() {
        if (adUnitId.isBlank() || appOpenAd != null) return

        reporter.resetForNewLoad()
        reporter.request()

        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            application,
            adUnitId,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    Log.d(TAG, "AppOpenAd loaded.")
                    val loadedAdapter = ad.responseInfo.loadedAdapterResponseInfo

                    if (loadedAdapter != null) {
                        reporter.updateNetworkFirmId(loadedAdapter.adSourceName)
                    }
                    reporter.inventory()
                    ad.onPaidEventListener = OnPaidEventListener { adValue ->
                        reporter.onPaid(adValue)
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

    fun showIfAvailable(activity: Activity) {
        if (isShowingAd) return

        val ad = appOpenAd ?: run {
            loadAd()
            return
        }

        isShowingAd = true
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed fullscreen content.")
                appOpenAd = null
                isShowingAd = false
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d(TAG, "error ${adError.message}")
                appOpenAd = null
                isShowingAd = false
                loadAd()
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
                reporter.click()
            }
        }
        ad.show(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        startedActivityCount++
        if (startedActivityCount == 1 && !isChangingConfigurations) {
            showIfAvailable(activity)
        }
        isChangingConfigurations = false
    }

    override fun onActivityStopped(activity: Activity) {
        isChangingConfigurations = activity.isChangingConfigurations
        startedActivityCount--
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity === activity) currentActivity = null
    }

    companion object {
        private const val TAG = "AdMobAppOpenAdManager"
    }
}
