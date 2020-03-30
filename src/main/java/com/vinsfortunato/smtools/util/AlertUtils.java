package com.vinsfortunato.smtools.util;

import com.vinsfortunato.smtools.Main;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

public class AlertUtils {
    public static void error(String name) {
        alert(Alert.AlertType.ERROR, name);
    }

    public static void warn(String name) {
        alert(Alert.AlertType.WARNING, name);
    }

    public static void info(String name) {
        alert(Alert.AlertType.INFORMATION, name);
    }

    public static void confirm(String name, Runnable onConfirm) {
        Optional<ButtonType> result = alert(Alert.AlertType.CONFIRMATION, name);
        if (result.isPresent() && result.get() == ButtonType.OK && onConfirm != null) {
            onConfirm.run();
        }
    }

    public static Optional<String> input(String name, String def) {
        TextInputDialog dialog = new TextInputDialog(def);
        dialog.setTitle(getTitle(name));
        dialog.setHeaderText(getHeader(name));
        dialog.setContentText(getContent(name));
        return dialog.showAndWait();
    }

    public static Optional<ButtonType> alert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    public static Optional<ButtonType> alert(Alert.AlertType type, String name) {
        Alert alert = new Alert(type);
        alert.setTitle(getTitle(name));
        alert.setHeaderText(getHeader(name));
        alert.setContentText(getContent(name));
        return alert.showAndWait();
    }

    public static void exception(String name, Throwable e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(getTitle(name));
        alert.setHeaderText(getHeader(name));
        alert.setContentText(getContent(name));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label(Main.getLang().getString("alert.exception.label"));

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    public static void longMessage(Alert.AlertType type, String title, String header, String content, String longMessage) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        TextArea textArea = new TextArea(longMessage);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    public static void longMessage(String name, String longMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(getTitle(name));
        alert.setHeaderText(getHeader(name));
        alert.setContentText(getContent(name));

        TextArea textArea = new TextArea(longMessage);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    private static String getTitle(String name) {
        return Main.getLang().getString("alert." + name + ".title");
    }

    private static String getHeader(String name) {
        return Main.getLang().getString("alert." + name + ".header");
    }

    private static String getContent(String name) {
        return Main.getLang().getString("alert." + name + ".content");
    }
}