package com.bamboo.ktf.ad

import android.app.Activity
import android.content.Context

class AdMobInterstitialPool(
    private val context: Context,
) {
    private val managers = mutableMapOf<String, AdMobInterstitialManager>()

    private fun managerOf(adUnitId: String): AdMobInterstitialManager {
        return managers.getOrPut(adUnitId) {
            AdMobInterstitialManager(
                context = context,
                adUnitId = adUnitId,
            )
        }
    }

    fun load(adUnitId: String, scene: String) {
        managerOf(adUnitId).load(scene)
    }

    fun show(activity: Activity, adUnitId: String) {
        managerOf(adUnitId).show(activity)
    }
}
