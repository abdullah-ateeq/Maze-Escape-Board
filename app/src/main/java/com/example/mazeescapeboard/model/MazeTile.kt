package com.example.mazeescapeboard.model

data class MazeTile(
    val kind: MazeTileKind,
    val rotation: Int = 0,
    val isLocked: Boolean = false
) {
    init {
        require(rotation in setOf(0, 90, 180, 270)) {
            "Rotation must be 0, 90, 180, or 270 degrees, but was $rotation"
        }
    }

    fun getConnections(): Set<Direction> {
        val steps = (rotation / 90) % 4
        if (steps == 0) return kind.baseConnections
        return kind.baseConnections.map { dir ->
            dir.rotateClockwise(steps)
        }.toSet()
    }

    fun rotateClockwise(): MazeTile {
        if (isLocked) return this
        val nextRotation = (rotation + 90) % 360
        return copy(rotation = nextRotation)
    }
}
