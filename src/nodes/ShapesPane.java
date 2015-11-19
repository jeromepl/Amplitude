package nodes;

import java.io.IOException;

import shapes.ReactiveShape;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;

/**
 * This Pane contains three buttons, each of which creates a different shape.
 */
public class ShapesPane extends FlowPane {

	/**
	 * constructs a shape pane and loads the corresponding FXML in the main stage
	 */
	public ShapesPane() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/ShapesLayout.fxml"));
	    fxmlLoader.setRoot(this);
	    fxmlLoader.setController(this);
	    try {
	        fxmlLoader.load();
	    } catch (IOException exception) {
	        throw new RuntimeException(exception);
	    }
	}
	
	/**
	 * method triggered by JavaFX when the user presses the rectangle button
	 * @param e the JavaFX event
	 */
	@FXML
	protected void rectangleClicked(ActionEvent e) {
		addShape(ReactiveShape.RECTANGLE);
	}
	
	/**
	 * method triggered by JavaFX when the user presses the circle button
	 * @param e the JavaFX event
	 */
	@FXML
	protected void circleClicked(ActionEvent e) {
		addShape(ReactiveShape.CIRCLE);
	}
	
	/**
	 * method triggered by JavaFX when the user presses the triangle button
	 * @param e the JavaFX event
	 */
	@FXML
	protected void triangleClicked(ActionEvent e) {
		addShape(ReactiveShape.TRIANGLE);
	}
	
	/**
	 * adds a shape to the main stage and updates it
	 * @param shapeType the type of the new shape
	 */
	private void addShape(int shapeType) {
		ReactiveShape shape = new ReactiveShape(shapeType);
		MainStage.getInstance().addShape(shape);
		MainStage.getInstance().selectShape(shape);
	}
}
