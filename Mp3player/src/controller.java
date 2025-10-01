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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
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
    private MenuButton speedButton;

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
    

    @FXML
    void nextSong(ActionEvent event) {

    }

    @FXML
    void pause(ActionEvent event) {
        mediaPlayer.pause();

    }

    @FXML
    void play(ActionEvent event) {
        mediaPlayer.play();

    }

    @FXML
    void prevSong(ActionEvent event) {

    }

    @FXML
    void reset(ActionEvent event) {
        mediaPlayer.seek(Duration.seconds(0.0));

    }

    @FXML
    void speedChange(ActionEvent event) {

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


        
    }


}
