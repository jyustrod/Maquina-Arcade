package com.arcade.facade;

import com.arcade.entity.GameResult;
import com.arcade.model.Game;
import com.arcade.util.DatabaseManager;
import javafx.stage.Stage;

public class GameFacade {
    
    private final Game game;
    
    public GameFacade(Game game) {
        this.game = game;
    }
    
    public void startGame(Stage stage) {
        game.start(stage);
    }
    
    public void saveGameResult(String parameter, int steps, boolean completed) {
        GameResult result = new GameResult();
        result.setGameType(game.getName());
        result.setParameter(parameter);
        result.setSteps(steps);
        result.setCompleted(completed);
        
        DatabaseManager.getInstance().saveGameResult(result);
    }
}
