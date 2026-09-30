package com.example.mazeescapeboard.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun TopHud(
    moves: Int,
    rotations: Int,
    steps: Int,
    elapsedMillis: Long,
    showTimer: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        StatCard(
            label = "Moves",
            value = moves.toString(),
            modifier = Modifier.weight(1f),
            highlight = false
        )
        StatCard(
            label = "Rotations",
            value = rotations.toString(),
            modifier = Modifier.weight(1f),
            highlight = false
        )
        StatCard(
            label = "Steps",
            value = steps.toString(),
            modifier = Modifier.weight(1f),
            highlight = false
        )
        if (showTimer) {
            val totalSeconds = elapsedMillis / 1000L
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val timeText = String.format(Locale.US, "%02d:%02d", minutes, seconds)
            StatCard(
                label = "Time",
                value = timeText,
                modifier = Modifier.weight(1f),
                highlight = false
            )
        }
    }
}
