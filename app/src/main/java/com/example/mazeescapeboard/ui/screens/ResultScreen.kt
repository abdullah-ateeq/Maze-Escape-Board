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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazeescapeboard.ui.components.StatCard
import com.example.mazeescapeboard.ui.theme.LocalMazeBoardColors
import com.example.mazeescapeboard.ui.viewmodel.GameViewModel
import java.util.Locale

@Composable
fun ResultScreen(
    gameViewModel: GameViewModel,
    onPlayAgainClicked: () -> Unit,
    onHomeClicked: () -> Unit
) {
    val state by gameViewModel.gameState.collectAsState()
    val boardColors = LocalMazeBoardColors.current

    val totalSeconds = (state.elapsedMillis / 1000L).coerceAtLeast(1L)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val timeFormatted = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    // Efficiency Rating based on total moves
    val (ratingTitle, ratingDesc) = when {
        state.moveCount <= 22 -> "Master Architect" to "Flawless precision and spatial anticipation."
        state.moveCount <= 32 -> "Pathfinder" to "Methodical navigation through shifting corridors."
        else -> "Persistent Escaper" to "Determined exploration until the exit yielded."
    }

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

            // Architectural Trophy / Seal Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(boardColors.tileBackground)
                        .border(1.dp, boardColors.exitGlowColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "MAZE SOLVED",
                        style = MaterialTheme.typography.labelSmall,
                        color = boardColors.exitGlowColor,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "ESCAPE COMPLETE",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Efficiency Rating Banner
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(boardColors.tileBackground.copy(alpha = 0.6f))
                        .border(1.dp, boardColors.tileBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "RATING",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = ratingTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = boardColors.tokenColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ratingDesc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Game Session Metrics
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(boardColors.tileBackground.copy(alpha = 0.65f))
                    .border(1.dp, boardColors.tileBorder.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "MISSION REPORT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        label = "Total Moves",
                        value = state.moveCount.toString(),
                        modifier = Modifier.weight(1f),
                        highlight = true
                    )
                    StatCard(
                        label = "Rotations",
                        value = state.rotationCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        label = "Steps",
                        value = state.stepCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Time",
                        value = timeFormatted,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        gameViewModel.restartGame()
                        onPlayAgainClicked()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = boardColors.tokenColor,
                        contentColor = Color(0xFF1E242E)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFF1E242E)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PLAY AGAIN",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }

                OutlinedButton(
                    onClick = onHomeClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        boardColors.tileBorder
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = boardColors.channelActiveColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HOME",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
