package com.example.mazeescapeboard.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.mazeescapeboard.data.BoardTheme
import com.example.mazeescapeboard.data.ThemeMode

data class MazeBoardColors(
    val boardBackground: Color,
    val tileBackground: Color,
    val tileBorder: Color,
    val channelColor: Color,
    val channelActiveColor: Color,
    val tokenColor: Color,
    val tokenInnerColor: Color,
    val exitGlowColor: Color,
    val legalMoveGlow: Color,
    val lockedMarkerColor: Color
)

val LocalMazeBoardColors = staticCompositionLocalOf {
    MazeBoardColors(
        boardBackground = DarkSlateBoard,
        tileBackground = TileSurfaceBase,
        tileBorder = TileBorderNormal,
        channelColor = ChannelBaseMutedBronze,
        channelActiveColor = AccentTealRoute,
        tokenColor = AccentGoldToken,
        tokenInnerColor = AccentGoldTokenInner,
        exitGlowColor = AccentExitPortal,
        legalMoveGlow = AccentLegalMove,
        lockedMarkerColor = AccentGoldToken
    )
}

private val DarkSlateColorScheme = darkColorScheme(
    primary = SlatePrimary,
    onPrimary = MidnightBackground,
    primaryContainer = TileSurfaceBase,
    onPrimaryContainer = TextPrimary,
    secondary = AccentTealRoute,
    onSecondary = MidnightBackground,
    background = DarkSlateBackground,
    onBackground = TextPrimary,
    surface = DarkSlateSurface,
    onSurface = TextPrimary,
    surfaceVariant = TileSurfaceBase,
    onSurfaceVariant = TextSecondary,
    outline = AccentBorder
)

private val MidnightBronzeColorScheme = darkColorScheme(
    primary = MidnightPrimary,
    onPrimary = MidnightBg,
    primaryContainer = TileSurfaceBase,
    onPrimaryContainer = TextPrimary,
    secondary = MidnightChannel,
    onSecondary = MidnightBg,
    background = MidnightBg,
    onBackground = TextPrimary,
    surface = MidnightSurf,
    onSurface = TextPrimary,
    surfaceVariant = TileSurfaceBase,
    onSurfaceVariant = TextSecondary,
    outline = AccentBorder
)

private val LightArchitecturalColorScheme = lightColorScheme(
    primary = Color(0xFF8B6B38),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8E4DC),
    onPrimaryContainer = Color(0xFF22201D),
    secondary = Color(0xFF25756C),
    onSecondary = Color.White,
    background = Color(0xFFF4F1EA),
    onBackground = Color(0xFF1E2024),
    surface = Color(0xFFEDE9E0),
    onSurface = Color(0xFF1E2024),
    surfaceVariant = Color(0xFFE2DDD2),
    onSurfaceVariant = Color(0xFF4A4E57),
    outline = Color(0xFFC0B8A9)
)

@Composable
fun MazeEscapeBoardTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    boardTheme: BoardTheme = BoardTheme.CLASSIC_SLATE,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        !isDark -> LightArchitecturalColorScheme
        boardTheme == BoardTheme.MIDNIGHT_BRONZE -> MidnightBronzeColorScheme
        else -> DarkSlateColorScheme
    }

    val boardColors = when {
        !isDark -> MazeBoardColors(
            boardBackground = Color(0xFFDED8CB),
            tileBackground = Color(0xFFF7F5F0),
            tileBorder = Color(0xFFB5AC9B),
            channelColor = Color(0xFF8C7355),
            channelActiveColor = Color(0xFF25756C),
            tokenColor = Color(0xFFB8860B),
            tokenInnerColor = Color(0xFFFAF0D7),
            exitGlowColor = Color(0xFF1F8A70),
            legalMoveGlow = Color(0x6625756C),
            lockedMarkerColor = Color(0xFF8C7355)
        )
        boardTheme == BoardTheme.MIDNIGHT_BRONZE -> MazeBoardColors(
            boardBackground = Color(0xFF0F1218),
            tileBackground = Color(0xFF1C222D),
            tileBorder = Color(0xFF2E384A),
            channelColor = Color(0xFFB38959),
            channelActiveColor = Color(0xFF4ECDC4),
            tokenColor = Color(0xFFFFCC66),
            tokenInnerColor = Color(0xFFFFF0D0),
            exitGlowColor = Color(0xFF4ECDC4),
            legalMoveGlow = Color(0x884ECDC4),
            lockedMarkerColor = Color(0xFFB38959)
        )
        else -> MazeBoardColors(
            boardBackground = DarkSlateBoard,
            tileBackground = TileSurfaceBase,
            tileBorder = TileBorderNormal,
            channelColor = ChannelBaseMutedBronze,
            channelActiveColor = AccentTealRoute,
            tokenColor = AccentGoldToken,
            tokenInnerColor = AccentGoldTokenInner,
            exitGlowColor = AccentExitPortal,
            legalMoveGlow = AccentLegalMove,
            lockedMarkerColor = AccentGoldToken
        )
    }

    CompositionLocalProvider(LocalMazeBoardColors provides boardColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}