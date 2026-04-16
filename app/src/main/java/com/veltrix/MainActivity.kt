package com.veltrix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.veltrix.ui.screens.SplashScreen
import com.veltrix.ui.theme.VeltrixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var showNativeSplash by remember { mutableStateOf(true) }

            VeltrixTheme(darkTheme = true) {
                if (showNativeSplash) {
                    SplashScreen(
                        onAnimationFinished = { showNativeSplash = false }
                    )
                } else {
                    WebAppScreen(
                        activity = this,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
