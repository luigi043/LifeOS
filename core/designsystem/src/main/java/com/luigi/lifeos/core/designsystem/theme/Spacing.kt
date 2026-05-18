package com.luigi.lifeos.core.designsystem.theme

import androidx.compose.ui.unit.dp

/**
 * Centralised spacing scale. Using these instead of raw `.dp` keeps
 * layouts rhythmically consistent and makes a future "comfortable / compact"
 * density toggle a single-file change.
 *
 * Scale loosely follows an 8pt grid with two sub-grid steps (4, 12).
 */
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val huge = 48.dp
}

/** Standard horizontal padding for a primary screen — Today, Finance, etc. */
val ScreenHorizontalPadding = Spacing.xl

/** Standard vertical padding between screen header and the first card. */
val ScreenTopPadding = Spacing.xxl
