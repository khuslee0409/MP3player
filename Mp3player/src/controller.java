import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class controller implements Initializable {

    @FXML
    private AnchorPane Pane;

    @FXML
    private Button nextButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button playButton;

    @FXML
    private Button prevButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button restartButton;

    @FXML
    private Button songListButton;

    @FXML
    private Label songName;

    @FXML
    private ComboBox<String> speedSet;

    @FXML
    private Slider volSlider;

    private List<Media> songs;
    private int songNumber;
    private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};

    private Timer timer;
    private TimerTask task;
    private Media media;
    private MediaPlayer mediaPlayer;
    private boolean running;
    private Stage stage;
    private Scene scene;


    public void beginTimer(){
        if (mediaPlayer == null) return;
        timer = new Timer();
        task = new TimerTask() {
            public void run(){
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                if (end <= 0) return; // avoid divide by 0
                progressBar.setProgress(current / end);
                if (current / end >= 0.999) cancelTimer();
            }
        };
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    public void cancelTimer(){
        running = false;
        timer.cancel();

    }

    @FXML
    public void SongListButton(ActionEvent event) throws IOException{

        try {
            if (timer != null) cancelTimer();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
        } catch (Exception ignore) {}

            PlayerRegistry.release();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("SongsPage.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        }
    
    

    @FXML
    void nextSong(ActionEvent event) {
        if (mediaPlayer == null || songs.isEmpty()) return;

        if(songNumber < songs.size()-1){
            songNumber++;
            mediaPlayer.stop();
            if(running){
                cancelTimer();
            }
            media = new Media(songs.get(songNumber).getSource());
            mediaPlayer = new MediaPlayer(media);

            String source = songs.get(songNumber).getSource();
            String fileName = Paths.get(java.net.URI.create(source)).getFileName().toString();
            songName.setText(fileName);
            this.play(event);
        }else{
            songNumber = 0;
            mediaPlayer.stop();
            if(running){
                cancelTimer();
            }
            media = new Media(songs.get(songNumber).getSource());
            mediaPlayer = new MediaPlayer(media);

            String source = songs.get(songNumber).getSource();
            String fileName = Paths.get(java.net.URI.create(source)).getFileName().toString();
            songName.setText(fileName);
            this.play(event);
        }

    }

    @FXML
    void pause(ActionEvent event) {
        if (mediaPlayer == null) return;
        cancelTimer();
        mediaPlayer.pause();

    }

    @FXML
    void play(ActionEvent event) {
        if (mediaPlayer == null) return;
        beginTimer();
        speedChange(null);
        mediaPlayer.setVolume(volSlider.getValue()*0.01);
        mediaPlayer.play();

    }

    @FXML
    void prevSong(ActionEvent event) {
        if (mediaPlayer == null || songs.isEmpty()) return;

        if(songNumber > 0){
            songNumber--    ;
            mediaPlayer.stop();
            if(running){
                cancelTimer();
            }
            media = new Media(songs.get(songNumber).getSource());
            mediaPlayer = new MediaPlayer(media);

            String source = songs.get(songNumber).getSource();
            String fileName = Paths.get(java.net.URI.create(source)).getFileName().toString();
            songName.setText(fileName);
            this.play(event);
        }else{
            songNumber = songs.size()-1;
            mediaPlayer.stop();
            if(running){
                cancelTimer();
            }
            media = new Media(songs.get(songNumber).getSource());
            mediaPlayer = new MediaPlayer(media);

            String source = songs.get(songNumber).getSource();
            String fileName = Paths.get(java.net.URI.create(source)).getFileName().toString();
            songName.setText(fileName);
            this.play(event);
        }

    }

    @FXML
    void reset(ActionEvent event) {
        if (mediaPlayer == null) return;

        progressBar.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0.0));

    }

    @FXML
    void speedChange(ActionEvent event) {
        if(speedSet.getValue()== null){
            mediaPlayer.setRate(1);
        }else{
            mediaPlayer.setRate(Integer.parseInt(speedSet.getValue().substring(0, speedSet.getValue().length()-1))*0.01);
        }

    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        try {
        Path songsDir = Paths.get("songs"); // folder beside your JAR or EXE
        songs = new ArrayList<>();

        if (Files.isDirectory(songsDir)) {
            try (var stream = Files.list(songsDir)) {
                songs = stream
                        .filter(Files::isRegularFile)
                        .filter(p -> {
                            String name = p.getFileName().toString().toLowerCase(Locale.ROOT);
                            return name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".m4a");
                        })
                        .map(p -> new Media(p.toUri().toString()))
                        .collect(Collectors.toList());
            }
        }

        if (songs.isEmpty()) {
            if (timer != null) cancelTimer();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            PlayerRegistry.release();

            mediaPlayer = null;
            media = null;
            songName.setText("No songs yet");
            progressBar.setProgress(0);
            setPlayerEnabled(false);
            return; 
        }
            setPlayerEnabled(true);
            initPlayer(0);
           

    } catch (Exception e) {
        System.out.println("Error loading songs");
        e.printStackTrace();
        songs = new ArrayList<>();
    }


        media = new Media(songs.get(songNumber).getSource());
        mediaPlayer = new MediaPlayer(media);
        PlayerRegistry.set(mediaPlayer, media);


        String source = songs.get(songNumber).getSource();
        String fileName = Paths.get(java.net.URI.create(source)).getFileName().toString();
        songName.setText(fileName);

        for(int i = 0; i < speeds.length; i++){
            speedSet.getItems().add(Integer.toString(speeds[i]) + "%");
        }

        speedSet.setOnAction(this::speedChange);

        volSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                        mediaPlayer.setVolume(volSlider.getValue()*0.01);

            }   
        });

        progressBar.setStyle("-fx-accent : red");
        
    }

    private void setPlayerEnabled(boolean enabled) {
    playButton.setDisable(!enabled);
    pauseButton.setDisable(!enabled);
    nextButton.setDisable(!enabled);
    prevButton.setDisable(!enabled);
    restartButton.setDisable(!enabled);
    volSlider.setDisable(!enabled);
    speedSet.setDisable(!enabled);
}

private void initPlayer(int index) {
    songNumber = index;
    media = new Media(songs.get(songNumber).getSource());
    mediaPlayer = new MediaPlayer(media);
    // if you use the registry:
    PlayerRegistry.set(mediaPlayer, media);

    String source = songs.get(songNumber).getSource();
    String fileName = java.nio.file.Paths.get(java.net.URI.create(source)).getFileName().toString();
    songName.setText(fileName);
}





}
