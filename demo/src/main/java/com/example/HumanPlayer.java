package com.example;

import java.util.Objects;
import java.util.Scanner;

/**
 * 
 * /** A player who chooses moves by entering coordinates in the console. 
 */
public class HumanPlayer extends Player {
    private final Scanner scanner;
    private String name = "Player #1";

    /**
     * Creates a human player that reads moves from the supplied scanner.
     *
     * @param marker this player's marker
     * @param scanner source of console input
     * @throws NullPointerException if {@code scanner} is null
     * @throws IllegalArgumentException if {@code marker} is not {@code X} or {@code O}
     */
    public HumanPlayer(char marker, Scanner scanner) {
        super(marker);
        this.scanner = Objects.requireNonNull(scanner, "scanner");
    }

    /** Returns this human player's name. */
    public String getName() {
        return name;
    }

    /**
     * Sets this human player's name.
     *
     * @param name the new name
     * @throws NullPointerException if {@code name} is null
     * @throws IllegalArgumentException if {@code name} is empty or blank
     */
    public void setName(String name) {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("A player name cannot be empty or blank");
        }
        this.name = name;
    }

    /** Prompts for coordinates until this player's marker is placed successfully. */
    @Override
    public void makeMove(Board board) {
        while (true) {
            System.out.printf("Player %c, enter row and column (1-3), separated by a space: ", marker);
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("No more input available for the human player's move");
            }

            String[] coordinates = scanner.nextLine().trim().split("\\s+");
            if (coordinates.length != 2) {
                System.out.println("Please enter exactly two numbers.");
                continue;
            }

            try {
                int row = Integer.parseInt(coordinates[0]) - 1;
                int column = Integer.parseInt(coordinates[1]) - 1;
                if (makeMove(board, row, column)) {
                    return;
                }
            } catch (NumberFormatException ignored) {
                // Report malformed entries using the same prompt as invalid positions.
            }
            System.out.println("That position is invalid or occupied. Choose an empty cell from 1 to 3.");
        }
    }

    /**
     * Places this player's marker at a zero-based board position.
     *
     * @param board board on which to make the move
     * @param row zero-based row index
     * @param column zero-based column index
     * @return {@code true} if the marker was placed
     */
    public boolean makeMove(Board board, int row, int column) {
        return board.place(row, column, marker);
    }
}