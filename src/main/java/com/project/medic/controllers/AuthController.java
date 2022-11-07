package com.project.medic.controllers;

import com.project.medic.Run;
import com.project.medic.config.Config;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class AuthController extends Application {

    @FXML
    private Button btnNoConnect;

    @FXML
    private Button buttonAuth;

    @FXML
    private Group groupNoConnect;

    @FXML
    private ImageView imageClose;

    @FXML
    private ImageView imageCollapse;

    @FXML
    private ImageView imageInfo;

    @FXML
    private Label labelInfo;

    @FXML
    private Label labelNoConnect;

    @FXML
    private Label labelRecovery;

    @FXML
    private Label labelRegister;

    @FXML
    private Pane paneAuth;

    @FXML
    private Pane paneInfo;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField textFieldEmail;

    private double offsetPosX;
    private double offsetPosY;

    private Image imageError;
    private Image imageInformation;

    private Timer timerNoConnect;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Run.class.getResource("scene/auth.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 700);
        stage.setScene(scene);
        stage.show();

        scene.setOnMousePressed(event -> {
            offsetPosX = stage.getX() - event.getScreenX();
            offsetPosY = stage.getY() - event.getScreenY();
        });
        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + offsetPosX);
            stage.setY(event.getScreenY() + offsetPosY);
        });

    }

    @FXML
    void initialize() throws URISyntaxException {

        // Top panel
        actionWithCloseImg();
        actionWithCollapseImg();

        // For notify
        String path = Run.class.getResource("images/info.png").toURI().toString();
        imageInformation = new Image(path);

        path = Run.class.getResource("images/error.png").toURI().toString();
        imageError = new Image(path);

        // Check connection
        if (!isConnection()) drawNoConnection();

    }

    /**
     * A function to interact with the minimize button.
     */
    @FXML
    public void actionWithCloseImg() {

        imageClose.setOnMouseEntered(event -> imageClose.setOpacity(1.0));
        imageClose.setOnMouseExited(event -> imageClose.setOpacity(0.5));
        imageClose.setOnMouseClicked(event -> {
            Stage stage = (Stage) imageClose.getScene().getWindow();
            stage.close();

            System.exit(1);
        });

    }

    /**
     * A function to interact with the close button.
     */
    @FXML
    public void actionWithCollapseImg(){

        imageCollapse.setOnMouseEntered(event -> imageCollapse.setOpacity(1.0));
        imageCollapse.setOnMouseExited(event -> imageCollapse.setOpacity(0.5));
        imageCollapse.setOnMouseClicked(event -> {
            Stage stage = (Stage) imageCollapse.getScene().getWindow();
            stage.setIconified(true);
        });

    }

    /**
     * Function to display information.
     * @param text
     * @param error
     */
    @FXML
    public void sendNotify(String text, boolean error){

        imageInfo.setImage(error ? imageError : imageInformation);
        labelInfo.setText(text);

    }

    /**
     * A function to check the connection to the server.
     * @return result connect
     */
    public boolean isConnection(){

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(Config.host, Config.port), 10);
            return true;
        } catch (IOException e) {

            drawNoConnection();
            return false;
        }

    }

    /**
     * The function is called when there is no internet connection.
     */
    @FXML
    public void drawNoConnection(){
        groupNoConnect.setVisible(true);
        if(timerNoConnect != null){
            timerNoConnect.cancel();
            timerNoConnect = null;
        }

        timerNoConnect = new Timer();
        timerNoConnect.schedule(new TaskTimer(), 1000, 1000);
    }

    /**
     * The class is used to create a timer.
     */
    private class TaskTimer extends TimerTask{

        private int countdown;

        public TaskTimer(){
            this.countdown = 30;
        }

        @Override
        public void run() {

            this.countdown -= 1;
            if(this.countdown <= 0 && isConnection()) {

                groupNoConnect.setVisible(false);
                timerNoConnect.cancel();
                timerNoConnect = null;

            } else if(this.countdown <= 0) {
                this.countdown = 30;
            }

            Platform.runLater(() -> labelNoConnect.setText(String.format("%0.2f:%0.2f", (this.countdown / 60), (this.countdown % 60))));
        }

    }

}
