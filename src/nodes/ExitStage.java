package nodes;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * This Stage shows options to the users before exiting the program.
 * This ensures that the user doesn't lose his project since he has the option to save it before exiting
 */
public class ExitStage {
	
	/**
	 * the stage containing the option buttons
	 */
	private Stage stage = new Stage();
	
	/**
	 * constructs a new exit stage without displaying it
	 */
	public ExitStage() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/ExitLayout.fxml"));
		loader.setController(this);
		
		Parent p;
		try {
            p = loader.load();            
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
		
        stage.setScene(new Scene(p));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/Amp.png")));
        stage.setResizable(false);
	}
	
	/**
	 * displays the exit pane
	 */
	public void show() {
		stage.show();
		//MainStage.getInstance().show();
	}
	
	/**
	 * method triggered by JavaFX when the save button is pressed
	 * @param e the JavaFX event
	 */
	@FXML
	protected void savePressed(ActionEvent e) {
		if(MainStage.getInstance().getMenuBar().isSaved()) {
			TopMenuBar.saveAmpFile(MainStage.getInstance().getMenuBar().getSaveFile());
		}
		else { //Save in a new file
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save As...");
			
			//filter to save only in amp files 
			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("AMP files	(*.amp)","*.amp");
			fileChooser.getExtensionFilters().add(filter);
			
			File file =	fileChooser.showSaveDialog(MainStage.getInstance().getPrimaryStage());
			
			if(file != null) {
				TopMenuBar.saveAmpFile(file);
				MainStage.getInstance().getMenuBar().setSaveFile(file);
			}
		}
		
		
		
		new Thread(new Runnable() {
			public void run() {
				while(TopMenuBar.saving); //Wait for the save thread to end TODO show its saving (thread it?)
				MainStage.getInstance().exit(true);
			}
		}).start();
	}
	
	/**
	 * method triggered by JavaFX when the 'dont save' button is pressed
	 * @param e the JavaFX event
	 */
	@FXML
	protected void dontSavePressed(ActionEvent e) {
		MainStage.getInstance().exit(true);
	}
	
	/**
	 * method triggered by JavaFX when the 'cancel' button is pressed
	 * @param e the JavaFX event
	 */
	@FXML
	protected void cancelPressed(ActionEvent e) {
		stage.hide();
		//MainStage.getInstance().show();
	}
}
