package com.bamboo.ktf

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bamboo.ktf.ui.theme.MyApplicationTheme
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.json.JSONObject
import cn.dataeye.android.DataEyeAnalyticsSDK

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "欢迎使用基础组件展示",
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = {
                val intent = Intent(context, ComponentsActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.width(200.dp)
        ) {
            Text("查看基础组件")
        }

        Button(
            onClick = {
                val intent = Intent(context, AdTestActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .padding(top = 12.dp)
                .width(200.dp)
        ) {
            Text("测试AdMob广告")
        }

        Button(
            onClick = {
                FirebaseCrashlytics.getInstance().log("Manual Crashlytics test (non-fatal)")
                FirebaseCrashlytics.getInstance().recordException(IllegalStateException("Manual non-fatal test"))
            },
            modifier = Modifier
                .padding(top = 12.dp)
                .width(200.dp)
        ) {
            Text("测试Crashlytics")
        }

        if (BuildConfig.DEBUG) {
            Button(
                onClick = {
                    FirebaseCrashlytics.getInstance().log("Manual Crashlytics crash test (debug only)")
                    throw RuntimeException("Test Crash (debug only)")
                },
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(200.dp)
            ) {
                Text("触发崩溃(Debug)")
            }

            Button(
                onClick = {
                    val appId = Constants.DATAEYE_APP_ID
                    val serverUrl = Constants.DATAEYE_SERVER_URL
                    if (appId.isBlank() || serverUrl.isBlank()) {
                        return@Button
                    }
                    val instance = DataEyeAnalyticsSDK.sharedInstance(context, appId, serverUrl)
                    val props = JSONObject().apply { put("from", "debug_button") }
                    instance.track("dataeye_test", props)
                },
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(200.dp)
            ) {
                Text("DataEye测试事件")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }
    Button(onClick = { count++ }) {
        Text("点击次数：$count")
    }
}

//@Preview(showBackground = false)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}
