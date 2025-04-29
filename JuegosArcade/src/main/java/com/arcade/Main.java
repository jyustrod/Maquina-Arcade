package com.arcade;

import com.arcade.factory.GameFactory;
import com.arcade.model.Game;
import com.arcade.util.DatabaseManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application {

    private static final String TITLE = "Arcade de Juegos Lógicos";
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    
    private final GameFactory gameFactory = new GameFactory();
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        
        Label titleLabel = new Label(TITLE);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        VBox gameButtons = new VBox(15);
        gameButtons.setAlignment(Pos.CENTER);
        
        Button nQueensButton = createGameButton("N-Reinas", "nqueens", primaryStage);
        Button knightTourButton = createGameButton("Recorrido del Caballo", "knighttour", primaryStage);
        Button hanoiButton = createGameButton("Torres de Hanói", "hanoi", primaryStage);
        
        gameButtons.getChildren().addAll(
            nQueensButton,
            knightTourButton,
            hanoiButton
        );
        
        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().addAll(titleLabel, gameButtons);
        
        root.setCenter(centerBox);
        
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        DatabaseManager.getInstance().initializeDatabase();
    }
    
    private Button createGameButton(String text, String gameType, Stage primaryStage) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(50);
        button.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));
        
        button.setOnAction(e -> {
            Game game = gameFactory.createGame(gameType);
            if (game != null) {
                game.start(primaryStage);
            }
        });
        
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void stop() {
        DatabaseManager.getInstance().closeConnection();
    }
}
