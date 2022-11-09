package com.project.medic.controllers;

import com.project.medic.Run;
import com.project.medic.config.Config;
import com.project.medic.entity.ConfigEntity;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DriverController extends Application {

    @FXML
    private Button buttonAuth;

    @FXML
    private Button buttonCheck;

    @FXML
    private Button buttonFile;

    @FXML
    private ImageView imageClose;

    @FXML
    private ImageView imageCollapse;

    @FXML
    private ImageView imageInfo;

    @FXML
    private Label labelInfo;

    @FXML
    private Label labelPath;

    @FXML
    private Label labelPathInfo;

    @FXML
    private Pane paneInfo;

    @FXML
    private Pane paneMain;

    @FXML
    private Pane paneSuccessful;

    private double offsetPosX;
    private double offsetPosY;

    private Image imageError;
    private Image imageInformation;

    private String path;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Run.class.getResource("scene/driver.fxml"));
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
        mouseOnButtonPath();
        mouseOnButtonCheck();
        mouseOnButtonAuth();

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
    public void mouseOnButtonPath(){
        buttonFile.setOnMouseEntered(event -> buttonFile.setStyle("-fx-background-color: gray"));
        buttonFile.setOnMouseExited(event -> buttonFile.setStyle("-fx-background-color: white"));
        buttonFile.setOnMouseClicked(event -> {

            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(buttonFile.getScene().getWindow());

            if(file == null){
                return;
            }

            if(!file.getPath().endsWith("chromedriver.exe")) {
                sendNotify("Данный файл не является драйвером!", true);
                return;
            }

            labelPath.setText(file.getPath());
            path = file.getPath();

            labelPathInfo.setDisable(false);
            buttonCheck.setDisable(false);
        });
    }

    /**
     *
     */
    @FXML
    public void mouseOnButtonCheck(){
        buttonCheck.setOnMouseEntered(event -> buttonCheck.setStyle("-fx-background-color: gray"));
        buttonCheck.setOnMouseExited(event -> buttonCheck.setStyle("-fx-background-color: white"));
        buttonCheck.setOnMouseClicked(event -> {
            paneMain.setDisable(true);
            sendNotify("Пожалуйста подождите", false);

            ExecutorService service = Executors.newFixedThreadPool(3);
            service.execute(() -> {
                try {
                    System.setProperty("webdriver.chrome.driver", path);

                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--headless");
                    ChromeDriver driver = new ChromeDriver(chromeOptions);
                    driver.get("https://selftest.mededtech.ru/");
                    driver.quit();

                    paneMain.setVisible(false);
                    paneInfo.setVisible(false);
                    paneSuccessful.setVisible(true);

                    ConfigEntity configEntity = new ConfigEntity();
                    configEntity.setPathToDriver(path);
                    configEntity.setStart(true);

                    Config config = new Config();
                    config.setData(configEntity);

                } catch (WebDriverException exception) {
                    paneMain.setDisable(false);
                    Platform.runLater(() -> sendNotify("Неверно указан путь до драйвера!", true));
                }
            });
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

}
