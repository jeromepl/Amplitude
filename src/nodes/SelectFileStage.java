package nodes;

import java.io.File;
import java.io.IOException;
import reactions.FrequencyReaction;
import reactions.Reaction;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This is the splash screen stage. It shows an introductory paragraph with a link to the tutorial if the user needs help
 * It also displays options to either create a new project with a sound file or to load an existing one
 */
public class SelectFileStage implements EventHandler<WindowEvent> {
	
	/**
	 * the stage containing all JavaFX elements in the splash screen
	 */
	private Stage stage = new Stage();
	
	/**
	 * the JavaFX element allowing the user to either open an existing project or create a new one
	 */
	@FXML private RadioButton rb1;
	/**
	 * the JavaFX element allowing the user to either open an existing project or create a new one
	 */
	@FXML private RadioButton rb2;
	
	/**
	 * the JavaFX element telling the user if a general error occurred
	 */
	@FXML private Label error;
	/**
	 * the JavaFX element telling the user if a sound file loading error occurred
	 */
	@FXML private Label sfError;
	
	/**
	 * the JavaFX element telling the path of the loaded file
	 */
	@FXML private TextField tf1;
	/**
	 * the JavaFX element telling the path of the loaded file
	 */
	@FXML private TextField tf2;
	
	/**
	 * the JavaFX element displaying the Ampl!tude logo
	 */
	@FXML private ImageView logo;
	
	/**
	 * the loaded sound file
	 */
	private File soundFile;
	/**
	 * the loaded ampl!tude file
	 */
	private File ampFile;
	
	/**
	 * whether this stage is visible or not
	 */
	private boolean showed;
	/**
	 * the reaction making the Ampl!tude logo react to the intro music
	 */
	private FrequencyReaction reaction = new FrequencyReaction(50, 150, 1, 1.5, Reaction.SIZE);

	/**
	 * constructs a select file stage without displaying it
	 */
	public SelectFileStage() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/SelectFileLayout.fxml"));
		loader.setController(this);
		
		Parent p;
		try {
            p = loader.load();            
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
		
        stage.setTitle("Welcome!");
        stage.setScene(new Scene(p, 591, 525));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/Amp.png")));
        stage.setResizable(false);
        
        stage.setOnCloseRequest(this); //Allows to force the program to close
        
        tf1.setEditable(false);
        tf2.setEditable(false);

	}
	
	/**
	 * updates the size of the logo based on the the frequency of the intro sound
	 */
	public void update() {
		double value = reaction.update();
		logo.setScaleX(value);
		logo.setScaleY(value);
	}
	
	/**
	 * displays the select file stage
	 */
	public void show() {
		showed = true;
		
		soundFile = null;
		ampFile = null;
		tf1.setText("");
		tf2.setText("");
		error.setVisible(false);
		
		File kickLoop = new File("resources/kickLoop.wav");
			
		MainStage.getInstance().loadSound(kickLoop, true);
		MainStage.getInstance().getAudioPlayer().setVolume(0.8f);
		MainStage.getInstance().getAudioPlayer().play();
		
		MainStage.getInstance().hide();
		stage.show(); 
	}
	
	/**
	 * method triggered by JavaFX when the user presses the 'open' button
	 * @param e the JavaFX event
	 */
	@FXML
	protected void openPressed(ActionEvent e) {
		error.setVisible(false);
		sfError.setVisible(false);
		
		if(rb1.isSelected()){
			if(soundFile == null) {
				sfError.setVisible(true);
			}
			else if(!soundFile.exists()) {
				error.setVisible(true);
			}
			else {
				showed = false;
				MainStage.getInstance().getAudioPlayer().stop();
				MainStage.getInstance().getAudioPlayer().setVolume(1);
				MainStage.getInstance().loadSound(soundFile, false);
				MainStage.getInstance().show();
				stage.hide();
			}
		}
		else {
			if(ampFile == null) {
				sfError.setVisible(true);
			}
			else if(!ampFile.exists()) {
				error.setVisible(true);
			}
			else {
				showed = false;
				try {
					MainStage.getInstance().getAudioPlayer().stop();
					MainStage.getInstance().getAudioPlayer().setVolume(1);
				} catch(Exception ex) {
					//Do not do anything as it does not affect the usage of the application
				}
				TopMenuBar.openAmpFile(ampFile);
				MainStage.getInstance().getMenuBar().setSaveFile(ampFile);
				MainStage.getInstance().show();
				stage.hide();
			}
		}
	}
	
	/**
	 * method triggered by JavaFX when the user presses the first 'browse' button
	 * @param e the JavaFX event
	 */
	@FXML
	protected void browse1Pressed(ActionEvent e) {
		soundFile = TopMenuBar.selectSoundFile();
		if(soundFile != null) 
			tf1.setText(soundFile.getAbsolutePath());
	}
	
	/**
	 * method triggered by JavaFX when the user presses the second 'browse' button
	 * @param e the JavaFX event
	 */
	@FXML
	protected void browse2Pressed(ActionEvent e) {
		ampFile = TopMenuBar.selectAmpFile();
		if(ampFile != null)
			tf2.setText(ampFile.getAbsolutePath());
	}
	
	/**
	 * method triggered by JavaFX when the user presses the 'tutorial' button
	 * @param e the JavaFX event
	 */
	@FXML
	protected void tutorialClicked(MouseEvent e) {
		TopMenuBar.openTutorial();
	}
	
	/**
	 * @return whether the stage is visible or not
	 */
	public boolean isShowed() {
		return showed;
	}
	
	/**
	 * sets the value of the 'showed' variable
	 * @param showed whether the stage is shown or not
	 */
	public void setShowed(boolean showed) {
		this.showed = showed;
	}
	
	/* (non-Javadoc)
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(WindowEvent event) { //Called when closing the window
		event.consume(); //Prevent the premature closing of the window (Need to show the saving dialog first)
		MainStage.getInstance().exit(true);
	}
}
