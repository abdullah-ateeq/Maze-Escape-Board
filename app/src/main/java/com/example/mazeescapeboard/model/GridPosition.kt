package com.example.mazeescapeboard.model

data class GridPosition(
    val row: Int,
    val col: Int
) {
    fun isInBounds(rows: Int = 6, cols: Int = 6): Boolean {
        return row in 0 until rows && col in 0 until cols
    }

    fun step(direction: Direction): GridPosition {
        return GridPosition(row + direction.deltaRow, col + direction.deltaCol)
    }

    fun directionTo(other: GridPosition): Direction? {
        val dRow = other.row - row
        val dCol = other.col - col
        return when {
            dRow == -1 && dCol == 0 -> Direction.UP
            dRow == 1 && dCol == 0 -> Direction.DOWN
            dRow == 0 && dCol == 1 -> Direction.RIGHT
            dRow == 0 && dCol == -1 -> Direction.LEFT
            else -> null // Diagonal or non-adjacent
        }
    }

    fun isOrthogonallyAdjacent(other: GridPosition): Boolean {
        return directionTo(other) != null
    }
}
