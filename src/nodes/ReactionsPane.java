package nodes;

import java.io.IOException;

import reactions.AmplitudeReaction;
import reactions.Reaction;
import shapes.ReactiveShape;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * This Pane contains buttons that allow the user to add reactions to the selected shape.
 * This pane is hidden until it is linked to a Shape.
 */
public class ReactionsPane extends VBox {
	
	/**
	 * the JavaFX element that allows the user to open the reaction stage
	 */
	@FXML private Button addReaction;
	/**
	 * the JavaFX element that tells the user if no shape is selected (and thus no reactions can be added)
	 */
	@FXML private Label noSelection;

	/**
	 * constructs a reaction pane and displays a text telling the user that no shape is selected
	 */
	public ReactionsPane() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/ReactionsLayout.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		
		setSpacing(20); //Space between the elements in the VBox
		
		hide(); //Hide resets everything and deletes the addReaction button
	}
	
	/**
	 * displays the buttons in the pane to allow the user to add reactions to the selected shape
	 * @param shape the selected shape
	 */
	public void show(ReactiveShape shape) {
		getChildren().clear(); //Removes the "noSelection" Label
		
		for(Reaction reaction: shape.getReactions()) {
			String text = "";
			if(reaction instanceof AmplitudeReaction)
				text = "Amplitude Reaction";
			else
				text = "Frequency Reaction";
			
			Button b = new Button(text);
			b.setOnAction((event) -> {
				MainStage.getInstance().getReactionStage().show(reaction);
			});
			
			b.setPrefWidth(165);
			b.setPrefHeight(60);
			getChildren().add(b); //prepend the button
		}
		
		getChildren().add(addReaction);
	}
	
	/**
	 * hides the buttons and shows a text telling the user that no shape is selected
	 */
	public void hide() {
		getChildren().clear();
		getChildren().add(noSelection);
	}
	
	/**
	 * method triggered by JavaFX when the user clicks on the button to add a reaction. Opens the reaction stage
	 * @param e the JavaFX event
	 */
	@FXML
	protected void addReaction(ActionEvent e) {
		MainStage.getInstance().getReactionStage().show(null); //Putting null means it's a new reaction
	}
}
