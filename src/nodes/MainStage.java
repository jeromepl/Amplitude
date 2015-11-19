package nodes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.UnsupportedAudioFileException;

import audio.AudioPlayer;
import shapes.ReactiveShape;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This is the main class of this project. It contains the main method, but also most of the
important variables such as shapes, player and FFT. Since most of the key variables are in
this class, an easy way to retrieve their value or object is to use the Singleton pattern.
This pattern makes sure that only one instance of this class can be created (the
constructor of this class is private) and puts the reference to this instance in a static
variable so that it can easily be accessed anywhere by accessing this static instance.
 * @author Jerome Parent-Levesque
 */
public class MainStage extends Application implements EventHandler<WindowEvent>, ChangeListener<Number> {
	
	/**
	 * the first stage to open, allowing the user to select a file to load
	 */
	private SelectFileStage selectFileStage = new SelectFileStage();
	
	/**
	 * the stage that opens whenever an unexpected error occurs
	 */
	private ErrorStage errorStage = new ErrorStage();
	
	/**
	 * the stage that opens whenever the user clicks on the exit button
	 */
	private ExitStage exitStage = new ExitStage();
	
	/**
	 * the stage allowing the user to edit or create a new reaction attached to a shape
	 */
	private ReactionStage reactionStage = new ReactionStage();
	
	/**
	 * pane on the left of the screen containing the options to edit shapes
	 */
	@FXML private EditingPane editingPane;
	
	/**
	 * pane on the right of the screen containing the options to
add reactions
	 */
	@FXML private ReactionsPane reactionsPane;
	
	/**
	 * pane on the top of the screen showing the song title,
progression as well as control buttons such as play and
pause
	 */
	@FXML private PlayerPane playerPane;
	
	/**
	 * menu bar at the top of the screen containing the tools
	 */
	@FXML private TopMenuBar topMenuBar;
	
	/**
	 * pane on the left of the screen containing buttons to create
new shapes
	 */
	@FXML private ShapesPane shapesPane;
	
	/**
	 * pane in the middle of the screen showing the shapes
animated in function of the music playing
	 */
	@FXML private VideoPane videoPane;
	
	/**
	 * the indicator telling the user when the application is saving
	 */
	@FXML private HBox saveIndicator;
	
	/**
	 * all the shapes to render on the screen
	 */
	private ArrayList<ReactiveShape> shapes = new ArrayList<ReactiveShape>();
	
	/**
	 * the current shape being modified. Equals null if no shape is
selected
	 */
	private ReactiveShape selectedShape;
	
	/**
	 * the audio player. Allows to load, play, pause and change
the volume of a sound
	 */
	private AudioPlayer player;
	
	/**
	 * reference to this object. Follows the Singleton pattern
	 */
	private static MainStage instance;
	
	/**
	 * reference to the main JavaFX stage
	 */
	private Stage primaryStage;
	
	/**
	 * Constructs a MainStage and stores the reference in the instance variable 
	 */
	public MainStage() {
		instance = this;
	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/MainLayout.fxml"));
		loader.setController(this);
		
		Parent p;
		try {
            p = loader.load();            
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

		Scene scene = new Scene(p, 1400, 900);

		primaryStage.setTitle("Ampl!tude");
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image("/resources/Amp.png"));
		primaryStage.setOnCloseRequest(this); //Allows to force the program to close
		
		hideSaveIndicator();
		
		selectFileStage.show();
		
		//Set Window events
		primaryStage.widthProperty().addListener(this);
		primaryStage.heightProperty().addListener(this);
		primaryStage.maximizedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
		    	TimerTask task = new TimerTask() { 
		    		@Override
		          	public void run()
		          	{
		    			Platform.runLater(new Runnable() {
							@Override
							public void run() {
								videoPane.resize();
								scaleAllShapes();
							}		    				
		    			});
		          	}
		        };
		        
		        Timer timer = new Timer();
		        timer.schedule(task, 50); //delay the resize to wait for the sizes to be updated
		    }
		});
	}
	
	/**
	 * hides the main stage (the primary stage)
	 */
	public void hide() {
		primaryStage.hide();
	}
	
	/**
	 * shows the main stage (the primary stage) and calculates the initial size of the video pane
	 */
	public void show() {
		primaryStage.show(); //After this, the width and height of panes become available
		videoPane.setInitSize();
	}
	
	/**
	 * Resets everything to either create a new project or open another one
	 */
	public void clear() {
		videoPane.setBackground(Color.WHITE);
		editingPane.setColorPickerValue(Color.WHITE);
		shapes.clear();
		videoPane.clear();
		if(player != null)
			player.stop();
		playerPane.setPlayingText(false);
		reactionsPane.hide();
	}
	
	/**
	 * Loads a sound to be played by the audio player
	 * @param soundFile the sound file to load
	 */
	public void loadSound(File soundFile, boolean loop) {
		try {
			if(player != null) { //If there was already an audioPlayer, transfer the volume level from one to the other
				player.stop(); //Stop the running thread
				float volume = player.getVolume();
				boolean muted = player.isMuted();
				player = new AudioPlayer(soundFile, loop);
				player.setVolume(volume);
				if(muted) player.mute();
			}
			else
				player = new AudioPlayer(soundFile, loop);
			
			playerPane.setSoundTitle(soundFile.getName());
			playerPane.setSoundProgression(0);
		} catch (UnsupportedAudioFileException | IOException e) {
			errorStage.show("An error occured while trying to load the sound. Sorry for the inconvenience.", "MS236");
		}
	}
	
	/**
	 * Loads a sound to be played by the audio player
	 * @param soundBytes the sound bytes from the audio file to load
	 * @param name the file name to be displayed on the progress bar
	 */
	public void loadSound(byte[] soundBytes, String name, boolean loop) {
		try {
			if(player != null) { //If there was already an audioPlayer, transfer the volume level from one to the other
				player.stop(); //Stop the running thread
				float volume = player.getVolume();
				boolean muted = player.isMuted();
				player = new AudioPlayer(soundBytes, name, loop);
				player.setVolume(volume);
				if(muted) player.mute();
			}
			else
				player = new AudioPlayer(soundBytes, name, loop);
			
			playerPane.setSoundTitle(name);
			playerPane.setSoundProgression(0);
		} catch (UnsupportedAudioFileException e) {
			errorStage.show("An error occured while trying to load the sound. Sorry for the inconvenience.", "MS261");
		}
	}
	
	/**
	 * @return the static reference to the instance of this class
	 */
	public static MainStage getInstance() {
		return instance;
	}
	
	/**
	 * update either the selectFile stage or the main stage depending on what is displayed
	 */
	public void update() {
		if(selectFileStage.isShowed()) {
			selectFileStage.update();
		}
		else {
			updateAllShapes();
		}
	}
	
	/**
	 * updates all the shapes according to their reactions (Note: the shapes' scale is updated in the shapes' update method)
	 */
	public void updateAllShapes() {
		playerPane.setSoundProgression(player.getProgression());
		
		for(ReactiveShape shape: MainStage.getInstance().getShapes()) {
    		shape.update();
    	}
	}
	
	/**
	 * scales all the shapes according to the size of the window
	 */
	public void scaleAllShapes() {
		for(ReactiveShape shape: MainStage.getInstance().getShapes()) {
    		shape.scale();
    	}
	}
	
	/**
	 * hides the save indicator
	 */
	public void hideSaveIndicator() {
		saveIndicator.setVisible(false);
	}
	
	/**
	 * shows the save indicator
	 */
	public void showSaveIndicator() {
		saveIndicator.setVisible(true);
	}

	/**
	 * @return the main JavaFX Stage 
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	/**
	 * @return the opening stage, the select file stage
	 */
	public SelectFileStage getSelectFileStage() {
		return selectFileStage;
	}
	
	/**
	 * @return the error stage
	 */
	public ErrorStage getErrorStage() {
		return errorStage;
	}
	
	/**
	 * @return the exit stage
	 */
	public ExitStage getExitStage() {
		return exitStage;
	}
	
	/**
	 * @return the editing pane
	 */
	public EditingPane getEditingPane() {
		return editingPane;
	}
	
	/**
	 * @return the top menu bar
	 */
	public TopMenuBar getMenuBar() {
		return topMenuBar;
	}
	
	/**
	 * @return the reactions pane
	 */
	public ReactionsPane getReactionsPane() {
		return reactionsPane;
	}
	
	/**
	 * @return the shapes pane
	 */
	public ShapesPane getShapesPane() {
		return shapesPane;
	}
	
	/**
	 * @return the player pane
	 */
	public PlayerPane getPlayerPane() {
		return playerPane;
	}
	
	/** 
	 * @return the video pane
	 */
	public VideoPane getVideoPane() {
		return videoPane;
	}
	
	/**
	 * @return the reaction stage
	 */
	public ReactionStage getReactionStage() {
		return reactionStage;
	}
	
	/**
	 * @return the audio player
	 */
	public AudioPlayer getAudioPlayer() {
		return player;
	}
	
	/**
	 * @return all the shapes contained in the current project
	 */
	public ArrayList<ReactiveShape> getShapes() {
		return shapes;
	}
	
	/**
	 * @param shape the shape to add to the video pane
	 */
	public void addShape(ReactiveShape shape) {
		shapes.add(shape);
	}
	
	/**
	 * @param shape the shape to remove from the video pane
	 */
	public void removeShape(ReactiveShape shape) {
		videoPane.removeShape(shape.getFXShape());
		shapes.remove(shape);
	}
	
	/**
	 * @return the currently selected shape (returns null if no shape is selected)
	 */
	public ReactiveShape getSelectedShape() {
		return selectedShape;
	}
	
	/**
	 * selects a shape
	 * @param selectedShape the shape to select (null if the background was selected)
	 */
	public void selectShape(ReactiveShape selectedShape) {
		this.selectedShape = selectedShape;
		
		if(selectedShape == null) {
			editingPane.backgroundSelected();
			reactionsPane.hide();
		}
		else {
			editingPane.shapeSelected(selectedShape);
			reactionsPane.show(selectedShape);
		}
	}
	
	/**
	 * sets a new array list of shapes to be displayed
	 * @param shapes the new shapes to be put in the video pane and updated with the music
	 */
	public void setShapes(ArrayList<ReactiveShape> shapes) {
		this.shapes = shapes;
	}
	
	/**
	 * @return the audio player
	 */
	public AudioPlayer getPlayer() {
		return player;
	}
	
	/**
	 * launches the application. Starting point of the program
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * exits the application
	 * @param forcedExit whether to quit automatically or to show the save dialog first
	 */
	public void exit(boolean forcedExit) {
		if(forcedExit) {
			if(player != null)
				player.stop(); //Free the memory used by the AudioPlayer
			System.exit(0);
		}
		else { //Show the save dialog
			exitStage.show(); //Also hides the main stage
		}
	}

	/* (non-Javadoc)
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(WindowEvent event) { //Called when closing the window
		event.consume(); //Prevent the premature closing of the window (Need to show the saving dialog first)
		exit(false);
	}

	/* (non-Javadoc)
	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldVal, Number newVal) {
		videoPane.resize();
		scaleAllShapes();
	}
}
