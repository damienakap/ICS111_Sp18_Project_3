
/**
 * @author Damien Apilando
 * e-mail: damienakap@gmail.com
 * 
 * ICS111
 * lecturer Jason Leigh
 * 
 * Note:
 * 	This class handles all of the main game states and stores global variable for the game.
 * 
 */

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class GameManager {
	
	public static EZRectangle screenFilter;
	public static double screenFilterAlpha = 255;
	public static int screenFilterMode = 0;
	public static double screenFilterFadeFactor = 0;
	
	public static boolean running = true;
	
	public static ArrayList<Car> cars = new ArrayList<Car>();
	public static ArrayList<CarData> carData = new ArrayList<CarData>();
	
	public static boolean raceFinished = false;
	public static int raceFinishedTimer = 0;
	public static int raceStartTimer = 0;
	public static int raceTime = 0;
	
	public static Random rand;
	
	public static EZSound music1;
	
	static boolean paused = false;
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
		GameManager.rand = new Random();
		
		GE.worldGroup = EZ.addGroup();
		GE.worldGroup.rotateTo(0);
		
	}
	
	/**
	 * Updates the game state
	 */
	public static void updateState() {
		
		switch(GE.game_state) {
			case 1:
				TestStage.run();
				break;
			case 2: 
				EndOfRaceScreen.run();
				break;
			default:
				IntroScreen.run();
				break;
		}
		
		updateScreenFilter();
		
	}	
	
	private static void updateScreenFilter() {
		
		switch(screenFilterMode) {
			case 1:			// fade-in
				fadeInScreenFromBlack(screenFilterFadeFactor);
				if(screenFilterAlpha==0) { screenFilterMode = 0; }
				break;
			case 2:			// fade to black
				fadeScreenToBlack(screenFilterFadeFactor);
				if(screenFilterAlpha==255) { screenFilterMode = 0; }
			default:
				break;
		}
	}
	
	
	public static void setScreenFilterMode( int m, float f) {
		screenFilterMode = m;
		screenFilterFadeFactor = f;
	}
	public static void setScreenFilterAlpha( double alpha ) {
		screenFilterAlpha = alpha;
		screenFilter.setColor( new Color(0,0,0, (int)screenFilterAlpha) );
	}
	public static void fadeScreenToBlack( double factor ) {
		//screenFilterAlpha = screenFilter.color.getAlpha();
		if(screenFilterAlpha < 255) {
			screenFilterAlpha += factor*EZ.getDeltaTime()/1000;
			if(screenFilterAlpha > 255 ) {screenFilterAlpha = 255; }
		}
		setScreenFilterAlpha(screenFilterAlpha);
	}
	public static void fadeInScreenFromBlack( double factor ) {
		//screenFilterAlpha = screenFilter.color.getAlpha();
		if(screenFilterAlpha > 0) {
			screenFilterAlpha -= factor*EZ.getDeltaTime()/1000;
			if(screenFilterAlpha < 0 ) {screenFilterAlpha = 0; }
		}
		setScreenFilterAlpha(screenFilterAlpha);
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
		//for( EZElement e : ezg.getChildren() ) { ezg.removeElement(e); }
		EZ.removeEZElement(ezg);
		ezg = null;
	}
	
}
