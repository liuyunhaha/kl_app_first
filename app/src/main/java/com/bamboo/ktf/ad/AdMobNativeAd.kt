package com.bamboo.ktf.ad

import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.bamboo.ktf.dataeye.DataEyeAdConstants
import com.bamboo.ktf.dataeye.AdLoadSession
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

@Composable
fun AdMobNativeAd(
    adUnitId: String,
    modifier: Modifier = Modifier,
    scene: String = "unknown",
) {
    val context = LocalContext.current
    val nativeAdState: MutableState<NativeAd?> = remember { mutableStateOf(null) }
    val loadSession = remember(adUnitId, scene) {
        AdLoadSession(
            context = context,
            adType = DataEyeAdConstants.AD_TYPE_NATIVE,
            placementId = adUnitId,
            scene = scene,
        )
    }

    LaunchedEffect(adUnitId) {
        nativeAdState.value?.destroy()
        nativeAdState.value = null

        if (adUnitId.isBlank()) return@LaunchedEffect

        loadSession.request()

        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad: NativeAd ->
                nativeAdState.value?.destroy()
                nativeAdState.value = ad
                loadSession.updateNetworkFirmId(ad.responseInfo?.loadedAdapterResponseInfo?.adSourceName)
                loadSession.inventory()
                ad.setOnPaidEventListener { adValue ->
                    loadSession.onPaid(adValue)
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdImpression() {
                }

                override fun onAdClicked() {
                    loadSession.click()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    nativeAdState.value?.destroy()
                    nativeAdState.value = null
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    AndroidView(
        modifier = modifier,
        factory = {
            val headlineView = TextView(context).apply { textSize = 16f }
            val bodyView = TextView(context).apply { textSize = 13f }
            val iconView = ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(96, 96)
            }
            val ctaView = Button(context)

            val container = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                addView(headlineView)
                addView(bodyView)
                addView(iconView)
                addView(ctaView)
            }

            NativeAdView(context).apply {
                this.headlineView = headlineView
                this.bodyView = bodyView
                this.iconView = iconView
                this.callToActionView = ctaView
                addView(container)
            }
        },
        update = { adView ->
            val ad = nativeAdState.value
            if (ad == null) return@AndroidView

            (adView.headlineView as? TextView)?.text = ad.headline
            (adView.bodyView as? TextView)?.text = ad.body

            val icon = ad.icon
            val iconView = adView.iconView as? ImageView
            if (icon != null && iconView != null) {
                iconView.setImageDrawable(icon.drawable)
                iconView.visibility = android.view.View.VISIBLE
            } else {
                iconView?.visibility = android.view.View.GONE
            }

            val ctaText = ad.callToAction
            val ctaView = adView.callToActionView as? Button
            if (ctaText != null && ctaView != null) {
                ctaView.text = ctaText
                ctaView.visibility = android.view.View.VISIBLE
            } else {
                ctaView?.visibility = android.view.View.GONE
            }

            adView.setNativeAd(ad)
        },
    )

    DisposableEffect(Unit) {
        onDispose {
            nativeAdState.value?.destroy()
            nativeAdState.value = null
        }
    }
}
