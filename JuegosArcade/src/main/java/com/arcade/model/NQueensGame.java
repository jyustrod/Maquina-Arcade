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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class NQueensGame implements Game {

    private static final int MIN_BOARD_SIZE = 4;
    private static final int MAX_BOARD_SIZE = 12;
    private static final int DEFAULT_BOARD_SIZE = 8;
    
    private int boardSize = DEFAULT_BOARD_SIZE;
    private int[] queens;
    private GridPane chessboard;
    private Label statusLabel;
    private int steps = 0;
    private boolean solved = false;
    
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Problema de las N-Reinas");
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
        
        Button solveButton = new Button("Resolver");
        solveButton.setPrefWidth(150);
        
        solveButton.setOnAction(e -> {
            resetBoard();
            queens = new int[boardSize];
            steps = 0;
            solved = solveNQueens(0);
            updateBoard();
            
            if (solved) {
                statusLabel.setText("¡Problema resuelto en " + steps + " pasos!");
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
        
        statusLabel = new Label("Selecciona un tamaño y presiona Resolver");
        
        controlBox.getChildren().addAll(
            sizeLabel,
            sizeSlider,
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
        stage.setTitle("N-Reinas - Arcade de Juegos Lógicos");
        stage.setScene(scene);
        stage.show();
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
        
        if (queens != null && solved) {
            double cellSize = Math.min(500, 500) / boardSize;
            
            for (int row = 0; row < boardSize; row++) {
                Rectangle queenRect = new Rectangle(cellSize * 0.8, cellSize * 0.8);
                queenRect.setFill(Color.RED);
                queenRect.setStroke(Color.BLACK);
                
                GridPane.setMargin(queenRect, new Insets(cellSize * 0.1));
                chessboard.add(queenRect, queens[row], row);
            }
        }
    }
    
    private boolean solveNQueens(int row) {
        if (row >= boardSize) {
            return true;
        }
        
        for (int col = 0; col < boardSize; col++) {
            steps++;
            
            if (isSafe(row, col)) {
                queens[row] = col;
                
                if (solveNQueens(row + 1)) {
                    return true;
                }
                
                queens[row] = -1;
            }
        }
        
        return false;
    }
    
    private boolean isSafe(int row, int col) {
        for (int i = 0; i < row; i++) {
            if (queens[i] == col || 
                queens[i] - i == col - row || 
                queens[i] + i == col + row) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void saveResult() {
        GameResult result = new GameResult();
        result.setGameType("NQueens");
        result.setParameter(String.valueOf(boardSize));
        result.setSteps(steps);
        result.setCompleted(solved);
        
        DatabaseManager.getInstance().saveGameResult(result);
    }
    
    @Override
    public String getName() {
        return "N-Reinas";
    }
}
