import java.awt.Color;
import java.awt.event.KeyEvent;

//this is the intro screen class
//
//
// Written by: In Woo Park 
// Pictures: Initial D cars 
// Song: Running in the 90s 8bit 

//these are all of the variables that are being used in this class
public class IntroScreen {
	
	static EZImage sky1,sky2,sky3,title,ground1,ground2,car,car2,car3, car4, wasd;
	static EZText title1, controls;
	static EZSound music, music1;
	static Color c;
	static int posX, directionX, directionXX, clickX, clickY; 
	
	//location of images and text
	private static void init() {
		//location of background
		sky1 = EZ.addImage("night.jpeg", 500, 325);
		sky2 = EZ.addImage("night.jpeg", 1700, 325);
		sky3 = EZ.addImage("black.png", 600, 375);
		
		//location of text
		title = EZ.addImage("title.png", 600, 275);
		
		//location of ground 
		ground1 = EZ.addImage("ground.png", 500, 525);
		ground2 = EZ.addImage("ground.png", 1700, 525);
		
		//location of cars
		car = EZ.addImage("ae86.png", -850, 592);
		car2 = EZ.addImage("car2.png", -100, 592);
		car3 = EZ.addImage("ae86.png", -2850, 592);
		car4 = EZ.addImage("car2.png", -2100, 592);
		
		//location of controls
		wasd = EZ.addImage("wasd.png", 920,670);
		wasd.scaleTo(0.5);
		
		//c = font color
		c = new Color (200,200,200);
		
		//text location
		title1 = EZ.addText("Serpentine",600, 405, "PRESS SPACE TO START GAME", c, 30);
		controls = EZ.addText(920, 730, "USE ME TO MOVE", c, 20);
		
		//music that is used during the intro screen
		//Running in the 90s by Initial D
		music = EZ.addSound("90s.wav");
		music.play();
		GameManager.music1 = EZ.addSound("gas.wav");
		
		
		//set the value of variables
		posX=0;
		directionX = 2;
		directionXX = 1;
		clickX = 0;
		clickY = 0;
		
		//game state of the intro class is true 
		GE.game_state_init = true;
	}
	
	//this tells the class to run
	public static void run() {
		if(!GE.game_state_init) { init(); }
		if(GE.game_state != GE.game_state_change_to) {
			end();
		}
		
		//move pictures in respect to background image
		sky1.moveForward(-1);
		sky2.moveForward(-1);
		ground1.moveForward(-4);
		ground2.moveForward(-4);
		car3.moveForward(2);
		car4.moveForward(1);
		
		//forever loop the images if they go beyond a certain restriction
		if (sky1.getXCenter() < -500) {
			sky1.moveForward(2000);
		}
		if (sky2.getXCenter() < -500) {
			sky2.moveForward(2000);
		}
		if (ground1.getXCenter() < -600) {
			ground1.moveForward(2400);
		}
		if (ground2.getXCenter() < -600) {
			ground2.moveForward(2400);
		}
		if (car.getXCenter() < 1400) {
			car.moveForward(2);
		}
		if (car.getXCenter() > 1400) {
			car.moveForward(-1000);
		}
		if (car2.getXCenter() < 1400) {
			car2.moveForward(1);
		}
		if (EZInteraction.isKeyDown(KeyEvent.VK_SPACE)) {
			GE.game_state_change_to = 1;
			end();
		}
		//keep track of cursor location
			clickX = EZInteraction.getXMouse();
			clickY = EZInteraction.getYMouse();
		
		//if you click start game, it will change the game state to 1
		if (EZInteraction.wasMouseLeftButtonPressed()) {
			if (title1.isPointInElement(clickX, clickY)) {
			GE.game_state_change_to = 1;
			}
		} 
		EZ.refreshScreen();
	}
	
	//when the code changes states, change/end the use of these variables 
	private static void end() {
		
		GameManager.disposeEZImage(car);
		GameManager.disposeEZImage(car2);
		GameManager.disposeEZImage(car3);
		GameManager.disposeEZImage(car4);
		GameManager.disposeEZImage(sky1);
		GameManager.disposeEZImage(sky2);
		GameManager.disposeEZImage(sky3);
		GameManager.disposeEZImage(ground1);
		GameManager.disposeEZImage(ground2);
		GameManager.disposeEZImage(wasd);
		EZ.removeEZElement(title);
		EZ.removeEZElement(title1);
		EZ.removeEZElement(controls);
		
		music.sound.close();
		GameManager.music1.loop();
		
		//change the elements of the game state 
		GE.worldGroup.scaleTo(1);
		GE.worldGroup.translateTo(0, 0);
		GE.worldGroup.rotateTo(0);
		GE.game_state_init = false;
		GE.game_state = GE.game_state_change_to;
		return; //return values to class
	}
	
}
