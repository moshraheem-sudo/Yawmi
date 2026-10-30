package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanSkyPrimary, // 0xFF38BDF8 - Sky Cyan from screenshots
    onPrimary = MidnightSlateBg, // 0xFF0D1424 - High contrast on pill buttons like "حفظ"
    primaryContainer = CyanSkyContainer,
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = CyanSkyPrimaryVariant,
    onSecondary = Color.White,
    secondaryContainer = MidnightSlateSurfaceVariant,
    onSecondaryContainer = Color(0xFFF1F5F9),
    tertiary = Color(0xFF38BDF8),
    onTertiary = MidnightSlateBg,
    background = MidnightSlateBg, // 0xFF0D1424 - Deep luxury slate navy
    onBackground = Color(0xFFF8FAFC),
    surface = MidnightSlateSurface, // 0xFF162235 - Sleek slate card surface from screenshots
    onSurface = Color.White,
    surfaceVariant = MidnightSlateSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = MidnightSlateBorder, // 0xFF263952 - Subtle card border
    outlineVariant = MidnightSlateBorder,
    error = PriorityHigh,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleContainerLight,
    onPrimaryContainer = PurplePrimary,
    secondary = Color(0xFF6366F1),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E7FF),
    onSecondaryContainer = Color(0xFF312E81),
    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    background = SlateLightBg,
    onBackground = Navy900,
    surface = SlateLightSurface,
    onSurface = Navy900,
    surfaceVariant = SlateLightSurfaceVariant,
    onSurfaceVariant = Navy600,
    outline = SlateLightBorder,
    outlineVariant = Color(0xFFCBD5E1),
    error = PriorityHigh,
    onError = Color.White
)

// Pure White Color Scheme (خلفية بيضاء ناصعة)
private val PureWhiteColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3E8FF),
    onPrimaryContainer = Color(0xFF581C87),
    secondary = Color(0xFF4F46E5),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEEF2FF),
    onSecondaryContainer = Color(0xFF312E81),
    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    background = Color(0xFFFFFFFF), // Pure White Background
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),    // Pure White Surface
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    error = PriorityHigh,
    onError = Color.White
)

// Pure Black AMOLED Color Scheme (خلفية سوداء حالكة للبطارية والراحة البصرية)
private val PureBlackColorScheme = darkColorScheme(
    primary = PurplePrimaryLight,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF241547),
    onPrimaryContainer = Color(0xFFDDD6FE),
    secondary = Color(0xFF818CF8),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF18181B),
    onSecondaryContainer = Color(0xFFE4E4E7),
    tertiary = Color(0xFF38BDF8),
    onTertiary = Color.Black,
    background = Color(0xFF000000), // Pure Black AMOLED Background
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0A0A0A),    // Ultra Dark Surface
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF141414),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF27272A),
    outlineVariant = Color(0xFF18181B),
    error = PriorityHigh,
    onError = Color.White
)

// Natural Organic Nature Color Scheme (خلفية طبيعية هادئة بدرجات خضراء وترابية مريحة للعين)
private val NaturalColorScheme = lightColorScheme(
    primary = Color(0xFF2D6A4F),       // Deep Forest Natural Green
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8F3DC), // Soft Fresh Sage
    onPrimaryContainer = Color(0xFF081C15),
    secondary = Color(0xFF52B788),     // Mint Leaf
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE9F5EC),
    onSecondaryContainer = Color(0xFF1B4332),
    tertiary = Color(0xFF8D6E63),      // Natural Earth Wood
    onTertiary = Color.White,
    background = Color(0xFFF4F7F4),    // Organic Nature Mist
    onBackground = Color(0xFF1C281F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C281F),
    surfaceVariant = Color(0xFFE8EFE8),
    onSurfaceVariant = Color(0xFF2D5A40),
    outline = Color(0xFFB7CDBA),
    outlineVariant = Color(0xFFD5E3D7),
    error = PriorityHigh,
    onError = Color.White
)

// Transparent / Glassmorphism Frosted Color Scheme (خلفية شفافة وزجاجية راقية)
private val TransparentGlassColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8),       // Translucent Ice Blue
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF0C4A6E),
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = Color(0xFFA78BFA),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF2E1A63),
    onSecondaryContainer = Color(0xFFE0E7FF),
    tertiary = Color(0xFF34D399),
    onTertiary = Color(0xFF0F172A),
    background = Color(0xFF080D1A),    // Deep glass background
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF0F1B2E),       // Frosted glass card surface
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF16253C),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF38BDF8).copy(alpha = 0.4f), // Glass glow border
    outlineVariant = Color(0xFF1E293B),
    error = PriorityHigh,
    onError = Color.White
)

// Notes Signature Dark-Blue & Sky-Cyan Color Scheme (ثيم الملاحظات المعمم على التطبيق بالكامل)
private val NotesBlueColorScheme = darkColorScheme(
    primary = CyanSkyPrimary,          // 0xFF38BDF8 - Sky Cyan
    onPrimary = Color(0xFF0A111A),     // High contrast dark navy on primary buttons & nav indicator
    primaryContainer = Color(0xFF0C4A6E),
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = CyanSkyPrimaryVariant, // 0xFF0EA5E9
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E2E42),
    onSecondaryContainer = Color(0xFFF1F5F9),
    tertiary = Color(0xFF38BDF8),
    onTertiary = Color(0xFF0A111A),
    background = Color(0xFF0A111A),    // Exact Notes deep navy background
    onBackground = Color.White,
    surface = Color(0xFF131E2E),       // Exact Notes slate card surface
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1C2C42),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF263952),       // Exact Notes card border
    outlineVariant = Color(0xFF1E2E42),
    error = PriorityHigh,
    onError = Color.White
)

@Composable
fun YawmiTheme(
    themeMode: String = "system",
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val colorScheme = when (themeMode.lowercase()) {
        "notes" -> NotesBlueColorScheme
        "white" -> PureWhiteColorScheme
        "black" -> PureBlackColorScheme
        "natural" -> NaturalColorScheme
        "transparent" -> TransparentGlassColorScheme
        "light" -> LightColorScheme
        "dark" -> DarkColorScheme
        "system" -> if (isSystemDark) DarkColorScheme else LightColorScheme
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
