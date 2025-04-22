package com.arcade.builder;

import com.arcade.decorator.LoggingGameDecorator;
import com.arcade.facade.GameFacade;
import com.arcade.model.Game;
import com.arcade.model.HanoiGame;
import com.arcade.model.KnightTourGame;
import com.arcade.model.NQueensGame;

public class GameBuilder {
    
    private Game game;
    private boolean enableLogging = false;
    private boolean useFacade = false;
    
    public GameBuilder withGame(String gameType) {
        switch (gameType.toLowerCase()) {
            case "nqueens":
                game = new NQueensGame();
                break;
            case "knighttour":
                game = new KnightTourGame();
                break;
            case "hanoi":
                game = new HanoiGame();
                break;
            default:
                throw new IllegalArgumentException("Tipo de juego no válido: " + gameType);
        }
        return this;
    }
    
    public GameBuilder withLogging(boolean enableLogging) {
        this.enableLogging = enableLogging;
        return this;
    }
    
    public GameBuilder withFacade(boolean useFacade) {
        this.useFacade = useFacade;
        return this;
    }
    
    public Object build() {
        if (game == null) {
            throw new IllegalStateException("Debe especificar un tipo de juego");
        }
        
        if (enableLogging) {
            game = new LoggingGameDecorator(game);
        }
        
        if (useFacade) {
            return new GameFacade(game);
        }
        
        return game;
    }
}
