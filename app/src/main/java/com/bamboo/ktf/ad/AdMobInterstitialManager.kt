package com.bamboo.ktf.ad

import android.app.Activity
import android.content.Context
import android.util.Log
import com.bamboo.ktf.BuildConfig
import com.bamboo.ktf.dataeye.AdLoadSession
import com.bamboo.ktf.dataeye.DataEyeAdConstants
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdMobInterstitialManager(
    private val context: Context,
    private val adUnitId: String,
) {
    private var interstitialAd: InterstitialAd? = null
    private var currentLoadSession: AdLoadSession? = null

    fun load(scene: String) {
        if (adUnitId.isBlank()) return
        if (interstitialAd != null) return

        val loadSession = AdLoadSession(
            context = context,
            adType = DataEyeAdConstants.AD_TYPE_INTERSTITIAL,
            placementId = adUnitId,
            scene = scene,
        )
        currentLoadSession = loadSession
        loadSession.request()

        InterstitialAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
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
                    interstitialAd = null
                    if (BuildConfig.DEBUG) {
                        Log.w("AdMob", "Interstitial failed to load: $loadAdError")
                    }
                }
            },
        )
    }

    fun show(activity: Activity) {
        val ad = interstitialAd ?: run {
            return
        }
        val loadSession = currentLoadSession ?: return

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.")
                interstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                Log.d(TAG, "Ad failed to show fullscreen content.")
                interstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
                interstitialAd = null
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
        private const val TAG = "AdMobInterstitialManager"
    }
}
