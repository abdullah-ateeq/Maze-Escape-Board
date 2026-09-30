package com.example.mazeescapeboard.model

data class UndoSnapshot(
    val board: List<List<MazeTile>>,
    val playerPosition: GridPosition,
    val moveCount: Int,
    val rotationCount: Int,
    val stepCount: Int
)

data class MazeEscapeState(
    val board: List<List<MazeTile>>,
    val playerPosition: GridPosition = MazeEscapeRules.START_POSITION,
    val startPosition: GridPosition = MazeEscapeRules.START_POSITION,
    val exitPosition: GridPosition = MazeEscapeRules.EXIT_POSITION,
    val legalMoves: Set<GridPosition> = emptySet(),
    val reachableRegion: Set<GridPosition> = emptySet(),
    val moveCount: Int = 0,
    val rotationCount: Int = 0,
    val stepCount: Int = 0,
    val elapsedMillis: Long = 0L,
    val isComplete: Boolean = false,
    val isAnimating: Boolean = false,
    val canUndo: Boolean = false,
    val lastRotatedTile: GridPosition? = null
)
