package com.veltrix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.veltrix.ui.theme.VeltrixTheme
import com.veltrix.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val state by mainViewModel.uiState.collectAsStateWithLifecycle()

            VeltrixTheme(darkTheme = state.isDarkMode) {
                VeltrixApp(viewModel = mainViewModel)
            }
        }
    }
}
