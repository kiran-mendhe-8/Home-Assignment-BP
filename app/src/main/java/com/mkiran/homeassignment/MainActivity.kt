package com.mkiran.homeassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.mkiran.homeassignment.presentation.ServiceStationScreen
import com.mkiran.homeassignment.ui.theme.HomeAssignmentTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeAssignmentTheme {
                ServiceStationScreen()
            }
        }
    }
} 