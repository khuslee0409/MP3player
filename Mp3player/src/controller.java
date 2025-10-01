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
import javafx.fxml.Initializable;
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

    public void beginTimer(){
        timer = new Timer();
        task = new TimerTask() {
            public void run(){
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                progressBar.setProgress(current/end);

                if(current/end == 1){
                    cancelTimer();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    public void cancelTimer(){
        running = false;
        timer.cancel();

    }
    

    @FXML
    void nextSong(ActionEvent event) {
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
        cancelTimer();
        mediaPlayer.pause();

    }

    @FXML
    void play(ActionEvent event) {
        beginTimer();
        speedChange(null);
        mediaPlayer.setVolume(volSlider.getValue()*0.01);
        mediaPlayer.play();

    }

    @FXML
    void prevSong(ActionEvent event) {
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
            URL dirUrl = getClass().getResource("/resources");
            Path dir = Paths.get(dirUrl.toURI());
                songs = Files.list(dir)
                        .filter(Files::isRegularFile)
                        .filter(p-> {
                            String song = p.getFileName().toString().toLowerCase(Locale.ROOT);
                            return song.endsWith("mp3") || song.endsWith(".wav")|| song.endsWith(".m4a");   
                        })
                        .map(p -> new Media(p.toUri().toString()))
                        .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Error loading songs");
            e.printStackTrace();
            songs = new ArrayList<>();
        }

        media = new Media(songs.get(songNumber).getSource());
        mediaPlayer = new MediaPlayer(media);

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


}
