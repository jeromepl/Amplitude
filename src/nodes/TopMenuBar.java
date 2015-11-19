package nodes;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import reactions.AmplitudeReaction;
import reactions.FrequencyReaction;
import shapes.ReactiveShape;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

/**
 * This JavaFX MenuBar contains the “File” menu as well as the “Help” menu.
 * Those contain multiple options that allow the user to do things such as saving and opening their creations.
 * It is located at the very top of the window.
 */
public class TopMenuBar extends MenuBar {
	
	/**
	 * the file to which the project is saved
	 */
	private File saveFile = null;
	/**
	 * whether a file is being saved right now
	 */
	public static volatile boolean saving = false;
	
	/**
	 * constructs a top menu bar and loads the corresponding FXML in the main stage
	 */
	public TopMenuBar() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/MenuBarLayout.fxml"));
	    fxmlLoader.setRoot(this);
	    fxmlLoader.setController(this);
	    try {
	        fxmlLoader.load();
	    } catch (IOException exception) {
	        throw new RuntimeException(exception);
	    }
	}
	
	/**
	 * @return whether the project has been saved or not
	 */
	public boolean isSaved(){
		return (saveFile != null);
	}
	
	/**
	 * sets a new save file
	 * @param saveFile the new file to which the project should be saved
	 */
	public void setSaveFile(File saveFile){
		this.saveFile = saveFile;
	}
	
	/**
	 * @return the current file in which the project is saved
	 */
	public File getSaveFile() {
		return saveFile;
	}
	
	/**
	 * @return whether a file is being saved at the moment or not
	 */
	public boolean isSaving() {
		return saving;
	}
	
	/**
	 * method triggered by JavaFX when the user clicks on the 'save' option in the menu bar
	 * @param event JavaFX event
	 */
	@FXML
	protected void savePressed(ActionEvent event){
		if(saveFile == null){
			saveAsPressed(event);
		}
		else {
			saveAmpFile(saveFile);
		}
	}
	
	/**
	 * method triggered by JavaFX when the user clicks on the 'save as' option in the menu bar
	 * @param event JavaFX event
	 */
	@FXML
	protected void saveAsPressed(ActionEvent event){
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save As...");
		
		//filter to save only in amp files 
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("AMP files	(*.amp)","*.amp");
		fileChooser.getExtensionFilters().add(filter);
		
		File file =	fileChooser.showSaveDialog(MainStage.getInstance().getPrimaryStage());
		
		if(file != null) {
			saveAmpFile(file);
			saveFile = file;
		}
	}
	
	/**
	 * saves the project in a file on the user's computer
	 * @param file to file to which the project should be saved
	 */
	public static void saveAmpFile(File file) {
		if(file != null && !saving) {
			MainStage.getInstance().showSaveIndicator();
			saving = true;
			
			new Thread(new Runnable() {
				public void run() {
					try {
						long start = System.currentTimeMillis();
						
						ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
						ArrayList<ReactiveShape> shapes = MainStage.getInstance().getShapes();
						
						output.writeUTF(MainStage.getInstance().getAudioPlayer().getName());
			        	output.writeObject(MainStage.getInstance().getAudioPlayer().getSoundBytes());
			        	
			        	//Video pane background
			        	boolean isColor = (MainStage.getInstance().getVideoPane().getInnerPane().getBackground().getImages().size() == 0)? true: false;
			        	output.writeBoolean(isColor); //tell if image or color
			        	
			        	if(isColor) {
			        		output.writeUTF(((Color)(MainStage.getInstance().getVideoPane().getInnerPane().getBackground().getFills().get(0).getFill())).toString());
			        	}
			        	else { //it's an image
			        		BufferedImage bi = SwingFXUtils.fromFXImage(MainStage.getInstance().getVideoPane().getInnerPane().getBackground().getImages().get(0).getImage(), null);
				        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
				        	ImageIO.write(bi, "png", baos); //png only specifies how the image is going to be saved. This thus works for all types of images supported by java
				        	output.writeObject(baos.toByteArray());
				        	baos.close();
			        	}
			        	
			        	output.writeDouble(MainStage.getInstance().getPrimaryStage().getHeight());
			        	output.writeDouble(MainStage.getInstance().getPrimaryStage().getWidth());
			        	
			        	//write to the file created 
			        	output.writeInt(shapes.size());
			        	for(int i = 0; i < shapes.size(); i++){
			        		output.writeObject(new String[] {
			        				"" + shapes.get(i).getShapeType(),
			        				"" + shapes.get(i).getXPosition(),
			        				"" + shapes.get(i).getYPosition(), 
			        				"" + shapes.get(i).getWidth(), 
			        				"" + shapes.get(i).getHeight(), 
			        				shapes.get(i).getColor().toString(), 
			        				shapes.get(i).isFilled()? "true": "false",
			        				"" + shapes.get(i).getBorderThickness(), 
			        				shapes.get(i).getBorderColor().toString() 
			        				});
			        		
			        		//reactions of the shape
			        		output.writeObject(shapes.get(i).getAmplitudeReactions());
			        		output.writeObject(shapes.get(i).getFrequencyReactions());
			        	}
			        	
			        	output.close();
			        	saving = false;
			        	
			        	System.out.println(System.currentTimeMillis() - start);
			        	
					} catch (IOException e) {
						MainStage.getInstance().getErrorStage().show("An error occured while trying to save the project. Sorry for the inconvenience.", "TMB192");
					}
					
					//Hide the save indicator when the saving is done
					Platform.runLater(new Runnable() { //Only way to modify the GUI from another thread than the Application thread
					    @Override
					    public void run() {
					    	MainStage.getInstance().hideSaveIndicator();
					    }
					});
			
				}
			}).start();
		}
	}
	
	/**
	 * method triggered by JavaFX when the user clicks on the 'exit' option in the menu bar
	 * @param event JavaFX event
	 */
	@FXML
	protected void exitPressed(ActionEvent event){
		MainStage.getInstance().exit(false);
	}
	
	/**
	 * method triggered by JavaFX when the user clicks on the 'new' option in the menu bar
	 * @param event JavaFX event
	 */
	@FXML
	protected void newPressed(ActionEvent event){
		saveFile = null;
		MainStage.getInstance().clear();
		new SelectFileStage().show();
	}
	
	/**
	 * method triggered by JavaFX when the user clicks on the 'import sound' option in the menu bar
	 * @param event JavaFX event
	 */
	@FXML
	protected void importSoundPressed(ActionEvent event){
		File soundFile = selectSoundFile();
		MainStage.getInstance().getPlayerPane().setPlayingText(false);
		if(soundFile != null && soundFile.exists())
			MainStage.getInstance().loadSound(soundFile, false);
		
	}
	
	/**
	 * allows the user to select a sound file to import by showing a file chooser
	 * @return the selected sound file
	 */
	public static File selectSoundFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import WAV File");
		
		//filter to get only wav file 
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("WAV files	(*.wav)","*.wav");
		fileChooser.getExtensionFilters().add(filter);
		
		return fileChooser.showOpenDialog(MainStage.getInstance().getPrimaryStage());
	}
	
	/**
	 * method triggered by JavaFX when the user clicks on the 'open' option in the menu bar
	 * @param event JavaFX event
	 */
	@FXML
	protected void openPressed(ActionEvent event){
		File file = selectAmpFile();
		
		if (file != null && file.exists()) {
			openAmpFile(file);
			saveFile = file;
		}
	}
	
	/**
	 * allows the user to select an Amp file to open by showing a file chooser
	 * @return the selected amp file
	 */
	public static File selectAmpFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open");
		
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("AMP files	(*.amp)","*.amp");
		fileChooser.getExtensionFilters().add(filter);
		
		
		return fileChooser.showOpenDialog(MainStage.getInstance().getPrimaryStage());
	}
	
	/**
	 * opens an ampl!tude project into the program
	 * @param file the ampl!tude file to open
	 */
	@SuppressWarnings("unchecked")
	public static void openAmpFile(File file) {
		
		MainStage.getInstance().clear(); //Resets the layout
		
		try {
			ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
			
			String name = input.readUTF();
			MainStage.getInstance().loadSound((byte[])(input.readObject()), name, false);
			
			//Set the video pane background
			if(input.readBoolean()) { //If it's a color
				MainStage.getInstance().getVideoPane().setBackground(Color.valueOf(input.readUTF()));
			}
			else { //if it's an image
				MainStage.getInstance().getVideoPane().setBackground(new Image(new ByteArrayInputStream((byte[])(input.readObject()))));
			}
			
			MainStage.getInstance().getPrimaryStage().setHeight(input.readDouble());
			MainStage.getInstance().getPrimaryStage().setWidth(input.readDouble());
		
			int shapeSize = input.readInt();
			for(int i = 0; i < shapeSize; i++) {
				String[] values = (String[])(input.readObject());
				ReactiveShape currentShape = new ReactiveShape(Integer.parseInt(values[0]),
						Double.parseDouble(values[1]), 	
						Double.parseDouble(values[2]), 
						Double.parseDouble(values[3]),
						Double.parseDouble(values[4]),
						Color.valueOf(values[5]),
						Boolean.parseBoolean(values[6]),
						Integer.parseInt(values[7]),
						Color.valueOf(values[8]));
				
				currentShape.getReactions().addAll((ArrayList<AmplitudeReaction>)(input.readObject()));
				currentShape.getReactions().addAll((ArrayList<FrequencyReaction>)(input.readObject()));
				MainStage.getInstance().addShape(currentShape);
			}
			
			TimerTask task = new TimerTask() { 
	    		@Override
	          	public void run()
	          	{
	    			Platform.runLater(new Runnable() {
						@Override
						public void run() {
							MainStage.getInstance().getVideoPane().setInitSize();
							MainStage.getInstance().scaleAllShapes();
							MainStage.getInstance().updateAllShapes();
						}		    				
	    			});
	          	}
	        };
	        
	        Timer timer = new Timer();
	        timer.schedule(task, 50); //delay the resize to wait for the sizes to be updated
		
			input.close();
		} catch (ClassNotFoundException | IOException e) {
			MainStage.getInstance().getErrorStage().show("An error occured while trying to open the AMP file. Sorry for the inconvenience.", "TMB349");
		}
	}
	
	/**
	 * method triggered by JavaFX when the user clicks on the 'tutorial' option in the menu bar
	 * @param event the JavaFX event
	 */
	@FXML
	protected void tutorialPressed(ActionEvent event){
		openTutorial();
	}
	
	/**
	 * opens the tutorial in the user's default browser
	 */
	public static void openTutorial() {
		try {
			File tutorial = new File("tutorial/tutorial.html");
			
			Desktop.getDesktop().open(tutorial);
		} catch (IOException er) {
			er.printStackTrace();
			MainStage.getInstance().getErrorStage().show("An error occured while trying to open the tutorial. Sorry for the inconvenience.", "TMB372");
		}
	}
	
	/**
	 * opens a specific preset in the "presets" folder
	 * @param filename the file name of the preset (must include the extension type)
	 */
	private void openPreset(String filename) {
		try {
			File preset = new File("presets/" + filename);
		
			openAmpFile(preset);
			saveFile = null;
		} catch(Exception e) {
			MainStage.getInstance().getErrorStage().show("An error occured while trying to open the preset. Sorry for the inconvenience", "TMB387");
		}
	}
	
	/**
	 * opens the first preset
	 * @param event the JavaFX event
	 */
	@FXML
	protected void preset1Pressed(ActionEvent event) {
		openPreset("preset1.amp");
	}
	
	/**
	 * opens the second preset
	 * @param event the JavaFX event
	 */
	@FXML
	protected void preset2Pressed(ActionEvent event) {
		openPreset("preset2.amp");
	}
	
	/**
	 * opens the third preset
	 * @param event the JavaFX event
	 */
	@FXML
	protected void preset3Pressed(ActionEvent event) {
		openPreset("preset3.amp");
	}
	
}
