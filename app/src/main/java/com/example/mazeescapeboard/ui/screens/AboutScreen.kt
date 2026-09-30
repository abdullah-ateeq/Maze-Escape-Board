package com.example.mazeescapeboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazeescapeboard.ui.theme.LocalMazeBoardColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    val boardColors = LocalMazeBoardColors.current

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
                text = "ABOUT",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Architectural Seal Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(boardColors.tileBackground)
                    .border(2.dp, boardColors.tokenColor, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = null,
                    tint = boardColors.tokenColor,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Title & Version
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Maze Escape Board",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Version 1.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = boardColors.channelActiveColor,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Description Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(boardColors.tileBackground.copy(alpha = 0.6f))
                    .border(1.dp, boardColors.tileBorder.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "A polished rotating-maze puzzle where players reshape the board and navigate a token through changing pathways to reach the exit.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            // Feature Badges
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "CORE ARCHITECTURE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val features = listOf(
                    "6×6 Tile Maze",
                    "Rotating Paths",
                    "Dynamic Navigation",
                    "Offline Play",
                    "Premium Themes"
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    features.forEach { feature ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(boardColors.tileBackground)
                                .border(1.dp, boardColors.tileBorder.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = feature,
                                style = MaterialTheme.typography.labelLarge,
                                color = boardColors.tokenInnerColor,
                                fontSize = 12.sp,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                }
            }

            // Design Philosophy Note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(boardColors.boardBackground)
                    .border(1.dp, boardColors.tileBorder.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "DESIGN PHILOSOPHY",
                        style = MaterialTheme.typography.labelSmall,
                        color = boardColors.tokenColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Built natively with Jetpack Compose and Material 3. Inspired by architectural labyrinths and physical mechanical puzzle boards. Designed with zero ads, zero trackers, and pure deterministic logic.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
