package com.example;

import java.util.Scanner;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/** JavaFX interface for a human-versus-CPU Tic-Tac-Toe game. */
public class App extends Application {
    private final Board board = new Board();
    private final HumanPlayer human = new HumanPlayer('X', new Scanner(""));
    private final CpuPlayer cpu = new CpuPlayer('O');
    private final Button[][] cells = new Button[3][3];
    private final Label status = new Label();
    private boolean gameOver;
    private PauseTransition pendingCpuTurn;

    /** Creates and displays the JavaFX game window. */
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        Label eyebrow = new Label("HUMAN VS CPU");
        eyebrow.getStyleClass().add("eyebrow");

        Label title = new Label("Tic-Tac-Toe");
        title.getStyleClass().add("title");

        status.getStyleClass().add("status");

        GridPane gameBoard = createBoard();
        Button restartButton = new Button("Restart game");
        restartButton.getStyleClass().add("restart-button");
        restartButton.setOnAction(event -> restartGame());

        Label hint = new Label("You play as X  |  CPU plays as O");
        hint.getStyleClass().add("hint");

        VBox content = new VBox(18, eyebrow, title, status, gameBoard, hint, restartButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(32));
        content.getStyleClass().add("content");
        root.setCenter(content);

        Scene scene = new Scene(root, 520, 670);
        scene.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());
        stage.setTitle("Tic-Tac-Toe");
        stage.setScene(scene);
        stage.setMinWidth(480);
        stage.setMinHeight(620);

        restartGame();
        stage.show();
    }

    /** Builds a 3-by-3 grid of clickable square buttons. */
    private GridPane createBoard() {
        GridPane gameBoard = new GridPane();
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.setHgap(12);
        gameBoard.setVgap(12);
        gameBoard.getStyleClass().add("board");

        for (int index = 0; index < 3; index++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / 3);
            column.setHgrow(Priority.ALWAYS);
            gameBoard.getColumnConstraints().add(column);

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / 3);
            row.setVgrow(Priority.ALWAYS);
            gameBoard.getRowConstraints().add(row);
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                final int selectedRow = row;
                final int selectedColumn = column;

                Button cell = new Button();
                cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                cell.getStyleClass().add("cell");
                cell.setOnAction(event -> playHumanMove(selectedRow, selectedColumn));
                cells[row][column] = cell;
                gameBoard.add(cell, column, row);
            }
        }
        return gameBoard;
    }

    /** Places the human move and, if the game continues, lets the CPU respond. */
    private void playHumanMove(int row, int column) {
        if (gameOver || !human.makeMove(board, row, column)) {
            return;
        }

        updateCell(row, column);
        if (finishGameIfNeeded(human.getMarker(), "You")) {
            return;
        }

        status.setText("CPU's turn (O)");
        setAllCellsDisabled(true);
        pendingCpuTurn = new PauseTransition(Duration.millis(350));
        pendingCpuTurn.setOnFinished(event -> playCpuTurn());
        pendingCpuTurn.play();
    }

    /** Plays the CPU's move after a short pause so its turn message is visible. */
    private void playCpuTurn() {
        pendingCpuTurn = null;
        if (gameOver) {
            return;
        }

        cpu.makeMove(board);
        updateBoard();
        if (!finishGameIfNeeded(cpu.getMarker(), "CPU")) {
            status.setText("Your turn (X)");
        }
    }

    /** Updates the message and locks the board when the game has ended. */
    private boolean finishGameIfNeeded(char marker, String playerName) {
        if (board.checkWin()) {
            status.setText(playerName + " (" + marker + ") wins!");
            gameOver = true;
            highlightWinningCells(marker);
            setAllCellsDisabled(true);
            return true;
        }
        if (board.isFull()) {
            status.setText("It's a draw!");
            gameOver = true;
            setAllCellsDisabled(true);
            return true;
        }
        return false;
    }

    /**
     * Adds a style class to the three cells in the winner's completed line.
     * Board.checkWin remains responsible for deciding whether the game was won.
     */
    private void highlightWinningCells(char marker) {
        for (int index = 0; index < 3; index++) {
            if (board.getCell(index, 0) == marker
                    && board.getCell(index, 1) == marker
                    && board.getCell(index, 2) == marker) {
                addWinningStyle(index, 0, index, 1, index, 2);
                return;
            }
            if (board.getCell(0, index) == marker
                    && board.getCell(1, index) == marker
                    && board.getCell(2, index) == marker) {
                addWinningStyle(0, index, 1, index, 2, index);
                return;
            }
        }

        if (board.getCell(0, 0) == marker
                && board.getCell(1, 1) == marker
                && board.getCell(2, 2) == marker) {
            addWinningStyle(0, 0, 1, 1, 2, 2);
        } else if (board.getCell(0, 2) == marker
                && board.getCell(1, 1) == marker
                && board.getCell(2, 0) == marker) {
            addWinningStyle(0, 2, 1, 1, 2, 0);
        }
    }

    /** Applies the winning-line style to each cell in a completed triple. */
    private void addWinningStyle(int row1, int column1, int row2, int column2,
            int row3, int column3) {
        cells[row1][column1].getStyleClass().add("winning-cell");
        cells[row2][column2].getStyleClass().add("winning-cell");
        cells[row3][column3].getStyleClass().add("winning-cell");
    }

    /** Displays the latest board markers and disables occupied squares. */
    private void updateBoard() {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                updateCell(row, column);
            }
        }
    }

    /** Synchronizes one button with the corresponding board cell. */
    private void updateCell(int row, int column) {
        char marker = board.getCell(row, column);
        Button cell = cells[row][column];
        // Clearing the style here also removes old highlights when restarting.
        cell.getStyleClass().remove("winning-cell");
        if (marker == ' ') {
            cell.setText("");
            cell.setDisable(false);
            cell.getStyleClass().removeAll("x-cell", "o-cell");
        } else {
            cell.setText(String.valueOf(marker));
            cell.setDisable(true);
            String markerStyle = marker == 'X' ? "x-cell" : "o-cell";
            cell.getStyleClass().removeAll("x-cell", "o-cell");
            cell.getStyleClass().add(markerStyle);
        }
    }

    /** Enables or disables every square, for example after a win or draw. */
    private void setAllCellsDisabled(boolean disabled) {
        for (Button[] row : cells) {
            for (Button cell : row) {
                cell.setDisable(disabled || !cell.getText().isEmpty());
            }
        }
    }

    /** Clears the board and starts a fresh game with the human first. */
    private void restartGame() {
        if (pendingCpuTurn != null) {
            pendingCpuTurn.stop();
            pendingCpuTurn = null;
        }
        board.initialize();
        gameOver = false;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                updateCell(row, column);
            }
        }
        status.setText("Your turn (X)");
    }
}
