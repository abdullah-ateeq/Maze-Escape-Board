package com.example.mazeescapeboard.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazeescapeboard.ui.theme.LocalMazeBoardColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val boardColors = LocalMazeBoardColors.current

    val gridAlpha = remember { Animatable(0f) }
    val tileRotation = remember { Animatable(0f) }
    val routeProgress = remember { Animatable(0f) }
    val tokenProgress = remember { Animatable(0f) }
    val exitGlow = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Timeline (~1.5s):
        // 0-300ms: Grid fades in
        launch {
            gridAlpha.animateTo(1f, tween(300, easing = LinearEasing))
        }
        // 200-600ms: Tiles rotate
        launch {
            delay(150)
            tileRotation.animateTo(90f, tween(400, easing = FastOutSlowInEasing))
        }
        // 500-900ms: Route opens & token travels
        launch {
            delay(400)
            routeProgress.animateTo(1f, tween(450, easing = FastOutSlowInEasing))
        }
        launch {
            delay(500)
            tokenProgress.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        }
        // 900-1300ms: Exit glows and Title appears
        launch {
            delay(850)
            exitGlow.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
            titleAlpha.animateTo(1f, tween(350, easing = LinearEasing))
        }
        // 1600ms: Complete and transition
        delay(1650)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Maze Motif Canvas
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val alpha = gridAlpha.value

                    // Draw outer labyrinth frame
                    drawRoundRect(
                        color = boardColors.tileBorder.copy(alpha = 0.5f * alpha),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Draw internal corridors
                    val step = w / 3f
                    for (i in 1..2) {
                        drawLine(
                            color = boardColors.tileBorder.copy(alpha = 0.25f * alpha),
                            start = Offset(step * i, 0f),
                            end = Offset(step * i, h),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = boardColors.tileBorder.copy(alpha = 0.25f * alpha),
                            start = Offset(0f, step * i),
                            end = Offset(w, step * i),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Rotating tile corridor
                    val centerOffset = Offset(step * 1.5f, step * 1.5f)
                    rotate(degrees = tileRotation.value, pivot = centerOffset) {
                        drawLine(
                            color = boardColors.channelColor.copy(alpha = alpha),
                            start = Offset(step, step * 1.5f),
                            end = Offset(step * 2f, step * 1.5f),
                            strokeWidth = 14.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = boardColors.channelColor.copy(alpha = alpha),
                            start = Offset(step * 1.5f, step),
                            end = Offset(step * 1.5f, step * 2f),
                            strokeWidth = 14.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Animated Glowing Route
                    if (routeProgress.value > 0f) {
                        val pathLength = routeProgress.value
                        drawLine(
                            color = boardColors.channelActiveColor.copy(alpha = 0.85f * routeProgress.value),
                            start = Offset(step * 0.5f, step * 2.5f),
                            end = Offset(
                                step * 0.5f + (step * 2f * pathLength),
                                step * 2.5f - (step * 2f * pathLength)
                            ),
                            strokeWidth = 6.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Exit Glow
                    if (exitGlow.value > 0f) {
                        val exitPos = Offset(step * 2.5f, step * 0.5f)
                        drawCircle(
                            color = boardColors.exitGlowColor.copy(alpha = 0.4f * exitGlow.value),
                            radius = 24.dp.toPx() * exitGlow.value,
                            center = exitPos
                        )
                        drawCircle(
                            color = boardColors.exitGlowColor.copy(alpha = exitGlow.value),
                            radius = 12.dp.toPx(),
                            center = exitPos,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    // Player Token Traveling
                    val tokenX = step * 0.5f + (step * 2f * tokenProgress.value)
                    val tokenY = step * 2.5f - (step * 2f * tokenProgress.value)
                    drawCircle(
                        color = Color(0x66000000),
                        radius = 11.dp.toPx(),
                        center = Offset(tokenX, tokenY + 2.dp.toPx())
                    )
                    drawCircle(
                        color = boardColors.tokenColor.copy(alpha = alpha),
                        radius = 10.dp.toPx(),
                        center = Offset(tokenX, tokenY)
                    )
                    drawCircle(
                        color = boardColors.tokenInnerColor.copy(alpha = alpha),
                        radius = 5.dp.toPx(),
                        center = Offset(tokenX, tokenY)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "MAZE ESCAPE BOARD",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = titleAlpha.value),
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ARCHITECTURAL LABYRINTH",
                style = MaterialTheme.typography.labelSmall,
                color = boardColors.channelActiveColor.copy(alpha = titleAlpha.value * 0.9f),
                letterSpacing = 2.sp
            )
        }
    }
}
