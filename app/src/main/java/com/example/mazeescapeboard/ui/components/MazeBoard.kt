package com.example.mazeescapeboard.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.mazeescapeboard.model.Direction
import com.example.mazeescapeboard.model.GridPosition
import com.example.mazeescapeboard.model.MazeEscapeRules
import com.example.mazeescapeboard.model.MazeTile
import com.example.mazeescapeboard.model.MazeTileKind
import com.example.mazeescapeboard.ui.theme.LocalMazeBoardColors

@Composable
fun MazeBoard(
    board: List<List<MazeTile>>,
    playerPosition: GridPosition,
    startPosition: GridPosition,
    exitPosition: GridPosition,
    legalMoves: Set<GridPosition>,
    reachableRegion: Set<GridPosition>,
    showLegalMoves: Boolean,
    onCellTapped: (GridPosition) -> Unit,
    modifier: Modifier = Modifier
) {
    val boardColors = LocalMazeBoardColors.current

    // Pulsing animation for exit and legal move targets
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_effects")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val legalPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "legal_pulse_alpha"
    )

    // Smooth token position translation
    val animRow by animateFloatAsState(
        targetValue = playerPosition.row.toFloat(),
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "token_row"
    )
    val animCol by animateFloatAsState(
        targetValue = playerPosition.col.toFloat(),
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "token_col"
    )

    // Map to track continuous clockwise angles for each tile to avoid backwards flips
    val cumulativeAngles = remember { mutableStateMapOf<GridPosition, Float>() }

    // Synchronize angles with board state
    for (r in 0 until MazeEscapeRules.BOARD_ROWS) {
        for (c in 0 until MazeEscapeRules.BOARD_COLS) {
            val pos = GridPosition(r, c)
            val tile = board[r][c]
            val current = cumulativeAngles[pos] ?: tile.rotation.toFloat()
            val currentMod = ((current % 360f) + 360f) % 360f
            val targetMod = tile.rotation.toFloat()
            if (currentMod != targetMod) {
                val diff = ((targetMod - currentMod) + 360f) % 360f
                cumulativeAngles[pos] = current + diff
            } else if (!cumulativeAngles.containsKey(pos)) {
                cumulativeAngles[pos] = tile.rotation.toFloat()
            }
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(16.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(boardColors.boardBackground)
            .border(2.dp, boardColors.tileBorder.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            .padding(6.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val boardWidth = maxWidth
            val boardHeight = maxHeight
            val cellWidth = boardWidth / MazeEscapeRules.BOARD_COLS
            val cellHeight = boardHeight / MazeEscapeRules.BOARD_ROWS

            // 1. Render all 36 Maze Tiles
            for (r in 0 until MazeEscapeRules.BOARD_ROWS) {
                for (c in 0 until MazeEscapeRules.BOARD_COLS) {
                    val pos = GridPosition(r, c)
                    val tile = board[r][c]
                    val isStart = (pos == startPosition)
                    val isExit = (pos == exitPosition)
                    val isLegal = showLegalMoves && (pos in legalMoves)
                    val isReachable = pos in reachableRegion

                    val targetAngle = cumulativeAngles[pos] ?: tile.rotation.toFloat()
                    val animatedAngle by animateFloatAsState(
                        targetValue = targetAngle,
                        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
                        label = "tile_angle_${r}_${c}"
                    )

                    val accessibilityLabel = buildString {
                        if (isStart) append("Start tile. ")
                        if (isExit) append("Exit tile. ")
                        if (tile.isLocked) append("Locked tile. ")
                        if (pos == playerPosition) append("Player current position. ")
                        if (isLegal) append("Reachable destination. ")
                        append("${tile.kind.name.lowercase()} tile at row $r, column $c, rotated ${tile.rotation} degrees.")
                    }

                    Box(
                        modifier = Modifier
                            .size(cellWidth, cellHeight)
                            .offset(x = cellWidth * c, y = cellHeight * r)
                            .padding(2.dp)
                            .semantics { contentDescription = accessibilityLabel }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onCellTapped(pos)
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawTileModule(
                                tile = tile,
                                rotationAngle = animatedAngle,
                                isExit = isExit,
                                isStart = isStart,
                                isLegalMove = isLegal,
                                isReachable = isReachable,
                                legalPulseAlpha = legalPulseAlpha,
                                exitPulseScale = pulseScale,
                                boardColors = boardColors
                            )
                        }
                    }
                }
            }

            // 2. Render Player Token Overlay
            val tokenSize = cellWidth * 0.72f
            val tokenOffsetX = (cellWidth * animCol) + ((cellWidth - tokenSize) / 2f)
            val tokenOffsetY = (cellHeight * animRow) + ((cellHeight - tokenSize) / 2f)

            Box(
                modifier = Modifier
                    .size(tokenSize)
                    .offset(x = tokenOffsetX, y = tokenOffsetY)
                    .semantics { contentDescription = "Player token at row ${playerPosition.row}, column ${playerPosition.col}" }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawPlayerToken(boardColors = boardColors)
                }
            }
        }
    }
}

private fun DrawScope.drawTileModule(
    tile: MazeTile,
    rotationAngle: Float,
    isExit: Boolean,
    isStart: Boolean,
    isLegalMove: Boolean,
    isReachable: Boolean,
    legalPulseAlpha: Float,
    exitPulseScale: Float,
    boardColors: com.example.mazeescapeboard.ui.theme.MazeBoardColors
) {
    val w = size.width
    val h = size.height
    val cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())

    // 1. Tile Base Gradient
    val baseBrush = Brush.linearGradient(
        colors = listOf(
            boardColors.tileBackground.copy(alpha = 0.95f),
            boardColors.tileBackground.copy(alpha = 0.75f)
        ),
        start = Offset(0f, 0f),
        end = Offset(w, h)
    )
    drawRoundRect(
        brush = baseBrush,
        size = Size(w, h),
        cornerRadius = cornerRadius
    )

    // 2. Tile Outer Border
    val borderColor = when {
        tile.isLocked -> boardColors.lockedMarkerColor.copy(alpha = 0.8f)
        else -> boardColors.tileBorder.copy(alpha = 0.65f)
    }
    drawRoundRect(
        color = borderColor,
        size = Size(w, h),
        cornerRadius = cornerRadius,
        style = Stroke(width = if (tile.isLocked) 2.dp.toPx() else 1.dp.toPx())
    )

    // 3. Subtle locked corner rivets
    if (tile.isLocked && !isExit && !isStart) {
        val rivetRadius = 2.dp.toPx()
        val margin = 5.dp.toPx()
        val rivetColor = boardColors.lockedMarkerColor.copy(alpha = 0.8f)
        drawCircle(color = rivetColor, radius = rivetRadius, center = Offset(margin, margin))
        drawCircle(color = rivetColor, radius = rivetRadius, center = Offset(w - margin, margin))
        drawCircle(color = rivetColor, radius = rivetRadius, center = Offset(margin, h - margin))
        drawCircle(color = rivetColor, radius = rivetRadius, center = Offset(w - margin, h - margin))
    }

    // 4. Carved Corridor Pathways (Rotated by animated angle)
    val cx = w / 2f
    val cy = h / 2f
    val channelWidth = w * 0.32f
    val recessedWidth = channelWidth * 1.25f

    rotate(degrees = rotationAngle, pivot = Offset(cx, cy)) {
        val baseConnections = tile.kind.baseConnections

        // Draw recessed dark channel background
        val recessColor = Color(0xFF0C0E12).copy(alpha = 0.75f)
        for (dir in baseConnections) {
            drawCorridorArm(
                dir = dir,
                cx = cx,
                cy = cy,
                w = w,
                h = h,
                armWidth = recessedWidth,
                color = recessColor
            )
        }
        drawCircle(color = recessColor, radius = recessedWidth / 2f, center = Offset(cx, cy))

        // Draw main channel surface
        val channelColor = boardColors.channelColor
        for (dir in baseConnections) {
            drawCorridorArm(
                dir = dir,
                cx = cx,
                cy = cy,
                w = w,
                h = h,
                armWidth = channelWidth,
                color = channelColor
            )
        }
        drawCircle(color = channelColor, radius = channelWidth / 2f, center = Offset(cx, cy))

        // If tile is in reachable region, draw an illuminated architectural core trace
        if (isReachable) {
            val traceColor = boardColors.channelActiveColor.copy(alpha = 0.75f)
            val traceWidth = channelWidth * 0.28f
            for (dir in baseConnections) {
                drawCorridorArm(
                    dir = dir,
                    cx = cx,
                    cy = cy,
                    w = w,
                    h = h,
                    armWidth = traceWidth,
                    color = traceColor
                )
            }
            drawCircle(color = traceColor, radius = traceWidth / 2f, center = Offset(cx, cy))
        }
    }

    // 5. Exit Cell Portal Emblem
    if (isExit) {
        val portalRadius = (w * 0.34f) * exitPulseScale
        // Outer pulsing ring
        drawCircle(
            color = boardColors.exitGlowColor.copy(alpha = 0.35f),
            radius = portalRadius * 1.15f,
            center = Offset(cx, cy)
        )
        drawCircle(
            color = boardColors.exitGlowColor,
            radius = portalRadius,
            center = Offset(cx, cy),
            style = Stroke(width = 2.5.dp.toPx())
        )
        // Portal arch doorway symbol
        val archPath = Path().apply {
            moveTo(cx - (w * 0.12f), cy + (h * 0.14f))
            lineTo(cx - (w * 0.12f), cy - (h * 0.04f))
            cubicTo(
                cx - (w * 0.12f), cy - (h * 0.16f),
                cx + (w * 0.12f), cy - (h * 0.16f),
                cx + (w * 0.12f), cy - (h * 0.04f)
            )
            lineTo(cx + (w * 0.12f), cy + (h * 0.14f))
        }
        drawPath(
            path = archPath,
            color = boardColors.tokenInnerColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }

    // 6. Start Cell Subtle Emblem
    if (isStart) {
        drawCircle(
            color = boardColors.tokenColor.copy(alpha = 0.3f),
            radius = w * 0.22f,
            center = Offset(cx, cy),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }

    // 7. Reachable Move Highlight
    if (isLegalMove) {
        val glowColor = boardColors.legalMoveGlow.copy(alpha = legalPulseAlpha)
        drawRoundRect(
            color = glowColor,
            size = Size(w, h),
            cornerRadius = cornerRadius,
            style = Stroke(width = 2.5.dp.toPx())
        )
        drawCircle(
            color = glowColor,
            radius = w * 0.16f,
            center = Offset(cx, cy),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

private fun DrawScope.drawCorridorArm(
    dir: Direction,
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    armWidth: Float,
    color: Color
) {
    val endOffset = when (dir) {
        Direction.UP -> Offset(cx, 0f)
        Direction.DOWN -> Offset(cx, h)
        Direction.LEFT -> Offset(0f, cy)
        Direction.RIGHT -> Offset(w, cy)
    }
    drawLine(
        color = color,
        start = Offset(cx, cy),
        end = endOffset,
        strokeWidth = armWidth,
        cap = StrokeCap.Square
    )
}

private fun DrawScope.drawPlayerToken(
    boardColors: com.example.mazeescapeboard.ui.theme.MazeBoardColors
) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f
    val radius = w / 2f * 0.88f

    // Soft drop shadow
    drawCircle(
        color = Color(0x66000000),
        radius = radius * 1.08f,
        center = Offset(cx, cy + 3.dp.toPx())
    )

    // Outer gold beveled ring
    val outerGoldBrush = Brush.radialGradient(
        colors = listOf(
            boardColors.tokenInnerColor,
            boardColors.tokenColor,
            Color(0xFF9E7B35)
        ),
        center = Offset(cx - radius * 0.3f, cy - radius * 0.3f),
        radius = radius * 1.2f
    )
    drawCircle(
        brush = outerGoldBrush,
        radius = radius,
        center = Offset(cx, cy)
    )

    // Inner dark obsidian seal disc
    val innerDiscRadius = radius * 0.72f
    val innerDiscBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF2F3746), Color(0xFF161B23)),
        start = Offset(cx - innerDiscRadius, cy - innerDiscRadius),
        end = Offset(cx + innerDiscRadius, cy + innerDiscRadius)
    )
    drawCircle(
        brush = innerDiscBrush,
        radius = innerDiscRadius,
        center = Offset(cx, cy)
    )

    // Inner gold geometric emblem (Compass / Labyrinth Emblem)
    val emblemSize = innerDiscRadius * 0.58f
    val diamondPath = Path().apply {
        moveTo(cx, cy - emblemSize)
        lineTo(cx + emblemSize * 0.65f, cy)
        lineTo(cx, cy + emblemSize)
        lineTo(cx - emblemSize * 0.65f, cy)
        close()
    }
    drawPath(
        path = diamondPath,
        color = boardColors.tokenInnerColor
    )
    drawCircle(
        color = Color(0xFF1B202A),
        radius = emblemSize * 0.28f,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = boardColors.tokenColor,
        radius = emblemSize * 0.12f,
        center = Offset(cx, cy)
    )
}
