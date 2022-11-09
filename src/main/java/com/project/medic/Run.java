package com.project.medic;

import com.project.medic.config.Config;
import com.project.medic.controllers.AuthController;
import com.project.medic.controllers.StartController;
import com.project.medic.entity.ConfigEntity;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URISyntaxException;

public class Run  extends Application {

    @Override
    public void start(Stage stage) throws URISyntaxException, IOException {

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.setTitle("Medic Helper");
        //stage.getIcons().add(new Image(Objects.requireNonNull(Run.class.getResource("images/icon/icon.png")).toURI().toString()));

        Config config = new Config();
        ConfigEntity configEntity = config.getData();

        if(configEntity == null || !configEntity.isStart()){
            new StartController().start(stage);
        } else {
            new AuthController().start(stage);
        }
    }

    public static void main(String[] args){
        launch();
    }
}
