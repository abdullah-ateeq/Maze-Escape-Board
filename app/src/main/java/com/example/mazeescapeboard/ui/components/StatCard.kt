package com.example.mazeescapeboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazeescapeboard.ui.theme.LocalMazeBoardColors

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false
) {
    val boardColors = LocalMazeBoardColors.current
    val borderColor = if (highlight) boardColors.channelActiveColor else boardColors.tileBorder.copy(alpha = 0.5f)
    val valueColor = if (highlight) boardColors.channelActiveColor else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(boardColors.tileBackground.copy(alpha = 0.85f))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                letterSpacing = 1.2.sp,
                fontSize = 10.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = valueColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
