package com.vinsfortunato.smtools;

import com.vinsfortunato.smtools.util.AlertUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;

public class MainController implements Initializable {
    private ResourceBundle resources;
    private File lastDir;

    @FXML private CheckBox removeCheckBox;
    @FXML private Button incrementButton;
    @FXML private ListView<File> filesListView;
    @FXML private Spinner<Double> incrementSpinner;
    @FXML private Label statusLabel;
    @FXML private Node progressNode;
    @FXML private Label progressLabel;
    @FXML private ProgressBar progressBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        //Setup increment spinner
        DoubleSpinnerValueFactory factory = new DoubleSpinnerValueFactory(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0D);
        factory.setAmountToStepBy(0.001D);
        factory.setConverter(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return String.format(object < 0 ? "%.3f" : "+%.3f", object); //Just 3 digits
            }

            @Override
            public Double fromString(String string) {
                try {
                    return Double.parseDouble(string);
                } catch (NumberFormatException e) {
                    //Do this just to update the editor visually
                    incrementSpinner.getEditor().setText(toString(incrementSpinner.getValue()));
                    return incrementSpinner.getValue();
                }
            }
        });
        incrementSpinner.setValueFactory(factory);
        incrementSpinner.setEditable(true);
        incrementSpinner.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                //on focus lost commit value
                incrementSpinner.getEditor().commitValue();
            }
        });

        //Setup files list view
        filesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        filesListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<File>) c -> {
            incrementButton.setDisable(filesListView.getSelectionModel().getSelectedItems().isEmpty());
        });
    }

    @FXML
    private void onRemoveSelectedButtonAction(ActionEvent event) {
        filesListView.getItems().removeAll(filesListView.getSelectionModel().getSelectedItems());
    }

    @FXML
    private void onSelectAllButtonAction(ActionEvent event) {
        filesListView.getSelectionModel().selectAll();
    }

    @FXML
    private void onDeselectAllButtonAction(ActionEvent event) {
        filesListView.getSelectionModel().clearSelection();
    }

    @FXML
    private void onOpenDirButtonAction(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("main.files.chooser.title");
        if(lastDir != null)
            chooser.setInitialDirectory(lastDir);
        File dir = chooser.showDialog(Main.getStage());
        if(dir != null) {
            lastDir = dir;
            ScanTask task = new ScanTask(dir);
            task.setOnSucceeded(e -> {
                //Update list view items
                filesListView.getItems().clear();
                filesListView.getItems().addAll(task.getValue());
            });
            task.setOnFailed(e -> {
                //Log exception and show alert message
                Exception exception = new Exception(
                        String.format("Exception caught scanning %s", dir.getPath()),
                        task.getException());

                Main.getLogger().error("Cannot scan directory", exception);
                AlertUtils.exception("scan.error", exception);
            });
            Main.getExecutor().execute(task);
        }
    }

    @FXML
    private void onIncrementButtonAction(ActionEvent event) {
        List<File> files = new ArrayList<>(filesListView.getSelectionModel().getSelectedItems());
        Double increment = incrementSpinner.getValue();
        OffsetTask task = new OffsetTask(files, increment);
        task.setOnSucceeded(e -> {
            //Build a string containing all modified files
            List<File> result = task.getValue();
            StringBuilder fileList = new StringBuilder();
            for(File file : result) {
                fileList.append(file.getPath());
                fileList.append("\n");
            }

            //Alert about task success
            AlertUtils.longMessage(
                    Alert.AlertType.INFORMATION,
                    resources.getString("alert.offset.done.title"),
                    MessageFormat.format(resources.getString("alert.offset.done.header"), result.size()),
                    resources.getString("alert.offset.done.content"),
                    fileList.toString());

            //Remove processed files from the list view if option is selected
            if(removeCheckBox.isSelected()) {
                filesListView.getItems().removeAll(task.getProcessedFiles());
            }
            progressNode.setVisible(false);
        });
        task.setOnFailed(e -> {
            //Log exception and show alert message
            Main.getLogger().error("Exception caught running offset task", task.getException());
            AlertUtils.exception("offset.error", task.getException());
            progressNode.setVisible(false);
        });
        task.messageProperty().addListener((observable, oldValue, newValue) -> {
            statusLabel.setText(newValue);
        });
        task.progressProperty().addListener((observable, oldValue, newValue) -> {
            progressBar.setProgress((double) newValue);
            progressLabel.setText(String.format("%d/%d", (int) task.getWorkDone(), (int) task.getTotalWork()));
        });

        progressLabel.setText("");
        progressBar.setProgress(-1);
        progressNode.setVisible(true);
        Main.getExecutor().execute(task);
    }
}
