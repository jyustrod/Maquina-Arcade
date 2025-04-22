package com.arcade.decorator;

import com.arcade.model.Game;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingGameDecorator extends GameDecorator {
    
    public LoggingGameDecorator(Game decoratedGame) {
        super(decoratedGame);
    }
    
    @Override
    public void start(Stage stage) {
        System.out.println(getTimestamp() + " - Iniciando juego: " + decoratedGame.getName());
        super.start(stage);
    }
    
    @Override
    public void saveResult() {
        System.out.println(getTimestamp() + " - Guardando resultado de: " + decoratedGame.getName());
        super.saveResult();
    }
    
    private String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
