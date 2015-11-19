package nodes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import shapes.ReactiveShape;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

/**
 * This Pane contains all the buttons, text fields and other input methods that allow the user to modify the shape however he prefers. All the fields of this class are retrieved from the ShapeEditingLayout with JavaFX.
 */
public class EditingPane extends StackPane {
	
	/**
	 * the rectangle type. Used in the combo box
	 */
	private static final String TYPE_RECTANGLE= "Rectangle";
	/**
	 * the circle type. Used in the combo box
	 */
	private static final String TYPE_CIRCLE = "Circle";
	/**
	 * the triangle type. Used in the combo box
	 */
	private static final String TYPE_TRIANGLE = "Triangle";
	
	/**
	 * the JavaFX element containing the background
	 */
	@FXML private VBox backgroundPane;
	/**
	 * the JavaFX element containing the shapes' options
	 */
	@FXML private FlowPane shapePane;
	
	//BACKGROUND VARIABLES:
	/**
	 * the JavaFX element allowing the user to select a color for the background
	 */
	@FXML private ColorPicker bgColorPicker;
	
	//SHAPE VARIABLES:
	/**
	 * the currently selected shape
	 */
	private ReactiveShape shape;
	/**
	 * the list of options to display in the combo box
	 */
	private ObservableList<String> typeOptions = FXCollections.observableArrayList(
			TYPE_RECTANGLE,
		    TYPE_CIRCLE,
		    TYPE_TRIANGLE
		);
	/**
	 * the JavaFX element containing a list of all shape types
	 */
	@FXML private ComboBox<String> type;
	/**
	 * the JavaFX element allowing the user to select a color for the shape
	 */
	@FXML private ColorPicker color;
	/**
	 * the JavaFX element allowing the user to select whether the shape is filled or not
	 */
	@FXML private CheckBox filled;
	/**
	 * the JavaFX element allowing the user to enter a border thickness value for the shape
	 */
	@FXML private TextField borderThickness;
	/**
	 * the JavaFX element allowing the user to select a border color for the shape
	 */
	@FXML private ColorPicker borderColor;
	/**
	 * the JavaFX element allowing the user to enter a width for the shape
	 */
	@FXML private TextField width;
	/**
	 * the JavaFX element allowing the user to enter a height for the shape
	 */
	@FXML private TextField height;
	/**
	 * the JavaFX element allowing the user to delete the shape
	 */
	@FXML private Button delete;
	/**
	 * the JavaFX element shown to tell the user if one if the entered value is incorrect
	 */
	@FXML private Label error;

	/**
	 * constructs a new editing pane and loads the FXML into the main stage
	 */
	public EditingPane() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/EditingLayout.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		
		type.setItems(typeOptions);
		
		backgroundSelected();
	}
	
	/**
	 * shows the options to edit the background
	 */
	public void backgroundSelected() {
		backgroundPane.setVisible(true);
		shapePane.setVisible(false);
	}
	
	/**
	 * shows the options to edit the selected shape
	 * @param selectedShape the shape currently selected
	 */
	public void shapeSelected(ReactiveShape selectedShape) {
		shape = selectedShape;
		
		switch(shape.getShapeType()) {
		case ReactiveShape.RECTANGLE:
			type.setValue(TYPE_RECTANGLE);
			break;
		case ReactiveShape.CIRCLE:
			type.setValue(TYPE_CIRCLE);
			break;
		case ReactiveShape.TRIANGLE:
			type.setValue(TYPE_TRIANGLE);
			break;
		}
		
		backgroundPane.setVisible(false);
		shapePane.setVisible(true);
		
		color.setValue(shape.getColor());
		filled.setSelected(shape.isFilled());
		borderThickness.setText("" + shape.getBorderThickness());
		borderColor.setValue(shape.getBorderColor());
		width.setText("" + (int) shape.getWidth());
		height.setText("" + (int) shape.getHeight());
	}
	
	/**
	 * sets the color value of the background color picker
	 * @param color the new color to which the color picker will be set
	 */
	public void setColorPickerValue(Color color) {
		bgColorPicker.setValue(color);
	}
	
	//====== BACKGROUND EVENTS ========
	/**
	 * JavaFX event triggered when the user selects a new background color
	 * @param e the event
	 */
	@FXML
	protected void bgColorPicked(ActionEvent e) {
		MainStage.getInstance().getVideoPane().setBackground(bgColorPicker.getValue());
	}
	
	/**
	 * JavaFX event triggered when the user clicks the browse button to select a background image
	 * @param e the JavaFX event
	 */
	@FXML
	protected void browseImagePressed(ActionEvent e) {
		FileChooser fileChooser = new FileChooser(); //Create a new file chooser to allow the user to pick an image
		fileChooser.setTitle("Select an Image");
		
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG"); //Set extension filter
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        
		File imageFile = fileChooser.showOpenDialog(MainStage.getInstance().getPrimaryStage());
		
		if(imageFile != null && imageFile.exists()) {
			try {
                BufferedImage bufferedImage = ImageIO.read(imageFile);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                MainStage.getInstance().getVideoPane().setBackground(image);
            } catch (IOException ex) {
                System.out.println("Error while loading the image");
            }
		}
	}
	
	//====== SHAPE EVENTS ========
	/**
	 * JavaFX event triggered when the user selects a new shape type
	 * @param e the JavaFX event
	 */
	@FXML
	protected void typeChanged(ActionEvent e) {
		switch(type.getValue()) {
		case TYPE_RECTANGLE:
			shape.setShapeType(ReactiveShape.RECTANGLE);
			break;
		case TYPE_CIRCLE:
			shape.setShapeType(ReactiveShape.CIRCLE);
			break;
		case TYPE_TRIANGLE:
			shape.setShapeType(ReactiveShape.TRIANGLE);
			break;
		}
	}
	
	/**
	 * JavaFX event triggered when the user selects a new color for the shape
	 * @param e the JavaFX event
	 */
	@FXML
	protected void colorPicked(ActionEvent e) {
		shape.setColor(color.getValue());
	}
	
	/**
	 * JavaFX event triggered when the user checks or unchecks the 'filled' check box
	 * @param e the JavaFX event
	 */
	@FXML
	protected void filledPressed(ActionEvent e) {
		shape.setFilled(filled.isSelected());
	}
	
	/**
	 * checks if there are any errors in the values entered by the user
	 */
	private void checkErrors() {
		try {
			Integer.parseInt(borderThickness.getText());
			Integer.parseInt(width.getText());
			Integer.parseInt(height.getText());
			error.setVisible(false); //Only hide the error message if no exceptions were thrown before
		} catch(Exception e) {
			//Don't do anything if an exception is thrown. The error just won't be hidden
		}
	}
	
	/**
	 * JavaFX event triggered when the user changes the border thickness of the shape
	 * @param e the JavaFX event
	 */
	@FXML
	protected void borderThicknessChanged(KeyEvent e) {
		try {
			shape.setBorderThickness(Integer.parseInt(borderThickness.getText()));
			checkErrors();
		} catch(Exception ex) {
			//The user did not enter an integer
			error.setVisible(true);
		}
	}
	
	/**
	 * JavaFX event triggered when the user changes the border color of the shape
	 * @param e the JavaFX event
	 */
	@FXML
	protected void borderColorPicked(ActionEvent e) {
		shape.setBorderColor(borderColor.getValue());
	}
	
	/**
	 * JavaFX event triggered when the user enters a new width for the shape
	 * @param e the JavaFX event
	 */
	@FXML
	protected void widthChanged(KeyEvent e) {
		try {
			shape.setWidth(Integer.parseInt(width.getText()));
			checkErrors();
		} catch(Exception ex) {
			//The user did not enter an integer
			error.setVisible(true);
		}
	}
	
	/**
	 * JavaFX event triggered when the user enters a new height for the shape
	 * @param e the JavaFX event
	 */
	@FXML
	protected void heightChanged(KeyEvent e) {
		try {
			shape.setHeight(Integer.parseInt(height.getText()));
			checkErrors();
		} catch(Exception ex) {
			//The user did not enter an integer
			error.setVisible(true);
		}
	}
	
	/**
	 * JavaFX event triggered when the user presses the delete button
	 * @param e the JavaFX event
	 */
	@FXML
	protected void deletePressed(ActionEvent e) {
		MainStage.getInstance().getVideoPane().removeShape(shape.getFXShape());
		MainStage.getInstance().removeShape(shape);
		backgroundSelected();
	}
}
