package com.beperfectsalon.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.beperfectsalon.app.ui.navigation.AppNavGraph
import com.beperfectsalon.app.ui.theme.BePerfectSalonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BePerfectSalonTheme {
                AppNavGraph()
            }
        }
    }
}
