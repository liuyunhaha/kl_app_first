package com.bamboo.ktf.dataeye

import android.content.Context
import com.google.android.gms.ads.AdValue

class AdLoadSession(
    private val context: Context,
    private val adType: String,
    private val placementId: String,
    private val scene: String,
) {
    private var networkFirmId: String = DataEyeAdConstants.NETWORK_FIRM_ID_ADMOB
    private var impressionReported: Boolean = false

    fun updateNetworkFirmId(networkId: String?) {
        val normalized = networkId?.trim().orEmpty()
        if (normalized.isNotEmpty()) {
            networkFirmId = normalized
        }
    }

    fun request() {
        DataEyeAdTracker.adRequest(
            context = context,
            adType = adType,
            placementId = placementId,
            networkFirmId = networkFirmId,
        )
    }

    fun inventory() {
        DataEyeAdTracker.adInventory(
            context = context,
            adType = adType,
            placementId = placementId,
            networkFirmId = networkFirmId,
        )
    }

    fun click() {
        DataEyeAdTracker.adClick(
            context = context,
            adType = adType,
            placementId = placementId,
            networkFirmId = networkFirmId,
        )
    }

    fun onPaid(adValue: AdValue) {
        if (impressionReported) return
        impressionReported = true
        DataEyeAdTracker.adImpression(
            context = context,
            adType = adType,
            placementId = placementId,
            scene = scene,
            revenue = adValue.valueMicros / 1_000_000.0,
            currency = adValue.currencyCode,
            networkFirmId = networkFirmId,
        )
    }
}
