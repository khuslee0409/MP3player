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
            // Get the songs folder path
            File songsFolder = new File("songs");
            
            
            // Get all files from the songs folder
            File[] files = songsFolder.listFiles();
            
            if (files != null) {
                for (File file : files) {
                    // Filter only mp3 files (you can add more extensions)
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
                        songs.add(file.getName());
                    }
                }
                
                if (songs.isEmpty()) {
                    label.setText("No MP3 files found in songs folder");
                }
            }
        } catch (Exception e) {
            label.setText("Error loading songs: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    // Optional: Method to refresh the song list
    @FXML
    private void refreshSongList() {
        songs.clear();
        loadSongsFromFolder();
    }
    
    // Add button functionality
    @FXML
    private void handleAddButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select MP3 Files");
        
        // Set extension filter to only show audio files
        FileChooser.ExtensionFilter audioFilter = new FileChooser.ExtensionFilter(
            "Audio Files", "*.mp3", "*.wav", "*.m4a", "*.flac"
        );
        fileChooser.getExtensionFilters().add(audioFilter);
        
        // Allow multiple file selection
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
        confirmAlert.setContentText("Are you sure you want to delete \"" + selectedSong + "\"?");
        
        if (confirmAlert.showAndWait().get().getText().equals("OK")) {
            try {
                File fileToDelete = new File("songs/" + selectedSong);
                
                if (fileToDelete.exists()) {
                    if (fileToDelete.delete()) {
                        songs.remove(selectedSong);
                    } else {
                        throw new IOException("Unable to delete file");
                    }
                } else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("File Not Found");
                    alert.setHeaderText("Cannot delete song");
                    alert.setContentText("The file does not exist in the songs folder.");
                    alert.showAndWait();
                }
            } catch (Exception e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Delete Error");
                alert.setHeaderText("Failed to delete song");
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }
}