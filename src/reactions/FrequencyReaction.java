package reactions;

import ddf.minim.analysis.FFT;
import nodes.MainStage;

/**
 * This class represents a specific type or Reaction that can affect a Shape.
 * It gets the amplitude of the frequencies of the sound from the FFT class and modifies the Shape accordingly.
 */
public class FrequencyReaction extends Reaction {
	
	private static final long serialVersionUID = -3596912037300167264L;
	
	/**
	 * the lower bound of the frequency range affecting the shape property
	 */
	private int minFreq;
	/**
	 * the higher bound of the frequency range affecting the shape property
	 */
	private int maxFreq;
	
	/**
	 * constructs a frequency reaction with the given parameters
	 * @param minFreq the lower bound of the frequency range affecting the shape property
	 * @param maxFreq the higher bound of the frequency range affecting the shape property
	 * @param minValue the minimum value of the shape property this reaction is affecting
	 * @param maxValue the maximum value of the shape property this reaction is affecting
	 * @param reactionType the reaction type (the shape property affected by this reaction)
	 */
	public FrequencyReaction(int minFreq, int maxFreq, double minValue, double maxValue, int reactionType) {
		super(minValue, maxValue, reactionType);
		
		this.minFreq = minFreq;
		this.maxFreq = maxFreq;
	}

	/* (non-Javadoc)
	 * @see reactions.Reaction#update()
	 */
	@Override
	public double update() {
		FFT fft = MainStage.getInstance().getPlayer().getFft();
		double factor = Math.min(70, fft.calcAvg(minFreq, maxFreq)) / 70; //Math.min to make sure that the factor does not go below 1 (which would shrink the shape in a Size reaction)
		
		double value = getMinValue() + factor * (getMaxValue() - getMinValue());
		return value;
	}

	/**
	 * @return the lower bound of the frequency range
	 */
	public int getMinFreq() {
		return minFreq;
	}

	/**
	 * sets a new lower bound for the frequency range
	 * @param minFreq the new minimum frequency
	 */
	public void setMinFreq(int minFreq) {
		this.minFreq = minFreq;
	}

	/**
	 * @return the higher bound of the frequency range
	 */
	public int getMaxFreq() {
		return maxFreq;
	}

	/**
	 * sets a new higher bound for the frequency range
	 * @param maxFreq the new maximum frequency
	 */
	public void setMaxFreq(int maxFreq) {
		this.maxFreq = maxFreq;
	}
}
