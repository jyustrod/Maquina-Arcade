package com.arcade.factory;

import com.arcade.model.Game;
import com.arcade.model.HanoiGame;
import com.arcade.model.KnightTourGame;
import com.arcade.model.NQueensGame;

public class GameFactory {
    
    public Game createGame(String gameType) {
        switch (gameType.toLowerCase()) {
            case "nqueens":
                return new NQueensGame();
            case "knighttour":
                return new KnightTourGame();
            case "hanoi":
                return new HanoiGame();
            default:
                return null;
        }
    }
}
