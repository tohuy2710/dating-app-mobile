package com.example.dating

import android.content.Intent
import android.os.Bundle
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.dating.ui.AppRoot
import com.example.dating.ui.theme.MarsPhotosTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {

    private val pendingMatchId = MutableStateFlow<Int?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }

        handleIntent(intent)

        setContent {
            val matchIdToNavigate by pendingMatchId.collectAsState()

            MarsPhotosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AppRoot(
                        initialMatchId = matchIdToNavigate,
                        onMatchIdHandled = { pendingMatchId.value = null }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val matchIdStr = intent?.getStringExtra("match_id")
        val fromNotification = intent?.getBooleanExtra("from_notification", false) ?: false
        if (fromNotification || !matchIdStr.isNullOrEmpty()) {
            pendingMatchId.value = matchIdStr?.toIntOrNull()
        }
    }
}