package com.luigi.lifeos.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Standard form text field — used across onboarding, add-expense, log-meal.
 *
 * Wraps Material3's OutlinedTextField with a tighter shape and consistent
 * defaults (single-line, full width). Plain wrapper rather than a custom
 * design so behaviours like a11y and IME work without surprises.
 */
@Composable
fun LifeOsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        supportingText = supportingText?.let { { Text(it) } },
        isError = isError,
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(),
    )
}
