package shapes;

import java.util.ArrayList;
import nodes.MainStage;
import nodes.VideoPane;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import reactions.AmplitudeReaction;
import reactions.FrequencyReaction;
import reactions.Reaction;

/**
 * Class that represents all shapes, allows to edit them, add reactions to them and finally to update and render them on the screen.
 */
public class ReactiveShape implements EventHandler<MouseEvent> {
	
	/**
	 * static value of the all the shape types
	 */
	public static final int RECTANGLE = 0, CIRCLE = 1, TRIANGLE = 2;

	/**
	 * the shape type of this shape
	 */
	private int shapeType;
	/**
	 * all the reactions attached to this shape
	 */
	private ArrayList<Reaction> reactions = new ArrayList<Reaction>();
	/**
	 * the color of this shape
	 */
	private Color color;
	/**
	 * whether this shape is filled or not
	 */
	private boolean filled;
	/**
	 * the border thickness, in pixels, of this shape
	 */
	private int borderThickness;
	/**
	 * the border color of this shape
	 */
	private Color borderColor;
	/**
	 * the width, in pixel, of this shape
	 */
	private double width;
	/**
	 * the height, in pixel, of this shape
	 */
	private double height;
	/**
	 * the x position, in pixel, of this shape
	 */
	private double xPosition; // The position is centered in all shapes
	/**
	 * the y position, in pixel, of this shape
	 */
	private double yPosition;
	/**
	 * the JavaFX shape that is rendered on the screen
	 */
	private Shape fxShape;
	
	/**
	 * the initial width, in pixel, of this shape. Used for scaling
	 */
	private double initWidth;
	/**
	 * the initial height, in pixel, of this shape. Used for scaling
	 */
	private double initHeight;
	/**
	 * the initial x position, in pixel, of this shape. Used for scaling
	 */
	private double initX;
	/**
	 * the initial y position, in pixel, of this shape. Used for scaling
	 */
	private double initY;
	
	/**
	 * the initial pressed layout x position, in pixels. Used for drag and drop
	 */
	private double initPressedLayoutX;
	/**
	 * the initial pressed layout y position, in pixels. Used for drag and drop
	 */
	private double initPressedLayoutY;
	/**
	 * the initial drag x position, in pixels. Used for drag and drop
	 */
	private double initDragX;
	/**
	 * the initial drag y position, in pixels. Used for drag and drop
	 */
	private double initDragY;
	/**
	 * the initial pressed x position, in pixels. Used for drag and drop
	 */
	private double initPressedXPosition;
	/**
	 * the initial pressed y position, in pixels. Used for drag and drop
	 */
	private double initPressedYPosition;
	
	/**
	 * constructs a default reaction shape with the given parameters
	 * @param shapeType the shape type
	 */
	public ReactiveShape(int shapeType) {
		this(shapeType, (MainStage.getInstance().getVideoPane().getInnerPane().getWidth() / 2 - 40 / 2) / MainStage.getInstance().getVideoPane().getWidthFactor(),
				(MainStage.getInstance().getVideoPane().getInnerPane().getHeight() / 2 - 40 / 2) / MainStage.getInstance().getVideoPane().getHeightFactor(),
				40, 40, Color.BLACK, true, 2, Color.LIGHTGRAY);
	}
	
	/**
	 * constructs a reactive shape with the given parameters
	 * @param shapeType the shape type
	 * @param xPosition the x position, in pixels
	 * @param yPosition the y position, in pixels
	 * @param width the width, in pixels
	 * @param height the height, in pixels
	 * @param color the color
	 * @param filled whether this shape is filled or not
	 * @param borderThickness the border thickness, in pixels
	 * @param borderColor the border color
	 */
	public ReactiveShape(int shapeType, double xPosition, double yPosition, double width, double height, Color color, boolean filled, int borderThickness, Color borderColor) {
		this.xPosition = initX = xPosition;
		this.yPosition = initY = yPosition;
		this.width = initWidth = width;
		this.height = initHeight = height;
		this.color = color;
		this.filled = filled;
		this.borderThickness = borderThickness;
		this.borderColor = borderColor;
		this.shapeType = shapeType;
		
		scale(); //Creates the JavaFX shape and scales it
	}
	
	/**
	 * updates all properties of this shape based on all its reactions
	 */
	public void update() {
		for(Reaction reaction: reactions) {
			double value = reaction.update();
			switch(reaction.getReactionType()) {
			case Reaction.SIZE:
				fxShape.setScaleX(value);
				fxShape.setScaleY(value);
				break;
			case Reaction.WIDTH:
				fxShape.setScaleX(value);
				break;
			case Reaction.HEIGHT:
				fxShape.setScaleY(value);
				break;
			case Reaction.COLOR_BLUE:
				fxShape.setFill(Color.color(((Color)fxShape.getFill()).getRed(), ((Color)fxShape.getFill()).getGreen(), value / 255.0));
				break;
			case Reaction.COLOR_GREEN:
				fxShape.setFill(Color.color(((Color)fxShape.getFill()).getRed(), value / 255.0, ((Color)fxShape.getFill()).getBlue()));
				break;
			case Reaction.COLOR_RED:
				fxShape.setFill(Color.color(value / 255.0, ((Color)fxShape.getFill()).getGreen(), ((Color)fxShape.getFill()).getBlue()));
				break;
			}
		}
		
		if(reactions.size() == 0) {
			fxShape.setScaleX(1);
			fxShape.setScaleY(1);
			fxShape.setFill(color);
		}
	}
	
	/**
	 * scales the shapes according to the size of the window
	 */
	public void scale() {
		VideoPane vp = MainStage.getInstance().getVideoPane();
		width = vp.getWidthFactor() * initWidth;
		height = vp.getHeightFactor() * initHeight;
		
		xPosition = vp.getWidthFactor() * initX;
		yPosition = vp.getHeightFactor() * initY;
		
		setShapeType(shapeType);
	}
	
	/**
	 * sets the shape type of this shape
	 * @param shapeType the new shape type
	 */
	public void setShapeType(int shapeType) {
		MainStage.getInstance().getVideoPane().removeShape(fxShape);
		
		this.shapeType = shapeType;
		//Create the JavaFX shape
		switch(shapeType) {
		case RECTANGLE:
			fxShape = new Rectangle(xPosition - width / 2, yPosition - height / 2, width, height);
			break;
		case CIRCLE:
			fxShape = new Ellipse(xPosition, yPosition, width / 2, height / 2);
			break;
		case TRIANGLE:
			Polygon polygon = new Polygon();
			polygon.getPoints().addAll(new Double[]{
			    xPosition, yPosition - height / 2,
			    xPosition - width / 2, yPosition + height / 2,
			    xPosition + width / 2, yPosition + height / 2});
			fxShape = polygon;
			break;
		}
		
		if(filled)
			fxShape.setFill(color);
		else
			fxShape.setFill(Color.TRANSPARENT);
		
		if(borderThickness > 0) {
			fxShape.setStrokeWidth(borderThickness);
			fxShape.setStroke(borderColor);
		}
		
		fxShape.setOnMousePressed(this);
		fxShape.setOnMouseDragged(this); //Set the event to move the shape when dragging it
		fxShape.setOnMouseReleased(this);
		fxShape.setOnMouseClicked(this);
		
		MainStage.getInstance().getVideoPane().addShape(fxShape);
		update();
	}
	
	/**
	 * @return all the reactions linked to this shape
	 */
	public ArrayList<Reaction> getReactions() {
		return reactions;
	}
	
	/**
	 * @return all the amplitude reactions linked to this shape
	 */
	public ArrayList<AmplitudeReaction> getAmplitudeReactions() {
		ArrayList<AmplitudeReaction> amps = new ArrayList<AmplitudeReaction>();
		for(Reaction r: reactions) {
			if(r instanceof AmplitudeReaction) amps.add((AmplitudeReaction) r);
		}
		
		return amps;
	}
	
	/**
	 * @return all the frequency reactions linked to this shape
	 */
	public ArrayList<FrequencyReaction> getFrequencyReactions() {
		ArrayList<FrequencyReaction> freqs = new ArrayList<FrequencyReaction>();
		for(Reaction r: reactions) {
			if(r instanceof FrequencyReaction) freqs.add((FrequencyReaction) r);
		}
		
		return freqs;
	}
	
	/**
	 * adds a reaction to this shape
	 * @param reaction the reaction to add
	 */
	public void addReaction(Reaction reaction) {
		reactions.add(reaction);
	}
	
	/**
	 * removes a reaction from this shape
	 * @param reaction the reaction to remove
	 */
	public void removeReaction(Reaction reaction) {
		reactions.remove(reaction);
	}

	/**
	 * @return the color of this shape
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * sets the color of this shape
	 * @param color the new color
	 */
	public void setColor(Color color) {
		this.color = color;
		setShapeType(shapeType);
	}

	/**
	 * @return whether this shape is filled or not
	 */
	public boolean isFilled() {
		return filled;
	}

	/**
	 * sets whether this shape is filled or not
	 * @param filled whether this shape is filled or not
	 */
	public void setFilled(boolean filled) {
		this.filled = filled;
		setShapeType(shapeType);
	}

	/**
	 * @return the border thickness of this shape
	 */
	public int getBorderThickness() {
		return borderThickness;
	}

	/**
	 * sets the border thickness of this shape
	 * @param borderThickness the new border thickness
	 */
	public void setBorderThickness(int borderThickness) {
		this.borderThickness = borderThickness;
		setShapeType(shapeType);
	}

	/**
	 * @return the x position, in pixels, of this shape
	 */
	public double getXPosition() {
		return xPosition;
	}

	/**
	 * sets the x position, in pixels, of this shape
	 * @param xPosition the new x position
	 */
	public void setXPosition(double xPosition) {
		this.xPosition = xPosition;
	}

	/**
	 * @return the y position, in pixels, of this shape
	 */
	public double getYPosition() {
		return yPosition;
	}

	/**
	 * sets the y position, in pixels, of this shape
	 * @param yPosition the new y position
	 */
	public void setYPosition(double yPosition) {
		this.yPosition = yPosition;
	}

	/**
	 * @return the JavaFX shape rendered on the screen
	 */
	public Shape getFXShape() {
		return fxShape;
	}

	/**
	 * @return the shape type of this shape
	 */
	public int getShapeType() {
		return shapeType;
	}

	/**
	 * @return the width, in pixels, of this shape
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * sets the width, in pixels, of this shape
	 * @param width the new width
	 */
	public void setWidth(double width) {
		initWidth = width / MainStage.getInstance().getVideoPane().getWidthFactor();
		scale();
	}

	/**
	 * @return the height, in pixels, of this shape
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * sets the height, in pixels, of this shape
	 * @param height the new height
	 */
	public void setHeight(double height) {
		initHeight = height / MainStage.getInstance().getVideoPane().getHeightFactor();
		scale();
	}

	/**
	 * @return the border color of this shape
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * sets the border color of this shape
	 * @param borderColor the new border color
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		setShapeType(shapeType);
	}

	/* (non-Javadoc)
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(MouseEvent e) {
		if(e.getEventType() == MouseEvent.MOUSE_PRESSED) { //Should be triggered before the drag, sets the last x and y positions
			initDragX = e.getSceneX();
			initDragY = e.getSceneY();
			initPressedLayoutX = fxShape.getLayoutX();
			initPressedLayoutY = fxShape.getLayoutY();
			initPressedXPosition = xPosition;
			initPressedYPosition = yPosition;
		}
		else if(e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			xPosition = initPressedLayoutX + e.getSceneX() - initDragX;
			yPosition = initPressedLayoutY + e.getSceneY() - initDragY;
			
			fxShape.setLayoutX(xPosition);
			fxShape.setLayoutY(yPosition);
			
			//Reset the initX and Y so that the scale works fine
			VideoPane vp = MainStage.getInstance().getVideoPane();
			initX = (initPressedXPosition + e.getSceneX() - initDragX) / vp.getWidthFactor();
			initY = (initPressedYPosition + e.getSceneY() - initDragY) / vp.getHeightFactor();
		}
		else if(e.getEventType() == MouseEvent.MOUSE_RELEASED) {
			scale();
			MainStage.getInstance().selectShape(this);
		}
	}
}
