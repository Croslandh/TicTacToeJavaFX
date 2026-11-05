package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

/** Unit tests for human and CPU player move behavior. */
class PlayerTest {
    /** Verifies new human and CPU players receive their specified default names. */
    @Test
    void playersHaveDefaultNames() {
        HumanPlayer human = new HumanPlayer('X', new Scanner(""));
        CpuPlayer cpu = new CpuPlayer('O');

        assertEquals("Player #1", human.getName());
        assertEquals("CPU #1", cpu.getName());
    }

    /** Verifies both player types return a name after it has been changed. */
    @Test
    void playersCanHaveTheirNamesChanged() {
        HumanPlayer human = new HumanPlayer('X', new Scanner(""));
        CpuPlayer cpu = new CpuPlayer('O');

        human.setName("Taylor");
        cpu.setName("Computer opponent");

        assertEquals("Taylor", human.getName());
        assertEquals("Computer opponent", cpu.getName());
    }

    /** Verifies neither player accepts a null name. */
    @Test
    void playersRejectNullNames() {
        HumanPlayer human = new HumanPlayer('X', new Scanner(""));
        CpuPlayer cpu = new CpuPlayer('O');

        assertThrows(NullPointerException.class, () -> human.setName(null));
        assertThrows(NullPointerException.class, () -> cpu.setName(null));
    }

    /** Verifies neither player accepts empty or whitespace-only names. */
    @Test
    void playersRejectEmptyAndWhitespaceOnlyNames() {
        HumanPlayer human = new HumanPlayer('X', new Scanner(""));
        CpuPlayer cpu = new CpuPlayer('O');

        assertThrows(IllegalArgumentException.class, () -> human.setName(""));
        assertThrows(IllegalArgumentException.class, () -> human.setName(" \t\n"));
        assertThrows(IllegalArgumentException.class, () -> cpu.setName(""));
        assertThrows(IllegalArgumentException.class, () -> cpu.setName(" \t\n"));
    }

    /** Verifies a human can place a marker at valid coordinates. */
    @Test
    void humanCanPlaceAMoveFromSelectedCoordinates() {
        Board board = new Board();
        HumanPlayer player = new HumanPlayer('X', new Scanner(""));

        assertTrue(player.makeMove(board, 2, 1));
        assertEquals('X', board.getCell(2, 1));
        assertFalse(player.makeMove(board, 2, 1));
        assertFalse(player.makeMove(board, -1, 0));
    }

    /** Verifies the human player retries malformed and occupied coordinates. */
    @Test
    void humanRetriesMalformedAndOccupiedPositions() {
        Board board = new Board();
        board.place(0, 0, 'O');
        HumanPlayer player = new HumanPlayer('X', new Scanner("bad input\n1 1\n2 3\n"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            player.makeMove(board);
        } finally {
            System.setOut(originalOut);
        }
        assertEquals('X', board.getCell(1, 2));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("invalid or occupied"));
    }

    /** Verifies the CPU chooses an empty cell and rejects a full board. */
    @Test
    void cpuSelectsAnEmptyCellAndCannotPlayOnAFullBoard() {
        Board board = new Board();
        board.place(0, 0, 'X');
        CpuPlayer cpu = new CpuPlayer('O', new Random(7));
        cpu.makeMove(board);
        assertEquals(1, countMarker(board, 'O'));
        assertEquals('X', board.getCell(0, 0));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if (board.isValid(row, column)) {
                    board.place(row, column, 'X');
                }
            }
        }
        assertThrows(IllegalStateException.class, () -> cpu.makeMove(board));
    }

    /** Counts the occurrences of a marker on the supplied board. */
    private int countMarker(Board board, char marker) {
        int count = 0;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if (board.getCell(row, column) == marker) {
                    count++;
                }
            }
        }
        return count;
    }
}