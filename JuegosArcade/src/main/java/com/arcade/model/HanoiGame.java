package com.arcade.model;

import com.arcade.Main;
import com.arcade.entity.GameResult;
import com.arcade.util.DatabaseManager;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class HanoiGame implements Game {

    private static final int MIN_DISKS = 3;
    private static final int MAX_DISKS = 8;
    private static final int DEFAULT_DISKS = 3;
    
    private int numDisks = DEFAULT_DISKS;
    private Stack<Integer>[] towers;
    private Pane gamePane;
    private Label statusLabel;
    private Label movesLabel;
    private int moves = 0;
    private boolean solved = false;
    private List<int[]> moveHistory;
    
    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Torres de Hanói");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        HBox topBox = new HBox(10);
        topBox.setAlignment(Pos.CENTER);
        topBox.getChildren().add(titleLabel);
        
        VBox controlBox = new VBox(15);
        controlBox.setAlignment(Pos.CENTER);
        
        Label disksLabel = new Label("Número de discos: " + numDisks);
        
        Slider disksSlider = new Slider(MIN_DISKS, MAX_DISKS, DEFAULT_DISKS);
        disksSlider.setShowTickMarks(true);
        disksSlider.setShowTickLabels(true);
        disksSlider.setMajorTickUnit(1);
        disksSlider.setMinorTickCount(0);
        disksSlider.setSnapToTicks(true);
        
        disksSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            numDisks = newVal.intValue();
            disksLabel.setText("Número de discos: " + numDisks);
            resetGame();
        });
        
        Button solveButton = new Button("Resolver");
        solveButton.setPrefWidth(150);
        
        movesLabel = new Label("Movimientos: 0");
        
        solveButton.setOnAction(e -> {
            resetGame();
            moveHistory = new ArrayList<>();
            solveTowers(numDisks, 0, 2, 1);
            
            animateSolution(stage);
        });
        
        Button resetButton = new Button("Reiniciar");
        resetButton.setPrefWidth(150);
        
        resetButton.setOnAction(e -> {
            resetGame();
        });
        
        Button backButton = new Button("Volver al Menú");
        backButton.setPrefWidth(150);
        
        backButton.setOnAction(e -> {
            new Main().start(stage);
        });
        
        statusLabel = new Label("Selecciona el número de discos y presiona Resolver");
        
        controlBox.getChildren().addAll(
            disksLabel,
            disksSlider,
            solveButton,
            resetButton,
            movesLabel,
            statusLabel,
            backButton
        );
        
        gamePane = new Pane();
        gamePane.setPrefSize(600, 400);
        
        resetGame();
        
        root.setTop(topBox);
        root.setCenter(gamePane);
        root.setRight(controlBox);
        
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Torres de Hanói - Arcade de Juegos Lógicos");
        stage.setScene(scene);
        stage.show();
    }
    
    @SuppressWarnings("unchecked")
    private void resetGame() {
        gamePane.getChildren().clear();
        
        towers = new Stack[3];
        for (int i = 0; i < 3; i++) {
            towers[i] = new Stack<>();
        }
        
        for (int i = numDisks; i > 0; i--) {
            towers[0].push(i);
        }
        
        moves = 0;
        solved = false;
        movesLabel.setText("Movimientos: 0");
        statusLabel.setText("Selecciona el número de discos y presiona Resolver");
        
        drawTowers();
    }
    
    private void drawTowers() {
        gamePane.getChildren().clear();
        
        double width = gamePane.getPrefWidth();
        double height = gamePane.getPrefHeight();
        
        double towerWidth = 10;
        double towerHeight = height * 0.6;
        double baseHeight = 20;
        double baseWidth = width * 0.8;
        
        double towerSpacing = width / 4;
        double diskMaxWidth = towerSpacing * 0.8;
        double diskHeight = towerHeight / (numDisks + 2);
        
        Rectangle base = new Rectangle(width * 0.1, height * 0.7, baseWidth, baseHeight);
        base.setFill(Color.BROWN);
        gamePane.getChildren().add(base);
        
        for (int i = 0; i < 3; i++) {
            double towerX = width * 0.1 + towerSpacing * (i + 0.5) - towerWidth / 2;
            double towerY = height * 0.7 - towerHeight;
            
            Rectangle tower = new Rectangle(towerX, towerY, towerWidth, towerHeight);
            tower.setFill(Color.BLACK);
            gamePane.getChildren().add(tower);
            
            Stack<Integer> towerStack = towers[i];
            for (int j = 0; j < towerStack.size(); j++) {
                int diskSize = towerStack.get(j);
                double diskWidth = diskMaxWidth * diskSize / numDisks;
                
                double diskX = towerX + towerWidth / 2 - diskWidth / 2;
                double diskY = height * 0.7 - (j + 1) * diskHeight;
                
                Rectangle disk = new Rectangle(diskX, diskY, diskWidth, diskHeight);
                disk.setFill(getColorForDisk(diskSize));
                disk.setStroke(Color.BLACK);
                gamePane.getChildren().add(disk);
            }
        }
    }
    
    private Color getColorForDisk(int size) {
        double hue = (size * 360.0 / numDisks) % 360;
        return Color.hsb(hue, 0.8, 0.8);
    }
    
    private void solveTowers(int n, int source, int target, int auxiliary) {
        if (n > 0) {
            solveTowers(n - 1, source, auxiliary, target);
            moveHistory.add(new int[]{source, target});
            solveTowers(n - 1, auxiliary, target, source);
        }
    }
    
    private void animateSolution(Stage stage) {
        if (moveHistory.isEmpty()) {
            statusLabel.setText("No hay movimientos para animar");
            return;
        }
        
        Button stopButton = new Button("Detener Animación");
        stopButton.setPrefWidth(150);
        
        VBox controlBox = (VBox) ((BorderPane) stage.getScene().getRoot()).getRight();
        controlBox.getChildren().add(2, stopButton);
        
        PauseTransition[] animations = new PauseTransition[moveHistory.size()];
        
        for (int i = 0; i < moveHistory.size(); i++) {
            int[] move = moveHistory.get(i);
            int source = move[0];
            int target = move[1];
            
            animations[i] = new PauseTransition(Duration.seconds(0.5));
            final int moveIndex = i;
            
            animations[i].setOnFinished(e -> {
                if (!towers[source].isEmpty()) {
                    int disk = towers[source].pop();
                    towers[target].push(disk);
                    moves++;
                    movesLabel.setText("Movimientos: " + moves);
                    drawTowers();
                    
                    if (moveIndex == moveHistory.size() - 1) {
                        solved = true;
                        statusLabel.setText("¡Torres resueltas en " + moves + " movimientos!");
                        saveResult();
                        controlBox.getChildren().remove(stopButton);
                    }
                }
            });
        }
        
        for (int i = 0; i < animations.length - 1; i++) {
            final int nextIndex = i + 1;
            int finalI = i;
            animations[i].setOnFinished(e -> {
                if (!towers[moveHistory.get(finalI)[0]].isEmpty()) {
                    int disk = towers[moveHistory.get(finalI)[0]].pop();
                    towers[moveHistory.get(finalI)[1]].push(disk);
                    moves++;
                    movesLabel.setText("Movimientos: " + moves);
                    drawTowers();
                    
                    animations[nextIndex].play();
                }
            });
        }
        
        if (animations.length > 0) {
            final int lastIndex = animations.length - 1;
            animations[lastIndex].setOnFinished(e -> {
                if (!towers[moveHistory.get(lastIndex)[0]].isEmpty()) {
                    int disk = towers[moveHistory.get(lastIndex)[0]].pop();
                    towers[moveHistory.get(lastIndex)[1]].push(disk);
                    moves++;
                    movesLabel.setText("Movimientos: " + moves);
                    drawTowers();
                    
                    solved = true;
                    statusLabel.setText("¡Torres resueltas en " + moves + " movimientos!");
                    saveResult();
                    controlBox.getChildren().remove(stopButton);
                }
            });
            
            animations[0].play();
            
            stopButton.setOnAction(e -> {
                for (PauseTransition animation : animations) {
                    animation.stop();
                }
                controlBox.getChildren().remove(stopButton);
                statusLabel.setText("Animación detenida");
            });
        }
    }
    
    @Override
    public void saveResult() {
        GameResult result = new GameResult();
        result.setGameType("Hanoi");
        result.setParameter(String.valueOf(numDisks));
        result.setSteps(moves);
        result.setCompleted(solved);
        
        DatabaseManager.getInstance().saveGameResult(result);
    }
    
    @Override
    public String getName() {
        return "Torres de Hanói";
    }
}
