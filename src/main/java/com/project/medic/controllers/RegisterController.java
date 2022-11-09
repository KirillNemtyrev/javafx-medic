package com.project.medic.controllers;

import com.google.gson.Gson;
import com.project.medic.Run;
import com.project.medic.config.Config;
import com.project.medic.dto.SignUpDTO;
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
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class RegisterController extends Application {

    @FXML
    private Button btnNoConnect;

    @FXML
    private Button buttonAuth;

    @FXML
    private Button buttonRegister;

    @FXML
    private Group groupNoConnect;

    @FXML
    private ImageView imageClose;

    @FXML
    private ImageView imageCollapse;

    @FXML
    private ImageView imageInfo;

    @FXML
    private Label labelAuth;

    @FXML
    private Label labelInfo;

    @FXML
    private Label labelLenghtLogin;

    @FXML
    private Label labelNoConnect;

    @FXML
    private Pane paneInfo;

    @FXML
    private Pane paneMain;

    @FXML
    private Pane paneSuccessful;

    @FXML
    private PasswordField passwordFieldConfirm;

    @FXML
    private PasswordField passwordFieldNew;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldLogin;

    private double offsetPosX;
    private double offsetPosY;

    private Image imageError;
    private Image imageInformation;

    private Timer timerNoConnect;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Run.class.getResource("scene/register.fxml"));
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
        String path = Objects.requireNonNull(Run.class.getResource("images/info.png")).toURI().toString();
        imageInformation = new Image(path);

        path = Objects.requireNonNull(Run.class.getResource("images/error.png")).toURI().toString();
        imageError = new Image(path);

        // Keyboard
        keyboardOnEmailField();
        keyboardOnLoginField();
        keyboardOnPasswordsField();

        // Mouse on buttons
        mouseOnButtonAuth();
        mouseOnButtonRegister();

        // Mouse on labels
        mouseOnLabelAuth();


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
     */
    @FXML
    public void sendNotify(String text, boolean error){
        imageInfo.setImage(error ? imageError : imageInformation);
        labelInfo.setText(text);
        paneInfo.setVisible(true);
    }

    /**
     *
     */
    @FXML
    public void keyboardOnEmailField(){

        textFieldEmail.textProperty().addListener((observableValue, oldValue, newValue) -> {

            if(!newValue.matches("^[-\\w.]+@([A-z\\d][-A-z\\d]+\\.)+[A-z]{2,4}$")){

                textFieldEmail.setStyle("-fx-background-color: white; -fx-border-color: red");
                sendNotify("Неверный формат E-mail!", false);
                return;

            }

            paneInfo.setVisible(false);
            textFieldEmail.setStyle("-fx-background-color: white");
        });
    }

    /**
     *
     */
    @FXML
    public void keyboardOnLoginField(){

        textFieldLogin.textProperty().addListener((observableValue, oldValue, newValue) -> {

            labelLenghtLogin.setText(newValue.length() + "/40");
            labelLenghtLogin.setTextFill(Paint.valueOf(newValue.length() >= 6 && newValue.length() <= 40 ? "#218f2d" : "#ffaa00"));

            if(!newValue.matches("^[a-zA-Z\\d]{6,40}+$")){

                textFieldLogin.setStyle("-fx-background-color: white; -fx-border-color: red");
                sendNotify("Неверный формат логина!", false);
                return;

            }

            paneInfo.setVisible(false);
            textFieldLogin.setStyle("-fx-background-color: white");
        });
    }

    /**
     * 
     */
    @FXML
    public void keyboardOnPasswordsField(){
        // Field new password
        passwordFieldNew.textProperty().addListener(((observableValue, oldValue, newValue) -> {

            if(newValue.isEmpty()){
                passwordFieldNew.setStyle("-fx-background-color: white; -fx-border-color: red");
                sendNotify("Пароль пустой!", false);
                return;
            }

            if(!newValue.matches("(?=^.{6,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")){
                passwordFieldNew.setStyle("-fx-background-color: white; -fx-border-color: red");
                sendNotify("Неверный формат пароля!", false);
                return;
            }

            passwordFieldNew.setStyle("-fx-background-color: white");
            paneInfo.setVisible(false);

        }));
        // Field Confirm password
        passwordFieldConfirm.textProperty().addListener(((observableValue, oldValue, newValue) -> {

            if(!passwordFieldNew.getText().equals(newValue)){
                passwordFieldConfirm.setStyle("-fx-background-color: white; -fx-border-color: red");
                sendNotify("Пароли не совпадают!", false);
                return;
            }

            if(!newValue.matches("(?=^.{6,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")){
                passwordFieldConfirm.setStyle("-fx-background-color: white; -fx-border-color: red");
                sendNotify("Неверный формат пароля!", false);
                return;
            }

            passwordFieldConfirm.setStyle("-fx-background-color: white");
            paneInfo.setVisible(false);

        }));
    }

    /**
     * 
     */
    @FXML
    public void register(){

        if(!isConnection()) {
            drawNoConnection();
            return;
        }

        if(!textFieldEmail.getText().matches("^[-\\w.]+@([A-z\\d][-A-z\\d]+\\.)+[A-z]{2,4}$")){
            textFieldEmail.setStyle("-fx-background-color: white; -fx-border-color: red");
            sendNotify("Неверный формат E-mail!", false);
            return;
        }

        if(!textFieldLogin.getText().matches("^[a-zA-Z\\d]{6,40}+$")){
            textFieldLogin.setStyle("-fx-background-color: white; -fx-border-color: red");
            sendNotify("Неверный формат логина!", false);
            return;
        }

        if(!passwordFieldNew.getText().matches("(?=^.{6,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")){
            passwordFieldNew.setStyle("-fx-background-color: white; -fx-border-color: red");
            sendNotify("Неверный формат пароля!", false);
            return;
        }

        if(!passwordFieldNew.getText().equals(passwordFieldConfirm.getText())){
            passwordFieldConfirm.setStyle("-fx-background-color: white; -fx-border-color: red");
            sendNotify("Пароли не совпадают!", false);
            return;
        }

        String email = textFieldEmail.getText();
        String username = textFieldLogin.getText();
        String name = textFieldLogin.getText();
        String password = passwordFieldConfirm.getText();

        SignUpDTO sign = new SignUpDTO(email, username, name, password);
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            
            HttpPost request = new HttpPost(Config.url + Config.host + ":" + Config.port + "/api/auth/signup");
            request.setHeader("content-type", "application/json");
            request.setEntity(new StringEntity(new Gson().toJson(sign), "UTF-8"));

            CloseableHttpResponse response = client.execute(request);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuilder stringBuffer = new StringBuilder();
            String lineForBuffer = "";

            while ((lineForBuffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(lineForBuffer);
            }

            switch (stringBuffer.toString()){
                case "Email Address already in use!":
                    sendNotify("Данный e-mail занят!", true);
                    break;
                case "Username is already taken!" :
                    sendNotify("Данный логин занят!", true);
                    break;
                case "User registered successfully":
                    paneMain.setVisible(false);
                    paneInfo.setVisible(false);

                    paneSuccessful.setVisible(true);
                    break;
                default: sendNotify("Ошибка! Попробуйте позже.", true);
            }
        } catch (IOException e) {
            sendNotify("Произошла ошибка!", true);
        }
        
    }

    /**
     * Function to track mouse events for the login button.
     */
    @FXML
    public void mouseOnButtonAuth(){
        buttonAuth.setOnMouseEntered(event -> buttonAuth.setStyle("-fx-background-color: gray"));
        buttonAuth.setOnMouseExited(event -> buttonAuth.setStyle("-fx-background-color: white"));
        buttonAuth.setOnMouseClicked(event -> {
            try {
                new AuthController().start((Stage) buttonAuth.getScene().getWindow());
            } catch (IOException e) {
                sendNotify("Произошла ошибка!", true);
            }

        });
    }

    /**
     * Function to track mouse events for the register button.
     */
    @FXML
    public void mouseOnButtonRegister(){
        buttonRegister.setOnMouseEntered(event -> buttonRegister.setStyle("-fx-background-color: gray"));
        buttonRegister.setOnMouseExited(event -> buttonRegister.setStyle("-fx-background-color: white"));
        buttonRegister.setOnMouseClicked(event -> register());
    }

    /**
     * Function to track mouse events for the auth label.
     */
    @FXML
    public void mouseOnLabelAuth(){
        labelAuth.setOnMouseEntered(event -> labelAuth.setTextFill(Paint.valueOf("#215790")));
        labelAuth.setOnMouseExited(event -> labelAuth.setTextFill(Paint.valueOf("#6198d3")));
        labelAuth.setOnMouseClicked(event -> {
            try {
                new AuthController().start((Stage) buttonAuth.getScene().getWindow());
            } catch (IOException e) {
                sendNotify("Произошла ошибка!", true);
            }
        });
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
    private class TaskTimer extends TimerTask {

        private int countdown;

        public TaskTimer(){
            this.countdown = 30;

            btnNoConnect.setOnMouseEntered(event -> btnNoConnect.setTextFill(Paint.valueOf("gray")));
            btnNoConnect.setOnMouseExited(event -> btnNoConnect.setTextFill(Paint.valueOf("white")));

            btnNoConnect.setOnMouseClicked(event -> {
                this.countdown = 30;
                if(isConnection()){
                    groupNoConnect.setVisible(false);
                    timerNoConnect.cancel();
                    timerNoConnect = null;
                }
            });
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

            Platform.runLater(() -> labelNoConnect.setText(String.format("%02d:%02d", (this.countdown / 60), (this.countdown % 60))));
        }

    }

}
