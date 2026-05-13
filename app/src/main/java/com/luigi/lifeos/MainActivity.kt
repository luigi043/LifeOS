package com.luigi.lifeos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.luigi.lifeos.core.designsystem.theme.LifeOsTheme
import com.luigi.lifeos.navigation.LifeOsBottomBar
import com.luigi.lifeos.navigation.LifeOsNavHost
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity host. All feature screens are Composables wired into
 * [LifeOsNavHost]. The activity stays intentionally thin — UI state and
 * theming live in `:core:designsystem` and feature modules.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(0x00000000, 0x00000000),
            navigationBarStyle = SystemBarStyle.auto(0x00000000, 0x00000000),
        )
        super.onCreate(savedInstanceState)

        setContent {
            LifeOsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LifeOsApp()
                }
            }
        }
    }
}

@Composable
private fun LifeOsApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { LifeOsBottomBar(navController = navController) },
    ) { padding ->
        LifeOsNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        )
    }
}
