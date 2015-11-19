package nodes;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * This Stage shows error codes and messages if anything goes wrong. Shows only when an error is caught
 */
public class ErrorStage {
	
	/**
	 * the stage in which the error is going to be displayed
	 */
	private Stage stage = new Stage();

	/**
	 * the error message
	 */
	@FXML private Label message;
	/**
	 * the error code. Helps the programmers to find where the error occured
	 */
	@FXML private Label code;
	
	/**
	 * constructs a new error stage without displaying any error
	 */
	public ErrorStage() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/ErrorLayout.fxml"));
		loader.setController(this);
		
		Parent p;
		try {
            p = loader.load();            
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
		
        stage.setTitle("Error");
        stage.setScene(new Scene(p));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/Amp.png")));
        stage.setResizable(false);
	}
	
	/**
	 * shows an error message
	 * @param message the error message
	 * @param code the error code
	 */
	public void show(String message, String code) {
		this.message.setText(message);
		this.code.setText(code);
		
		MainStage.getInstance().hide();
		stage.show();
	}
	
	/**
	 * hides the error message
	 */
	public void hide() {
		stage.hide();
		MainStage.getInstance().show();
	}
	
	
	/**
	 * method triggered by JavaFX when the 'ok' button is pressed. Closes the application
	 * @param e the JavaFX event
	 */
	@FXML
	protected void okPressed(ActionEvent e) {
		MainStage.getInstance().exit(true);
	}
	
}
