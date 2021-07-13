
/**
 * @author Damien Apilando
 * e-mail: damienakap@gmail.com
 * 
 * ICS111
 * lecturer Jason Leigh
 * 
 * Note:
 * 	This is the end of race screen. This state displays the race results after playing a stage.
 * 
 */

import java.awt.Color;
import java.util.ArrayList;

/*
 * Show time, score, and places of cars in the race
 * 
 */

public class EndOfRaceScreen {
	
	// use "GE.game_state_change_to" to change depending on what the next state is.
	
	private static EZText titleText, contText, playText;
	private static EZImage background;
	
	private static EZGroup resultGroup;
	private static ArrayList<ResultNamePlate> resultPlates = new ArrayList<ResultNamePlate>();
	
	private static MP3Sound music;
	
	private static void init() {
		GE.worldGroup.placeBelow(GameManager.screenFilter);
		GE.backgroundColor = new Color( 20,20,20,255 );
		
		background = EZ.addImage("img/city.jpg", EZ.getWindowWidth()/2, EZ.getWindowHeight()/2);
		background.scaleTo(5);
		GE.worldGroup.addElement(background);
		
		contText = EZ.addText( EZ.getWindowWidth()-200, EZ.getWindowHeight()-100, "Back To Title", new Color(200,80,80,255) );
		contText.setFont("fonts/Raleway-Black.ttf");
		contText.setFontSize(40);
		GE.worldGroup.addElement(contText);
		
		playText = EZ.addText( 200, EZ.getWindowHeight()-100, "Play Again", new Color(200,80,80,255) );
		playText.setFont("fonts/Raleway-Black.ttf");
		playText.setFontSize(40);
		GE.worldGroup.addElement(playText);
		
		titleText = EZ.addText( EZ.getWindowWidth()/2, 100, "Race Resutls", new Color(200,80,80,255) );
		titleText.setFont("fonts/Raleway-Black.ttf");
		titleText.setFontSize(60);
		GE.worldGroup.addElement(titleText);
		
		GameManager.setScreenFilterMode(1, 100);
		
		resultGroup = EZ.addGroup();
		GE.worldGroup.addElement(resultGroup);
		
		int sh = (GameManager.carData.size()*(50+10))/2;
		
		for( int i=0; i<GameManager.carData.size(); i++ ) {
			resultPlates.add( new ResultNamePlate(GameManager.carData.get(i)) );
			resultPlates.get(resultPlates.size()-1).group.translateTo( 0, - sh + (50+10)*i );
			resultGroup.addElement( resultPlates.get(resultPlates.size()-1).group );
		}
		resultGroup.translateTo(EZ.getWindowWidth()/2, EZ.getWindowHeight()/2);
		
		music = GE.addMP3Sound("sounds/Nihilore_-_01_-_Sparkwood__21.mp3");
		music.setLoops(true);
		music.setVolume(0);
		music.fadeTo(0.5, 0.1);
		music.play();
		
		GE.setScreenFocusObject(null, 0, 0);
		GE.worldGroup.rotateTo(0);
		GE.worldGroup.scaleTo(1);
		GE.worldGroup.translateTo(0, 0);
		GE.game_state_init = true;
	}
	
	public static void run() {
		if(!GE.game_state_init) { init(); }
		if(GE.game_state != GE.game_state_change_to) {
			
			if(GameManager.screenFilterMode != 2 && GameManager.screenFilterAlpha != 255) {
				GameManager.setScreenFilterMode(2, 100);
				music.fadeTo(0, 0.01);
			}
			if(GameManager.screenFilterMode == 0) {
				end();
				return;
			}
		}else{
			if(contText.isPointInElement((int)Mouse.pos.x, (int)Mouse.pos.y) && Mouse.ButtonsJustReleased[1]) {
				GE.game_state_change_to = 0;
			}
			if(playText.isPointInElement((int)Mouse.pos.x, (int)Mouse.pos.y) && Mouse.ButtonsJustReleased[1]) {
				GE.game_state_change_to = 1;
			}
		}
	}
	
	public static void end() {
		
		GE.removeMP3Sound(music);
		
		GE.worldGroup.removeElement(background);
		GameManager.disposeEZImage(background);
		
		GE.worldGroup.removeElement(titleText);
		EZ.removeEZElement(titleText);
		GE.worldGroup.removeElement(contText);
		EZ.removeEZElement(contText);
		
		for( int i=0; i<resultPlates.size(); i++ ) {
			resultGroup.removeElement(resultPlates.get(i).group);
			resultPlates.get(i).dispose();
		}
		GameManager.disposeEZGroup(resultGroup);
		
		resultPlates.clear();
		GameManager.carData.clear();
		
		GE.worldGroup.pushToBack();
		GameManager.screenFilter.pullToFront();
		GE.setScreenFocusObject(null, 0, 0);
		GE.worldGroup.rotateTo(0);
		GE.worldGroup.scaleTo(1);
		GE.worldGroup.translateTo(0, 0);
		GE.game_state_init = false;
		GE.game_state = GE.game_state_change_to;
	}
	
}

class ResultNamePlate{
	
	public EZGroup group;
	private EZRectangle backPlate;
	private EZText name, place, bestTime;
	
	public ResultNamePlate( CarData cd) {
		group = EZ.addGroup();
		
		backPlate = EZ.addRectangle(0, 0, 800, 50, new Color( 180,80,80 ), true);
		place = EZ.addText(-320, 0, cd.place+"", Color.WHITE);
		place.setFont("fonts/Raleway-Black.ttf");
		place.setFontSize(40);
		name = EZ.addText(0, 0, cd.name, Color.WHITE);
		name.setFont("fonts/Raleway-Black.ttf");
		name.setFontSize(40);
		int min = (int)(cd.bestLapTime/6000);
		int sec = cd.bestLapTime%6000;
		sec /= 1000;
		//bestTime = EZ.addText(280, 0, (int)(cd.bestLapTime/60000)+":"+(int)((cd.bestLapTime%60)/1000), Color.WHITE);
		bestTime = EZ.addText(280, 0, min+":"+sec, Color.WHITE);
		bestTime.setFont("fonts/Raleway-Black.ttf");
		bestTime.setFontSize(40);
		
		group.addElement(backPlate);
		group.addElement(place);
		group.addElement(name);
		group.addElement(bestTime);
		
	}
	
	public void dispose() {
		this.group.removeElement(backPlate);
		this.group.removeElement(bestTime);
		this.group.removeElement(name);
		
		EZ.removeEZElement(backPlate);
		EZ.removeEZElement(bestTime);
		EZ.removeEZElement(name);
		EZ.removeEZElement(place);
		
		GameManager.disposeEZGroup(group);
		
	}
	
}

