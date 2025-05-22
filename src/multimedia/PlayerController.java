package multimedia;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.media.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;

public class PlayerController {

    @FXML
    private MediaView mediaView;
    @FXML
    private Slider progressSlider;
    @FXML
    private Button btnPlayPause;
    @FXML
    private Slider volumeSlider;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Media File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Media Files", "*.mp4", "*.mp3", "*.wav", "*.m4a")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (!progressSlider.isValueChanging()) {
                    progressSlider.setValue(newTime.toSeconds());
                }
            });

            mediaPlayer.setOnReady(() -> {
                progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            });

            progressSlider.setOnMousePressed(event
                    -> mediaPlayer.seek(Duration.seconds(progressSlider.getValue()))
            );

            progressSlider.setOnMouseDragged(event
                    -> mediaPlayer.seek(Duration.seconds(progressSlider.getValue()))
            );

            mediaPlayer.play();
            isPlaying = true;
            btnPlayPause.setText("⏸");
        }
        
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newVal.doubleValue());
            }
        });
        volumeSlider.setValue(0.5); // Volume default

    }

    @FXML
    private void handlePlayPause() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.pause();
                btnPlayPause.setText("▶");
            } else {
                mediaPlayer.play();
                btnPlayPause.setText("⏸");
            }
            isPlaying = !isPlaying;
        }
    }

    @FXML
    private void handleStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying = false;
            btnPlayPause.setText("▶");
        }
    }

    @FXML
    private void handleForward() {
        if (mediaPlayer != null) {
            Duration currentTime = mediaPlayer.getCurrentTime();
            Duration forwardTime = currentTime.add(Duration.seconds(5));
            mediaPlayer.seek(forwardTime);
        }
    }

    @FXML
    private void handleBackward() {
        if (mediaPlayer != null) {
            Duration currentTime = mediaPlayer.getCurrentTime();
            Duration backwardTime = currentTime.subtract(Duration.seconds(5));
            if (backwardTime.lessThan(Duration.ZERO)) {
                backwardTime = Duration.ZERO;
            }
            mediaPlayer.seek(backwardTime);
        }
    }
}
