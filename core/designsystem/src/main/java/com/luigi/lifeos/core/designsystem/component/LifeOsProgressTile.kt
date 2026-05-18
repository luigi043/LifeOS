package com.luigi.lifeos.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Compact tile that surfaces a label + big value + a linear progress bar.
 * Used by dashboard tiles ("Calories", "Hydration", "Budget left") and
 * onboarding step-count indicator.
 *
 * Progress is 0f..1f, clamped. Pass null to hide the bar (used when the
 * metric is non-progress, e.g. workout name).
 */
@Composable
fun LifeOsProgressTile(
    label: String,
    value: String,
    progress: Float?,
    modifier: Modifier = Modifier,
) {
    LifeOsCard(modifier = modifier) {
        Column {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (progress != null) {
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                )
            }
        }
    }
}
