package com.bamboo.ktf.dataeye

import android.content.Context
import com.bamboo.ktf.KtfApplication
import org.json.JSONObject

object DataEyeAdTracker {
    fun adRequest(
        context: Context,
        adType: String,
        placementId: String,
        networkFirmId: String,
    ) {
        track(
            context = context,
            eventName = DataEyeAdConstants.EVENT_REQUEST,
            properties = JSONObject()
                .put("ad_type", adType)
                .put("PlacementId", placementId)
                .put("NetworkFirmId", networkFirmId),
        )
    }

    fun adInventory(
        context: Context,
        adType: String,
        placementId: String,
        networkFirmId: String,
    ) {
        track(
            context = context,
            eventName = DataEyeAdConstants.EVENT_INVENTORY,
            properties = JSONObject()
                .put("ad_type", adType)
                .put("PlacementId", placementId)
                .put("NetworkFirmId", networkFirmId),
        )
    }

    fun adImpression(
        context: Context,
        adType: String,
        placementId: String,
        scene: String,
        revenue: Double = 0.0,
        currency: String = "",
        networkFirmId: String,
    ) {
        val ecpm = revenue * 1000.0
        track(
            context = context,
            eventName = DataEyeAdConstants.EVENT_IMPRESSION,
            properties = JSONObject()
                .put("ad_type", adType)
                .put("Ecpm", ecpm)
                .put("Revenue", revenue)
                .put("Currency", currency)
                .put("PlacementId", placementId)
                .put("scene", scene)
                .put("NetworkFirmId", networkFirmId),
        )
    }

    fun adClick(
        context: Context,
        adType: String,
        placementId: String,
        networkFirmId: String,
    ) {
        track(
            context = context,
            eventName = DataEyeAdConstants.EVENT_CLICK,
            properties = JSONObject()
                .put("ad_type", adType)
                .put("PlacementId", placementId)
                .put("NetworkFirmId", networkFirmId),
        )
    }

    private fun track(context: Context, eventName: String, properties: JSONObject) {
        val instance = KtfApplication.dataEyeInstance ?: return
        instance.track(eventName, properties)
    }
}
