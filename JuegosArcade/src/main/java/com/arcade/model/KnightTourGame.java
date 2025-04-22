package com.arcade.model;

import com.arcade.Main;
import com.arcade.entity.GameResult;
import com.arcade.util.DatabaseManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class KnightTourGame implements Game {

    private static final int MIN_BOARD_SIZE = 5;
    private static final int MAX_BOARD_SIZE = 8;
    private static final int DEFAULT_BOARD_SIZE = 8;
    
    private static final int[] MOVE_X = {2, 1, -1, -2, -2, -1, 1, 2};
    private static final int[] MOVE_Y = {1, 2, 2, 1, -1, -2, -2, -1};
    
    private int boardSize = DEFAULT_BOARD_SIZE;
    private int startX = 0;
    private int startY = 0;
    private int[][] board;
    private GridPane chessboard;
    private Label statusLabel;
    private int moves = 0;
    private boolean solved = false;
    
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Recorrido del Caballo");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        HBox topBox = new HBox(10);
        topBox.setAlignment(Pos.CENTER);
        topBox.getChildren().add(titleLabel);
        
        VBox controlBox = new VBox(15);
        controlBox.setAlignment(Pos.CENTER);
        
        Label sizeLabel = new Label("Tamaño del tablero: " + boardSize);
        
        Slider sizeSlider = new Slider(MIN_BOARD_SIZE, MAX_BOARD_SIZE, DEFAULT_BOARD_SIZE);
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setMajorTickUnit(1);
        sizeSlider.setMinorTickCount(0);
        sizeSlider.setSnapToTicks(true);
        
        sizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            boardSize = newVal.intValue();
            sizeLabel.setText("Tamaño del tablero: " + boardSize);
            resetBoard();
        });
        
        Label startPosLabel = new Label("Posición inicial: (" + startX + "," + startY + ")");
        
        HBox startPosBox = new HBox(10);
        startPosBox.setAlignment(Pos.CENTER);
        
        Slider startXSlider = new Slider(0, boardSize - 1, 0);
        startXSlider.setShowTickMarks(true);
        startXSlider.setShowTickLabels(true);
        startXSlider.setMajorTickUnit(1);
        startXSlider.setMinorTickCount(0);
        startXSlider.setSnapToTicks(true);
        
        Slider startYSlider = new Slider(0, boardSize - 1, 0);
        startYSlider.setShowTickMarks(true);
        startYSlider.setShowTickLabels(true);
        startYSlider.setMajorTickUnit(1);
        startYSlider.setMinorTickCount(0);
        startYSlider.setSnapToTicks(true);
        
        startXSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            startX = newVal.intValue();
            updateStartPosLabel(startPosLabel);
        });
        
        startYSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            startY = newVal.intValue();
            updateStartPosLabel(startPosLabel);
        });
        
        sizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            boardSize = newVal.intValue();
            sizeLabel.setText("Tamaño del tablero: " + boardSize);
            
            startXSlider.setMax(boardSize - 1);
            startYSlider.setMax(boardSize - 1);
            
            if (startX >= boardSize) {
                startX = boardSize - 1;
                startXSlider.setValue(startX);
            }
            
            if (startY >= boardSize) {
                startY = boardSize - 1;
                startYSlider.setValue(startY);
            }
            
            updateStartPosLabel(startPosLabel);
            resetBoard();
        });
        
        startPosBox.getChildren().addAll(startXSlider, startYSlider);
        
        Button solveButton = new Button("Resolver");
        solveButton.setPrefWidth(150);
        
        solveButton.setOnAction(e -> {
            resetBoard();
            board = new int[boardSize][boardSize];
            
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    board[i][j] = -1;
                }
            }
            
            board[startY][startX] = 0;
            moves = 0;
            
            solved = solveKnightTour(startX, startY, 1);
            updateBoard();
            
            if (solved) {
                statusLabel.setText("¡Recorrido completo en " + (boardSize * boardSize - 1) + " movimientos!");
            } else {
                statusLabel.setText("No se encontró solución");
            }
            
            saveResult();
        });
        
        Button backButton = new Button("Volver al Menú");
        backButton.setPrefWidth(150);
        
        backButton.setOnAction(e -> {
            new Main().start(stage);
        });
        
        statusLabel = new Label("Selecciona un tamaño y posición inicial, luego presiona Resolver");
        
        controlBox.getChildren().addAll(
            sizeLabel,
            sizeSlider,
            startPosLabel,
            startPosBox,
            solveButton,
            statusLabel,
            backButton
        );
        
        chessboard = new GridPane();
        chessboard.setAlignment(Pos.CENTER);
        
        resetBoard();
        
        root.setTop(topBox);
        root.setCenter(chessboard);
        root.setRight(controlBox);
        
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Recorrido del Caballo - Arcade de Juegos Lógicos");
        stage.setScene(scene);
        stage.show();
    }
    
    private void updateStartPosLabel(Label label) {
        label.setText("Posición inicial: (" + startX + "," + startY + ")");
    }
    
    private void resetBoard() {
        chessboard.getChildren().clear();
        
        double cellSize = Math.min(500, 500) / boardSize;
        
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Rectangle cell = new Rectangle(cellSize, cellSize);
                cell.setFill((row + col) % 2 == 0 ? Color.WHITE : Color.LIGHTGRAY);
                cell.setStroke(Color.BLACK);
                chessboard.add(cell, col, row);
            }
        }
    }
    
    private void updateBoard() {
        resetBoard();
        
        if (board != null && solved) {
            double cellSize = Math.min(500, 500) / boardSize;
            
            for (int row = 0; row < boardSize; row++) {
                for (int col = 0; col < boardSize; col++) {
                    if (board[row][col] >= 0) {
                        StackPane cellContent = new StackPane();
                        Rectangle highlight = new Rectangle(cellSize * 0.9, cellSize * 0.9);
                        highlight.setFill(Color.LIGHTGREEN);
                        highlight.setStroke(Color.BLACK);
                        
                        Text moveText = new Text(String.valueOf(board[row][col]));
                        moveText.setFont(Font.font("Arial", FontWeight.BOLD, cellSize / 3));
                        
                        cellContent.getChildren().addAll(highlight, moveText);
                        chessboard.add(cellContent, col, row);
                    }
                }
            }
        }
    }
    
    private boolean solveKnightTour(int x, int y, int moveCount) {
        if (moveCount == boardSize * boardSize) {
            return true;
        }
        
        for (int i = 0; i < 8; i++) {
            int nextX = x + MOVE_X[i];
            int nextY = y + MOVE_Y[i];
            
            if (isValidMove(nextX, nextY)) {
                board[nextY][nextX] = moveCount;
                moves++;
                
                if (solveKnightTour(nextX, nextY, moveCount + 1)) {
                    return true;
                }
                
                board[nextY][nextX] = -1;
            }
        }
        
        return false;
    }
    
    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize && board[y][x] == -1;
    }
    
    @Override
    public void saveResult() {
        GameResult result = new GameResult();
        result.setGameType("KnightTour");
        result.setParameter(startX + "," + startY);
        result.setSteps(moves);
        result.setCompleted(solved);
        
        DatabaseManager.getInstance().saveGameResult(result);
    }
    
    @Override
    public String getName() {
        return "Recorrido del Caballo";
    }
}
