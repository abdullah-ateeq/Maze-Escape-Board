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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazeescapeboard.ui.theme.LocalMazeBoardColors

@Composable
fun HowToPlayScreen(
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
                text = "HOW TO PLAY",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Master the shifting mechanics of the 6×6 labyrinth.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Connection Example Diagram Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(boardColors.tileBackground)
                    .border(1.dp, boardColors.tileBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "CONNECTION PRINCIPLE",
                        style = MaterialTheme.typography.labelSmall,
                        color = boardColors.tokenColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Open match
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFF1E242E), RoundedCornerShape(6.dp))
                                        .border(1.dp, boardColors.channelActiveColor, RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("—>", color = boardColors.channelActiveColor, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFF1E242E), RoundedCornerShape(6.dp))
                                        .border(1.dp, boardColors.channelActiveColor, RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("<—", color = boardColors.channelActiveColor, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = boardColors.channelActiveColor, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Connected", style = MaterialTheme.typography.labelSmall, color = boardColors.channelActiveColor)
                            }
                        }

                        // Blocked mismatch
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFF1E242E), RoundedCornerShape(6.dp))
                                        .border(1.dp, Color(0xFFE06C75), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("—>", color = Color(0xFFE06C75), fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFF1E242E), RoundedCornerShape(6.dp))
                                        .border(1.dp, Color(0xFFE06C75), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("|", color = Color(0xFFE06C75), fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFE06C75), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Blocked", style = MaterialTheme.typography.labelSmall, color = Color(0xFFE06C75))
                            }
                        }
                    }
                }
            }

            // Rules Cards
            RuleCard(
                icon = Icons.Default.Refresh,
                title = "Tap to Rotate",
                description = "Tap any unlocked maze tile to rotate it 90° clockwise. Rotating shifts the corridors and opens fresh passageways."
            )

            RuleCard(
                icon = Icons.Default.PanTool,
                title = "Current Tile is Locked",
                description = "The tile beneath your token cannot rotate. You must anticipate the route ahead and adjust adjacent tiles before stepping forward."
            )

            RuleCard(
                icon = Icons.Default.Lock,
                title = "Fixed Anchors",
                description = "Start (bottom-left) and Exit (top-right) are fixed in place, alongside key structural pillar tiles."
            )

            RuleCard(
                icon = Icons.Default.Navigation,
                title = "Tap to Move",
                description = "When a neighboring tile aligns its opening with your current tile, it is highlighted. Tap the neighbor to advance your token."
            )

            RuleCard(
                icon = Icons.Default.CheckCircle,
                title = "Escape to Win",
                description = "Guide your token through the labyrinth to the pulsing Exit module in as few total moves and rotations as possible."
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun RuleCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    val boardColors = LocalMazeBoardColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(boardColors.tileBackground.copy(alpha = 0.6f))
            .border(1.dp, boardColors.tileBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(boardColors.channelActiveColor.copy(alpha = 0.15f))
                .border(1.dp, boardColors.channelActiveColor.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = boardColors.channelActiveColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
