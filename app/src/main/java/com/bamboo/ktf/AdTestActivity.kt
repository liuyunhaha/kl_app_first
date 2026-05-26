package com.bamboo.ktf

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bamboo.ktf.ad.AdMobBanner
import com.bamboo.ktf.ad.AdMobInterstitialManager
import com.bamboo.ktf.ad.AdMobNativeAd
import com.bamboo.ktf.ad.AdMobRewardedManager
import com.bamboo.ktf.ui.theme.MyApplicationTheme

class AdTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AdTestScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
private fun AdTestScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context as? Activity

    val interstitialManager = remember {
        AdMobInterstitialManager(
            context = context,
            adUnitId = Constants.ADMOB_INTERSTITIAL_AD_UNIT_ID,
            scene = "ad_test_page",
        )
    }
    val rewardedManager = remember {
        AdMobRewardedManager(
            context = context,
            adUnitId = Constants.ADMOB_REWARDED_AD_UNIT_ID,
            scene = "ad_test_page",
        )
    }

    LaunchedEffect(Unit) {
        interstitialManager.load()
        rewardedManager.load()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("AdMob 测试页（使用测试广告位）")

        Text("AppOpenAd")
        Button(
            onClick = {
                val mgr = KtfApplication.appOpenAdManager
                if (activity == null || mgr == null) return@Button
                mgr.showIfAvailable(activity)
            },
        ) {
            Text("展示 AppOpenAd")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Banner")
        AdMobBanner(
            adUnitId = Constants.ADMOB_BANNER_AD_UNIT_ID,
            scene = "ad_test_page",
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("InterstitialAd")
        Button(onClick = { interstitialManager.load() }) { Text("加载 Interstitial") }
        Button(
            onClick = {
                if (activity == null) return@Button
                interstitialManager.show(activity)
            },
        ) { Text("展示 Interstitial") }

        Spacer(modifier = Modifier.height(8.dp))

        Text("RewardedAd")
        Button(onClick = { rewardedManager.load() }) { Text("加载 Rewarded") }
        Button(
            onClick = {
                if (activity == null) return@Button
                rewardedManager.show(activity) { amount, type ->
                    Toast.makeText(context, "获得奖励：$amount $type", Toast.LENGTH_SHORT).show()
                }
            },
        ) { Text("展示 Rewarded") }

        Spacer(modifier = Modifier.height(8.dp))

        Text("NativeAd")
        AdMobNativeAd(
            adUnitId = Constants.ADMOB_NATIVE_AD_UNIT_ID,
            scene = "ad_test_page",
        )
    }
}
