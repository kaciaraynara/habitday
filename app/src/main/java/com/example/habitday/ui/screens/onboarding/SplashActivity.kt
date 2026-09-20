package com.example.habitday.ui.screens.onboarding

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.habitday.MainActivity
import com.example.habitday.ui.components.HabitDayLogo
import com.example.habitday.ui.theme.BackgroundWhite
import com.example.habitday.ui.theme.HabitdayTheme
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitdayTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundWhite),
                    contentAlignment = Alignment.Center
                ) {
                    LaunchedEffect(Unit) {
                        delay(2000)
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                    HabitDayLogo(sizeMultiplier = 2f)
                }
            }
        }
    }
}
