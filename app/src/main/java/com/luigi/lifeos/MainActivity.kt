package com.luigi.lifeos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.luigi.lifeos.core.designsystem.theme.LifeOsTheme
import com.luigi.lifeos.feature.onboarding.navigation.OnboardingRoute
import com.luigi.lifeos.feature.today.navigation.TodayRoute
import com.luigi.lifeos.navigation.LifeOsBottomBar
import com.luigi.lifeos.navigation.LifeOsNavHost
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity host. All feature screens are Composables wired into
 * [LifeOsNavHost]. The activity stays intentionally thin — UI state and
 * theming live in `:core:designsystem` and feature modules.
 *
 * Cold-start routing: [AppEntryViewModel] resolves `onboardingDone` from
 * DataStore. Until it resolves we keep showing the splash screen — this
 * avoids the * Today screen blinks then onboarding takes over" flash on
 * first install.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val entryViewModel: AppEntryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Hold the system splash until we know which start destination to use.
        val splash = androidx.core.splashscreen.SplashScreen.installSplashScreen(this)
        splash.setKeepOnScreenCondition { entryViewModel.entryState.value is EntryState.Loading }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(0x00000000, 0x00000000),
            navigationBarStyle = SystemBarStyle.auto(0x00000000, 0x00000000),
        )
        super.onCreate(savedInstanceState)

        setContent {
            LifeOsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val entry by entryViewModel.entryState.collectAsState()
                    when (val s = entry) {
                        EntryState.Loading -> Unit // splash still showing
                        is EntryState.Ready -> LifeOsApp(
                            startDestination = if (s.onboardingDone) TodayRoute else OnboardingRoute,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LifeOsApp(startDestination: String) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.hierarchy?.firstOrNull()?.route

    // Hide bottom bar while onboarding is the top route — it would steal focus
    // from the multi-step CTAs and visually clutter a flow that's supposed to
    // feel guided.
    val showBottomBar = currentRoute != null && currentRoute != OnboardingRoute

    Scaffold(
        bottomBar = {
            if (showBottomBar) LifeOsBottomBar(navController = navController)
        },
    ) { padding ->
        LifeOsNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        )
    }
}
