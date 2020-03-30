/*
 * Copyright [2020] [Vincenzo Fortunato]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.vinsfortunato.smtools;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
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
        stage.setTitle(MessageFormat.format(getLang().getString("main.stage.title"), Version.getVersion()));
        stage.getIcons().addAll(getIcons());
        stage.setScene(scene);
        stage.setMinWidth(550.0);
        stage.setMinHeight(400.0);
        stage.show();
    }

    @Override
    public void stop() {
        getLogger().info("Disposing resources...");
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

    private static Image[] getIcons() {
        return new Image[] {
                new Image(Main.class.getResourceAsStream("/images/logo48.png")),
                new Image(Main.class.getResourceAsStream("/images/logo32.png"))
        };
    }
}