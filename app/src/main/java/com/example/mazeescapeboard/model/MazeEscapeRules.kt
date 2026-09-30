package com.example.mazeescapeboard.model

import java.util.ArrayDeque

object MazeEscapeRules {
    const val BOARD_ROWS = 6
    const val BOARD_COLS = 6

    val START_POSITION = GridPosition(5, 0)
    val EXIT_POSITION = GridPosition(0, 5)

    fun canMove(
        board: List<List<MazeTile>>,
        from: GridPosition,
        to: GridPosition
    ): Boolean {
        if (!from.isInBounds(BOARD_ROWS, BOARD_COLS) || !to.isInBounds(BOARD_ROWS, BOARD_COLS)) {
            return false
        }
        val direction = from.directionTo(to) ?: return false

        val currentTile = board[from.row][from.col]
        val destTile = board[to.row][to.col]

        val currentHasDir = direction in currentTile.getConnections()
        val destHasOpposite = direction.opposite() in destTile.getConnections()

        return currentHasDir && destHasOpposite
    }

    fun getLegalMoves(
        board: List<List<MazeTile>>,
        playerPosition: GridPosition
    ): Set<GridPosition> {
        if (!playerPosition.isInBounds(BOARD_ROWS, BOARD_COLS)) return emptySet()

        val legal = mutableSetOf<GridPosition>()
        val currentTile = board[playerPosition.row][playerPosition.col]
        val connections = currentTile.getConnections()

        for (dir in Direction.entries) {
            if (dir in connections) {
                val neighbor = playerPosition.step(dir)
                if (neighbor.isInBounds(BOARD_ROWS, BOARD_COLS)) {
                    val neighborTile = board[neighbor.row][neighbor.col]
                    if (dir.opposite() in neighborTile.getConnections()) {
                        legal.add(neighbor)
                    }
                }
            }
        }
        return legal
    }

    fun canRotate(
        board: List<List<MazeTile>>,
        playerPosition: GridPosition,
        target: GridPosition,
        isComplete: Boolean,
        isAnimating: Boolean
    ): Boolean {
        if (isComplete || isAnimating) return false
        if (!target.isInBounds(BOARD_ROWS, BOARD_COLS)) return false
        if (target == playerPosition) return false // Cannot rotate tile occupied by player

        val tile = board[target.row][target.col]
        return !tile.isLocked
    }

    /**
     * Optional connected region: BFS find all tiles currently reachable from start via
     * open matching connections.
     */
    fun getReachableRegion(
        board: List<List<MazeTile>>,
        start: GridPosition
    ): Set<GridPosition> {
        if (!start.isInBounds(BOARD_ROWS, BOARD_COLS)) return emptySet()

        val visited = mutableSetOf<GridPosition>()
        val queue = ArrayDeque<GridPosition>()

        visited.add(start)
        queue.add(start)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val currentTile = board[current.row][current.col]
            val connections = currentTile.getConnections()

            for (dir in Direction.entries) {
                if (dir in connections) {
                    val next = current.step(dir)
                    if (next.isInBounds(BOARD_ROWS, BOARD_COLS) && next !in visited) {
                        val nextTile = board[next.row][next.col]
                        if (dir.opposite() in nextTile.getConnections()) {
                            visited.add(next)
                            queue.add(next)
                        }
                    }
                }
            }
        }

        return visited
    }
}
