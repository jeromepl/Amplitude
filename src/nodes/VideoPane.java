package nodes;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * This class is the Pane in which the shapes are animated and rendered.
 * It provides a viewport for the user to see what the final result of his creation will be.
 */
public class VideoPane extends StackPane implements EventHandler<MouseEvent> {
	
	/**
	 * the x component of the aspect ratio of the video pane. Used to keep the video pane at a constant width/height ratio
	 */
	public static final double RATIO_X = 16;
	/**
	 * the y component of the aspect ratio of the video pane. Used to keep the video pane at a constant width/height ratio
	 */
	public static final double RATIO_Y = 9;
	
	/**
	 * the JavaFX element that keeps a constant ratio and contains all shapes
	 */
	@FXML private Pane innerPane; //Its initial size if 1x1, which allows the scale method to work in pixels
	/**
	 * a rectangle that specifies the size of the clip which prevents all shapes from being displayed outside of the video pane
	 */
	private Rectangle clip = new Rectangle(); //Used to clip elements outside the video pane
	
	/**
	 * the initial width of the video pane
	 */
	private double initWidth;
	/**
	 * the initial height of the video pane
	 */
	private double initHeight;
	
	/**
	 * constructs a video pane and loads the corresponding FXML in the main stage
	 */
	public VideoPane() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/VideoLayout.fxml"));
	    fxmlLoader.setRoot(this);
	    fxmlLoader.setController(this);
	    try {
	        fxmlLoader.load();
	    } catch (IOException exception) {
	        throw new RuntimeException(exception);
	    }
	    
	    setBackground(Color.WHITE);
	    innerPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
	    
	    innerPane.setOnMouseClicked(this); //used to select the background
	}
	
	/**
	 * sets a background image to the video pane
	 * @param image the new background image
	 */
	public void setBackground(Image image) {
		BackgroundSize bgSize = new BackgroundSize(innerPane.getWidth(), innerPane.getHeight(), false, false, true, true);
		innerPane.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize)));
	}
	
	/**
	 * sets a background color to the video pane
	 * @param color the new background color
	 */
	public void setBackground(Color color) {
		innerPane.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
	}
	
	/**
	 * clears the video pane. Removes all shapes
	 */
	public void clear() {
		innerPane.getChildren().clear();
	}
	
	/**
	 * add a shape to be displayed in the video pane
	 * @param shape the shape to be added
	 */
	public void addShape(Shape shape) {
		innerPane.getChildren().add(shape);
	}
	
	/**
	 * set the initial size of the video pane. Used for scaling
	 */
	public void setInitSize() {
		resize(); //Initialize the inside pane to the right size
		initWidth = innerPane.getPrefWidth();
	    initHeight = innerPane.getPrefHeight();
	}
	
	/**
	 * resizes the video pane based on the aspect ratio and the size of the window
	 */
	public void resize() {
		//Remember that getWidth() and getHeight() return the width and height of the CONTAINER pane
		if(getWidth() / RATIO_X > getHeight() / RATIO_Y) { //The limiting size is the height
			innerPane.setPrefHeight(getHeight()) ; 
			innerPane.setPrefWidth(getHeight() / RATIO_Y * RATIO_X) ;
		}
		else { //The limiting size is the width
			innerPane.setPrefWidth(getWidth());
			innerPane.setPrefHeight(getWidth() / RATIO_X * RATIO_Y);
		}
		
		//The clip hides element outside the bounds of the video pane
		clip.setWidth(innerPane.getPrefWidth());
		clip.setHeight(innerPane.getPrefHeight());
		innerPane.setClip(clip);
	}
	
	/**
	 * @return the inner pane containing all the shapes
	 */
	public Pane getInnerPane() {
		return innerPane;
	}
	
	/**
	 * @return the scaling in x since the setInitSize was last called
	 */
	public double getWidthFactor() {
		return innerPane.getPrefWidth() / initWidth;
	}
	
	/**
	 * @return the scaling in y since the setInitSize was last called
	 */
	public double getHeightFactor() {
		return innerPane.getPrefHeight() / initHeight;
	}

	/**
	 * removes a shape from the video pane
	 * @param shape the shape to remove
	 */
	public void removeShape(Shape shape) {
		innerPane.getChildren().remove(shape);
	}

	/* (non-Javadoc)
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(MouseEvent e) {
		if(e.getTarget() instanceof Pane) {
			MainStage.getInstance().selectShape(null); //Selecting null means that no shape is selected
		}
	}
}
