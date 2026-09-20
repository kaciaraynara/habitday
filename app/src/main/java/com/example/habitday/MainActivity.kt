package com.example.habitday

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitday.navigation.AppNavigation
import com.example.habitday.ui.theme.HabitdayTheme
import com.example.habitday.viewmodel.SettingsViewModel
import com.example.habitday.viewmodel.SettingsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = application as HabitApplication
            val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(app.repository))
            val prefs by settingsViewModel.preferences.collectAsState()

            HabitdayTheme(preferences = prefs) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { }
                    LaunchedEffect(Unit) {
                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                AppNavigation(application = application as HabitApplication)
            }
        }
    }
}
