package com.example.aiplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.aiplanner.ui.StartScreen
import com.example.aiplanner.ui.theme.AIPlannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIPlannerTheme {
                StartScreen()
            }
        }
    }
}
