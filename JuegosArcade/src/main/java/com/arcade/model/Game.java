package com.arcade.model;

import javafx.stage.Stage;

public interface Game {
    void start(Stage stage);
    void saveResult();
    String getName();
}
