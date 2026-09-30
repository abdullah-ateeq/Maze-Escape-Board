package com.example.mazeescapeboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazeescapeboard.ui.components.StatCard
import com.example.mazeescapeboard.ui.theme.LocalMazeBoardColors
import com.example.mazeescapeboard.ui.viewmodel.SettingsViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    settingsViewModel: SettingsViewModel,
    onPlayClicked: () -> Unit,
    onHowToPlayClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onAboutClicked: () -> Unit
) {
    val stats by settingsViewModel.stats.collectAsState()
    val boardColors = LocalMazeBoardColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Architectural badge / decorative mark
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(boardColors.tileBackground)
                        .border(1.dp, boardColors.tileBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SOLO PUZZLE LABYRINTH",
                        style = MaterialTheme.typography.labelSmall,
                        color = boardColors.channelActiveColor,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "MAZE ESCAPE\nBOARD",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "“Rotate the maze. Find your escape.”",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Primary & Secondary Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Primary PLAY Button
                Button(
                    onClick = onPlayClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = boardColors.tokenColor,
                        contentColor = Color(0xFF1E242E)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFF1E242E)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PLAY",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }

                // Secondary Navigation Buttons
                HomeNavButton(
                    title = "How to Play",
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    onClick = onHowToPlayClicked
                )
                HomeNavButton(
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    onClick = onSettingsClicked
                )
                HomeNavButton(
                    title = "About",
                    icon = Icons.Default.Info,
                    onClick = onAboutClicked
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Compact Stats Card Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(boardColors.tileBackground.copy(alpha = 0.5f))
                    .border(1.dp, boardColors.tileBorder.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "LIFETIME STATISTICS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        label = "Played",
                        value = stats.gamesPlayed.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Escapes",
                        value = stats.escapesCompleted.toString(),
                        modifier = Modifier.weight(1f),
                        highlight = stats.escapesCompleted > 0
                    )
                    StatCard(
                        label = "Best Moves",
                        value = if (stats.bestMoves > 0) stats.bestMoves.toString() else "—",
                        modifier = Modifier.weight(1f)
                    )
                    val timeStr = if (stats.bestTimeSeconds > 0L) {
                        String.format(
                            Locale.US,
                            "%02d:%02d",
                            stats.bestTimeSeconds / 60,
                            stats.bestTimeSeconds % 60
                        )
                    } else "—"
                    StatCard(
                        label = "Best Time",
                        value = timeStr,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HomeNavButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val boardColors = LocalMazeBoardColors.current

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            boardColors.tileBorder.copy(alpha = 0.6f)
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = boardColors.channelActiveColor
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )
    }
}
