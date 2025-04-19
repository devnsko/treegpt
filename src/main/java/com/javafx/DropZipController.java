package com.javafx;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DropZipController {
    @FXML
    private VBox dropZone;

    @FXML
    private Label droplabel;

    @FXML
    private Button dropbutton;

    @FXML
    private ListView<String> listView;

    @FXML
    public void initialize() {
        setupDragAndDrop();
    }

    private void setupDragAndDrop() {
        dropZone.setOnDragOver(event -> {
            if(event.getGestureSource() != dropZone && 
            event.getDragboard().hasFiles()) {

                boolean hasZip = event.getDragboard().getFiles().stream()
                .anyMatch(file -> file.getName().toLowerCase().endsWith(".zip"));

                if (hasZip) {
                    event.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
                }
            }
            event.consume();
        });

        dropZone.setOnDragDropped(event -> {
            var db = event.getDragboard();
            boolean success = false;

            if (db.hasFiles()) {
                File zip = db.getFiles().get(0);

                if (isValidZip(zip)) {
                    File outputDir = new File(System.getProperty("user.home"), ".treegpt/unzipped");

                    try {
                        clearDirectory(outputDir);
                        unzip(zip, outputDir);
                        listView.getItems().clear();
                        listFilesRecursive(outputDir, outputDir, listView);
                        success = true;
                    } catch (IOException e) {
                        listView.getItems().add("❌ Ошибка распаковки: " + e.getMessage());
                    }
                } else {
                    listView.getItems().add("❌ Неверный формат файла: нужен .zip");
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    @FXML
    private void DropZip() throws IOException {
        FileChooser chooser = new FileChooser();
        droplabel.setText("Choose ZIP with chats");

        FileChooser.ExtensionFilter zipFilter = new FileChooser.ExtensionFilter("ZIP файлы (*.zip)", "*.zip");
        chooser.getExtensionFilters().add(zipFilter);
        File file = chooser.showOpenDialog(new Stage());

        if (file == null) {
            droplabel.setText("lol");
            return;
        } else {
            if (!isValidZip(file)) {
                droplabel.setText("Wrong file format. needs to be .zip");
                return;
            } else {
                droplabel.setText(file.getName());

                File outputDir = new File(System.getProperty("user.home"), ".treegpt/unzipped");
                try {
                    clearDirectory(outputDir);
                    unzip(file, outputDir);

                    listView.getItems().clear();
                    listFilesRecursive(outputDir, outputDir, listView);

                    droplabel.setText("here your zip");
                } catch (IOException e) {
                    droplabel.setText("failed to parse ZIP");
                }
            }

        }
    }

    private boolean isValidZip(File file) {
        try(ZipFile zipf = new ZipFile(file)) {
            zipf.entries();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void unzip(File zipFile, File outputDir) throws IOException {
        if (!outputDir.exists())
            outputDir.mkdirs();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(outputDir, entry.getName());

                if (entry.isDirectory()) 
                    newFile.mkdirs();
                else {
                    new File(newFile.getParent()).mkdirs();

                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }

    private void listFilesRecursive(File base, File current, ListView<String> listView) {
        for (File file : current.listFiles()) {
            if (file.isDirectory()) {
                listFilesRecursive(base, file, listView);
            } else {
                String relativePath = base.toURI().relativize(file.toURI()).getPath();
                listView.getItems().add(relativePath);
            }
        }
    }

    private void clearDirectory(File dir) {
        if (!dir.exists()) return;
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                clearDirectory(file);
            }
            file.delete();
        }
    }
    
}
