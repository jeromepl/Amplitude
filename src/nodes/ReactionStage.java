package nodes;

import java.io.IOException;

import reactions.AmplitudeReaction;
import reactions.FrequencyReaction;
import reactions.Reaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * This Stage contains all input objects that allow the user to create or edit a Reaction. It opens in a new window.
 */
public class ReactionStage {
	
	/**
	 * the amplitude type. Used in the 'type' combo box
	 */
	private static final String TYPE_AMPLITUDE = "Amplitude";
	/**
	 * the frequency type. Used in the 'type' combo box
	 */
	private static final String TYPE_FREQUENCY = "Frequency";
	
	/**
	 * the size property. Used in the 'property' combo box
	 */
	private static final String PROP_SIZE = "Size";
	/**
	 * the width property. Used in the 'property' combo box
	 */
	private static final String PROP_WIDTH = "Width";
	/**
	 * the height property. Used in the 'property' combo box
	 */
	private static final String PROP_HEIGHT = "Height";
	/**
	 * the red color component property. Used in the 'property' combo box
	 */
	private static final String PROP_COLOR_R = "Red Color Component";
	/**
	 * the green color component property. Used in the 'property' combo box
	 */
	private static final String PROP_COLOR_G = "Green Color Component";
	/**
	 * the blue color component property. Used in the 'property' combo box
	 */
	private static final String PROP_COLOR_B = "Blue Color Component";
	
	/**
	 * the stage containing all the options for the user to add or edit a reaction
	 */
	private Stage stage = new Stage();
	
	/**
	 * the JavaFX element allowing the user to select a reaction type
	 */
	@FXML private ComboBox<String> type;
	/**
	 * all the reaction types that the user can select
	 */
	private ObservableList<String> typeOptions = FXCollections.observableArrayList(
			TYPE_AMPLITUDE,
		    TYPE_FREQUENCY
		);
	/**
	 * the JavaFX element allowing the user to select a shape property to animate
	 */
	@FXML private ComboBox<String> property;
	/**
	 * all the properties that the user can select
	 */
	private ObservableList<String> propertyOptions = FXCollections.observableArrayList(
			PROP_SIZE,
			PROP_WIDTH,
			PROP_HEIGHT,
			PROP_COLOR_R,
			PROP_COLOR_G,
			PROP_COLOR_B
		);
	/**
	 * the JavaFX element allowing the user to enter a minimum value for the reaction
	 */
	@FXML private TextField minValue;
	/**
	 * the JavaFX element allowing the user to enter a maximum value for the reaction
	 */
	@FXML private TextField maxValue;
	/**
	 * the JavaFX element telling the user how to enter a frequency range
	 */
	@FXML private Label freqText;
	/**
	 * the JavaFX element containing all frequency options. Is hidden when the selected type is 'Amplitude'
	 */
	@FXML private HBox freqContainer;
	/**
	 * the JavaFX element allowing the user to enter a minimum frequency that will affect the shape property
	 */
	@FXML private TextField minFreq;
	/**
	 * the JavaFX element allowing the user to enter a maximum frequency that will affect the shape property
	 */
	@FXML private TextField maxFreq;
	/**
	 * the JavaFX element allowing the user to apply the current reaction to the selected shape
	 */
	@FXML private Button apply;
	/**
	 * the JavaFX element allowing the user to cancel the changes made to the reaction
	 */
	@FXML private Button cancel;
	/**
	 * the JavaFX element telling the user whenever an error occurs.
	 */
	@FXML private Label error;
	
	/**
	 * the current reaction opened in this stage
	 */
	private Reaction reaction;
	
	/**
	 * constructs a reaction stage without displaying it
	 */
	public ReactionStage() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/ReactionStageLayout.fxml"));
	    fxmlLoader.setController(this);
	    
	    Parent p;
	    try {
	        p = fxmlLoader.load();
	    } catch (IOException exception) {
	        throw new RuntimeException(exception);
	    }
	    
	    type.setItems(typeOptions);
	    property.setItems(propertyOptions);
	    
	    stage.setTitle("Create/Modify a Reaction");
        stage.setScene(new Scene(p));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/Amp.png")));
        stage.setResizable(false);
	}
	
	/**
	 * displays the reaction stage and update the values of the JavaFX elements based on the selected reaction
	 * @param reaction the selected reaction
	 */
	public void show(Reaction reaction) {
		this.reaction = reaction;
		error.setVisible(false);
		
		if(reaction ==  null) {
			type.setValue(TYPE_AMPLITUDE);
			property.setValue(PROP_SIZE);
			minValue.setText("0");
			maxValue.setText("2");
			minFreq.setText("100");
			maxFreq.setText("150");
			
			freqText.setVisible(false);
			freqContainer.setVisible(false);
		}
		else {
			if(reaction instanceof AmplitudeReaction) {
				type.setValue(TYPE_AMPLITUDE);
				freqText.setVisible(false);
				freqContainer.setVisible(false);
			}
			else {
				type.setValue(TYPE_FREQUENCY);
				minFreq.setText("" + ((FrequencyReaction) reaction).getMinFreq());
				maxFreq.setText("" + ((FrequencyReaction) reaction).getMaxFreq());
				freqText.setVisible(true);
				freqContainer.setVisible(true);
			}
			
			switch(reaction.getReactionType()) {
			case Reaction.SIZE:
				property.setValue(PROP_SIZE);
				break;
			case Reaction.WIDTH:
				property.setValue(PROP_WIDTH);
				break;
			case Reaction.HEIGHT:
				property.setValue(PROP_HEIGHT);
				break;
			case Reaction.COLOR_RED:
				property.setValue(PROP_COLOR_R);
				break;
			case Reaction.COLOR_GREEN:
				property.setValue(PROP_COLOR_G);
				break;
			case Reaction.COLOR_BLUE:
				property.setValue(PROP_COLOR_B);
				break;
			}
			
			minValue.setText("" + reaction.getMinValue());
			maxValue.setText("" + reaction.getMaxValue());
		}
		
		stage.show();
	}
	
	/**
	 * hides the stage
	 */
	public void hide() {
		stage.hide();
		MainStage.getInstance().getReactionsPane().show(MainStage.getInstance().getSelectedShape()); //Resets the buttons in the reactions pane
	}
	
	/**
	 * helper method to get the currently selected shape property that the reaction affects
	 * @return the selected reaction property in the 'property' combo box
	 */
	private int getReactionProperty() {
		switch(property.getValue()) {
		case PROP_SIZE:
			return Reaction.SIZE;
		case PROP_WIDTH:
			return Reaction.WIDTH;
		case PROP_HEIGHT:
			return Reaction.HEIGHT;
		case PROP_COLOR_R:
			return Reaction.COLOR_RED;
		case PROP_COLOR_G:
			return Reaction.COLOR_GREEN;
		default:
			return Reaction.COLOR_BLUE;
		}
	}
	
	//===== EVENTS ======
	/**
	 * JavaFX event triggered when the user presses the cancel button
	 * @param e the JavaFX event
	 */
	@FXML
	protected void cancelPressed(ActionEvent e) {
		hide();
	}
	
	/**
	 * JavaFX event triggered when the user presses the apply button. Updates the selected reaction
	 * @param e the JavaFX event
	 */
	@FXML
	protected void applyPressed(ActionEvent e) {
		try {
			if(reaction != null)
				MainStage.getInstance().getSelectedShape().removeReaction(reaction);
			
			if(type.getValue() == TYPE_AMPLITUDE)
				reaction = new AmplitudeReaction(Double.parseDouble(minValue.getText()), Double.parseDouble(maxValue.getText()), getReactionProperty());
			else
				reaction = new FrequencyReaction(Integer.parseInt(minFreq.getText()), Integer.parseInt(maxFreq.getText()), Double.parseDouble(minValue.getText()), Double.parseDouble(maxValue.getText()), getReactionProperty());
			
			MainStage.getInstance().getSelectedShape().addReaction(reaction); //Add the reaction to the selected shape
			MainStage.getInstance().getSelectedShape().update();
			MainStage.getInstance().getSelectedShape().scale();
			
			hide(); //hide only if no errors occured
				
		} catch(Exception ex) {
			//Some values were not double or integers
			error.setVisible(true);
		}
	}
	
	/**
	 * JavaFX event triggered when the user selects a new reaction type
	 * @param e the JavaFX event
	 */
	@FXML
	protected void typeChanged(ActionEvent e) {
		if(type.getValue() == TYPE_AMPLITUDE) {
			freqText.setVisible(false);
			freqContainer.setVisible(false);
		}
		else {
			minFreq.setText("100");
			maxFreq.setText("150");
			freqText.setVisible(true);
			freqContainer.setVisible(true);
		}
	}
	
	/**
	 * JavaFX event triggered when the user presses the delete button. Deletes the selected reaction
	 * @param e the JavaFX event
	 */
	@FXML
	protected void deletePressed(ActionEvent e) {
		if(reaction != null) {
			MainStage.getInstance().getSelectedShape().removeReaction(reaction);
			MainStage.getInstance().getSelectedShape().update(); //Refresh the current size of the shape
			MainStage.getInstance().getSelectedShape().scale();
			hide();
		}
	}
}
