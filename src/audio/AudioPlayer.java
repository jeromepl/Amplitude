package audio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import javafx.application.Platform;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import nodes.MainStage;
import ddf.minim.analysis.FFT;

/**
 * This class is responsible for the import and processing of the audio file.
 * In addition, it adds an easy way to play, pause, skip and set the volume of the sound playing.
 * It uses a buffer to load the sound part by part so that the FFT class can process the audio input in pieces.
 */
public class AudioPlayer implements Runnable {

	/**
	 * the buffer size of the audio sample. A big number will use more memory and pausing the sound will take longer. A small number will likely affect performances
	 */
	public static final int BUFFER_SIZE = 1024;
	/**
	 * the gain value at which the volume is considered to be zero
	 */
	private static final float MIN_GAIN = -15;

	/**
	 * the AudioInputStream containing the currently loaded sound
	 */
	private AudioInputStream audioInputStream;
	/**
	 * the AudioFormat object containing all information about the sound's encoding
	 */
	private AudioFormat audioFormat;
	/**
	 * the byte buffer containing the current sound sample
	 */
	private byte[] buffer;
	/**
	 * the buffer after all channels have been merged together
	 */
	private float[] mixBuffer = new float[BUFFER_SIZE];;
	/**
	 * all of the sound file's bytes
	 */
	private byte[] soundBytes;
	/**
	 * the filename of the currently loaded sound
	 */
	private String name;
	/**
	 * the current volume of the sound. Ranges from 0 to 1
	 */
	private float volume = 1f;
	/**
	 * whether the music is playing or paused at the moment
	 */
	private volatile boolean playing = false;
	/**
	 * whether the sound is muted or not at the moment
	 */
	private boolean muted = false;
	/**
	 * specifies whether to loop or not the sound when it reaches its end
	 */
	private boolean loop = false;
	
	/**
	 * the progression of the sound. Ranges from 0 to 1, 0 being the very beginning and 1 being the sound has reached its end
	 */
	private double progression = 0; //Ranges from 0 to 1
	/**
	 * the number of bytes that have been read. Used to calculate the progression
	 */
	private long totalBytesRead = 0; //Resets when the progression is changed
	/**
	 * the SourceDataLine that plays the sound with Java audio
	 */
	private SourceDataLine line = null; //The line that reads the audioInputStream
	
	/**
	 * the thread in which the sound is played and where the bytes are analyzed by the FFT
	 */
	private Thread thread;
	
	/**
	 * the FFT (stand for Fast Fourier Transform). Used to calculate the amplitude of the frequencies of the sound
	 */
	private FFT fft;

	/**
	 * constructs a new audio player with a sound file
	 * @param soundFile the audio file to play
	 * @param loop whether to loop the sound or not
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	public AudioPlayer(File soundFile, boolean loop) throws UnsupportedAudioFileException, IOException {
		
		if (soundFile == null || !soundFile.exists()) {
			throw new FileNotFoundException("The file " + soundFile.getAbsolutePath() + " can't be found");
		}
		
		this.loop = loop;
		
		soundBytes = Files.readAllBytes(soundFile.toPath());
		name = soundFile.getName();
		
		init();
	}
	
	/**
	 * constructs a new audio player with a sound file
	 * @param soundBytes the audio bytes to play
	 * @param name the file name of the sound
	 * @param loop whether to loop the sound or not
	 * @throws UnsupportedAudioFileException
	 */
	public AudioPlayer(byte[] soundBytes, String name, boolean loop) throws UnsupportedAudioFileException {
		this.soundBytes = soundBytes;
		this.name = name;
		this.loop = loop;
		
		init(); //Load the Audio Input Stream from the file
	}
	
	/**
	 * initializes the audio player. This method is triggered from both constructors
	 * @throws UnsupportedAudioFileException
	 */
	private void init() throws UnsupportedAudioFileException {
		loadAudioFile(); //Load the Audio Input Stream from the file
		
		// Get Audio Format information
		audioFormat = audioInputStream.getFormat();
		
		if(audioFormat.getSampleSizeInBits() / 8 != 2) //Currently supports only if the sample size is 2 bytes (for little and big endian)
			throw new UnsupportedAudioFileException();
		
		fft = new FFT(BUFFER_SIZE, audioFormat.getSampleRate());
		
		thread = new Thread(this);
		thread.start();
	}
	
	/**
	 * start to play the sound
	 */
	public void play() {
		playing = true;
	}
	
	/**
	 * stop playing the sound
	 */
	public void pause() {
		playing = false;
	}
	
	/**
	 * mute the sound
	 */
	public void mute() {
		muted = true;
	}
	
	/**
	 * unmute the sound
	 */
	public void unmute() {
		muted = false;
	}
	
	/**
	 * stop the audio player to free memory
	 */
	public void stop() {
		// close the line and the audio input stream
		pause();
		try {
			line.drain();
			line.close();
			audioInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		thread.interrupt();
	}
	
	/**
	 * load the audio file in the audio input stream
	 * @throws UnsupportedAudioFileException
	 */
	private void loadAudioFile() throws UnsupportedAudioFileException {
		try {
			//For mp3? https://blogs.oracle.com/kashmir/entry/java_sound_api_2_mp3
			audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(soundBytes));
			//audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		} catch (IOException e) {
			MainStage.getInstance().getErrorStage().show("An error occured while trying to load the sound. Sorry for the inconvenience.", "AP213");
		}
	}
	
	/**
	 * skip to a specific part of the sound
	 * @param progression the progression to which the audio player should skip (Ranges from 0 to 1)
	 */
	public void setProgression(float progression) {
		this.progression = progression;
		try {
			loadAudioFile();
			
			totalBytesRead = (long) (progression * soundBytes.length);
			audioInputStream.skip(totalBytesRead);
			
		} catch (UnsupportedAudioFileException|IOException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		// Handle opening the line
		FloatControl volumeControl = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(audioFormat, line.getBufferSize() / 5); //The second argument is the buffer size of the line. The bigger, the more delay, the smaller the more laggy
		
			volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Start playing the sound
		line.start();

		// Write the sound to an array of bytes
		int nBytesRead = 0;
		buffer = new byte[BUFFER_SIZE * audioFormat.getFrameSize()];
		while(!Thread.interrupted()) {
			if(playing) {
				try {
					nBytesRead = audioInputStream.read(buffer, 0, buffer.length);
					
					if (nBytesRead == -1) {
						if(loop) {
							setProgression(0);
						}
						continue; //Continue because the user could decide to rewind the song and continue to play the music
					}
					
					totalBytesRead += nBytesRead;
					
					for(int i = 0; i < BUFFER_SIZE; i++) {
						float avg = getSampleMix(i * audioFormat.getFrameSize());
						mixBuffer[i] = avg;
					}
	
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				fft.forward(mixBuffer);
				
				//Update the volume
				if(volume == 0 || muted) {
					volumeControl.setValue(volumeControl.getMinimum());
				}
				else {
					volumeControl.setValue((float) (Math.floor((volumeControl.getMaximum()- MIN_GAIN) * volume) + MIN_GAIN));
				}
				
				line.write(buffer, 0, nBytesRead);
				
				//Calculate the progress of the song
				progression = totalBytesRead * 1.0 / soundBytes.length;
				
				Platform.runLater(new Runnable() { //Only way to modify the GUI from another thread than the Application thread
				    @Override
				    public void run() {
				    	MainStage.getInstance().update();
				    }
				});
			}
		}
	}
	
	/**
	 * mixes all channels in to a float, taking care of the big and little endian conversions
	 * @param index
	 * @return the sample mix
	 */
	private float getSampleMix(int index) {
		float avg = 0;
		
		for(int i = 0; i < audioFormat.getChannels() * 2; i += 2) {
			if(audioFormat.isBigEndian()) {
				avg += (buffer[i + index] << 8) + buffer[i + index + 1];
			}
			else {
				avg += (buffer[i + index + 1] << 8) + buffer[i + index];
			}
		}
		
		avg /= audioFormat.getChannels();
		avg /= 32768;		
		return avg;
	}
	
	/**
	 * @return the amplitude of the current sound sample
	 */
	public float getAmplitude() {
		float sum = 0;
		for(int i = 0; i < BUFFER_SIZE; i++) {
			sum += Math.abs(mixBuffer[i]);
		}
		return sum / BUFFER_SIZE;
	}
	
	/**
	 * @return the sound progression (ranges from 0 to 1)
	 */
	public double getProgression() {
		return progression;
	}
	
	/**
	 * @return the fast fourier transform object
	 */
	public FFT getFft() {
		return fft;
	}
	
	/**
	 * @return the file name of the sound
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * sets the name of the sound
	 * @param name the new name of the sound
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the sound bytes in the sound file
	 */
	public byte[] getSoundBytes() {
		return soundBytes;
	}
	
	/**
	 * @return the buffer after the samples from each channel have been merged
	 */
	public float[] getMixBuffer() {
		return mixBuffer;
	}
	
	/**
	 * @return the current volume of the sound (Ranges from 0 to 1)
	 */
	public float getVolume() {
		return volume;
	}
	
	/**
	 * sets the volume of the sound
	 * @param volume the new volume of the sound
	 */
	public void setVolume(float volume) {
		this.volume = volume;
	}
	
	/**
	 * @return whether the sound is playing right now or not
	 */
	public boolean isPlaying() {
		return playing;
	}
	
	/**
	 * @return whether the sound is muted or unmuted right now
	 */
	public boolean isMuted() {
		return muted;
	}
}
