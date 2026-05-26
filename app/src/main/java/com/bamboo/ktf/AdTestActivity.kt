package com.bamboo.ktf

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
import com.bamboo.ktf.ad.AdMobNativeAd
import com.bamboo.ktf.ad.AdMobInterstitialPool
import com.bamboo.ktf.ad.AdMobRewardedPool
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
    val activity = context as? android.app.Activity

    val interstitialPool = remember {
        AdMobInterstitialPool(context = context)
    }
    val rewardedPool = remember {
        AdMobRewardedPool(context = context)
    }

    LaunchedEffect(Unit) {
        interstitialPool.load(Constants.ADMOB_INTERSTITIAL_AD_UNIT_ID, "ad_test_interstitial_a")
        interstitialPool.load(Constants.ADMOB_INTERSTITIAL_AD_UNIT_ID_B, "ad_test_interstitial_b")
        rewardedPool.load(Constants.ADMOB_REWARDED_AD_UNIT_ID, "ad_test_rewarded_a")
        rewardedPool.load(Constants.ADMOB_REWARDED_AD_UNIT_ID_B, "ad_test_rewarded_b")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("AdMob 测试页（使用测试广告位）")

        Text("Banner")
        AdMobBanner(
            adUnitId = Constants.ADMOB_BANNER_AD_UNIT_ID,
            scene = "ad_test_page",
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("InterstitialAd")
        Button(onClick = { interstitialPool.load(Constants.ADMOB_INTERSTITIAL_AD_UNIT_ID, "ad_test_interstitial_a") }) { Text("加载 Interstitial A") }
        Button(onClick = { interstitialPool.load(Constants.ADMOB_INTERSTITIAL_AD_UNIT_ID_B, "ad_test_interstitial_b") }) { Text("加载 Interstitial B") }
        Button(
            onClick = {
                if (activity == null) return@Button
                interstitialPool.show(activity, Constants.ADMOB_INTERSTITIAL_AD_UNIT_ID)
            },
        ) { Text("展示 Interstitial A") }
        Button(
            onClick = {
                if (activity == null) return@Button
                interstitialPool.show(activity, Constants.ADMOB_INTERSTITIAL_AD_UNIT_ID_B)
            },
        ) { Text("展示 Interstitial B") }

        Spacer(modifier = Modifier.height(8.dp))

        Text("RewardedAd")
        Button(onClick = { rewardedPool.load(Constants.ADMOB_REWARDED_AD_UNIT_ID, "ad_test_rewarded_a") }) { Text("加载 Rewarded A") }
        Button(onClick = { rewardedPool.load(Constants.ADMOB_REWARDED_AD_UNIT_ID_B, "ad_test_rewarded_b") }) { Text("加载 Rewarded B") }
        Button(
            onClick = {
                if (activity == null) return@Button
                rewardedPool.show(activity, Constants.ADMOB_REWARDED_AD_UNIT_ID) { amount, type ->
                    Toast.makeText(context, "A奖励：$amount $type", Toast.LENGTH_SHORT).show()
                }
            },
        ) { Text("展示 Rewarded A") }
        Button(
            onClick = {
                if (activity == null) return@Button
                rewardedPool.show(activity, Constants.ADMOB_REWARDED_AD_UNIT_ID_B) { amount, type ->
                    Toast.makeText(context, "获得奖励：$amount $type", Toast.LENGTH_SHORT).show()
                }
            },
        ) { Text("展示 Rewarded B") }

        Spacer(modifier = Modifier.height(8.dp))

        Text("NativeAd")
        AdMobNativeAd(
            adUnitId = Constants.ADMOB_NATIVE_AD_UNIT_ID,
            scene = "ad_test_page",
        )
    }
}
