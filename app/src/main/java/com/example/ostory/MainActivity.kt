package com.example.ostory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ostory.presentation.navigation.OSToryApp
import com.example.ostory.ui.theme.OSToryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OSToryTheme {
                OSToryApp()
            }
        }
    }
}