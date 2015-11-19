package reactions;

import nodes.MainStage;

/**
 * This class represents a specific type or Reaction that can affect a Shape.
 * It gets the amplitude of the of the sound from the AudioPlayer class and modifies the Shape accordingly.
 */
public class AmplitudeReaction extends Reaction {
	
	private static final long serialVersionUID = -1741654148553928996L;

	/**
	 * constructs a new amplitude reaction with the given parameters
	 * @param minValue the minimum value of the shape property this reaction is affecting
	 * @param maxValue the maximum value of the shape property this reaction is affecting
	 * @param reactionType the reaction type (the shape property affected by this reaction)
	 */
	public AmplitudeReaction(double minValue, double maxValue, int reactionType) {
		super(minValue, maxValue, reactionType);
	}

	/* (non-Javadoc)
	 * @see reactions.Reaction#update()
	 */
	@Override
	public double update() {
		double factor = MainStage.getInstance().getPlayer().getAmplitude(); //Ranges from 0 to 1
		double value = getMinValue() + factor * (getMaxValue() - getMinValue());
		return value;
	}
}
