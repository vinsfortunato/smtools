package com.vinsfortunato.smtools;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
    private static Stage stage;
    private static Logger logger;
    private static ResourceBundle lang;
    private static ExecutorService executor;

    public static void main(String[]args) {
        //Setup logger
        logger = LogManager.getLogger(Main.class);

        //Load localized resource bundle
        lang = ResourceBundle.getBundle("bundles.lang", Locale.getDefault());

        //Create a single thread executor for task execution
        executor = Executors.newSingleThreadExecutor();

        //Launch application
        getLogger().info("Launching application...");
        launch(args);
        getLogger().info("Application closed. Bye!");
    }

    @Override
    public void start(Stage stage) throws Exception {
        Main.stage = stage;

        //Load main layout
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/main.fxml"));
        loader.setResources(lang);
        Parent root = loader.load();

        //Setup scene
        Scene scene = new Scene(root, 600, 400);

        //Setup and show stage
        stage.setTitle(getLang().getString("main.stage.title"));
        stage.setScene(scene);
        stage.setMinWidth(550.0);
        stage.setMinHeight(400.0);
        stage.show();
    }


    @Override
    public void stop() {
        getLogger().info("Stopping application...");
        executor.shutdownNow();
    }

    public static ResourceBundle getLang() {
        return lang;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static Stage getStage() {
        return stage;
    }

    public static Executor getExecutor() {
        return executor;
    }
}