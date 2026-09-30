package com.example.mazeescapeboard.model

object HandcraftedMaze {

    /**
     * Initial 6x6 handcrafted maze layout.
     * Solvable in roughly 12-20 actions (rotations + moves).
     *
     * Start: (5, 0) - Locked
     * Exit: (0, 5) - Locked
     * Structural locked anchors: (2, 2), (3, 4)
     */
    fun createInitialBoard(): List<List<MazeTile>> {
        return listOf(
            // Row 0
            listOf(
                MazeTile(MazeTileKind.CORNER, rotation = 90),     // (0, 0)
                MazeTile(MazeTileKind.STRAIGHT, rotation = 0),     // (0, 1)
                MazeTile(MazeTileKind.T_JUNCTION, rotation = 180), // (0, 2)
                MazeTile(MazeTileKind.DEAD_END, rotation = 180),   // (0, 3)
                MazeTile(MazeTileKind.CORNER, rotation = 0),       // (0, 4) -> Needs 1 turn to connect DOWN+RIGHT
                MazeTile(MazeTileKind.DEAD_END, rotation = 270, isLocked = true) // (0, 5) EXIT (faces LEFT)
            ),
            // Row 1
            listOf(
                MazeTile(MazeTileKind.STRAIGHT, rotation = 90),    // (1, 0)
                MazeTile(MazeTileKind.CORNER, rotation = 180),    // (1, 1)
                MazeTile(MazeTileKind.CROSS, rotation = 0),        // (1, 2)
                MazeTile(MazeTileKind.T_JUNCTION, rotation = 270), // (1, 3)
                MazeTile(MazeTileKind.STRAIGHT, rotation = 0),     // (1, 4) -> Needs 1 turn (rot 90) to connect UP+DOWN
                MazeTile(MazeTileKind.DEAD_END, rotation = 0)      // (1, 5)
            ),
            // Row 2
            listOf(
                MazeTile(MazeTileKind.T_JUNCTION, rotation = 0),   // (2, 0)
                MazeTile(MazeTileKind.STRAIGHT, rotation = 0),     // (2, 1)
                MazeTile(MazeTileKind.CROSS, rotation = 0, isLocked = true), // (2, 2) Locked Anchor
                MazeTile(MazeTileKind.STRAIGHT, rotation = 90),    // (2, 3) -> Needs 1 turn (rot 0) to connect LEFT+RIGHT
                MazeTile(MazeTileKind.CORNER, rotation = 180),    // (2, 4) -> Needs 1 turn (rot 270) to connect LEFT+UP
                MazeTile(MazeTileKind.STRAIGHT, rotation = 90)     // (2, 5)
            ),
            // Row 3
            listOf(
                MazeTile(MazeTileKind.CORNER, rotation = 0),       // (3, 0)
                MazeTile(MazeTileKind.CROSS, rotation = 0),        // (3, 1)
                MazeTile(MazeTileKind.STRAIGHT, rotation = 0),     // (3, 2) -> Needs 1 turn (rot 90) to connect UP+DOWN
                MazeTile(MazeTileKind.T_JUNCTION, rotation = 90),  // (3, 3)
                MazeTile(MazeTileKind.CORNER, rotation = 0, isLocked = true), // (3, 4) Locked Anchor
                MazeTile(MazeTileKind.DEAD_END, rotation = 270)    // (3, 5)
            ),
            // Row 4
            listOf(
                MazeTile(MazeTileKind.STRAIGHT, rotation = 90),    // (4, 0)
                MazeTile(MazeTileKind.CORNER, rotation = 0),       // (4, 1) -> Needs 1 turn (rot 90) to connect DOWN+RIGHT
                MazeTile(MazeTileKind.CORNER, rotation = 180),    // (4, 2) -> Needs 1 turn (rot 270) to connect LEFT+UP
                MazeTile(MazeTileKind.STRAIGHT, rotation = 0),     // (4, 3)
                MazeTile(MazeTileKind.T_JUNCTION, rotation = 180), // (4, 4)
                MazeTile(MazeTileKind.CORNER, rotation = 270)      // (4, 5)
            ),
            // Row 5
            listOf(
                MazeTile(MazeTileKind.DEAD_END, rotation = 90, isLocked = true), // (5, 0) START (faces RIGHT)
                MazeTile(MazeTileKind.CORNER, rotation = 180),    // (5, 1) -> Needs 1 turn (rot 270) to connect LEFT+UP
                MazeTile(MazeTileKind.STRAIGHT, rotation = 90),    // (5, 2)
                MazeTile(MazeTileKind.DEAD_END, rotation = 90),    // (5, 3)
                MazeTile(MazeTileKind.CORNER, rotation = 90),      // (5, 4)
                MazeTile(MazeTileKind.STRAIGHT, rotation = 0)      // (5, 5)
            )
        )
    }
}
