package com.oliverastell.habitask.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
//import androidx.compose.material3.ColorScheme
//import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun HabitaskTheme(colorScheme: ColorScheme? = null, content: @Composable () -> Unit) {

    MaterialTheme(
        colorScheme = colorScheme ?: darkColorScheme(),
        content = content
    )
//    if (isSystemInDarkTheme())
//    MaterialTheme()
//    MaterialTheme(
//        colorScheme = colorScheme ?: ColorScheme(
//            primary = Color.Unspecified,
//            onPrimary = Color.Unspecified,
//            primaryContainer = Color.Unspecified,
//            onPrimaryContainer = Color.Unspecified,
//            inversePrimary = Color.Unspecified,
//            secondary = Color.Unspecified,
//            onSecondary = Color.Unspecified,
//            secondaryContainer = Color.Unspecified,
//            onSecondaryContainer = Color.Unspecified,
//            tertiary = Color.Unspecified,
//            onTertiary = Color.Unspecified,
//            tertiaryContainer = Color.Unspecified,
//            onTertiaryContainer = Color.Unspecified,
//            background = Color(38, 37, 57, 255),
//            onBackground = Color.Unspecified,
//            surface = Color(38, 37, 57, 255),
//            onSurface = Color.Unspecified,
//            surfaceVariant = Color.Unspecified,
//            onSurfaceVariant = Color.Unspecified,
//            surfaceTint = Color.Unspecified,
//            inverseSurface = Color.Unspecified,
//            inverseOnSurface = Color.Unspecified,
//            error = Color.Unspecified,
//            onError = Color.Unspecified,
//            errorContainer = Color.Unspecified,
//            onErrorContainer = Color.Unspecified,
//            outline = Color.Unspecified,
//            outlineVariant = Color.Unspecified,
//            scrim = Color.Unspecified,
//            surfaceBright = Color.Unspecified,
//            surfaceDim = Color.Unspecified,
//            surfaceContainer = Color.Unspecified,
//            surfaceContainerHigh = Color.Unspecified,
//            surfaceContainerHighest = Color.Unspecified,
//            surfaceContainerLow = Color.Unspecified,
//            surfaceContainerLowest = Color.Unspecified,
//            primaryFixed = Color.Unspecified,
//            primaryFixedDim = Color.Unspecified,
//            onPrimaryFixed = Color.Unspecified,
//            onPrimaryFixedVariant = Color.Unspecified,
//            secondaryFixed = Color.Unspecified,
//            secondaryFixedDim = Color.Unspecified,
//            onSecondaryFixed = Color.Unspecified,
//            onSecondaryFixedVariant = Color.Unspecified,
//            tertiaryFixed = Color.Unspecified,
//            tertiaryFixedDim = Color.Unspecified,
//            onTertiaryFixed = Color.Unspecified,
//            onTertiaryFixedVariant = Color.Unspecified
//        ),
//        content = content
//    )
//    else
}