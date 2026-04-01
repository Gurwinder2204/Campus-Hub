package com.campushub.mobile

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object CampusTheme {
    val Primary = Color(0xFF2563EB)
    val PrimaryDark = Color(0xFF1D4ED8)
    val PrimaryLight = Color(0xFFDBEAFE)
    val Secondary = Color(0xFF7C3AED)
    val SecondaryLight = Color(0xFFEDE9FE)
    val Accent = Color(0xFFF59E0B)
    val AccentLight = Color(0xFFFEF3C7)
    val Success = Color(0xFF10B981)
    val SuccessLight = Color(0xFFD1FAE5)
    val Warning = Color(0xFFF59E0B)
    val WarningLight = Color(0xFFFEF3C7)
    val Danger = Color(0xFFEF4444)
    val DangerLight = Color(0xFFFEE2E2)
    val Info = Color(0xFF06B6D4)
    val InfoLight = Color(0xFFCFFAFE)

    val Background = Color(0xFFF1F5F9)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceSecondary = Color(0xFFF8FAFC)
    val Border = Color(0xFFE2E8F0)
    val BorderLight = Color(0xFFF1F5F9)

    val TextPrimary = Color(0xFF0F172A)
    val TextSecondary = Color(0xFF475569)
    val TextMuted = Color(0xFF94A3B8)
    val TextInverse = Color(0xFFFFFFFF)

    val Study = Primary
    val StudyLight = PrimaryLight
    val Campus = Success
    val CampusLight = SuccessLight
    val Community = Accent
    val CommunityLight = AccentLight
    val Booking = Secondary
    val BookingLight = SecondaryLight
}

object AppSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}

object AppRadius {
    val sm = RoundedCornerShape(8.dp)
    val md = RoundedCornerShape(12.dp)
    val lg = RoundedCornerShape(16.dp)
    val xl = RoundedCornerShape(24.dp)
    val full = RoundedCornerShape(999.dp)
}

fun campusHubColorScheme(): ColorScheme = lightColorScheme(
    primary = CampusTheme.Primary,
    secondary = CampusTheme.Secondary,
    tertiary = CampusTheme.Accent,
    background = CampusTheme.Background,
    surface = CampusTheme.Surface,
    onPrimary = CampusTheme.TextInverse,
    onSecondary = CampusTheme.TextInverse,
    onTertiary = CampusTheme.TextPrimary,
    onBackground = CampusTheme.TextPrimary,
    onSurface = CampusTheme.TextPrimary,
    surfaceVariant = CampusTheme.SurfaceSecondary,
    outline = CampusTheme.Border
)

fun campusHubTypography(): Typography = Typography(
    headlineLarge = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = CampusTheme.TextPrimary),
    headlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = CampusTheme.TextPrimary),
    headlineSmall = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CampusTheme.TextPrimary),
    titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CampusTheme.TextPrimary),
    titleMedium = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = CampusTheme.TextPrimary),
    bodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, color = CampusTheme.TextSecondary),
    bodyMedium = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, color = CampusTheme.TextSecondary),
    bodySmall = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, color = CampusTheme.TextMuted),
    labelLarge = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CampusTheme.TextPrimary),
    labelMedium = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CampusTheme.TextMuted)
)

fun campusHubDarkColorScheme(): ColorScheme = darkColorScheme(
    primary = CampusTheme.Primary,
    secondary = CampusTheme.Secondary,
    tertiary = CampusTheme.Accent
)
