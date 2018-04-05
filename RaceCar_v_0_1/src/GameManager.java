import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.FloatControl;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class GameManager {
	
	public static EZRectangle screenFilter;
	public static double screenFilterAlpha;
	
	public static boolean running = true;
	
	public static Random rand;
	
	public static int game_score = 0;
	
	public static int game_state = 0;
	public static boolean game_state_init = false;
	public static int game_state_change_to = 0;
	
	public GameManager() {
		
	}
	
	/**
	 * Initializes the game manager
	 */
	public static void init( ) {
		
		Runtime.getRuntime().addShutdownHook( new Thread(new Runnable() {
	        public void run() {
	        	GameManager.running = false;
	        	
	        }
	     }));
		
		screenFilter = EZ.addRectangle((int)(EZ.getWindowWidth()/2), (int)(EZ.getWindowHeight()/2), (int)(EZ.getWindowWidth()*1.1), (int)(EZ.getWindowHeight()*1.1), Color.BLACK, true);
		screenFilterAlpha = 255;
		GameManager.rand = new Random();
		
		GE.worldGroup = EZ.addGroup();
		GE.worldGroup.rotateTo(0);
		
	}
	
	/**
	 * Updates the game state
	 */
	public static void updateState() {
		switch(game_state) {
			default:
				if(!game_state_init) {TestStage.init(); game_state_init = true;}
				TestStage.update();
				break;
			
		}
	}
	
	public static void fadeScreenToBlack( double factor ) {
		if(screenFilterAlpha < 255) {
			screenFilterAlpha += factor*EZ.getDeltaTime()/1000;
			if(screenFilterAlpha > 255 ) {screenFilterAlpha = 255; }
			screenFilter.setColor(new Color( 0, 0, 0, (int)screenFilterAlpha ));
		}
	}
	public static void fadeInScreenFromBlack( double factor ) {
		if(screenFilterAlpha > 0) {
			screenFilterAlpha -= factor*EZ.getDeltaTime()/1000;
			if(screenFilterAlpha < 0 ) {screenFilterAlpha = 0; }
			screenFilter.setColor(new Color( 0, 0, 0, (int)screenFilterAlpha ));
		}
	}
	
	public static void disposeEZImage(EZImage ezi) {
		if(ezi.hasParent()) { ezi.getParent().removeElement(ezi); }
		ezi.img = null;
		//for(BufferedImage i : ezi.loadedImages) { i = null; }
		//ezi.loadedImages.clear();
		EZ.removeEZElement(ezi);
		ezi = null;
	}
	
	public static void disposeEZGroup( EZGroup ezg ) {
		if(ezg.hasParent()) { ezg.getParent().removeElement(ezg); }
		for( EZElement e : ezg.getChildren() ) { ezg.removeElement(e); }
		EZ.removeEZElement(ezg);
		ezg = null;
	}
	
}
