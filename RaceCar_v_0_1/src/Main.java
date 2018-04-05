
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
	public static WAVSound wavs;
	public static void main(String[] args) {
		// Initialize 
		GE.initialize( 800, 800 );
		GameManager.init();
		
		// Run Game
		while(GameManager.running){ loop(); }
		
		// clean up
		
		GE.close();
		
	}

}
