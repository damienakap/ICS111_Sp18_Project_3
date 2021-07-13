
/**
 * @author Damien Apilando
 * e-mail: damienakap@gmail.com
 * 
 * ICS111
 * lecturer Jason Leigh
 * 
 * Note:
 * 	This is a game stage.
 * 
 */

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestStage {
	
	private static Player player;
	private static ArrayList<AICar> npcCars = new ArrayList<AICar>();
	
	private static int state = 0;
	private static int stateLast = 0;
	private static boolean stateInit = false;
	
	private static WAVSound beep1, beep2;
	private static MP3Sound backgroundMusic;
	
	
	public static void init() {
		GE.worldGroup.placeBelow(GameManager.screenFilter);
		GE.backgroundColor = new Color(40,55,40,255);
		PhysicsManager.setGravity(0, 0);
		
		GameManager.paused = false;
		GameManager.raceTime = 0;
		
		// load sounds
		backgroundMusic = GE.addMP3Sound("sounds/Nihilore_-_05_-_Bush_Week.mp3");
		beep1 = GE.addWAVSound("sounds/beep_start_second.wav");
		beep2 = GE.addWAVSound("sounds/beep_start.wav");
		
		// load main map
		MapLoader.loadMap("maps/map_01.txt");
		
		// create racers
		player = new Player("img/car.png", 150,350);
		for( int i=0; i<5; i++) { npcCars.add( new AICar("img/car.png", 150,350) ); }
		
		String aiNames[] = new String[]{"Jim", "Taylor", "Ben", "Joe", "Alex", "Sophie", "Shannon", "Dude", "Randy", "Alexa", "Seri"};
		player.name = "Player";
		for( int i=0; i<npcCars.size(); i++ ) {
			npcCars.get(i).name = aiNames[i];
		}
		
		GameManager.cars.add(player);
		for( AICar ai: npcCars ) {
			GameManager.cars.add(ai);
		}
		
		// load map objects and navigation nodes
		MapLoader.loadMap("maps/map_01_objects.txt");
		
		MapLoader.loadMap("maps/map_01_buildings.txt");
		MapLoader.loadMap("maps/map_01_navNodes.txt");
		
		// load hud elements
		for( AICar ai: npcCars ){ ai.setNameTag(); }
		player.initHud();
		
		
		
		// load music
		//backgroundMusic.setLoops(true);
		//backgroundMusic.seek(50000);
		//backgroundMusic.setVolume(0);
		//backgroundMusic.fadeTo(0.8, 0.1);
		//backgroundMusic.play();
		
		
		state = 0;
		stateLast = 0;
		stateInit = false;
		
		GE.game_state_init = true;
	}
	
	// set the state of the stage
	public static void setState(int s) {
		stateLast = state;
		state = s;
		stateInit = false;
	}
	
	// run stage
	public static void run() {
		
		// check if not initialized or state ended
		if(GE.game_state != GE.game_state_change_to) {
			if(GameManager.screenFilterMode == 0) {
				GameManager.raceFinishedTimer = 0;
				end();
			}
			return;
		}else {
			if(!GE.game_state_init) { init(); }
		}
		
		// pause game
		if(Keyboard.KeysJustPressedBuffer[80]) {
			if(GameManager.paused) {
				GameManager.paused = false;
			}else {
				GameManager.paused = true;
				setState(2);
			}
		}
		
		switch(state) {
			case 1:		// race
				if(!stateInit) {
					stateInit = true;
				}
				GameManager.raceTime += EZ.getDeltaTime();
				updateCars();
				break;
			case 2:		// pause
				if(!stateInit) {
					for( Car c : GameManager.cars ) {
						c.setState(0);
						c.hit_box.active = false;
					}
					stateInit = true;
				}
				
				//update cars
				player.update();
				if(!GameManager.paused) {
					for( Car c : GameManager.cars ) {
						c.setState(c.stateLast);
						c.hit_box.active = true;
					}
					setState(stateLast);
				}
				
				break;
			default:	// start of race
				raceStart();
				break;
		}
		updateProgress();
		
	}
	
	// start of race state
	public static void raceStart() {
		if(!stateInit) {
			if(GameManager.raceStartTimer<=0) {
				if(GE.game_state == GE.game_state_change_to) {
					GameManager.setScreenFilterAlpha(255);
					GameManager.setScreenFilterMode(1, 180);
				}
				GE.worldGroup.scaleTo(0.2);
				
				for( AICar ai: npcCars ){ ai.setState(0); }
				player.setState(0);
				
				for( int i=GameManager.cars.size()-1; i>=0; i--) {
					if(i<GameManager.cars.size()/2) {
						GameManager.cars.get(i).translateTo(-250, 2200-550*i);
					}else {
						GameManager.cars.get(i).translateTo(250, 2200-550*(i- GameManager.cars.size()/2));
					}
				}
				
			
				GameManager.raceStartTimer = 10000;
			}
			stateInit = true;
		}
		updateCars();
		if(!GameManager.paused) {
			int t = (GameManager.raceStartTimer%60000)/1000;
			GameManager.raceStartTimer -= EZ.getDeltaTime();
			if(GameManager.raceStartTimer<=0) {
				GameManager.raceStartTimer = 0;
				beep2.play();
				for( AICar ai: npcCars ){ ai.setState(1); }
				player.setState(1);
				setState(1);
			}else if( t != (GameManager.raceStartTimer%60000)/1000) {
				beep1.play();
			}
		}
	}
	
	// update car place, lap, and lap times
	// checks if race ended
	public static void updateProgress() {
		
		if(GameManager.raceFinished) {
			if(GameManager.raceFinishedTimer<=0) {
				System.out.println("Next State");
				GameManager.setScreenFilterMode(2, 180);
				GE.game_state_change_to = 2;
				return;
			}else {
				GameManager.raceFinishedTimer -= EZ.getDeltaTime();
			}
		}else {
		
			Collections.sort( GameManager.cars, new ProgressComparator() );
			for( int i=0; i<GameManager.cars.size(); i++) {
				GameManager.cars.get(i).place = i+1;
				if( !GameManager.raceFinished && (1+(GameManager.cars.get(i).trackProgress-1)/61) > 2) {
					GameManager.raceFinished = true;
					GameManager.raceFinishedTimer = 30000;
				}
				
			}
		}
	}
	
	// updates cars
	public static void updateCars() {
		
		if(player.state == 1) {
			if( 
				player.group.getXCenter() < -7000 || player.group.getXCenter() > 17000 ||
				player.group.getYCenter() < -12000 || player.group.getYCenter() > 11000
					
					) {
				player.setState(1);
			}
		}
		
		player.update();
		for( AICar ai: npcCars) {
			ai.update(player);
		}
		
	}
	
	public static void end() {
		
		GameManager.music1.sound.close();
		backgroundMusic = null;
		beep1 = null;
		beep2 = null;
		GE.removeAllSounds();
		GameManager.carData.clear();
		for( Car c : GameManager.cars) {
			GameManager.carData.add( new CarData(c.name, c.place, c.bestLapTime) );
		}
		
		GameManager.cars.clear();
		for( AICar ai: npcCars) { ai.dispose(); }
		npcCars.clear();
		player.dispose();
		player = null;
		
		GameManager.raceFinished = false;
		GameManager.paused = false;
		MapLoader.unLoadMap();
		GameManager.screenFilter.setLayer(0);
		
		for( EZElement e :GE.worldGroup.getChildren()) {
			EZ.removeEZElement(e);
		}
		
		GE.worldGroup.pushToBack();
		GameManager.screenFilter.pullToFront();
		GE.setScreenFocusObject(null, 0, 0);
		GE.worldGroup.scaleTo(1);
		GE.worldGroup.translateTo(0, 0);
		GE.worldGroup.rotateTo(0);
		GE.game_state = GE.game_state_change_to;
		GE.game_state_init = false;
	}
	
	public static class ProgressComparator implements Comparator<Car> {
	    @Override
	    public int compare(Car o1, Car o2) {
	        return o2.getTrackProgress() - o1.getTrackProgress();
	    }
	}
	
}
