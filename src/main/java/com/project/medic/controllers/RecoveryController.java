package com.project.medic.controllers;

import com.google.gson.Gson;
import com.project.medic.Run;
import com.project.medic.config.Config;
import com.project.medic.dto.RecoveryDTO;
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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
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

public class RecoveryController extends Application {

    @FXML
    private Button btnNoConnect;

    @FXML
    private Button buttonAuth;

    @FXML
    private Button buttonRecovery;

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
    private Label labelChangeEmail;

    @FXML
    private Label labelClearCode;

    @FXML
    private Label labelInfo;

    @FXML
    private Label labelNoConnect;

    @FXML
    private Label labelSendCode;

    @FXML
    private Pane paneInfo;

    @FXML
    private Pane paneMain;

    @FXML
    private Pane paneSuccessful;

    @FXML
    private Pane paneWithCode;

    @FXML
    private PasswordField passwordFieldConfirm;

    @FXML
    private PasswordField passwordFieldNew;

    @FXML
    private TextField textFieldCode1;

    @FXML
    private TextField textFieldCode2;

    @FXML
    private TextField textFieldCode3;

    @FXML
    private TextField textFieldCode4;

    @FXML
    private TextField textFieldCode5;

    @FXML
    private TextField textFieldCode6;

    @FXML
    private TextField textFieldEmail;

    private double offsetPosX;
    private double offsetPosY;

    private Image imageError;
    private Image imageInformation;

    private Timer timerNoConnect;

    // Variables for recovery save
    private static Timer timer;
    private static String data;
    private static Long countdownCode = 30L;


    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Run.class.getResource("scene/recovery.fxml"));
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

        // load
        load();

        // Mouse
        mouseOnButtonRecovery();
        mouseOnButtonAuth();

        mouseOnLabelAuth();
        mouseOnLabelSend();
        mouseOnLabelChange();
        mouseOnLabelClear();

        // Keyboard
        keyboardWithCodeFields();
        keyboardWithFieldLoginOrEmail();
        keyboardWithPasswordFields();

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
        paneInfo.setVisible(true);

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
     *
     */
    @FXML
    public void load(){
        textFieldEmail.setText(data);
    }

    /**
     *
     */
    @FXML
    public void drawRecovery(){

        if(!isConnection()) {
            drawNoConnection();
            return;
        }

        labelSendCode.setDisable(true);
        labelChangeEmail.setVisible(true);
        textFieldEmail.setEditable(false);

        paneWithCode.setDisable(false);

        timer = new Timer();
        timer.schedule(new TimerCode(), 1000, 1000);
    }

    /**
     *
     */
    @FXML
    public void mouseOnLabelSend(){
        labelSendCode.setOnMouseEntered(event -> labelSendCode.setTextFill(Paint.valueOf("#215790")));
        labelSendCode.setOnMouseExited(event -> labelSendCode.setTextFill(Paint.valueOf("#6198d3")));
        labelSendCode.setOnMouseClicked(event -> {

            if(!isConnection()) {
                drawNoConnection();
                return;
            }

            if(textFieldEmail.getText() == null || textFieldEmail.getText().isEmpty()) {
                textFieldEmail.setStyle("-fx-background-color: white; -fx-border-color: red");
                sendNotify("Укажите логин или e-mail", false);
                return;
            }

            try (CloseableHttpClient client = HttpClients.createDefault()) {

                RecoveryDTO recovery = new RecoveryDTO(textFieldEmail.getText());
                HttpPost request = new HttpPost(Config.url + Config.host + ":" + Config.port + "/api/recovery");
                request.setHeader("content-type", "application/json");
                request.setEntity(new StringEntity(new Gson().toJson(recovery), "UTF-8"));

                CloseableHttpResponse response = client.execute(request);
                response.close();

                switch (response.getStatusLine().getStatusCode()) {
                    case 401:
                        sendNotify("Пользователь не найден!", true);
                        break;
                    case 200:
                        drawRecovery();
                        sendNotify("Код отправлен на E-mail", false);
                        break;
                    default: sendNotify("Ошибка! Попробуйте позже", true);
                }
                response.close();
            } catch (IOException e) {
                sendNotify("Произошла ошибка!", true);
            }
        });
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

                if(timer != null){
                    timer.cancel();
                    timer = null;
                }
            } catch (IOException e) {
                sendNotify("Произошла ошибка!", true);
            }

        });
    }

    /**
     * Function to track mouse events for the login button.
     */
    @FXML
    public void mouseOnButtonRecovery(){
        buttonRecovery.setOnMouseEntered(event -> buttonAuth.setStyle("-fx-background-color: gray"));
        buttonRecovery.setOnMouseExited(event -> buttonAuth.setStyle("-fx-background-color: white"));
        buttonRecovery.setOnMouseClicked(event -> {

            if(textFieldCode1.getText().isEmpty() || textFieldCode2.getText().isEmpty() || textFieldCode3.getText().isEmpty()
                    || textFieldCode4.getText().isEmpty() || textFieldCode5.getText().isEmpty() || textFieldCode6.getText().isEmpty()){
                sendNotify("Не введён код!", true);
                return;
            }

            if(!passwordFieldNew.getText().matches("(?=^.{6,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")){
                passwordFieldNew.setStyle("-fx-background-color: #080808; -fx-border-color: #6f1010");
                sendNotify("Неверный формат пароля!", true);
                return;
            }

            if(!passwordFieldNew.getText().equals(passwordFieldConfirm.getText())){
                sendNotify("Пароли не совпадают!", true);
                passwordFieldConfirm.setStyle("-fx-background-color: #080808; -fx-border-color: #6f1010");
                return;
            }

            try (CloseableHttpClient client = HttpClients.createDefault()) {

                String code = textFieldCode1.getText() + textFieldCode2.getText() + textFieldCode3.getText() +
                        textFieldCode4.getText() + textFieldCode5.getText() + textFieldCode6.getText();

                RecoveryDTO recovery = new RecoveryDTO(textFieldEmail.getText(), passwordFieldConfirm.getText(), code);
                HttpPatch request = new HttpPatch(Config.url + Config.host + ":" + Config.port + "/api/recovery");
                request.setHeader("content-type", "application/json");
                request.setEntity(new StringEntity(new Gson().toJson(recovery), "UTF-8"));

                CloseableHttpResponse response = client.execute(request);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuilder stringBuffer = new StringBuilder();
                String lineForBuffer = "";

                while ((lineForBuffer = bufferedReader.readLine()) != null) {
                    stringBuffer.append(lineForBuffer);
                }

                switch (stringBuffer.toString()){

                    case "Invalid code entered!":
                        sendNotify("Не верно введён код!", true);
                        break;
                    case "Code time is up!":
                        sendNotify("Время кода истекло!", true);
                        break;
                    case "Password recovered!":
                        paneMain.setVisible(false);
                        paneInfo.setVisible(false);
                        paneSuccessful.setVisible(true);
                        break;
                    default: sendNotify("Ошибка! Попробуйте позже.", true);
                }
            } catch (IOException e) {
                sendNotify("Произошла ошибка.", true);
            }

        });
    }

    /**
     *
     */
    @FXML
    public void mouseOnLabelClear(){

        labelClearCode.setOnMouseEntered(event -> labelClearCode.setTextFill(Paint.valueOf("#215790")));
        labelClearCode.setOnMouseExited(event -> labelClearCode.setTextFill(Paint.valueOf("#6198d3")));
        labelClearCode.setOnMouseClicked(event -> {
            textFieldCode1.clear();
            textFieldCode2.clear();
            textFieldCode3.clear();
            textFieldCode4.clear();
            textFieldCode5.clear();
            textFieldCode6.clear();
        });

    }

    /**
     *
     */
    @FXML
    public void mouseOnLabelChange(){
        labelChangeEmail.setOnMouseEntered(event -> labelChangeEmail.setTextFill(Paint.valueOf("#215790")));
        labelChangeEmail.setOnMouseExited(event -> labelChangeEmail.setTextFill(Paint.valueOf("#6198d3")));
        labelChangeEmail.setOnMouseClicked(event -> {

            if(!isConnection()){
                drawNoConnection();
                return;
            }

            textFieldEmail.setEditable(true);
            textFieldEmail.clear();
            data = null;

            labelChangeEmail.setVisible(false);
            labelSendCode.setDisable(false);
            labelSendCode.setText("Запросить код");

            paneWithCode.setDisable(true);
            paneInfo.setVisible(false);

            passwordFieldNew.setStyle("-fx-background-color: white");
            passwordFieldConfirm.setStyle("-fx-background-color: white");

            textFieldCode1.clear();
            textFieldCode2.clear();
            textFieldCode3.clear();
            textFieldCode4.clear();
            textFieldCode5.clear();
            textFieldCode6.clear();

            passwordFieldNew.clear();
            passwordFieldConfirm.clear();

            if(timer != null){
                timer.cancel();
                timer = null;
            }
        });
    }

    /**
     *
     */
    @FXML
    public void keyboardWithCodeFields(){
        // Action with first field
        textFieldCode1.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.LEFT){
                textFieldCode6.requestFocus();
            }

            if(event.getCode() == KeyCode.RIGHT){
                textFieldCode2.requestFocus();
            }
        });
        textFieldCode1.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.isEmpty()){
                textFieldCode1.setText(textFieldCode1.getText().toUpperCase().substring(0, 1));
                textFieldCode2.requestFocus();
            }
        });
        // Action with second field
        textFieldCode2.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.BACK_SPACE && textFieldCode2.getText().isEmpty()){
                textFieldCode1.requestFocus();
            }

            if(event.getCode() == KeyCode.RIGHT){
                textFieldCode3.requestFocus();
            }
        });

        textFieldCode2.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.isEmpty()){
                textFieldCode2.setText(textFieldCode2.getText().toUpperCase().substring(0, 1));
                textFieldCode3.requestFocus();
            }
        });
        // Action with third field
        textFieldCode3.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.BACK_SPACE && textFieldCode3.getText().isEmpty()){
                textFieldCode2.requestFocus();
            }

            if(event.getCode() == KeyCode.RIGHT){
                textFieldCode4.requestFocus();
            }
        });

        textFieldCode3.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.isEmpty()){
                textFieldCode3.setText(textFieldCode3.getText().toUpperCase().substring(0, 1));
                textFieldCode4.requestFocus();
            }
        });
        // Action with fourth field
        textFieldCode4.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.BACK_SPACE && textFieldCode4.getText().isEmpty()){
                textFieldCode3.requestFocus();
            }

            if(event.getCode() == KeyCode.RIGHT){
                textFieldCode5.requestFocus();
            }
        });

        textFieldCode4.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.isEmpty()){
                textFieldCode4.setText(textFieldCode4.getText().toUpperCase().substring(0, 1));
                textFieldCode5.requestFocus();
            }
        });
        // Action with fifth field
        textFieldCode5.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.BACK_SPACE && textFieldCode5.getText().isEmpty()){
                textFieldCode4.requestFocus();
            }

            if(event.getCode() == KeyCode.RIGHT){
                textFieldCode6.requestFocus();
            }
        });

        textFieldCode5.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.isEmpty()){
                textFieldCode5.setText(textFieldCode5.getText().toUpperCase().substring(0, 1));
                textFieldCode6.requestFocus();
            }
        });

        // Action with six field
        textFieldCode6.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.BACK_SPACE && textFieldCode6.getText().isEmpty()){
                textFieldCode5.requestFocus();
            }

            if(event.getCode() == KeyCode.RIGHT){
                textFieldCode1.requestFocus();
            }
        });

        textFieldCode6.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.isEmpty()){
                textFieldCode6.setText(textFieldCode6.getText().toUpperCase().substring(0, 1));
            }
        });

    }

    /**
     *
     */
    @FXML
    public void keyboardWithFieldLoginOrEmail() {
        textFieldEmail.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.isEmpty() && !newValue.matches("^[a-zA-Z\\d]+$") && !newValue.matches("^[-\\w.]+@([A-z\\d][-A-z\\d]+\\.)+[A-z]{2,4}$")){

                textFieldEmail.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color:red");
                sendNotify("Неверный формат!", false);

            } else {

                data = newValue;
                textFieldEmail.setStyle("-fx-background-color: white; -fx-text-fill: black");
                paneInfo.setVisible(false);

            }
        });
    }
    /**
     *
     */
    @FXML
    public void keyboardWithPasswordFields(){
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
        //
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
    public class TimerCode extends TimerTask {

        @Override
        public void run() {

            Platform.runLater(() -> {

                if (countdownCode-- > 0) {
                    labelSendCode.setText("Повторная отправка через " + countdownCode + " секунд..");
                    return;
                }

                labelSendCode.setText("Запросить код");
                labelSendCode.setDisable(false);

                timer.cancel();
                timer = null;
                countdownCode = 30L;

            });

        }
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
        timerNoConnect.schedule(new TaskTimer(30), 1000, 1000);
    }

    /**
     * The class is used to create a timer.
     */
    private class TaskTimer extends TimerTask {

        private int countdown;

        public TaskTimer(int countdown){
            this.countdown = countdown;
            draw();
        }

        public void draw(){
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
