package com.luigi.lifeos.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Brand palette. Calm + clinical: deep teal for primary (associated with
 * stability/health), warm coral for accents (associated with energy/action),
 * neutral surfaces. Spec calls for a "premium feel like Apple/Tesla/BMW".
 *
 * Light + dark schemes are derived in [Theme.kt]. Update tokens here, not in
 * Compose `MaterialTheme()` calls.
 */

// Primary - deep teal
internal val Teal80 = Color(0xFF7FD5C8)
internal val Teal40 = Color(0xFF00897B)
internal val Teal20 = Color(0xFF00574B)

// Secondary - warm coral
internal val Coral80 = Color(0xFFFFB5A7)
internal val Coral40 = Color(0xFFE76F51)
internal val Coral20 = Color(0xFF7A2E1A)

// Tertiary - amber for warnings / energy
internal val Amber80 = Color(0xFFFFCC80)
internal val Amber40 = Color(0xFFF59E0B)
internal val Amber20 = Color(0xFF7A4D00)

// Neutrals
internal val Neutral99 = Color(0xFFFCFCFC)
internal val Neutral95 = Color(0xFFF3F4F6)
internal val Neutral10 = Color(0xFF141414)
internal val Neutral20 = Color(0xFF1F1F1F)

// Semantic
internal val ErrorRed = Color(0xFFB00020)
internal val SuccessGreen = Color(0xFF22C55E)
