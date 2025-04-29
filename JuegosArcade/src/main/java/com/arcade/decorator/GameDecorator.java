package com.arcade.decorator;

import com.arcade.model.Game;
import javafx.stage.Stage;

public abstract class GameDecorator implements Game {
    
    protected Game decoratedGame;
    
    public GameDecorator(Game decoratedGame) {
        this.decoratedGame = decoratedGame;
    }
    
    @Override
    public void start(Stage stage) {
        decoratedGame.start(stage);
    }
    
    @Override
    public void saveResult() {
        decoratedGame.saveResult();
    }
    
    @Override
    public String getName() {
        return decoratedGame.getName();
    }
}
