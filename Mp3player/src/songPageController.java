import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;

public class songPageController implements Initializable {
    @FXML
    private Button DeleteButton;
    @FXML
    private Button addButton;
    @FXML
    private Label label;
    @FXML
    private ListView<String> musicList;
    
    private ObservableList<String> songs = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadSongsFromFolder();
        musicList.setItems(songs);
    }

    @FXML
    private void backToMain(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // <-- get Stage safely
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    private void loadSongsFromFolder() {
        try {
           
            File songsFolder = new File("songs"); 
            File[] files = songsFolder.listFiles();
            
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
                        songs.add(file.getName());
                    }
                }
                
                if (songs.isEmpty()) {
                    label.setText("no songs");
                }
            }
        } catch (Exception e) {
            label.setText("Error loading songs: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    @FXML
    private void refreshSongList() {
        songs.clear();
        loadSongsFromFolder();
    }
    
    @FXML
    private void handleAddButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select MP3 Files");
        
        FileChooser.ExtensionFilter audioFilter = new FileChooser.ExtensionFilter(
            "Audio Files", "*.mp3", "*.wav", "*.m4a", "*.flac"
        );
        fileChooser.getExtensionFilters().add(audioFilter);
        
        //Multiple file selection
        Stage stage = (Stage) addButton.getScene().getWindow();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
        
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            int successCount = 0;
            int failCount = 0;
            
            // Ensure songs folder exists
            File songsFolder = new File("songs");
            if (!songsFolder.exists()) {
                songsFolder.mkdir();
            }
            
            // Copy each selected file to songs folder
            for (File file : selectedFiles) {
                try {
                    File destination = new File(songsFolder, file.getName());
                    
                    // Check if file already exists
                    if (destination.exists()) {
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setTitle("File Exists");
                        alert.setHeaderText("File already exists: " + file.getName());
                        alert.setContentText("Do you want to replace it?");
                        
                        if (alert.showAndWait().get().getText().equals("OK")) {
                            Files.copy(file.toPath(), destination.toPath(), 
                                     StandardCopyOption.REPLACE_EXISTING);
                            successCount++;
                        }
                    } else {
                        Files.copy(file.toPath(), destination.toPath());
                        successCount++;
                    }
                } catch (IOException e) {
                    failCount++;
                    e.printStackTrace();
                }
            }
            
            // Refresh the ListView
            refreshSongList();
            
            // fail case
            if (failCount > 0) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Import Warning");
                alert.setHeaderText("Some files failed to import");
                alert.setContentText(failCount + " file(s) could not be copied.");
                alert.showAndWait();
            }
        }
    }
    @FXML
    private void handleDeleteButton() {
        String selectedSong = musicList.getSelectionModel().getSelectedItem();

        if (selectedSong == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No song selected");
            alert.setContentText("Please select a song to delete.");
            alert.showAndWait();
            return;
        }

        // Confirmation dialog
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete \"" + selectedSong + "\"?");
        var result = confirmAlert.showAndWait();
        if (result.isEmpty() || result.get().getButtonData().isCancelButton()) return;

        File fileOnDisk = new File("songs", selectedSong);

        try {
            // try to delete the file
            Files.delete(fileOnDisk.toPath());
            songs.remove(selectedSong);

        } catch (java.nio.file.FileSystemException e) {
            // If file is locked by the MediaPlayer, release and retry
            PlayerRegistry.release();
            try {
                Thread.sleep(150);             // small delay for Windows file unlock
                Files.delete(fileOnDisk.toPath());
                songs.remove(selectedSong);
            } catch (Exception e2) {
                Alert error = new Alert(AlertType.ERROR);
                error.setTitle("Delete Failed");
                error.setHeaderText("Couldn't delete: " + selectedSong);
                error.setContentText("The file is still in use or locked.\n\nDetails: " + e2.getMessage());
                error.showAndWait();
            }

        } catch (Exception e) {
            Alert error = new Alert(AlertType.ERROR);
            error.setTitle("Delete Failed");
            error.setHeaderText("Couldn't delete: " + selectedSong);
            error.setContentText("Details: " + e.getMessage());
            error.showAndWait();
        }

        if (songs.isEmpty()) {
            label.setText("No songs");
        }
    }
}