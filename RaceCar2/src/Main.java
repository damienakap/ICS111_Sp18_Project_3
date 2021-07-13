
/**
 * @author Damien Apilando
 * e-mail: damienakap@gmail.com
 * 
 * ICS111
 * lecturer Jason Leigh
 * 
 * Note:
 * 	This is the programs entry point.
 * 
 */

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

public class Main {
	
	// Main game loop
	public static void loop() {
		
		// press esc to manually close game
		if(Keyboard.KeysJustPressedBuffer[27]) { GameManager.running = false; }
		GameManager.updateState();
		
		GE.update();
		
	}
	
	
	public static void main(String[] args) {
		// Initialize 
		GE.initialize( 1200, 800 );
		GameManager.init();
		EZSound dick = EZ.addSound("gas.wav");
		//dick.loop();
		
		// Run Game
		while(GameManager.running){ loop(); }
		
		// clean up
		GE.close();
		
	}

}
