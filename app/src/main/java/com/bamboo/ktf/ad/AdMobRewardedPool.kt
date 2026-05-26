package com.bamboo.ktf.ad

import android.app.Activity
import android.content.Context

class AdMobRewardedPool(
    private val context: Context,
) {
    private val managers = mutableMapOf<String, AdMobRewardedManager>()

    private fun managerOf(adUnitId: String): AdMobRewardedManager {
        return managers.getOrPut(adUnitId) {
            AdMobRewardedManager(
                context = context,
                adUnitId = adUnitId,
            )
        }
    }

    fun load(adUnitId: String, scene: String) {
        managerOf(adUnitId).load(scene)
    }

    fun show(activity: Activity, adUnitId: String, onRewardEarned: (amount: Int, type: String) -> Unit) {
        managerOf(adUnitId).show(activity, onRewardEarned)
    }
}
