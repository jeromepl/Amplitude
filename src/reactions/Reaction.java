package reactions;

import java.io.Serializable;

/**
 * Abstract class that updates a shape property in function of a specific component of the sound
 */
public abstract class Reaction implements Serializable {
	
	private static final long serialVersionUID = 420176639566388751L;

	/**
	 * all the possible reaction types
	 */
	public static final int SIZE = 0, WIDTH = 1, HEIGHT = 2, COLOR_RED = 3, COLOR_GREEN = 4, COLOR_BLUE = 5;
	
	/**
	 * the minimum value of the shape property this reaction is affecting
	 */
	private double minValue;
	/**
	 * the maximum value of the shape property this reaction is affecting
	 */
	private double maxValue;
	/**
	 * the reaction type (the shape property affected by this reaction)
	 */
	private int reactionType;
	
	/**
	 * constructs a new reaction with the specified parameters
	 * @param minValue the minimum value of the shape's property this reaction is affecting
	 * @param maxValue the maximum value of the shape's property this reaction is affecting
	 * @param reactionType the reaction type (the shape property affected by this reaction)
	 */
	public Reaction(double minValue, double maxValue, int reactionType) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.reactionType = reactionType;
		
		if(reactionType == COLOR_RED || reactionType == COLOR_GREEN || reactionType == COLOR_BLUE) {
			this.minValue %= 255; //Prevent going over 255
			this.maxValue %= 255;
		}
	}
	
	/**
	 * updates the value of the shape property based on the reaction
	 * @return the updated value
	 */
	public abstract double update();

	/**
	 * @return the minimum value of the shape property
	 */
	public double getMinValue() {
		return minValue;
	}

	/**
	 * sets a new minimum value of the shape property
	 * @param minValue the new minimum value
	 */
	public void setMinValue(double minValue) {
		this.minValue = minValue;
		
		if(reactionType == COLOR_RED || reactionType == COLOR_GREEN || reactionType == COLOR_BLUE) {
			this.minValue %= 255; //Prevent going over 255
		}
	}

	/**
	 * @return the maximum value of the shape property
	 */
	public double getMaxValue() {
		return maxValue;
	}

	/**
	 * sets a new maximum value of the shape property
	 * @param maxValue the new maximum value
	 */
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
		
		if(reactionType == COLOR_RED || reactionType == COLOR_GREEN || reactionType == COLOR_BLUE) {
			this.maxValue %= 255; //Prevent going over 255
		}
	}

	/**
	 * @return the reaction type (the affected shape property)
	 */
	public int getReactionType() {
		return reactionType;
	}

	/**
	 * sets a new reaction type (a new affected shape property)
	 * @param reactionType the new reaction type
	 */
	public void setReactionType(int reactionType) {
		this.reactionType = reactionType;
	}
	
}
