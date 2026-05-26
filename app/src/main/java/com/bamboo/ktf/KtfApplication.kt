package com.bamboo.ktf

import android.app.Application
import android.util.Log
import cn.dataeye.android.DataEyeAnalyticsSDK
import com.appsflyer.AppsFlyerLib
import com.bamboo.ktf.ad.AdMobAppOpenAdManager
import com.google.android.gms.ads.MobileAds

class KtfApplication : Application() {
    companion object {
        @Volatile
        var appOpenAdManager: AdMobAppOpenAdManager? = null
            private set

        @Volatile
        var dataEyeInstance: DataEyeAnalyticsSDK? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()

        initDataEye()
        initAdMob()
        initAppsFlyer()
    }

    private fun initAdMob() {
        MobileAds.initialize(this)
        appOpenAdManager = AdMobAppOpenAdManager(
            application = this,
            adUnitId = Constants.ADMOB_APP_OPEN_AD_UNIT_ID,
        )
    }

    private fun initAppsFlyer() {
        val devKey = Constants.APPSFLYER_DEV_KEY
        if (devKey.isBlank()) {
            if (BuildConfig.DEBUG) {
                Log.w("AppsFlyer", "AppsFlyer not initialized: missing APPSFLYER_DEV_KEY")
            }
            return
        }

        AppsFlyerLib.getInstance().apply {
            if (BuildConfig.DEBUG) {
                setDebugLog(true)
            }
            init(devKey, null, this@KtfApplication)
            start(this@KtfApplication)
        }
    }

    private fun initDataEye() {
        val appId = Constants.DATAEYE_APP_ID
        val serverUrl = Constants.DATAEYE_SERVER_URL

        if (appId.isBlank() || serverUrl.isBlank()) {
            if (BuildConfig.DEBUG) {
                Log.w("DataEye", "DataEye not initialized: missing DATAEYE_APP_ID/DATAEYE_SERVER_URL")
            }
            return
        }

        val instance = DataEyeAnalyticsSDK.sharedInstance(this, appId, serverUrl)
        dataEyeInstance = instance

        val eventTypes = listOf(
            DataEyeAnalyticsSDK.AutoTrackEventType.APP_INSTALL,
            DataEyeAnalyticsSDK.AutoTrackEventType.APP_START,
            DataEyeAnalyticsSDK.AutoTrackEventType.APP_END,
            DataEyeAnalyticsSDK.AutoTrackEventType.APP_CRASH,
        )
        instance.enableAutoTrack(eventTypes)
    }
}
