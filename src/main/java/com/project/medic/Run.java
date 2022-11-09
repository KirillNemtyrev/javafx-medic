package com.project.medic;

import com.project.medic.controllers.AuthController;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class Run  extends Application {

    @Override
    public void start(Stage stage) throws URISyntaxException, IOException {

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.setTitle("Medic Helper");
        //stage.getIcons().add(new Image(Objects.requireNonNull(Run.class.getResource("images/icon/icon.png")).toURI().toString()));

        new AuthController().start(stage);
    }

    public static void main(String[] args){
        launch();
    }
}
