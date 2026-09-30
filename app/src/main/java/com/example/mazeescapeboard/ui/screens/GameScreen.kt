package com.example.mazeescapeboard.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazeescapeboard.ui.components.MazeBoard
import com.example.mazeescapeboard.ui.components.TopHud
import com.example.mazeescapeboard.ui.theme.LocalMazeBoardColors
import com.example.mazeescapeboard.ui.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    gameViewModel: GameViewModel,
    onNavigateToResult: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by gameViewModel.gameState.collectAsState()
    val settings by gameViewModel.settings.collectAsState()
    val boardColors = LocalMazeBoardColors.current

    var showRestartDialog by remember { mutableStateOf(false) }

    // When game completes, show brief victory state then navigate to Result screen
    LaunchedEffect(state.isComplete) {
        if (state.isComplete) {
            delay(850)
            onNavigateToResult()
        }
    }

    // Restart Confirmation Dialog
    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { showRestartDialog = false },
            title = {
                Text(
                    text = "Restart this maze?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will restore the original tile layout and return your token to the start.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRestartDialog = false
                        gameViewModel.restartGame()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = boardColors.tokenColor,
                        contentColor = Color(0xFF1E242E)
                    )
                ) {
                    Text("Restart", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestartDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = boardColors.tileBackground,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight * 1.15f

            if (isLandscape) {
                // Landscape / Tablet layout: Maze on Left, HUD & Actions on Right
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Maze Board
                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        MazeBoard(
                            board = state.board,
                            playerPosition = state.playerPosition,
                            startPosition = state.startPosition,
                            exitPosition = state.exitPosition,
                            legalMoves = state.legalMoves,
                            reachableRegion = state.reachableRegion,
                            showLegalMoves = settings.showLegalMoves,
                            onCellTapped = { gameViewModel.onCellTapped(it) },
                            modifier = Modifier.fillMaxHeight()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right Column: HUD and Controls
                    Column(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header bar with back button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to Home",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "MAZE ESCAPE",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.width(48.dp))
                        }

                        // HUD
                        TopHud(
                            moves = state.moveCount,
                            rotations = state.rotationCount,
                            steps = state.stepCount,
                            elapsedMillis = state.elapsedMillis,
                            showTimer = settings.showTimer
                        )

                        // Action Controls
                        GameActionButtons(
                            canUndo = state.canUndo && !state.isAnimating && !state.isComplete,
                            onUndo = { gameViewModel.undo() },
                            onRestart = { showRestartDialog = true }
                        )
                    }
                }
            } else {
                // Portrait layout: HUD top, Maze center, Actions bottom
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top App Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Home",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "MAZE ESCAPE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )

                        IconButton(onClick = { showRestartDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Restart Maze",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Compact HUD
                    TopHud(
                        moves = state.moveCount,
                        rotations = state.rotationCount,
                        steps = state.stepCount,
                        elapsedMillis = state.elapsedMillis,
                        showTimer = settings.showTimer
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Maze Board
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        contentAlignment = Alignment.Center
                    ) {
                        MazeBoard(
                            board = state.board,
                            playerPosition = state.playerPosition,
                            startPosition = state.startPosition,
                            exitPosition = state.exitPosition,
                            legalMoves = state.legalMoves,
                            reachableRegion = state.reachableRegion,
                            showLegalMoves = settings.showLegalMoves,
                            onCellTapped = { gameViewModel.onCellTapped(it) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bottom Controls Bar
                    GameActionButtons(
                        canUndo = state.canUndo && !state.isAnimating && !state.isComplete,
                        onUndo = { gameViewModel.undo() },
                        onRestart = { showRestartDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Overlay banner when complete before navigation
        AnimatedVisibility(
            visible = state.isComplete,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xE6141820), RoundedCornerShape(16.dp))
                    .padding(horizontal = 28.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ESCAPE COMPLETE",
                    style = MaterialTheme.typography.displayMedium,
                    color = boardColors.tokenColor,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun GameActionButtons(
    canUndo: Boolean,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val boardColors = LocalMazeBoardColors.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Undo Button
        OutlinedButton(
            onClick = onUndo,
            enabled = canUndo,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (canUndo) boardColors.tileBorder else boardColors.tileBorder.copy(alpha = 0.25f)
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Undo,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "UNDO",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }

        // Restart Button
        OutlinedButton(
            onClick = onRestart,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                boardColors.tileBorder
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "RESTART",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }
    }
}
