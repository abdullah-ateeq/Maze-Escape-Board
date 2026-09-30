package com.example.mazeescapeboard.model

enum class MazeTileKind {
    STRAIGHT,
    CORNER,
    T_JUNCTION,
    CROSS,
    DEAD_END;

    val baseConnections: Set<Direction>
        get() = when (this) {
            STRAIGHT -> setOf(Direction.LEFT, Direction.RIGHT)
            CORNER -> setOf(Direction.UP, Direction.RIGHT)
            T_JUNCTION -> setOf(Direction.UP, Direction.LEFT, Direction.RIGHT)
            CROSS -> setOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)
            DEAD_END -> setOf(Direction.UP)
        }
}
