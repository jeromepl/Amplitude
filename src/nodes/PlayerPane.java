package nodes;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import audio.AudioPlayer;


/**
 * This Pane contains the buttons that allow the user to play and pause the song. It also
gives the user a graphical way of skipping to a particular point in the song as well as
volume control
 */
public class PlayerPane extends HBox implements ChangeListener<Number>, EventHandler<MouseEvent> {
	
	/**
	 * JavaFX Slider used to control the volume
	 */
	@FXML private Slider slider;
	
	/**
	 * JavaFX Button used to play and pause the music
	 */
	@FXML private Button play;
	
	/**
	 * JavaFX Button used to mute or unmute the music
	 */
	@FXML private Button mute;
	
	/**
	 * JavaFX Label used to show the title of the song
	 */
	@FXML private Label songTitle;
	
	/**
	 * JavaFX ProgressBar used to show the sound progression
	 */
	@FXML private ProgressBar progressBar;
	
	/**
	 * JavaFX Rectangle overlaid over the progress bar and the sound title to allow the sound progression to be changed
	 */
	@FXML private Rectangle progress_bounds;

	/**
	 * Constructs a PlayerPane
	 */
	public PlayerPane() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/PlayerLayout.fxml"));
	    fxmlLoader.setRoot(this);
	    fxmlLoader.setController(this);
	    try {
	        fxmlLoader.load();
	    } catch (IOException exception) {
	        throw new RuntimeException(exception);
	    }
	    
	    setBackground(new Background(new BackgroundFill(Color.rgb(230, 230, 230), CornerRadii.EMPTY, Insets.EMPTY)));
	    
	    slider.setValue(100);
	    slider.valueProperty().addListener(this);
	    
	    progress_bounds.setOnMouseClicked(this);
	    //progressPane.setOnMouseClicked(this);
	}
	
	/**
	 * plays the loaded sound. This method is triggered when the play
button is pressed
	 * @param e the JavaFX event
	 */
	@FXML
	protected void playPressed(ActionEvent e) {
		AudioPlayer ap = MainStage.getInstance().getAudioPlayer();
		if(ap.isPlaying()) {
			ap.pause();
			play.setText("Play");
		}
		else {
			ap.play();
			play.setText("Pause");
		}
	}
	
	/**
	 * mutes the sound. This method is triggered when the mute button
is pressed
	 * @param e the JavaFX event
	 */
	@FXML
	protected void mutePressed(ActionEvent e) {
		AudioPlayer ap = MainStage.getInstance().getAudioPlayer();
		if(ap.isMuted()) {
			ap.unmute();
			mute.setText("Mute");
		}
		else {
			ap.mute();
			mute.setText("Unmute");
		}
	}
	
	/**
	 * sets the sound title
	 * @param name the title of the sound
	 */
	public void setSoundTitle(String name) {
		songTitle.setText(name);
	}
	
	/**
	 * Sets the text on the play/pause button
	 * @param playing whether the sound is playing or not
	 */
	public void setPlayingText(boolean playing) {
		if(playing)
			play.setText("Pause");
		else
			play.setText("Play");
	}
	
	/**
	 * sets the sound progression
	 * @param progression the progression, from 0 to 1, of the current sound playing
	 */
	public void setSoundProgression(double progression) {
		progressBar.setProgress(progression);
	}

	/* (non-Javadoc)
	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		MainStage.getInstance().getAudioPlayer().setVolume(newValue.floatValue() / 100f);
	}

	/* (non-Javadoc)
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(MouseEvent e) {
		MainStage.getInstance().getAudioPlayer().setProgression((float) ((e.getX() + 2) / (progress_bounds.getWidth() - 4)));
		MainStage.getInstance().updateAllShapes();
	}
}
