package com.example.mazeescapeboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazeescapeboard.data.BoardTheme
import com.example.mazeescapeboard.data.ThemeMode
import com.example.mazeescapeboard.ui.theme.LocalMazeBoardColors
import com.example.mazeescapeboard.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by settingsViewModel.settings.collectAsState()
    val boardColors = LocalMazeBoardColors.current

    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Reset Statistics?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will permanently erase your games played, escape records, and best times.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        settingsViewModel.resetStatistics()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE06C75),
                        contentColor = Color.White
                    )
                ) {
                    Text("Reset All", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = boardColors.tileBackground,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SETTINGS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Theme Mode Selection
            SectionCard(title = "APPLICATION THEME") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeOptionButton(
                        label = "System",
                        selected = settings.themeMode == ThemeMode.SYSTEM,
                        onClick = { settingsViewModel.setThemeMode(ThemeMode.SYSTEM) },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeOptionButton(
                        label = "Dark",
                        selected = settings.themeMode == ThemeMode.DARK,
                        onClick = { settingsViewModel.setThemeMode(ThemeMode.DARK) },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeOptionButton(
                        label = "Light",
                        selected = settings.themeMode == ThemeMode.LIGHT,
                        onClick = { settingsViewModel.setThemeMode(ThemeMode.LIGHT) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Board Theme Selection
            SectionCard(title = "BOARD PALETTE") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeOptionButton(
                        label = "Classic Slate",
                        selected = settings.boardTheme == BoardTheme.CLASSIC_SLATE,
                        onClick = { settingsViewModel.setBoardTheme(BoardTheme.CLASSIC_SLATE) },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeOptionButton(
                        label = "Midnight Bronze",
                        selected = settings.boardTheme == BoardTheme.MIDNIGHT_BRONZE,
                        onClick = { settingsViewModel.setBoardTheme(BoardTheme.MIDNIGHT_BRONZE) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Audio & Haptic Controls
            SectionCard(title = "SENSORY FEEDBACK") {
                SettingToggleRow(
                    title = "Sound Effects",
                    description = "Acoustic feedback for rotations, moves, and victory",
                    checked = settings.soundEnabled,
                    onCheckedChange = { settingsViewModel.setSoundEnabled(it) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingToggleRow(
                    title = "Haptic Feedback",
                    description = "Tactile vibration pulses on taps and turns",
                    checked = settings.hapticsEnabled,
                    onCheckedChange = { settingsViewModel.setHapticsEnabled(it) }
                )
            }

            // Gameplay Assist Options
            SectionCard(title = "GAMEPLAY DISPLAY") {
                SettingToggleRow(
                    title = "Show Legal Moves",
                    description = "Highlight adjacent cells with open matching pathways",
                    checked = settings.showLegalMoves,
                    onCheckedChange = { settingsViewModel.setShowLegalMoves(it) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingToggleRow(
                    title = "Show Timer",
                    description = "Display session elapsed time in the game HUD",
                    checked = settings.showTimer,
                    onCheckedChange = { settingsViewModel.setShowTimer(it) }
                )
            }

            // Reset Statistics
            SectionCard(title = "DATA MANAGEMENT") {
                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE06C75)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE06C75).copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFE06C75)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reset Statistics",
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    val boardColors = LocalMazeBoardColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(boardColors.tileBackground.copy(alpha = 0.6f))
            .border(1.dp, boardColors.tileBorder.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = boardColors.tokenColor,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
private fun ThemeOptionButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val boardColors = LocalMazeBoardColors.current
    val bgColor = if (selected) boardColors.channelActiveColor.copy(alpha = 0.2f) else Color.Transparent
    val borderColor = if (selected) boardColors.channelActiveColor else boardColors.tileBorder.copy(alpha = 0.4f)
    val textColor = if (selected) boardColors.channelActiveColor else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val boardColors = LocalMazeBoardColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = boardColors.tokenColor,
                checkedTrackColor = boardColors.channelActiveColor.copy(alpha = 0.5f),
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = boardColors.tileBorder.copy(alpha = 0.3f)
            )
        )
    }
}
