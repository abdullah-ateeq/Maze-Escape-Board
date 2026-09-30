package com.example.mazeescapeboard.model

enum class Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    val deltaRow: Int
        get() = when (this) {
            UP -> -1
            DOWN -> 1
            LEFT -> 0
            RIGHT -> 0
        }

    val deltaCol: Int
        get() = when (this) {
            UP -> 0
            DOWN -> 0
            LEFT -> -1
            RIGHT -> 1
        }

    fun opposite(): Direction = when (this) {
        UP -> DOWN
        RIGHT -> LEFT
        DOWN -> UP
        LEFT -> RIGHT
    }

    fun rotateClockwise(steps: Int = 1): Direction {
        val normalizedSteps = ((steps % 4) + 4) % 4
        val values = entries
        return values[(ordinal + normalizedSteps) % 4]
    }
}
