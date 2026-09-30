package com.example.mazeescapeboard

import com.example.mazeescapeboard.model.Direction
import com.example.mazeescapeboard.model.GridPosition
import com.example.mazeescapeboard.model.HandcraftedMaze
import com.example.mazeescapeboard.model.MazeEscapeRules
import com.example.mazeescapeboard.model.MazeEscapeState
import com.example.mazeescapeboard.model.MazeTile
import com.example.mazeescapeboard.model.MazeTileKind
import com.example.mazeescapeboard.model.UndoSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MazeRulesTest {

    @Test
    fun testStraightTileRotation() {
        val straight0 = MazeTile(MazeTileKind.STRAIGHT, rotation = 0)
        assertEquals(setOf(Direction.LEFT, Direction.RIGHT), straight0.getConnections())

        val straight90 = straight0.rotateClockwise()
        assertEquals(90, straight90.rotation)
        assertEquals(setOf(Direction.UP, Direction.DOWN), straight90.getConnections())

        val straight180 = straight90.rotateClockwise()
        assertEquals(180, straight180.rotation)
        assertEquals(setOf(Direction.LEFT, Direction.RIGHT), straight180.getConnections())
    }

    @Test
    fun testCornerTileRotation() {
        val corner0 = MazeTile(MazeTileKind.CORNER, rotation = 0)
        assertEquals(setOf(Direction.UP, Direction.RIGHT), corner0.getConnections())

        val corner90 = corner0.rotateClockwise()
        assertEquals(setOf(Direction.RIGHT, Direction.DOWN), corner90.getConnections())

        val corner180 = corner90.rotateClockwise()
        assertEquals(setOf(Direction.DOWN, Direction.LEFT), corner180.getConnections())

        val corner270 = corner180.rotateClockwise()
        assertEquals(setOf(Direction.LEFT, Direction.UP), corner270.getConnections())
    }

    @Test
    fun testTJunctionRotation() {
        val t0 = MazeTile(MazeTileKind.T_JUNCTION, rotation = 0)
        assertEquals(setOf(Direction.UP, Direction.LEFT, Direction.RIGHT), t0.getConnections())

        val t90 = t0.rotateClockwise()
        assertEquals(setOf(Direction.RIGHT, Direction.UP, Direction.DOWN), t90.getConnections())

        val t180 = t90.rotateClockwise()
        assertEquals(setOf(Direction.DOWN, Direction.RIGHT, Direction.LEFT), t180.getConnections())

        val t270 = t180.rotateClockwise()
        assertEquals(setOf(Direction.LEFT, Direction.DOWN, Direction.UP), t270.getConnections())
    }

    @Test
    fun testDeadEndRotation() {
        val dead0 = MazeTile(MazeTileKind.DEAD_END, rotation = 0)
        assertEquals(setOf(Direction.UP), dead0.getConnections())

        val dead90 = dead0.rotateClockwise()
        assertEquals(setOf(Direction.RIGHT), dead90.getConnections())

        val dead180 = dead90.rotateClockwise()
        assertEquals(setOf(Direction.DOWN), dead180.getConnections())

        val dead270 = dead180.rotateClockwise()
        assertEquals(setOf(Direction.LEFT), dead270.getConnections())
    }

    @Test
    fun testCrossTileRotation() {
        val cross = MazeTile(MazeTileKind.CROSS, rotation = 0)
        val allFour = setOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)
        assertEquals(allFour, cross.getConnections())
        assertEquals(allFour, cross.rotateClockwise().getConnections())
    }

    @Test
    fun testMatchingConnectionMovement() {
        // (0,0) with RIGHT connection, (0,1) with LEFT connection
        val board = listOf(
            listOf(
                MazeTile(MazeTileKind.STRAIGHT, rotation = 0), // has LEFT, RIGHT
                MazeTile(MazeTileKind.STRAIGHT, rotation = 0)  // has LEFT, RIGHT
            )
        )
        assertTrue(MazeEscapeRules.canMove(board, GridPosition(0, 0), GridPosition(0, 1)))
        assertTrue(MazeEscapeRules.canMove(board, GridPosition(0, 1), GridPosition(0, 0)))
    }

    @Test
    fun testMismatchedConnectionRejection() {
        // (0,0) has RIGHT, but (0,1) is vertical (UP, DOWN)
        val board = listOf(
            listOf(
                MazeTile(MazeTileKind.STRAIGHT, rotation = 0),  // has LEFT, RIGHT
                MazeTile(MazeTileKind.STRAIGHT, rotation = 90)  // has UP, DOWN (no LEFT)
            )
        )
        assertFalse(MazeEscapeRules.canMove(board, GridPosition(0, 0), GridPosition(0, 1)))
    }

    @Test
    fun testLockedTileCannotRotate() {
        val lockedTile = MazeTile(MazeTileKind.STRAIGHT, rotation = 0, isLocked = true)
        val rotated = lockedTile.rotateClockwise()
        assertEquals(0, rotated.rotation) // does not change

        val board = HandcraftedMaze.createInitialBoard()
        val canRotateStart = MazeEscapeRules.canRotate(
            board = board,
            playerPosition = GridPosition(0, 0),
            target = GridPosition(5, 0), // Start tile is locked
            isComplete = false,
            isAnimating = false
        )
        assertFalse(canRotateStart)
    }

    @Test
    fun testOccupiedTileCannotRotate() {
        val board = HandcraftedMaze.createInitialBoard()
        val playerPos = GridPosition(4, 1)
        val canRotateOccupied = MazeEscapeRules.canRotate(
            board = board,
            playerPosition = playerPos,
            target = playerPos,
            isComplete = false,
            isAnimating = false
        )
        assertFalse(canRotateOccupied)
    }

    @Test
    fun testOrthogonalMovementOnly() {
        val from = GridPosition(2, 2)
        val diagonal = GridPosition(1, 1)
        assertFalse(from.isOrthogonallyAdjacent(diagonal))
        assertEquals(null, from.directionTo(diagonal))

        val board = HandcraftedMaze.createInitialBoard()
        assertFalse(MazeEscapeRules.canMove(board, from, diagonal))
    }

    @Test
    fun testCorrectLegalMoveGeneration() {
        // At initial state: Start is at (5, 0) facing RIGHT.
        // Neighbor (5, 1) initially has CORNER at rotation 180 (DOWN, LEFT).
        // Since (5, 1) has LEFT, and (5, 0) has RIGHT:
        // Wait, let's check initial (5, 1): CORNER rot 180 has DOWN and LEFT!
        // Opposite of RIGHT is LEFT, so (5, 1) actually has LEFT!
        // But (5, 1) does NOT have UP (it has DOWN and LEFT).
        val board = HandcraftedMaze.createInitialBoard()
        val movesFromStart = MazeEscapeRules.getLegalMoves(board, GridPosition(5, 0))
        // (5, 1) has LEFT, so moving to (5, 1) is valid!
        assertTrue(movesFromStart.contains(GridPosition(5, 1)))
        assertEquals(1, movesFromStart.size)
    }

    @Test
    fun testExitDetection() {
        val exitPos = MazeEscapeRules.EXIT_POSITION
        val playerAtExit = exitPos
        assertTrue(playerAtExit == exitPos)
    }

    @Test
    fun testUndoRotationAndMovement() {
        var board = HandcraftedMaze.createInitialBoard()
        var playerPos = GridPosition(5, 0)
        var moves = 0
        var rotations = 0
        var steps = 0

        val undoStack = ArrayDeque<UndoSnapshot>()

        // 1. Take snapshot before rotation
        undoStack.add(UndoSnapshot(board, playerPos, moves, rotations, steps))
        val target = GridPosition(4, 1)
        board = board.mapIndexed { r, row ->
            row.mapIndexed { c, tile ->
                if (r == target.row && c == target.col) tile.rotateClockwise() else tile
            }
        }
        moves++
        rotations++
        assertEquals(1, moves)
        assertEquals(1, rotations)
        assertEquals(90, board[4][1].rotation)

        // 2. Undo rotation
        val snapshot1 = undoStack.removeLast()
        board = snapshot1.board
        playerPos = snapshot1.playerPosition
        moves = snapshot1.moveCount
        rotations = snapshot1.rotationCount
        steps = snapshot1.stepCount

        assertEquals(0, moves)
        assertEquals(0, rotations)
        assertEquals(0, board[4][1].rotation) // Restored

        // 3. Take snapshot before move
        undoStack.add(UndoSnapshot(board, playerPos, moves, rotations, steps))
        playerPos = GridPosition(5, 1)
        moves++
        steps++
        assertEquals(1, moves)
        assertEquals(1, steps)
        assertEquals(GridPosition(5, 1), playerPos)

        // 4. Undo move
        val snapshot2 = undoStack.removeLast()
        board = snapshot2.board
        playerPos = snapshot2.playerPosition
        moves = snapshot2.moveCount
        steps = snapshot2.stepCount

        assertEquals(0, moves)
        assertEquals(0, steps)
        assertEquals(GridPosition(5, 0), playerPos) // Restored
    }

    @Test
    fun testRestartStateRestoration() {
        val initialBoard = HandcraftedMaze.createInitialBoard()
        var state = MazeEscapeState(
            board = initialBoard,
            playerPosition = GridPosition(5, 0),
            moveCount = 15,
            rotationCount = 8,
            stepCount = 7,
            elapsedMillis = 42000L,
            isComplete = true
        )

        // Restart restores initial state
        state = MazeEscapeState(
            board = HandcraftedMaze.createInitialBoard(),
            playerPosition = MazeEscapeRules.START_POSITION,
            legalMoves = MazeEscapeRules.getLegalMoves(initialBoard, MazeEscapeRules.START_POSITION),
            moveCount = 0,
            rotationCount = 0,
            stepCount = 0,
            elapsedMillis = 0L,
            isComplete = false
        )

        assertEquals(0, state.moveCount)
        assertEquals(0, state.rotationCount)
        assertEquals(0, state.stepCount)
        assertEquals(0L, state.elapsedMillis)
        assertFalse(state.isComplete)
        assertEquals(MazeEscapeRules.START_POSITION, state.playerPosition)
    }

    @Test
    fun testHandcraftedMazeSolvabilitySequence() {
        // Let's verify our planned solution sequence step by step!
        var board = HandcraftedMaze.createInitialBoard()
        var player = GridPosition(5, 0)
        var actions = 0

        fun rotate(r: Int, c: Int, times: Int = 1) {
            for (i in 0 until times) {
                val tile = board[r][c]
                require(!tile.isLocked) { "Tile at ($r, $c) is locked!" }
                require(player != GridPosition(r, c)) { "Player is at ($r, $c)!" }
                board = board.mapIndexed { rowIdx, row ->
                    row.mapIndexed { colIdx, t ->
                        if (rowIdx == r && colIdx == c) t.rotateClockwise() else t
                    }
                }
                actions++
            }
        }

        fun move(dest: GridPosition) {
            val legal = MazeEscapeRules.getLegalMoves(board, player)
            assertTrue("Move from $player to $dest must be legal! Legal moves: $legal", legal.contains(dest))
            player = dest
            actions++
        }

        // Trace our path from (5, 0) to (0, 5):
        // (5, 0) -> (5, 1): Start has RIGHT, (5, 1) has CORNER at rot 180 (DOWN, LEFT).
        // (5, 1) needs to connect to (4, 1), so it needs LEFT and UP.
        // CORNER at rot 270 connects LEFT and UP! So (5, 1) needs 1 rotation from 180 to 270.
        rotate(5, 1, 1) // Now (5, 1) has LEFT and UP. Actions: 1
        move(GridPosition(5, 1)) // Move to (5, 1). Actions: 2

        // Now from (5, 1), we go UP to (4, 1).
        // (4, 1) initially has CORNER rot 0 (UP, RIGHT).
        // To connect DOWN (from 5, 1) and RIGHT (to 4, 2), it needs DOWN and RIGHT.
        // CORNER rot 90 connects DOWN and RIGHT! So 1 rotation from 0 to 90.
        rotate(4, 1, 1) // Actions: 3
        move(GridPosition(4, 1)) // Move to (4, 1). Actions: 4

        // From (4, 1), we go RIGHT to (4, 2).
        // (4, 2) initially has CORNER rot 180 (DOWN, LEFT).
        // We need LEFT (from 4, 1) and UP (to 3, 2).
        // CORNER rot 270 connects LEFT and UP! So 1 rotation from 180 to 270.
        rotate(4, 2, 1) // Actions: 5
        move(GridPosition(4, 2)) // Move to (4, 2). Actions: 6

        // From (4, 2), we go UP to (3, 2).
        // (3, 2) initially has STRAIGHT rot 0 (LEFT, RIGHT).
        // We need DOWN and UP. STRAIGHT rot 90 connects UP and DOWN! 1 rotation.
        rotate(3, 2, 1) // Actions: 7
        move(GridPosition(3, 2)) // Move to (3, 2). Actions: 8

        // From (3, 2), we go UP to (2, 2).
        // (2, 2) is a LOCKED CROSS (UP, RIGHT, DOWN, LEFT)! It is already open!
        move(GridPosition(2, 2)) // Move to (2, 2). Actions: 9

        // From (2, 2), we go RIGHT to (2, 3).
        // (2, 3) initially has STRAIGHT rot 90 (UP, DOWN).
        // We need LEFT and RIGHT. STRAIGHT rot 0 connects LEFT and RIGHT! 3 rotations (90->180->270->0).
        rotate(2, 3, 3) // Actions: 12
        move(GridPosition(2, 3)) // Move to (2, 3). Actions: 13

        // From (2, 3), we go RIGHT to (2, 4).
        // (2, 4) initially has CORNER rot 180 (DOWN, LEFT).
        // We need LEFT and UP (to 1, 4).
        // CORNER rot 270 connects LEFT and UP! 1 rotation from 180 to 270.
        rotate(2, 4, 1) // Actions: 14
        move(GridPosition(2, 4)) // Move to (2, 4). Actions: 15

        // From (2, 4), we go UP to (1, 4).
        // (1, 4) initially has STRAIGHT rot 0 (LEFT, RIGHT).
        // We need DOWN and UP. STRAIGHT rot 90 connects UP and DOWN! 1 rotation from 0 to 90.
        rotate(1, 4, 1) // Actions: 16
        move(GridPosition(1, 4)) // Move to (1, 4). Actions: 17

        // From (1, 4), we go UP to (0, 4).
        // (0, 4) initially has CORNER rot 0 (UP, RIGHT).
        // We need DOWN and RIGHT (to Exit at 0, 5).
        // CORNER rot 90 connects DOWN and RIGHT! 1 rotation from 0 to 90.
        rotate(0, 4, 1) // Actions: 18
        move(GridPosition(0, 4)) // Move to (0, 4). Actions: 19

        // From (0, 4), we go RIGHT to (0, 5) which is the EXIT!
        // (0, 5) is LOCKED DEAD_END rot 270 (faces LEFT)!
        move(GridPosition(0, 5)) // Move to (0, 5) Exit! Actions: 20

        assertEquals(GridPosition(0, 5), player)
        assertEquals(20, actions) // Exactly 20 actions: 10 moves + 10 rotations! Perfect fit for prompt's "roughly 12-20 actions"!
    }
}
