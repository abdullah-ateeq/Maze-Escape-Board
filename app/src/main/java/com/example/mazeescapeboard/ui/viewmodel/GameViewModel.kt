package com.example.mazeescapeboard.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mazeescapeboard.data.GameSettings
import com.example.mazeescapeboard.data.SettingsRepository
import com.example.mazeescapeboard.data.SoundAndHapticFeedback
import com.example.mazeescapeboard.model.GridPosition
import com.example.mazeescapeboard.model.HandcraftedMaze
import com.example.mazeescapeboard.model.MazeEscapeRules
import com.example.mazeescapeboard.model.MazeEscapeState
import com.example.mazeescapeboard.model.UndoSnapshot
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.ArrayDeque

class GameViewModel(
    application: Application,
    private val settingsRepository: SettingsRepository,
    private val soundAndHaptic: SoundAndHapticFeedback
) : AndroidViewModel(application) {

    private val undoStack = ArrayDeque<UndoSnapshot>()
    private var timerJob: Job? = null

    val settings: StateFlow<GameSettings> = settingsRepository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = GameSettings()
        )

    private val _gameState = MutableStateFlow(createInitialState())
    val gameState: StateFlow<MazeEscapeState> = _gameState.asStateFlow()

    init {
        startTimer()
    }

    private fun createInitialState(): MazeEscapeState {
        val initialBoard = HandcraftedMaze.createInitialBoard()
        val initialLegalMoves = MazeEscapeRules.getLegalMoves(initialBoard, MazeEscapeRules.START_POSITION)
        val initialReachable = MazeEscapeRules.getReachableRegion(initialBoard, MazeEscapeRules.START_POSITION)

        return MazeEscapeState(
            board = initialBoard,
            playerPosition = MazeEscapeRules.START_POSITION,
            startPosition = MazeEscapeRules.START_POSITION,
            exitPosition = MazeEscapeRules.EXIT_POSITION,
            legalMoves = initialLegalMoves,
            reachableRegion = initialReachable,
            moveCount = 0,
            rotationCount = 0,
            stepCount = 0,
            elapsedMillis = 0L,
            isComplete = false,
            isAnimating = false,
            canUndo = false,
            lastRotatedTile = null
        )
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                if (!_gameState.value.isComplete && (_gameState.value.moveCount > 0 || _gameState.value.elapsedMillis > 0)) {
                    _gameState.update { it.copy(elapsedMillis = it.elapsedMillis + 1000L) }
                }
            }
        }
    }

    fun onCellTapped(position: GridPosition) {
        val state = _gameState.value
        if (state.isComplete || state.isAnimating) return

        // 1. Check if tapped cell is a legal adjacent move
        if (position in state.legalMoves) {
            movePlayerTo(position)
            return
        }

        // 2. Check if tapped cell can be rotated
        val canRotate = MazeEscapeRules.canRotate(
            board = state.board,
            playerPosition = state.playerPosition,
            target = position,
            isComplete = state.isComplete,
            isAnimating = state.isAnimating
        )

        if (canRotate) {
            rotateTileAt(position)
        } else {
            // Blocked tap feedback (e.g. tapping player cell, locked cell, or invalid unreachable cell)
            val currentSettings = settings.value
            soundAndHaptic.playBlockedTap(
                soundEnabled = currentSettings.soundEnabled,
                hapticsEnabled = currentSettings.hapticsEnabled
            )
        }
    }

    private fun rotateTileAt(position: GridPosition) {
        val currentState = _gameState.value

        // Record Undo snapshot
        undoStack.add(
            UndoSnapshot(
                board = currentState.board,
                playerPosition = currentState.playerPosition,
                moveCount = currentState.moveCount,
                rotationCount = currentState.rotationCount,
                stepCount = currentState.stepCount
            )
        )

        val newBoard = currentState.board.mapIndexed { r, row ->
            row.mapIndexed { c, tile ->
                if (r == position.row && c == position.col) {
                    tile.rotateClockwise()
                } else {
                    tile
                }
            }
        }

        val legal = MazeEscapeRules.getLegalMoves(newBoard, currentState.playerPosition)
        val reachable = MazeEscapeRules.getReachableRegion(newBoard, currentState.playerPosition)

        val currentSettings = settings.value
        soundAndHaptic.playTileRotation(
            soundEnabled = currentSettings.soundEnabled,
            hapticsEnabled = currentSettings.hapticsEnabled
        )

        _gameState.update {
            it.copy(
                board = newBoard,
                legalMoves = legal,
                reachableRegion = reachable,
                moveCount = it.moveCount + 1,
                rotationCount = it.rotationCount + 1,
                isAnimating = true,
                lastRotatedTile = position,
                canUndo = true
            )
        }

        viewModelScope.launch {
            delay(160L) // 160ms rotation animation duration
            _gameState.update { it.copy(isAnimating = false, lastRotatedTile = null) }
        }
    }

    private fun movePlayerTo(position: GridPosition) {
        val currentState = _gameState.value

        // Record Undo snapshot
        undoStack.add(
            UndoSnapshot(
                board = currentState.board,
                playerPosition = currentState.playerPosition,
                moveCount = currentState.moveCount,
                rotationCount = currentState.rotationCount,
                stepCount = currentState.stepCount
            )
        )

        val isExitReached = (position == currentState.exitPosition)
        val newLegal = if (isExitReached) emptySet() else MazeEscapeRules.getLegalMoves(currentState.board, position)
        val newReachable = MazeEscapeRules.getReachableRegion(currentState.board, position)

        val currentSettings = settings.value
        if (isExitReached) {
            soundAndHaptic.playEscapeComplete(
                soundEnabled = currentSettings.soundEnabled,
                hapticsEnabled = currentSettings.hapticsEnabled
            )
        } else {
            soundAndHaptic.playTokenMove(
                soundEnabled = currentSettings.soundEnabled,
                hapticsEnabled = currentSettings.hapticsEnabled
            )
        }

        _gameState.update {
            it.copy(
                playerPosition = position,
                legalMoves = newLegal,
                reachableRegion = newReachable,
                moveCount = it.moveCount + 1,
                stepCount = it.stepCount + 1,
                isComplete = isExitReached,
                isAnimating = true,
                canUndo = !isExitReached
            )
        }

        if (isExitReached) {
            viewModelScope.launch {
                val totalSeconds = (_gameState.value.elapsedMillis / 1000L).coerceAtLeast(1L)
                settingsRepository.recordEscapeCompleted(
                    moves = _gameState.value.moveCount,
                    rotations = _gameState.value.rotationCount,
                    timeSeconds = totalSeconds
                )
            }
        }

        viewModelScope.launch {
            delay(190L) // 190ms translation animation duration
            _gameState.update { it.copy(isAnimating = false) }
        }
    }

    fun undo() {
        val currentState = _gameState.value
        if (undoStack.isEmpty() || currentState.isAnimating || currentState.isComplete) return

        val snapshot = undoStack.removeLast()
        val legal = MazeEscapeRules.getLegalMoves(snapshot.board, snapshot.playerPosition)
        val reachable = MazeEscapeRules.getReachableRegion(snapshot.board, snapshot.playerPosition)

        val currentSettings = settings.value
        soundAndHaptic.playTokenMove(
            soundEnabled = currentSettings.soundEnabled,
            hapticsEnabled = currentSettings.hapticsEnabled
        )

        _gameState.update {
            it.copy(
                board = snapshot.board,
                playerPosition = snapshot.playerPosition,
                legalMoves = legal,
                reachableRegion = reachable,
                moveCount = snapshot.moveCount,
                rotationCount = snapshot.rotationCount,
                stepCount = snapshot.stepCount,
                canUndo = undoStack.isNotEmpty(),
                lastRotatedTile = null
            )
        }
    }

    fun restartGame() {
        undoStack.clear()
        _gameState.value = createInitialState()
        viewModelScope.launch {
            settingsRepository.incrementGamesPlayed()
        }
    }

    fun startNewGameSession() {
        restartGame()
    }

    class Factory(
        private val application: Application,
        private val settingsRepository: SettingsRepository,
        private val soundAndHaptic: SoundAndHapticFeedback
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GameViewModel(application, settingsRepository, soundAndHaptic) as T
        }
    }
}
