package com.project.medic.controllers;

import com.project.medic.Run;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class StartController  extends Application {

    @FXML
    private Button buttonNext;

    @FXML
    private ImageView imageClose;

    @FXML
    private ImageView imageCollapse;

    @FXML
    private ImageView imageCopyBrowser;

    @FXML
    private ImageView imageCopyDriver;

    @FXML
    private ImageView imageInfo;

    @FXML
    private Label labelBrowser;

    @FXML
    private Label labelDriver;

    @FXML
    private Label labelInfo;

    @FXML
    private Pane paneInfo;

    private double offsetPosX;
    private double offsetPosY;

    private Image imageError;
    private Image imageInformation;

    private Timer timer;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Run.class.getResource("scene/start.fxml"));
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

        // Mouse
        mouseOnBrowser();
        mouseOnDriver();
        mouseOnButton();

        // Timer
        timer = new Timer();
        timer.schedule(new TaskTimer(), 1000, 1000);

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
     *
     */
    @FXML
    public void mouseOnBrowser(){
        String URL = "https://www.google.com/intl/ru_ru/chrome/";

        labelBrowser.setOnMouseEntered(event -> labelBrowser.setTextFill(Paint.valueOf("#597ca1")));
        labelBrowser.setOnMouseExited(event -> labelBrowser.setTextFill(Paint.valueOf("#6198d3")));
        labelBrowser.setOnMouseClicked(event -> {
            try {
                Desktop.getDesktop().browse(URI.create(URL));
            } catch (IOException e) {
                sendNotify("Произошла ошибка", true);
            }
        });

        imageCopyBrowser.setOnMouseEntered(event -> imageCopyBrowser.setOpacity(1.0));
        imageCopyBrowser.setOnMouseExited(event -> imageCopyBrowser.setOpacity(0.75));
        imageCopyBrowser.setOnMouseClicked(event -> {
            StringSelection stringSelection = new StringSelection(URL);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            sendNotify("Ссылка на браузер скопирована!", false);
        });
    }

    /**
     *
     */
    @FXML
    public void mouseOnDriver(){
        String URL = "https://chromedriver.chromium.org/downloads";

        labelDriver.setOnMouseEntered(event -> labelDriver.setTextFill(Paint.valueOf("#597ca1")));
        labelDriver.setOnMouseExited(event -> labelDriver.setTextFill(Paint.valueOf("#6198d3")));
        labelDriver.setOnMouseClicked(event -> {
            try {
                Desktop.getDesktop().browse(URI.create(URL));
            } catch (IOException e) {
                sendNotify("Произошла ошибка", true);
            }
        });

        imageCopyDriver.setOnMouseEntered(event -> imageCopyDriver.setOpacity(1.0));
        imageCopyDriver.setOnMouseExited(event -> imageCopyDriver.setOpacity(0.75));
        imageCopyDriver.setOnMouseClicked(event -> {
            StringSelection stringSelection = new StringSelection(URL);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            sendNotify("Ссылка на драйвер скопирована!", false);
        });
    }

    /**
     *
     */
    @FXML
    public void mouseOnButton(){
        buttonNext.setOnMouseEntered(event -> buttonNext.setStyle("-fx-background-color: gray"));
        buttonNext.setOnMouseExited(event -> buttonNext.setStyle("-fx-background-color: white"));
        buttonNext.setOnMouseClicked(event -> {
            try {
                new DriverController().start((Stage) buttonNext.getScene().getWindow());
            } catch (IOException e) {
                sendNotify("Произошла ошибка!", true);
            }
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

    public class TaskTimer extends TimerTask{

        private int countdown = 1;

        @Override
        public void run(){
            this.countdown--;
            Platform.runLater(() -> {

                if(this.countdown <= 0){
                    timer.cancel();
                    timer = null;
                }

                buttonNext.setText(this.countdown <= 0 ? "Дальше" : String.format("Дальше (%d)", this.countdown));
                buttonNext.setDisable(!(this.countdown <= 0));
            });
        }

    }
}
