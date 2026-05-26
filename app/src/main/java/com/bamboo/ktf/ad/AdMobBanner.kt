package com.bamboo.ktf.ad

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.bamboo.ktf.dataeye.DataEyeAdConstants
import com.bamboo.ktf.dataeye.AdLoadSession
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener

@Composable
fun AdMobBanner(
    adUnitId: String,
    modifier: Modifier = Modifier,
    adSize: AdSize = AdSize.FULL_BANNER,
    scene: String = "unknown",
) {
    val context = LocalContext.current
    val loadSession = remember(adUnitId, scene) {
        AdLoadSession(
            context = context,
            adType = DataEyeAdConstants.AD_TYPE_BANNER,
            placementId = adUnitId,
            scene = scene,
        )
    }

    AndroidView(
        modifier = modifier,
        factory = {
            AdView(context).apply {
                setAdSize(adSize)
                this.adUnitId = adUnitId

                if (adUnitId.isNotBlank()) {
                    loadSession.request()
                }
                loadAd(AdRequest.Builder().build())
                adListener =
                    object : AdListener() {
                        override fun onAdClicked() {
                            Log.d("adview onClick", "onAdClicked")
                            loadSession.click()
                        }

                        override fun onAdClosed() {
                            // Code to be executed when the user is about to return
                            // to the app after tapping on an ad.
                            Log.d("adview onClose", "onAdClosed")
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.d("adview onFailed", "onAdFailedToLoad")
                        }

                        override fun onAdImpression() {
                            Log.d("adview onImpression", "onAdImpression")
                        }

                        override fun onAdLoaded() {
                            Log.d("adview onLoaded", "onAdLoaded")
                            loadSession.updateNetworkFirmId(responseInfo?.loadedAdapterResponseInfo?.adSourceName)
                            loadSession.inventory()
                        }

                        override fun onAdOpened() {
                            // Code to be executed when an ad opens an overlay that
                            // covers the screen.
                            Log.d("adview onOpened", "onAdOpened")
                        }
                    }
                onPaidEventListener = OnPaidEventListener { adValue ->
                    loadSession.onPaid(adValue)
                    Log.d(
                        "adview onPaid",
                        "valueMicros=${adValue.valueMicros}, currencyCode=${adValue.currencyCode}, precisionType=${adValue.precisionType}",
                    )
                }
            }
        },
        update = {},
    )

    DisposableEffect(Unit) {
        onDispose { }
    }
}
