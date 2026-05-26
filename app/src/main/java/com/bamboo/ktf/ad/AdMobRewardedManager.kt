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
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdMobRewardedManager(
    private val context: Context,
    private val adUnitId: String,
) {
    private var rewardedAd: RewardedAd? = null
    private var currentLoadSession: AdLoadSession? = null

    fun load(scene: String) {
        if (adUnitId.isBlank()) return
        if (rewardedAd != null) return

        val loadSession = AdLoadSession(
            context = context,
            adType = DataEyeAdConstants.AD_TYPE_REWARDED_VIDEO,
            placementId = adUnitId,
            scene = scene,
        )
        currentLoadSession = loadSession
        loadSession.request()

        RewardedAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "0 The ad loaded")
                    }
                    rewardedAd = ad
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
                    rewardedAd = null
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "Rewarded failed to load: $loadAdError")
                    }
                }
            },
        )
    }

    fun show(activity: Activity, onRewardEarned: (amount: Int, type: String) -> Unit) {
        val ad = rewardedAd ?: run {
            return
        }
        val loadSession = currentLoadSession ?: return

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "The ad was dismissed.")
                rewardedAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                Log.d(TAG, "The ad failed to show.")
                rewardedAd = null
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "The ad was shown.")
                rewardedAd = null
            }

            override fun onAdImpression() {
                Log.d(TAG, "1 The ad recorded an impression.")
            }

            override fun onAdClicked() {
                Log.d(TAG, "The ad was clicked.")
                loadSession.click()
            }
        }

        ad.show(activity) { rewardItem ->
            Log.d(TAG, "Reward amount: ${rewardItem.amount} Reward type: ${rewardItem.type}")
            onRewardEarned(rewardItem.amount, rewardItem.type)
        }
    }

    companion object {
        private const val TAG = "AdMobRewardedManager"
    }
}
