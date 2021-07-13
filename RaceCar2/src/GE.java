
/**
 * Copyright (c) 04/04/2018
 * @author Damien Apilando
 * e-mail: damienakap@gmail.com
 * 
 * ICS111
 * lecturer Jason Leigh
 * 
 * Note:
 * 	This is game engine that is based on the EZ Graphics library
 *  adding sprite animation and limited 2d physics ( Broken angular momentum conservation ). There are methods for raycasting
 *  and vector operations. This also provides some additional control of .wav and .mp3
 *  sound files.
 * 
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import javax.sound.sampled.FloatControl;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Color;
import java.awt.event.KeyAdapter;
	
public class GE {
	public static EZGroup cameraFocus = null;
	public static Vector cameraFocusOffset = new Vector(0,0);
	
	public static EZGroup worldGroup;
	public static Color backgroundColor = Color.BLACK;
	
	public static EZRectangle screenFilter;
	public static double screenFilterAlpha;
	
	public static boolean running = true;
	
	public static Random rand;
	
	public static int game_score = 0;
	
	public static int game_state = 0;
	public static boolean game_state_init = false;
	public static int game_state_change_to = 0;
	
	private static Vector temp1 = new Vector(0,0);
	private static Vector temp2 = new Vector(0,0);
	
	private static JFXPanel jfxpanel;
	
	/**
	 * Initializes the GameEngine
	 */
	public static void initialize( int width, int height) {
		
		EZ.initialize( width, height);
		EZ.setBackgroundColor(Color.BLACK);
		EZ.app.setFocusable(true);
		EZ.app.requestFocus();
		PhysicsManager.init(200);
		SpriteManager.init(200);
		Keyboard.init();
		EZ.app.addKeyListener(Keyboard.keyboard);
		Mouse.init();
		EZ.app.addMouseListener(Mouse.mouse);
		EZ.app.addMouseMotionListener(Mouse.mouse);
		//EZ.setFrameRateASAP(true);
		EZ.setFrameRate(60);
		
		worldGroup = EZ.addGroup();
		
		jfxpanel = new JFXPanel();
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		       
		    }
		});
	}
	
	/**
	 * Updates the GameEngine
	 */
	public static void update() {
		EZ.setBackgroundColor(backgroundColor);
		Mouse.update();
		Keyboard.update();
		
		PhysicsManager.update();
		SpriteManager.update();
		MapLoader.updateObjects();
		
		if(cameraFocus != null) {
			GE.setScreenFocus(
					cameraFocus.getXCenter(), cameraFocus.getYCenter(), cameraFocus.getRotation(),
					cameraFocusOffset.x, cameraFocusOffset.y
				); 
		}
		
		ParticleManager.update();
		SoundManager.update();
	}
	
	/**
	 * Closes the GameEngine
	 */
	public static void close() {
		SoundManager.removeAllSounds();
		EZ.closeWindowWithIndex(0);
		Platform.exit();
	}
	/**
	 * 
	 * Create a WAVSound Object
	 * 
	 * @param file - file path of the .wav sound file
	 * @return - the WAVSound object
	 */
	public static WAVSound addWAVSound( String file ) {
		return SoundManager.addWAVSound(file);
	}
	/**
	 * removes the WAVSound
	 * 
	 * @param wavs - input WAVSound
	 */
	public static void removeWAVSound( WAVSound wavs ) {
		SoundManager.removeEZSound( wavs );
	}
	/**
	 * 
	 * Create a MP3Sound Object
	 * 
	 * @param file - file path of the .mp3 sound file
	 * @return - the MP3Sound object
	 */
	public static MP3Sound addMP3Sound( String file ) {
		return SoundManager.addMP3Sound(file);
	}
	/**
	 * removes the MP3Sound
	 * 
	 * @param wavs - input MP3Sound
	 */
	public static void removeMP3Sound( MP3Sound mp3s) {
		SoundManager.removeMP3Sound(mp3s);
	}
	/**
	 * removes all sound objects. be sure that no external references exist before using.
	 */
	public static void removeAllSounds() {
		SoundManager.removeAllSounds();
	}
	
	/**
	 * Sets the screen focus center
	 * @param x	- the x position of the object
	 * @param y	- the t position of the object
	 * @param rot - the rotation of the object in degrees
	 * @param offx - screen x offset
	 * @param offy - screen y offset
	 */
	public static void setScreenFocus( double x, double y, double rot, double offx, double offy ) {
		Vector vt = new Vector(x, y);
		vt.rotate(-rot*Math.PI/180);
		Vector vr = new Vector( offx,offy);
		
		GE.worldGroup.rotateTo(-rot);
		GE.worldGroup.translateTo(GE.worldGroup.getScale()*(-vt.x)+vr.x+(EZ.getWindowWidth()/2), GE.worldGroup.getScale()*(-vt.y)+vr.y+(EZ.getWindowHeight()/2));
	}
	public static void setScreenFocusObject(EZGroup focus, double offx, double offy) {
		cameraFocus = focus;
		cameraFocusOffset.set(offx, offy);
	}
	/**
	 * Creates a rectangular collision object.
	 * @param p - Parent object
	 * @param w - Width of the rectangle
	 * @param h - Height of the rectangle
	 * @param t - the type of collision object:
	 * 	0: Pass-Over
	 * 	1: Sensor
	 * 	2: Dynamic
	 * 	3: Rigid
	 * 	4: Static
	 * @param g	- The physics group. Will collide if the other object has this value in their collision groups
	 */
	public static BoxCollider addBoxCollider(EZGroup p, int w, int h, int t, int g) {
		//BoxCollider bc = new BoxCollider( p, w, h, t, g);
		//PhysicsManager.addCollider( bc );
		return new BoxCollider( p, w, h, t, g);
	}
	/**
	 *  Creates a polygon collision object.
	 * @param p - parent object
	 * @param px - x position of vertecies.
	 * @param py - y position of vertecies.
	 * @param t - the type of collision object:
	 * 	0: Pass-Over
	 * 	1: Sensor
	 * 	2: Dynamic
	 * 	3: Rigid
	 * 	4: Static
	 * @param g - The physics group. Will collide if the other object has this value in their collision groups
	 * @return
	 */
	public static PolyCollider addPolyCollider( EZGroup p, int[] px, int[] py, int t, int g) {
		//PolyCollider pc = new PolyCollider( p, px, py, t, g);
		//PhysicsManager.addCollider( pc );
		return new PolyCollider( p, px, py, t, g);
	}
	/**
	 * Removes the collider
	 * 
	 * @param c - Collider to remove
	 */
	public static void removeCollider( Collider c ) {
		//PhysicsManager.removeCollider(c);
		c.dispose();
	}
	
	public static BasicParticleEmitter addEmitter( EZGroup p, int pc, int w, int h, Color c, double s, double sr, double lt) {
		return ParticleManager.addEmitter( p, pc, w, h, c, s, sr, lt);
	}
	
	public static void removeEmitter( BasicParticleEmitter e ) {
		ParticleManager.removeEmitter(e);
	}
	/**
	 *  Creates a Sprite
	 * @param p - Parent Oject
	 * @param img_name - file path to sprite sheet.
	 * @param fc - the number of frames in the current animation
	 * @param sprite_width - width of the animation frames
	 * @param sprite_height - height of the animation frames
	 * @param p_type - Animation play type:
	 * 	0: loop
	 * 	1: once
	 * 	2: stop
	 * 	3: set count
	 * @return - a sprite object
	 */
	public static Sprite addSprite( EZGroup p, String img_name, int fc, int sprite_width, int sprite_height, int p_type ) {
		return SpriteManager.addSprite(p, img_name, fc, sprite_width, sprite_height, 0, 0, p_type);
	}
	/**
	 *  Removes a sprite.
	 * @param s - sprite object to remove
	 */
	public static void removeSprite( Sprite s ) {
		s.dispose();
	}
	
}

class ParticleManager{
	
	private static ArrayList<BasicParticleEmitter> emitters = new ArrayList<BasicParticleEmitter>();
	
	public static void update() {
		for( BasicParticleEmitter e: emitters ) { e.update(); }
	}
	
	public static BasicParticleEmitter addEmitter( EZGroup p, int pc, int w, int h, Color c, double s, double sr, double lt) { 
		emitters.add( new BasicParticleEmitter( p, pc, w, h, c, s, sr, lt) );
		return emitters.get(emitters.size()-1);
	}
	
	public static void clearAllEmitters() {
		for( BasicParticleEmitter e : emitters ) { e.dispose(); }
		emitters.clear();
	}
	
	public static void removeEmitter( BasicParticleEmitter e ) {
		e.dispose();
		emitters.remove(e);
	}
	
}

class Particle{
	
	double speed, lifeTime;
	double timeToDeath = 0;
	EZCircle graphic;
	Vector velocity;
	
	public Particle( int w, int h, Color c, double s, double lt) {
		this.speed = s;
		this.lifeTime = lt;
		this.velocity = new Vector(0,0);
		
		this.graphic = EZ.addCircle(0, 0, w, h, c, true);
		GE.worldGroup.addElement( this.graphic );
	}
	
	public void emmit( double x, double y) {
		this.graphic.translateTo(x, y);
		this.graphic.show();
		this.timeToDeath = this.lifeTime;
		this.velocity.set(0, 1);
		this.velocity.rotate(2*Math.PI*( GameManager.rand.nextDouble()-0.5 ));
		this.velocity.multiply(this.speed);
		
	}
	
	public void update() {
		if( this.timeToDeath <= 0 ) {
			this.graphic.hide();
		}else {
			this.graphic.translateBy(this.velocity.x, this.velocity.y);
			this.timeToDeath -= EZ.getDeltaTime();
		}
	}
	
	public void dispose() {
		if(this.graphic.hasParent()) { this.graphic.getParent().removeElement(this.graphic); }
		EZ.removeEZElement(this.graphic);
	}
	
}

class BasicParticleEmitter{
	
	public EZGroup parent;
	ArrayList<Particle> particles = new ArrayList<Particle>();
	double spawnRate;
	double timeTonextSpawn = 0;
	int nextParticelToSpawn = 0;
	boolean active = false;
	
	public BasicParticleEmitter( EZGroup p, int pc, int w, int h, Color c, double s, double sr, double lt) {
		this.spawnRate = sr;
		this.parent = p;
		
		for( int i=0; i<pc; i++ ) {
			this.particles.add( new Particle( w, h, c, s, lt) );
		}
		
	}
	
	public void placeBelow(boolean below) {
		if(below) {
			for( Particle p: this.particles ) { p.graphic.placeBelow(this.parent); }
		}else {
			for( Particle p: this.particles ) { p.graphic.placeAbove(this.parent); }
		}
	}
	
	public void dispose() {
		for( Particle p: this.particles ) { p.dispose(); }
		this.parent = null;
	}

	public void update() {
		for( Particle p: this.particles ) { p.update(); }
		
		if(this.active) {
			if( this.timeTonextSpawn <=0 ) {
				while(this.timeTonextSpawn<0) {
					this.particles.get(nextParticelToSpawn).emmit( this.parent.getXCenter(), this.parent.getYCenter() );
					this.nextParticelToSpawn++;
					if(this.nextParticelToSpawn>=this.particles.size()) { this.nextParticelToSpawn=0; }
					this.timeTonextSpawn += this.spawnRate;
				}
				this.timeTonextSpawn = this.spawnRate-this.timeTonextSpawn;
			}else {
				this.timeTonextSpawn -= (double)(EZ.getDeltaTime());
			}
		}
	}
	
	
}

class Button {
	public boolean[] events; // indexes 0: hover, 1: Down?, 2: just_pressed, 3: just_release
	public Collider hitBox;
	public EZGroup group;
	public EZElement graphic;
	public Color[] graphicColors;
	public Sprite sprite;
	
	/**
	 *  Creates a button with an EZElement.
	 * @param w	- Width of the button
	 * @param h	- height of the button
	 * @param g - EZElement for a graphic
	 * @param c0 - default color
	 * @param c1 - hover color
	 * @param c2 - clicked color
	 */
	public Button(int w, int h, EZElement g, Color c0, Color c1, Color c2) {
		this.group = EZ.addGroup();
		this.graphic = g;
		this.events = new boolean[4];
		this.graphicColors = new Color[] {c0, c1, c2};
		
		this.graphic.setColor(this.graphicColors[0]);
		this.group.addElement(this.graphic);
		
		this.hitBox = new BoxCollider(this.group, w, h, 0, 101);
		this.hitBox.graphic.pullToFront();
		this.hitBox.addToCollisionGroups(100);
		
	}
	
	/**
	 *  Creates a button with a sprite.
	 * @param s - sprite file
	 * @param w	- Width of the button
	 * @param h	- height of the button
	 * @param sw - Width of the sprite
	 * @param sh - height of the sprite
	 */
	public Button( String s, int w, int h, int sw, int sh) {
		this.group = EZ.addGroup();
		this.events = new boolean[4];
		
		this.sprite = SpriteManager.addSprite(this.group, s, 1, sw, sh, 0, 0, 0);
		
		this.hitBox = new BoxCollider(this.group, w, h, 0, 101);
		this.hitBox.graphic.pullToFront();
		this.hitBox.addToCollisionGroups(100);
		
	}
	/**
	 *  Disposes the button
	 */
	public void dispose() {
		if(this.group.hasParent()) { this.group.getParent().removeElement(this.group);}
		this.hitBox.dispose();
		if(this.graphic != null) {
			if(this.graphic.hasParent()) { this.graphic.getParent().removeElement(this.graphic); }
			if(this.graphic.getClass().toString().equals("EZImage")) {
				((EZImage)this.graphic).img = null;
			}
			EZ.removeEZElement(this.graphic);
		}
		if(this.sprite != null) { this.sprite.dispose(); }
		EZ.removeEZElement(this.group);
	}
	
	/**
	 * Updates the button
	 */
	public void update() {
		// Reset events
		for(int i=0; i<this.events.length; i++)
			this.events[i] = false;
		
		// Check for mouse collision and events
		if(this.hitBox.hitObjectList.indexOf(Mouse.collider) >= 0) {
			this.events[0] = true;
			if(Mouse.ButtonsJustPressed[1]) {
				this.events[2] = true;
			}else if(Mouse.ButtonsJustReleased[1])
				this.events[3] = true;
			if(Mouse.Buttons[1])
				this.events[1] = true;
		}
		
		// Animates button
		if (this.graphic == null) {
			if(this.events[0]) {
				if(this.events[1]) {
					this.sprite.setAnimation(2, 0, 1);
				}else {
					this.sprite.setAnimation(1, 0, 1);
				}
			}else {
				this.sprite.setAnimation(0, 0, 1);
			}
		}else{
			if(this.events[0]) {
				if(this.events[1]) {
					this.graphic.setColor(this.graphicColors[2]);
				}else {
					this.graphic.setColor(this.graphicColors[1]);
				}
			}else {
				this.graphic.setColor(this.graphicColors[0]);
			}
		}
		
	}

}

class NavNode {
	
	public ArrayList<NavNode> visibleNodes = new ArrayList<NavNode>();
	public ArrayList<Integer> visibleNodeInds = new ArrayList<Integer>();
	public int[] VisibleNodeDistance;
	
	public EZCircle graphic;
	
	private Vector position;
	private Vector positionWorld;
	
	/**
	 * 
	 * @param x - x position in game world
	 * @param y - y position in game world
	 */
	public NavNode( double x, double y ) {
		this.position = new Vector(x,y);
		this.graphic = EZ.addCircle((int)this.position.x, (int)this.position.y, 100, 100, new Color(0,150,150,155), true);
		this.positionWorld = new Vector( this.graphic.getWorldXCenter(), this.graphic.getWorldYCenter() );
		//this.graphic.setLayer(20);
		this.graphic.hide();
		GE.worldGroup.addElement(this.graphic);
		this.graphic.setParent(GE.worldGroup);
		
	}
	public Vector getWorldPosition() {
		this.positionWorld.set(this.graphic.getWorldXCenter(), this.graphic.getWorldYCenter());
		return this.positionWorld.copy();
	}
	public Vector getPosition() {
		this.position.set(this.graphic.getXCenter(), this.graphic.getYCenter());
		return this.position.copy();
	}
	/**
	 * Disposes debug graphics
	 */
	public void dispose() {
		if(this.graphic.hasParent()) { this.graphic.getParent().removeElement(this.graphic); }
		EZ.removeEZElement(this.graphic);
	}
	
}

class SpriteManager {
	private static SpriteManager spriteManager;
	
	public static ArrayList<Sprite> sprites;
	
	/**
	 * Initializes the sprite manager
	 * @param max_sprites - Set the maximum number of sprites
	 */
	private SpriteManager(int max_sprites){
		sprites = new ArrayList<Sprite>();
	}
	
	public static void removeSprite(Sprite s) {
		s.dispose();
		sprites.remove(s);
	}

	/**
	 * Initializes the sprite manager
	 * @param max_sprites - Set the maximum number of sprites
	 */
	public static void init(int max_sprites) {
		spriteManager = new SpriteManager(max_sprites);
	}
	
	/**
	 * Updates all sprites
	 */
	public static void update() {
		for(int i=0; i<sprites.size(); i++) {
			if(sprites.get(i) != null) {
				sprites.get(i).update();
			}
		}
	}
	
	/**
	 * Create a new sprite and add to the sprite manager for tracking
	  * @param p - Parent object
	 * @param img_name - Image file
	 * @param fc - animation frame count
	 * @param sprite_width - width of the sprite frames
	 * @param sprite_height - height of the sprite frames
	 * @param start_ind_x - animation start index in the x position of sprite. starts at 0.
	 * @param start_ind_y - animation start index in the y position of sprite. starts at 0.
	 * @param p_type - Animation play type:
	 * 	0: loop
	 * 	1: once
	 * 	2: stop
	 * 	3: set count
	 * @return -  the created sprite
	 */
	public static Sprite addSprite(EZGroup p,
			String img_name, int fc,
			int sprite_width, int sprite_height,
			int start_ind_x, int start_ind_y,
			int p_type) {
		Sprite s = new Sprite(p, img_name, fc, sprite_width, sprite_height, start_ind_x, start_ind_y, p_type);
		sprites.add(s);
		return s;
	}
	
}

class Sprite{
	public EZImage image;
	private int[] img_ind_size = new int[2];	// the dimensions of the image in indexed frames
	private int[] sprite_size = new int[2];		// the dimensions of the frame in pixels
	private int[] sprite_start_ind = new int[2];// the starting frame in the img_ind_size
	private double sprite_play_speed = 1.0;
	private int sprite_play_type = 0;			// 0: loop, 1: once, 2: stop ,3: set count
	private double frame_current = 0;
	private int frame_count;					// number of frames in the sprite animation
	
	/**
	 * Creates a new sprite
	 * @param p - Parent object
	 * @param img_name - Image file
	 * @param fc - animation frame count
	 * @param sprite_width - width of the sprite frames
	 * @param sprite_height - height of the sprite frames
	 * @param start_ind_x - animation start index in the x position of sprite. starts at 0.
	 * @param start_ind_y - animation start index in the y position of sprite. starts at 0.
	 * @param p_type - Animation play type:
	 * 	0: loop
	 * 	1: once
	 * 	2: stop
	 * 	3: set count
	 */
	public Sprite(
			EZGroup p,
			String img_name, int fc,
			int sprite_width, int sprite_height,
			int start_ind_x, int start_ind_y,
			int p_type
		) {	
		this.image = EZ.addImage(img_name, 0, 0);
		if(p != null) {
			p.addElement(this.image);
			this.image.setParent(p);
		}
		
		//set_image(img_name);
		this.sprite_play_type = p_type;
		this.frame_count = fc;
		this.sprite_size[0] = sprite_width;
		this.sprite_size[1] = sprite_height;
		//Converts image dimensions to indexes
		this.img_ind_size[0] = ((int)this.image.getWidth())/this.sprite_size[0];
		this.img_ind_size[1] = ((int)this.image.getHeight())/this.sprite_size[1];
		// Set Start index
		this.sprite_start_ind[0] = start_ind_x;
		this.sprite_start_ind[1] = start_ind_y;
		// Set focus to the first sprite frame
		set_focus(0);
	}
	
	/**
	 * Set the focus area on the sprite. The frame to look at.
	 * @param frame - frame index. starts at 0.
	 */
	private void set_focus(int frame) {
		int ind_x = this.sprite_start_ind[0] + frame;
		int ind_y = sprite_start_ind[1];
		while( ind_x > img_ind_size[0]-1) {
			ind_x -= img_ind_size[0];
			ind_y += 1;
		}
		this.image.setFocus(
				(ind_x)*this.sprite_size[0], (ind_y)*this.sprite_size[1],
				(ind_x+1)*this.sprite_size[0], (ind_y+1)*this.sprite_size[1]);
	}
	/**
	 * Move the sprite to some point on the screen.
	 * @param x
	 * @param y
	 */
	public void translateTo( double x, double y) {
		this.image.translateTo(x, y);
	}
	/**
	 * Move the sprite by some x and y.
	 * @param x
	 * @param y
	 */
	public void translateBy( double x, double y) {
		this.image.translateBy(x, y);
	}
	/**
	 * rotate the sprite to "degrees"
	 * @param degrees
	 */
	public void rotateTo( double degrees) {
		this.image.rotateTo(degrees);
	}
	/**
	 * rotate the sprite by "degrees"
	 * @param degrees
	 */
	public void rotateBy( double degrees) {
		this.image.rotateBy(degrees);
	}
	/**
	 * Set the play speed of the sprite.
	 * @param speed - a double >= 0 .
	 */
	public void setPlaySpeed( double speed ) {
		this.sprite_play_speed = speed;
	}
	/**
	 * Scale the sprite to s.
	 * @param s
	 */
	public void scaleTo( double s) {
		this.image.scaleTo(s);
	}
	/**
	 * scale the sprite by s.
	 * @param s
	 */
	public void scaleBy( double s) {
		this.image.scaleBy(s);
	}
	/**
	 * Set animation, if multiple animations are on a sprite.
	 * @param start_ind_x - animation start index in the x position of sprite. starts at 0.
	 * @param start_ind_y - animation start index in the y position of sprite. starts at 0.
	 * @param fc - the number of frames in the animation
	 */
	public void setAnimation(int start_ind_x, int start_ind_y, int fc) {
		this.sprite_start_ind[0] = start_ind_x;
		this.sprite_start_ind[1] = start_ind_y;
		this.frame_count = fc;
		this.frame_current = 0;
		
	}
	/**
	 *  Advances sprite to the next frame depending on the time since the last screen refresh.
	 */
	public void update() {
		if(this.sprite_play_type == 0) {
			this.frame_current+= (double)(EZ.getDeltaTime())*(this.sprite_play_speed/15.8d);
			while(this.frame_current >= this.frame_count)
				this.frame_current -= this.frame_count;
			set_focus((int)(this.frame_current));
		}else if(this.sprite_play_type==1) {
			this.frame_current+= (double)(EZ.getDeltaTime())*(this.sprite_play_speed/15.8d);
			if(this.frame_current >= this.frame_count)
				this.frame_current = this.frame_count-1;
			set_focus((int)(this.frame_current));
		}
	}
	
	/**
	 * Disposes the sprite graphics
	 */
	public void dispose() {
		if(this.image.hasParent()) { this.image.getParent().removeElement(this.image); }
		this.image.img = null;
		//for(BufferedImage i : this.image.loadedImages) { i = null; }
		//this.image.loadedImages.clear();
		EZ.removeEZElement(this.image);
		SpriteManager.sprites.remove(this); 
	}
	
}

/*
 * This class handles all collision events. This includes both mouse and element collisions.
 * All colliders are assigned to a single physics group. Collisions only occur between objects if both have
 * the others physics group stored in their collisionGoups array.
 * 
 * Reserved physics groups:
 * 		mouse_pointer: 	100
 * 		buttons:		101
 * 
 * Note:
 * 		Currently no reaction forces are calculated.
 * 
 */
class PhysicsManager {
	private static PhysicsManager physics_manager;
	private static ArrayList<Collider> objectList;
	private static int max_col_objects;
	private static double meter = 100; // in pixels
	private static Vector gravity;
	private static double dragCoeff = 0.8;
	private static double angularDragCoeff = 0.0;
	private static double max_velocity;
	private static double max_angular_velocity;
	public static EZCircle dot0;
	//public EZCircle dot1;
	private static double updateDelay = 1.0d/60.0d;
	private static long timeLast = 0;
	private static double timeDelta = 0;
	
	/**
	 * Initialize physics manager
	 * @param max_c_o - Set the maximum number of physics objects
	 */
	private PhysicsManager(int max_c_o) {
		objectList = new ArrayList<Collider>();
		max_col_objects = max_c_o;
		gravity = new Vector(0,9.8); // in meters per second
		max_velocity = 30; // in meters per second
		max_angular_velocity = 2*Math.PI*4;
		dot0 = EZ.addCircle(0, 0, 5, 5, Color.WHITE,true);
		dot0.setLayer(1000);
		//this.dot1 = EZ.addCircle(0, 0, 5, 5, Color.WHITE,true);
		this.timeLast = 0;
	}
	
	/**
	 * Initialize physics manager
	 * @param max_c_o - Set the maximum number of physics objects
	 */
	public static void init(int max_c_o) {
		physics_manager = new PhysicsManager(max_c_o);
	}
	
	/**
	 * Set the world gravity.
	 * @param ax - the acceleration in the x direction.
	 * @param ay - the acceleration in the y direction.
	 */
	public static void setGravity( double ax, double ay ) { gravity.set(ax, ay); }
	
	/**
	 * 
	 * @return - the gravity vector in worldspace.
	 */
	public static Vector getGravity() { return gravity.copy(); }
	
	/**
	 * Updates physics manager
	 */
	public static void update() {
		if(timeLast == 0) { timeLast = System.currentTimeMillis(); return;}
		timeDelta = (double)(System.currentTimeMillis()-timeLast)/1000.0d;
		if(timeDelta < updateDelay)
			return;
		timeLast = System.currentTimeMillis();
		applyGravity();
		applyAirDrag();
		applyVelocity();
		updateColliderGeometry();
		updateCollisions();
		EZ.refreshScreen();
	}
	
	/**
	 * Update collider geometry
	 */
	private static void updateColliderGeometry() {
		for( Collider c :  objectList){
			if(c.active) { c.updateLines(); }
		}
		
	}
	
	/**
	 * apply drag to colliders
	 */
	private static void applyAirDrag() {
		for( Collider c :  objectList){
			if(c.active) {
				Vector vel = c.velocity.copy();
				//vel.multiply(dragCoeff*c.iMass);
				vel.multiply(dragCoeff);
				c.velocity.x-=(vel.x*timeDelta);
				c.velocity.y-=(vel.y*timeDelta);
				if(c.type==3) {
					//c.angularVelocity-=c.angularVelocity*c.momentInertia*dragCoeff*timeDelta;
					c.angularVelocity-=c.angularVelocity*dragCoeff*timeDelta;
				}
			}
		}
	}
	
	/**
	 * apply gravity
	 */
	private static void applyGravity() {
		for(Collider c : objectList) {
			if(c.active && (c.type == 2 || c.type == 3)) {
				c.velocity.x+=(gravity.x*timeDelta);
				c.velocity.y+=(gravity.y*timeDelta);
			}
		}
	}
	
	/**
	 * Apply velocity. Moves objects.
	 */
	public static void applyVelocity() {
		for(Collider c : objectList) {
			//c.updateSleepRotation();
			if(c.active) { applyVelocity(c); }
		}
	}
	public static void applyVelocity( Collider c ) {
		
		if(c.velocity.length()>max_velocity) {
			c.velocity.normalize();
			c.velocity.multiply(max_velocity);
		}
		if(Math.abs(c.angularVelocity)>max_angular_velocity)
			c.angularVelocity = Math.signum(c.angularVelocity)*max_angular_velocity;
		
		if(c.graphic.hasParent()) {
			c.graphic.getParent().translateBy(
					c.velocity.x*meter*timeDelta,
					c.velocity.y*meter*timeDelta
			);
			c.graphic.getParent().rotateBy( c.angularVelocity*180.d*timeDelta/Math.PI );
		}
	}
	/**
	 * Clear collided objects
	 */
	private static void clearObjectCollisions() {
		for(Collider c : objectList) {
			if(c != null) {
				c.hitObjectList.clear();
				c.collisionAppliedList.clear();
			}else {
				objectList.remove(c);
			}
		}
	}
	
	/**
	 * Update Collsions
	 */
	private static void updateCollisions() {
		clearObjectCollisions();
		for(int cc=0; cc<objectList.size()-1; cc++) {
			if(objectList.get(cc)!=null){
				for(int tc=objectList.size()-1; tc>cc; tc--) {
					if(objectList.get(tc)!=null) {
						if( 	!(objectList.get(cc).type == 4 && objectList.get(tc).type == 4) &&
								!(objectList.get(cc).type == 5 && objectList.get(tc).type == 5) &&
								objectList.get(cc).active && objectList.get(tc).active
								) {
							updateObjectCollision(objectList.get(cc), objectList.get(tc));
						}
					}else {
						objectList.remove(tc);
					}
				}
			}else {
				objectList.remove(cc);
			}
		}
	}
	
	/**
	 *  Add a new collider
	 * @param c - Collider to add
	 * @return - true if collider added successfully
	 */
	public static boolean addCollider(Collider c) {
		if(objectList.size()<max_col_objects) {
			objectList.add(c);
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 *  Remove a collider
	 * @param c - Collider to remove
	 * @return - true if collider removed successfully
	 */
	public static boolean removeCollider(Collider c) {
		if(objectList.indexOf(c)>=0) {
			objectList.remove(c);
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Check if two objects are colliding
	 * @param obj0 - object 1
	 * @param obj1 - object 2
	 */
	private static void updateObjectCollision(Collider obj0, Collider obj1) {
		// Check if objects contain the others' physics group
		boolean obj0HasObj1 = false;
		for(int i=0; i<obj0.collisionGroups.size(); i++) {
			if(obj0.collisionGroups.get(i)==obj1.physicsGroup)
				obj0HasObj1 = true;
		}
		boolean obj1HasObj0 = false;
		for(int i=0; i<obj1.collisionGroups.size(); i++) {
			if(obj1.collisionGroups.get(i)==obj0.physicsGroup)
				obj1HasObj0 = true;
		}
		if(!obj0HasObj1 || !obj1HasObj0) {
			return;
		}
		
		boolean in = false;
		if(obj0.hitObjectList.contains(obj1)) {
			in = true;
		}
	
		//	If each contains the others physics group
		if(!in) {
			if(obj0.lines.size()<1||obj1.lines.size()<1)
				return;
			Vector[] bb0 = obj0.getBoundingBox();
			Vector[] bb1 = obj1.getBoundingBox();
			
			
			//	Check Bounding Box Collision
			if(bb0[0].x <= bb1[1].x && bb0[1].x >= bb1[0].x &&
				bb0[0].y <= bb1[1].y && bb0[1].y >= bb1[0].y ) {
				
					Vector[] colDat = getMTV(obj0,obj1);
					if(colDat == null || colDat[0].length()==0)
						return;
					
					if(obj1.type !=1) { obj0.hitObjectList.add(obj1); }
					if(obj0.type !=1) { obj1.hitObjectList.add(obj0); }
					
					if(obj0.type == 0 || obj0.type == 1 || obj1.type == 0 || obj1.type == 1)
						return;
					
					colDat[0].rotate(-GE.worldGroup.getRotation()*Math.PI/180);
					Vector mtv = colDat[0].copy();
					applyCollisionVelocity(obj0,obj1,colDat[0],colDat[1], false);
					
					// move out of collision
					Vector tempV0 = mtv.copy();
					tempV0.normalize();
					tempV0.multiply(-3);
					Vector tempV1 = new Vector(gravity.x*meter*EZ.getDeltaTime()/1000, gravity.y*meter*EZ.getDeltaTime()/1000);
					if(obj0.type==3) { obj1.velocity.subtract(tempV1); }
					
					if(obj0.type==4) {
						obj1.graphic.getParent().translateBy((int)(mtv.x-tempV0.x), (int)(mtv.y-tempV0.y));
					}else if(obj1.type==4 ) {
						obj0.graphic.getParent().translateBy((int)(-mtv.x+tempV0.x), (int)(-mtv.y+tempV0.y));
					}else {
						obj0.graphic.getParent().translateBy((int)(-mtv.x+tempV0.x)/2, (int)(-mtv.y+tempV0.y)/2);
						obj1.graphic.getParent().translateBy((int)(mtv.x-tempV0.x)/2, (int)(mtv.y-tempV0.y)/2);
					}
				
			}
			
			
		}
		return;
	}
	
	/**
	 * Get the minimum translation vector between two objetcs
	 * @param obj0 - Object 1
	 * @param obj1 - Object 2
	 * @return - returns the minimum translation vector
	 */
	private static Vector[] getMTV(Collider obj0, Collider obj1) {
		Vector axis0, tempVP;
		double p00,p01,p10,p11;
		double p10_2,p00_2;
		
		double temp0, temp1;
		Vector tempV0, tempV1;
		
		Vector MTV = new Vector(0,0);
		double MTVLength, MTVLengthTemp;
		double MTV2Length, MTV2LengthTemp;
		MTVLength = -1; MTV2Length = -1;
		int axcol = 3;
		
		ArrayList<Vector> tempVerts00 = new ArrayList<Vector>();
		ArrayList<Vector> tempVerts01 = new ArrayList<Vector>();
		ArrayList<Vector> tempVerts10 = new ArrayList<Vector>();
		ArrayList<Vector> tempVerts11 = new ArrayList<Vector>();
		
		ArrayList<Vector> verts00 = new ArrayList<Vector>();
		ArrayList<Vector> verts01 = new ArrayList<Vector>();
		ArrayList<Vector> verts10 = new ArrayList<Vector>();
		ArrayList<Vector> verts11 = new ArrayList<Vector>();
		
		//	project verts onto obj0
		tempVP = obj0.getWorldPosition();
		for(int i=0; i<obj0.lines.size(); i++) {
			axis0 = obj0.lines.get(i).getNormalTo(tempVP);
			
			tempVerts01.clear();	tempVerts10.clear();
			tempVerts11.clear();	tempVerts00.clear();
		
			tempV0 = obj0.lines.get(0).worldPoints[0].copy();
			tempV0.subtract(tempVP);
			temp0 = tempV0.compOn(axis0); 
			p00 = temp0; p01 = temp0;
			for(int ii=0; ii<obj0.lines.size(); ii++) {
				tempV0 = obj0.lines.get(ii).worldPoints[0].copy();
				tempV0.subtract(tempVP);
				temp0 = tempV0.compOn(axis0);
				if(temp0<p00)
					p00 = temp0;
				else if(temp0>p01)
					p01 = temp0;
			}
			boolean first = true;
			tempV0 = obj1.lines.get(0).worldPoints[0].copy();
			tempV0.subtract(tempVP);
			temp1 = tempV0.compOn(axis0);
			p10 = temp1; p11 = temp1; p10_2 = temp1;
			tempVerts10.add(obj1.lines.get(0).worldPoints[0]);
			tempVerts10.add(obj1.lines.get(0).worldPoints[0]);
			
			for(int ii=0; ii<obj1.lines.size(); ii++) {
				tempV0 = obj1.lines.get(ii).worldPoints[0].copy();
				tempV0.subtract(tempVP);
				temp1 = tempV0.compOn(axis0);
				
				if(temp1<p10) {
					p10_2 = p10;
					p10 = temp1;
					tempVerts10.add(0,obj1.lines.get(ii).worldPoints[0].copy());
					tempVerts10.remove(2);
				}else if(temp1<p10_2) {
					p10_2 = temp1;
					tempVerts10.set(1,obj1.lines.get(ii).worldPoints[0].copy());
				
				}
				if(temp1>p11) {
					p11 = temp1;
				}
				
			}
			
			MTVLengthTemp = MTVLength + 1;
			if(p00<p10) {
				if(p01<p10)
					return null;
				else
					MTVLengthTemp = p01-p10;
			}else{
				if(p11<p00)
					return null;
				else
					MTVLengthTemp = p01-p10;
			}
			MTV2LengthTemp = MTV2Length + 1;
			if(p00<p10_2) {
				if(p01<p10_2)
					tempVerts10.remove(1);
				else
					MTV2LengthTemp = p01-p10_2;
			}else{
				if(p11<p00)
					tempVerts10.remove(1);
				else
					MTV2LengthTemp = p01-p10_2;
			}
			
			if(MTVLength<0 || MTVLength > MTVLengthTemp){
				verts00.clear(); verts11.clear(); verts01.clear();
				verts10 = (ArrayList<Vector>) tempVerts10.clone();
				MTV.copy(axis0);
				MTV2Length = MTV2LengthTemp;
				MTVLength = MTVLengthTemp;
				axcol = 0;
			}
					
		}
		
		//	project verts onto obj1
		tempVP = obj1.getWorldPosition();
		for(int i=0; i<obj1.lines.size(); i++) {
			axis0 = obj1.lines.get(i).getNormalTo(tempVP);
			tempVerts00.clear(); tempVerts11.clear();
			
			tempV0 = obj0.lines.get(0).worldPoints[0].copy();
			tempV0.subtract(tempVP);
			temp0 = tempV0.compOn(axis0);
			p00 = temp0; p01 = temp0; p00_2 = temp0;
			tempVerts00.add(obj0.lines.get(0).worldPoints[0]);
			tempVerts00.add(obj0.lines.get(0).worldPoints[0]);
			for(int ii=0; ii<obj0.lines.size(); ii++) {
				tempV0 = obj0.lines.get(ii).worldPoints[0].copy();
				tempV0.subtract(tempVP);
				temp0 = tempV0.compOn(axis0);
				
				if(temp0<p00) {
					p00_2 = p00;
					p00 = temp0;
					tempVerts00.add(0,obj0.lines.get(ii).worldPoints[0].copy());
					tempVerts00.remove(2);
				}if(temp0 < p00_2) {
					p00_2 = temp0;
					tempVerts00.set(1,obj0.lines.get(ii).worldPoints[0].copy());
				}
				if(temp0>p01){
					p01 = temp0;
				}
			}
			
			tempV0 = obj1.lines.get(0).worldPoints[0].copy();
			tempV0.subtract(tempVP);
			temp1 = tempV0.compOn(axis0);
			p10 = temp1; p11 = temp1;
			for(int ii=0; ii<obj1.lines.size(); ii++) {
				tempV0 = obj1.lines.get(ii).worldPoints[0].copy();
				tempV0.subtract(tempVP);
				temp1 = tempV0.compOn(axis0);
				if(temp1<p10)
					p10 = temp1;
				else if(temp1>p11)
					p11 = temp1;
			}
			
			MTVLengthTemp = MTVLength + 1;
			if(p10<p00) {
				if(p11<p00)
					return null;
				else
					MTVLengthTemp = p11-p00;
			}else{
				if(p01<p10)
					return null;
				else 
					MTVLengthTemp = p11-p00;
			}
			MTV2LengthTemp = MTV2Length + 1;
			if(p10<p00_2) {
				if(p11<p00_2)
					tempVerts00.remove(1);
				else
					MTV2LengthTemp = p11-p00_2;
			}else{
				if(p01<p10)
					tempVerts00.remove(1);
				else 
					MTV2LengthTemp = p11-p00_2;
			}
			
			if(MTVLength < 0 || MTVLength > MTVLengthTemp){
				verts01.clear(); verts10.clear(); verts11.clear();
				verts00 = (ArrayList<Vector>) tempVerts00.clone();
				//verts11 = (ArrayList<Vector>) tempVerts11.clone();
				MTV.copy(axis0);
				MTV2Length = MTV2LengthTemp;
				MTVLength = MTVLengthTemp;
				axcol = 1;
			}
		}
		if(axcol==3 || (int)MTVLength<=0)
			return null;
		
		tempV0 = new Vector(0, 0);
		tempV1 = new Vector(0, 0);
		ArrayList<Vector> tempV3 = new ArrayList<Vector>();
		
		if(axcol == 0) {
			tempV3 = (ArrayList<Vector>) verts10.clone();
		}if(axcol == 1) {
			tempV3 = (ArrayList<Vector>) verts00.clone();
		}
		//dot0.translateTo(tempV3.get(0).x, tempV3.get(0).y);
		//if(tempV3.size()>1)
		//	dot1.translateTo(tempV3.get(1).x, tempV3.get(1).y);
		MTV.normalize();
		Vector MTV2 = MTV.copy();
		MTV.multiply(MTVLength);
		MTV2.multiply(MTV2Length);
		Vector[] tempVA;
		if(axcol == 0) {
			MTV.multiply(-1);
			MTV2.multiply(-1);
		}
		if(tempV3.size()==1)
			tempVA = new Vector[] {MTV, tempV3.get(0)};
		else
			tempVA = new Vector[] {MTV, tempV3.get(0), MTV2, tempV3.get(1)};
		
		return tempVA;
		
	}
	
	/**
	 * Get impulse from collision between two objects
	 * @param obj0 - object 1
	 * @param obj1 - object 2
	 * @param dir - direction of impulse
	 * @return - impulse vector
	 */
	public static Vector getLinearImpulse(Collider obj0, Collider obj1, Vector dir) {
		Vector relVel = obj0.velocity.copy();
		relVel.subtract(obj1.velocity);
		double elasticity = Math.min(obj0.elasticity, obj1.elasticity);
		
		Vector impulse = dir.copy();
		impulse.multiply( relVel.dot(dir));
		impulse.multiply(-1-elasticity);
		impulse.multiply(1/( obj0.iMass + obj1.iMass ));
		
		return impulse;
	}
	
	/**
	 * Gets the change in linear and angular velocity from collision
	 * @param obj0 - object 1
	 * @param obj1 - Object 2
	 * @param MTV - minimum translation vector
	 * @param colPoint - point of collision
	 * @param invert - not used;
	 */
	public static void applyCollisionVelocity(Collider obj0, Collider obj1, Vector MTV, Vector colPoint, boolean angle_only) {
		Vector tempV0, tempV1;
		if(MTV.length() > 1) {
			MTV.normalize();
			MTV.multiply(1);
		}
		
		Vector normal = MTV.copy();
		normal.normalize();
		
		Vector relVel = obj0.velocity.copy();
		relVel.subtract(obj1.velocity);
		
		Vector r0 = colPoint.copy();
		r0.subtract(obj0.getWorldPosition());
		
		Vector r1 = colPoint.copy();
		r1.subtract(obj1.getWorldPosition());
		
		
		double elasticity = Math.min(obj0.elasticity, obj1.elasticity);
		
		double contactVel = relVel.dot(normal);
		
		Vector impulse, tempI;
		if(contactVel > 0 && !angle_only) {
			// velocity
			impulse = getLinearImpulse(obj0, obj1, normal);
			
			//Vector friction = getLinearImpulse(obj0, obj1, tanVec);
			
			
			//if(friction.length() > impulse.length()*1.2) {
			//	friction = impulse.copy();
			//	friction.multiply(-1.2);
			//}else {
			//	friction.multiply(0.5);
			//}
			//impulse.add(friction);
			
			if(obj0.type != 4) {
				tempI = impulse.copy();
				tempI.multiply(obj0.iMass);
				obj0.velocity.add(tempI);
					
			}
		
			if(obj1.type != 4) {
				tempI = impulse.copy();
				tempI.multiply(obj1.iMass);
				obj1.velocity.subtract(tempI);
			}
		
		}
			
		// angular velocity
		
		Vector Vw0 = r0.getUnitVector();
		Vw0.crossSV(obj0.angularVelocity);
		//Vw0.add(obj0.getWorldPosition());
		
		Vector Vw1 = r1.getUnitVector();
		Vw1.crossSV(obj1.angularVelocity);
		//Vw1.add(obj1.getWorldPosition());
		
		Vector relVelw = Vw0.copy();
		relVelw.subtract(Vw1);
		
		Vector tanVecw = new Vector( normal.y, -normal.x);
		if(relVelw.compOn(tanVecw)>0)
			tanVecw.multiply(-1);
		
		Vector tanVec = new Vector( normal.y, -normal.x);
		if(relVel.compOn(tanVec)>0)
			tanVec.multiply(-1);
		
		//contactVel = relVelw.cross(normal);
		
		//if(Math.abs(contactVel) < 0) {
			impulse = relVelw.copy();
			//impulse = relVelw.projOnto(normal);
			//impulse.multiply(relVelw.dot(normal));
			impulse.multiply( (-1-elasticity)/( obj0.iMass + obj1.iMass + (Math.pow(r0.cross(normal),2)/obj0.momentInertia) +  (Math.pow(r1.cross(normal),2)/obj1.momentInertia) ) );
			
			double temp0 = (1/obj0.momentInertia)*impulse.length()*1000;
			double temp1 = -(1/obj1.momentInertia)*impulse.length()*1000;
			
			tempV0 = relVelw.copy();
			tempV0.normalize();
			//if(angle_only) { temp0*=0.2; temp1*=0.2; }
			if(obj0.type==3) {
				obj0.angularVelocity += temp0;
				//System.out.println(impulse.length());
			}
			if(obj1.type==3 ) {
				obj1.angularVelocity += temp1;
				//System.out.println(impulse.length());
			}
			
		
		//}
		
	}

}


class Segment{
	public EZLine line,wline;
	//public EZCircle dot;
	public Vector[] points = new Vector[2];
	public Vector[] worldPoints = new Vector[2];
	public Vector direction, worldDirection;
	public double length;
	public double worldRot = 0;
	
	/**
	 * Create a new line segment
	 * @param l - length of the line segment
	 */
	public Segment(double l) {
		this.points[0] = new Vector(0,0);
		this.points[1] = new Vector(l,0);
		this.worldPoints = new Vector[] {this.points[0].copy(),this.points[1].copy()};
		this.direction = new Vector(1,0);
		this.worldDirection = this.direction.copy();
		this.length = l;
		this.line = EZ.addLine(0,0,(int)l, 0, Color.RED);
		this.wline = EZ.addLine(0,0,(int)l, 0, Color.orange);
		//this.dot = EZ.addCircle(0, 0, 5, 5, Color.RED, true);
		//this.dot.hide();
		this.line.hide();
		this.wline.hide();
		//this.line.setLayer(20);
		//this.dot.setLayer(20);
		
	}
	
	/**
	 * disposes the debug graphics
	 */
	public void dispose() {
		if(this.line.hasParent()) { this.line.getParent().removeElement(this.line); }
		EZ.removeEZElement(this.line);
		//EZ.removeEZElement(this.dot);
	}
	
	/**
	 * Check intersection between this and another line
	 * @param s - Line segment to check intersection
	 * @param asRay - If true, this acts as a raycast
	 * @return - point of intersection as a ray hit point object
	 */
	public RayHitPoint intersects(Segment s, boolean asRay) {
		//this.dot.hide();
		Vector r_p = this.worldPoints[0].copy();
		Vector r_dp = this.worldPoints[1].copy();
		r_dp.subtract(r_p);
		
		Vector s_p = s.worldPoints[0].copy();
		Vector s_dp = s.worldPoints[1].copy();	
		s_dp.subtract(s_p);
		
		if(r_dp.angleBetween(s_dp) == 0 || r_dp.angleBetween(s_dp) == Math.PI)
			return null;
		
		double T2 = ( r_dp.x*(s_p.y-r_p.y ) + r_dp.y*(r_p.x-s_p.x) )/( s_dp.x*r_dp.y - s_dp.y*r_dp.x );
		double T1 = ( s_p.x + s_dp.x*T2 - r_p.x )/( r_dp.x );
		
		if(asRay) {
			if(T1 <= 0 || T2 <= 0 || T2 >= 1) { return null; }
		}else {
			if(T1 <= 0 || T1 >= 1 || T2 <= 0 || T2 >= 1) {	return null; }
		}
				
		Vector v = new Vector( r_p.x + r_dp.x*T1 , r_p.y + r_dp.y*T1 );
		//this.dot.show();
		//this.dot.translateTo(v.x, v.y);
		//if(asRay) { return new RayHitPoint( v, T1, this.getRotation()); }
		return new RayHitPoint( v, T1, Math.signum(r_dp.y)*r_dp.angleBetween(new Vector(1,0)));
	}
	
	/**
	 * Updates the line segment after transformation
	 */
	public void updateSegment() {
		Vector tempV = this.direction.copy();
		tempV.multiply(this.length);
		this.points[1].copy(this.points[0]);
		this.points[1].add(tempV);
		
		if(this.line.hasParent()) {
			double scale = 1;
			EZGroup p = this.line.getParent();
			this.worldRot = this.line.getRotation();
			this.worldRot += p.getRotation();
			scale *= p.getScale();
			while( p.hasParent() ) {
				p = p.getParent();
				this.worldRot += p.getRotation();
				scale *= p.getScale();
			}
			this.worldRot *= Math.PI/180;
			Vector vv = new Vector(0,0);
			
			this.worldDirection.set(1,0);
			this.worldDirection.rotate(this.worldRot);
			
			vv.copy(this.worldDirection);
			vv.multiply(length*scale/2);
			
			//this.worldPoints[0].copy(this.points[0]);
			//this.worldPoints[0].rotate(group_rot);
			
			this.worldPoints[0].set(this.line.getWorldXCenter(), this.line.getWorldYCenter());
			this.worldPoints[0].subtract(vv);
			
			this.worldPoints[1].set(this.line.getWorldXCenter(), this.line.getWorldYCenter());
			this.worldPoints[1].add(vv);
			
			//this.worldPoints[0].add(this.line.getWorldXCenter(),this.line.getWorldYCenter());
			//this.worldPoints[0].rotate(group_rot);
			/*
			p = this.line.getParent();
			this.worldPoints[0].add(p.getXCenter(),p.getYCenter());
			while(p.hasParent()) {
				p = p.getParent();
				this.worldPoints[0].add(p.getXCenter(),p.getYCenter());
			}
			*/
			/*
			tempV.copy(this.worldDirection);
			tempV.multiply(this.length);
			tempV.rotate(group_rot);
			this.worldPoints[1].copy(this.worldPoints[0]);
			this.worldPoints[1].add(tempV);
			*/
			
		}else {
			this.worldDirection.copy(this.direction);
			this.worldPoints[0].copy(this.points[0]);
			this.worldPoints[1].copy(this.points[1]);
		}
		
		this.line.setPoint1( (int)( this.points[0].x) , (int)(this.points[0].y) );
		this.line.setPoint2( (int)( this.points[1].x) , (int)(this.points[1].y) );
		this.wline.setPoint1( (int)( this.worldPoints[0].x) , (int)(this.worldPoints[0].y) );
		this.wline.setPoint2( (int)( this.worldPoints[1].x) , (int)(this.worldPoints[1].y) );
				
	}
	
	/**
	 * Get the line normal vector relative to a point
	 * @param v - a point above or below
	 * @return - a vector orthogonal to this that is pointing in the opposite direction of v
	 */
	public Vector getNormalTo(Vector v) {
		this.updateSegment();
		Vector Pt = v.copy();
		Pt.subtract(this.worldPoints[0]);
		
		Vector normal = this.worldPoints[1].copy();
		normal.subtract(this.worldPoints[0]);
		normal.set(-normal.y,normal.x);
		
		if(Pt.dot(normal)>0)
			normal.multiply(-1);
		
		normal.normalize();
		//normal.rotate(-this.worldRot);
		return normal;
	}
	
	/**
	 * Get the line normal vector relative to a point
	 * @param p1x - point x position
	 * @param p1y - point y position
	 * @return - a vector orthogonal to this that is pointing in the opposite direction of the specified points
	 */
	public Vector getNormalTo(double p1x, double p1y) {
		return this.getNormalTo(new Vector(p1x,p1y));
	}
	
	/**
	 * rotate the line segment by theta about the first point in the line
	 * @param theta - in radians
	 */
	public void rotateBy(double theta) {
		this.direction.rotate(theta);
		this.updateSegment();
	}
	
	/**
	 * rotate the line segment to theta from the positive x-axis about the first point in the line
	 * @param theta - in radians
	 */
	public void rotateTo(double theta) {
		this.direction.set(1, 0);
		this.direction.rotate(theta);
		this.updateSegment();
	}
	
	/**
	 * 
	 * @return - line local rotation in radians
	 */
	public double getRotation() {
		double t = Math.atan2(this.direction.y, this.direction.x);
		while(t>=Math.PI*2) {
			t-=Math.PI*2;
		}
		while(t<-Math.PI*2) {
			t+=Math.PI*2;
		}
		return t;
	}
	
	/**
	 * 
	 * @return - line rotation in radians
	 */
	public double getWorldRotation() {
		this.updateSegment();
		/*
		EZGroup p = this.line.getParent();
		double wr = (p.getRotation()) +( getRotation()*180/Math.PI);
		while(p.hasParent()) {
			wr += p.getRotation();
		}
		while(wr>=360.0d) {
			wr-=360.0d;
			
		}
		while(wr<0.0d) {
			wr+=360.0d;
			
		}
		return (wr);
		*/
		return this.worldRot;
	}
	
	/**
	 * Translates the line segment to a point
	 * @param x - translation in the x direction
	 * @param y - translation in the y direction
	 */
	public void translateTo(double x, double y) {
		this.points[1].copy(this.direction.copy());
		this.points[1].multiply(this.length);
		this.points[1].add(x,y);
		this.points[0].set(x, y);
		this.updateSegment();
	}
	
	/**
	 * Translates the line segment 
	 * @param x - translation in the x direction
	 * @param y - translation in the y direction
	 */
	public void translateBy(double x, double y) {
		this.points[1].copy(this.direction.copy());
		this.points[1].multiply(this.length);
		this.points[1].add(x,y);
		this.points[0].add(x,y);
		this.updateSegment();
	}
	
}


class RayHitPoint implements Comparable<RayHitPoint> {
	public Vector point;
	public double angle;
	public double T1;
	
	/**
	 * Create a Ray hit point object
	 * @param p - hit position
	 * @param val - T1 value
	 * @param a - angle from the ray origin
	 */
	public RayHitPoint(Vector p, double val, double a) {
		this.point = p;
		this.T1 = val;
		this.angle = a;
	}
	
	/**
	 * Copy the ray hit point object
	 */
	public RayHitPoint copy() {
		return new RayHitPoint(this.point.copy(), this.T1, this.angle);
	}
	@Override
	public int compareTo(RayHitPoint arg0) {
		if(this.angle>arg0.angle) { return 1; }
		if(this.angle<arg0.angle) { return -1; }
		return 0;
		
	}

}

/*
 * Super class for collision objects.
 */
class Collider {
	public Entity parentEntity;
	
	public EZElement graphic = null;
	public int shape = 0;													// 0: rectangle, 1: circle
	public int physicsGroup = 0;
	public ArrayList<Integer> collisionGroups = new ArrayList<Integer>();	// collision occurs between objects in groups defines here. 
	public int  type = 0;													// 0: pass_over, 1: sensor, 2: dynamic, 3: rigid, 4: static, 5: player;
	public ArrayList<Collider> hitObjectList = new ArrayList<Collider>();
	public ArrayList<Collider> collisionAppliedList = new ArrayList<Collider>();
	public ArrayList<Segment> lines = new ArrayList<Segment>(); // x:[0][i] y:[1][j] drawn counter-clockwise
	public boolean active = true;
	public double elasticity = 1.0; // 1: elastic, 0: not elastic
	public double friction = 0.0;
	
	public double mass = 1.0;
	public double iMass = 1.0;
	public Vector velocity = new Vector(0,0);
	public double angularVelocity = 0.0d;
	public double totalArea = 0;
	public double momentInertia = 0;
	
	public boolean rotSleep = false;
	public long rotSleepTimeInit = 0;
	public double rotLast = 0;
	public double rotdelta = 0;
	
	public double rotationLast = 0;
	public double rotationLastTime = 0;
	public double rotationLastSleepTime = 1;
	public double rotationLastThresh = 0.0;
	
	
	public void updateSleepRotation() {
		if( Math.abs(this.angularVelocity - this.rotationLast) > rotationLastThresh ) {
			rotationLastTime = 0;
		}else { rotationLastTime += EZ.getDeltaTime(); }
		if( rotationLastTime/1000 >=  rotationLastSleepTime) { this.angularVelocity = 0; }
		this.rotationLast = this.angularVelocity;
	}
	/**
	 * Disposes the debug graphics
	 */
	public void dispose() {
		PhysicsManager.removeCollider(this);
		if(this.graphic.hasParent()) { this.graphic.getParent().removeElement(this.graphic); }
		EZ.removeEZElement(this.graphic);
		for(Segment s : this.lines) {
			s.dispose();
		}
		this.lines.clear();
	}
	/**
	 * Add a collision group to the collider
	 * @param c - collision group to enable collision with
	 */
	public void addToCollisionGroups(int c) {
		if(!this.collisionGroups.contains(c)) {
			this.collisionGroups.add(c);
		}
	}
	/**
	 * Translation in local-space of the collider
	 * @param x
	 * @param y
	 */
	public void setOffset(double x, double y) {
		this.graphic.translateTo(x, y);
	}
	/**
	 * Sets the colliders' mass
	 * @param m - mass
	 */
	public void setMass(double m) {
		this.mass = m;
		this.iMass = 1/this.mass;
		this.momentInertia = this.totalMassInertia();
	}
	
	
	/**
	 * 
	 * @return - Size after all Transformations
	 */
	public Vector getSize() {
		if(this.getParent() == null) {
			return new Vector(
					this.graphic.getWidth()*this.graphic.getScale(),
					this.graphic.getHeight()*this.graphic.getScale()
			);
		}else {
			return new Vector(
					this.graphic.getWidth()*this.graphic.getScale()*this.graphic.getParent().getScale(),
					this.graphic.getHeight()*this.graphic.getScale()*this.graphic.getParent().getScale()
			);
		}
	}
	
	/**
	 * 
	 * @return - Bounding-box dimensions as a vector
	 */
	public Vector[] getBoundingBox(){
		double x0 = this.lines.get(0).worldPoints[0].x;
		double y0 = this.lines.get(0).worldPoints[0].y;
		double x1 = this.lines.get(0).worldPoints[0].x;
		double y1 = this.lines.get(0).worldPoints[0].y;
		for(int i=1; i<this.lines.size(); i++) {
			this.lines.get(i).updateSegment();
			if(x0>this.lines.get(i).worldPoints[0].x)
				x0 = this.lines.get(i).worldPoints[0].x;
			if(x1<this.lines.get(i).worldPoints[0].x)
				x1 = this.lines.get(i).worldPoints[0].x;
			if(y0>this.lines.get(i).worldPoints[0].y)
				y0 = this.lines.get(i).worldPoints[0].y;
			if(y1<this.lines.get(i).worldPoints[0].y)
				y1 = this.lines.get(i).worldPoints[0].y;
		}
		Vector[] v = new Vector[]{
				new Vector(x0, y0),
				new Vector(x1, y1)
		};
		return v;
	}
	/**
	 * 
	 * @param v0 - point 1
	 * @param v1 - point 2
	 * @param v2 - point 3
	 * @return - Area inertia of the triangle
	 */
	public double areaInertiaTri(Vector v0, Vector v1, Vector v2) {
		ArrayList<Vector> sides = new ArrayList<Vector>();
		Vector side0 = v1.copy();
		side0.subtract(v0);
		Vector side1 = v2.copy();
		side0.subtract(v0);
		Vector side2 = v1.copy();
		side0.subtract(v2);
		
		
		sides.add(side0);
		if(side1.length()>side0.length())
			sides.add(0, side1);
		else
			sides.add(side1);
		
		if(side2.length()>sides.get(0).length())
			sides.add(0,side2);
		else if(side2.length()>sides.get(1).length())
			sides.add(1,side2);
		else
			sides.add(side2);
		
		double a = Math.abs( sides.get(2).compOn(sides.get(0)) );
		double b = sides.get(0).length();
		double h = Math.pow( (sides.get(2).length()*sides.get(2).length())-(a*a) , 0.5);
		
		return ((b*b*b*h)-(b*b*h*a)+(b*h*a*a)+(b*h*h*h))/36.0   /10; 								// modified
	}
	/**
	 * Update the sides of the collider
	 */
	public void updateLines() {
		for(int i=0; i<this.lines.size(); i++)
			this.lines.get(i).updateSegment();
	}
	/**
	 * 
	 * @return - Total mass inertia of the collider
	 */
	public double totalMassInertia() {
		double I = 0;
		for(int i=0; i<this.lines.size(); i++) {
			I += this.massInertiaTri(new Vector(0,0) , this.lines.get(i).points[0], this.lines.get(i).points[1]);
		}
		return I;
	}
	/**
	 * 
	 * @param v0 - point 1
	 * @param v1 - point 2
	 * @param v2 - point 3
	 * @return - Mass inertia of the triangle
	 */
	public double massInertiaTri(Vector v0, Vector v1, Vector v2) {
		Vector d = this.triangleCM(v0, v1, v2);
		d.subtract(v0);
		return (areaInertiaTri(v0, v1, v2)*this.mass/areaOfTriangle(v0, v1, v2)) + (this.mass*areaOfTriangle(v0, v1, v2)*d.length()*d.length()/this.totalArea);
	}
	/**
	 * 
	 * @param v0 - point 1
	 * @param v1 - point 2
	 * @param v2 - point 3
	 * @return - Area of the triangle
	 */
	public double areaOfTriangle(Vector v0, Vector v1, Vector v2) {
		Vector tempV1, tempV2;
		
		tempV1 = v1.copy();
		tempV1.subtract(v0);
		
		tempV2 = v2.copy();
		tempV2.subtract(v0);
		return tempV1.length()*tempV2.length()*Math.sin(tempV2.angleBetween(tempV1))/2;
	}
	/**
	 * 
	 * @return - Total area of the collider
	 */
	public double getTotalArea() {
		double a = 0;
		for(int i=0; i<this.lines.size(); i++) {
			a += areaOfTriangle(new Vector(0,0), this.lines.get(i).points[0], this.lines.get(i).points[1]);
		}
		return a;
	}
	/**
	 * 
	 * @param v0 - point 1
	 * @param v1 - point 2
	 * @param v2 - point 3
	 * @return - Center of mass of the triangle
	 */
	public Vector triangleCM(Vector v0, Vector v1, Vector v2) {
		Vector v = v0.copy();
		v.add(v1);
		v.add(v2);
		v.multiply(1/3);
		return v;
	}
	/**
	 * 
	 * @return - World rotation of the collider
	 */
	public double getWorldRotation() {
		if(this.getParent() != null) {
			EZElement p = this.getParent();
			double rot = p.getRotation()+this.graphic.getRotation();
			while( p.hasParent() ) {
				p = p.getParent();
				rot += p.getRotation();
			}
			return rot;
		}else {
			return this.graphic.getRotation();
		}
	}
	/**
	 * 
	 * @return - World position of the collider
	 */
	public Vector getWorldPosition() {
		return new Vector(this.graphic.getWorldXCenter(), this.graphic.getWorldYCenter());
	}
	/**
	 * 
	 * @return - Local position of the collider
	 */
	public Vector getOffset() {
		return new Vector(this.graphic.getXCenter(),this.graphic.getYCenter());
	}
	/**
	 * Sets the colliders' parent object
	 * @param p - parent object
	 */
	public void setParent(EZGroup p) {
		if(this.graphic.hasParent()) {this.graphic.getParent().removeElement(this.graphic);}
		p.addElement(this.graphic);
		this.graphic.setParent(p);
	}
	/**
	 * 
	 * @return - Parent object of the collider
	 */
	public EZGroup getParent() {
		return this.graphic.getParent();
	}
}

 class BoxCollider extends Collider{
	/**
	 * Creates a rectangular collision object.
	 * @param p - Parent object
	 * @param w - Width of the rectangle
	 * @param h - Height of the rectangle
	 * @param t - the type of collision object:
	 * 	0: Pass-Over
	 * 	1: Sensor
	 * 	2: Dynamic
	 * 	3: Rigid
	 * 	4: Static
	 * @param g	- The physics group. Will collide if the other object has this value in their collision groups
	 */
	public BoxCollider(EZGroup p, int w, int h, int t, int g) {
		
		this.physicsGroup = g;
		this.type = t;
		this.shape = 0;
		this.graphic = EZ.addRectangle(0, 0, w, h, new Color(255,0,0,100), true);
		this.graphic.hide();
		if(t==4) {
			this.iMass = 0;
			this.mass = 999999999;
		}else {
			this.mass = 1;
			this.iMass = 1;
		}
		
		PhysicsManager.addCollider(this);
		//if(this.type != 0 && this.type != 1) {
			this.lines.add( new Segment(w) );
			this.lines.get(0).rotateTo(0);
			this.lines.get(0).translateTo(-w/2, -h/2);
			this.lines.get(0).line.setColor(Color.WHITE);
			
			this.lines.add( new Segment(h) );
			this.lines.get(1).translateTo(w/2, -h/2);
			this.lines.get(1).rotateTo(Math.PI/2);
			
			this.lines.add( new Segment(w) );
			this.lines.get(2).translateTo(w/2, h/2);
			this.lines.get(2).rotateTo(Math.PI);
			
			this.lines.add( new Segment(h) );
			this.lines.get(3).translateTo(-w/2, h/2);
			this.lines.get(3).rotateTo(Math.PI*3/2);
			
			for(int i=0; i<this.lines.size(); i++) {
				//this.lines.get(i).line.hide();
				p.addElement(this.lines.get(i).line);
				this.lines.get(i).line.setParent(p);
			}
		//}
		
		p.addElement(this.graphic);
		this.graphic.setParent(p);
		
		this.totalArea = this.getTotalArea();
		this.momentInertia = totalMassInertia();
		//System.out.println(this.momentInertia);
		
	}
	
}

class PolyCollider extends Collider{
		
		/**
		 * Creates a polygon collider
		 * @param p - Parent group
		 * @param vx - Vertex x coordinate array
		 * @param vy - Vertex y coordinate array
		 * @param t - the type of collision object:
		 * 	0: Pass-Over
		 * 	1: Sensor
		 * 	2: Dynamic
		 * 	3: Rigid
		 * 	4: Static
		 * @param g - The physics group. Will collide if the other object has this value in their collision groups
		 */
	public PolyCollider(EZGroup p, int[] vx, int[] vy, int t, int g) {
			
			this.physicsGroup = g;
			this.type = t;
			this.shape = 0;
			this.graphic = EZ.addPolygon(vx, vy, new Color(255,0,0,100), true);
			this.graphic.hide();
			
			if(t==4) {
				this.iMass = 0;
				this.mass = 999999999;
			}else {
				this.mass = 1;
				this.iMass = 1;
			}
			
			PhysicsManager.addCollider(this);
			int x, k;
			for(int i=0; i<vx.length; i++) {
				x = i+1;
				if(x>=vx.length)
					x -= vx.length;
				this.lines.add( new Segment( magnitude(vx[i],vy[i],vx[x],vy[x]) ) );
				k = this.lines.size()-1;
				this.lines.get( k ).rotateTo(Math.atan2(vy[x]-vy[i],vx[x]-vx[i]) );
				this.lines.get( k ).translateTo(vx[i],vy[i]);
				this.lines.get( k ).line.setColor(Color.WHITE);
				
				p.addElement(this.lines.get( k ).line);
				this.lines.get( k ).line.setParent(p);
				//this.lines.get( k ).line.hide();
			}
			this.lines.get(0).line.setColor(Color.GREEN);
			
			p.addElement(this.graphic);
			this.graphic.setParent(p);
			
			this.totalArea = this.getTotalArea();
			this.momentInertia = this.totalMassInertia();
			
		}
	
	private double magnitude(int x0, int y0, int x1, int y1) {
			return Math.pow( ((x1-x0)*(x1-x0))+((y1-y0)*(y1-y0)) , 0.5);
		}
		
}

class SoundManager {
	
	private static ArrayList<MP3Sound> MP3Sounds = new ArrayList<MP3Sound>();
	private static ArrayList<WAVSound> WAVSounds = new ArrayList<WAVSound>();
	
	
	public static void update() {
		for( MP3Sound mp3s : MP3Sounds ) { mp3s.update(); }
	}
	
	public static WAVSound addWAVSound( String file ) {
		WAVSounds.add( new WAVSound( file ) );
		return WAVSounds.get( WAVSounds.size()-1 );
	}
	public static MP3Sound addMP3Sound( String file ) {
		MP3Sounds.add( new MP3Sound(file) );
		return MP3Sounds.get( MP3Sounds.size()-1 );
	}
	
	public static void removeEZSound(WAVSound wavs) { WAVSounds.remove(wavs); wavs.dispose(); }
	public static void removeMP3Sound( MP3Sound mp3s ) { MP3Sounds.remove(mp3s); mp3s.dispose(); }
	public static void removeAllSounds() {
		for( WAVSound ws : WAVSounds ) { ws.dispose(); } WAVSounds.clear();
		for( MP3Sound ms : MP3Sounds ) {ms.dispose(); } MP3Sounds.clear();
	}
}

class MP3Sound {
	
	private MediaPlayer mediaPlayer;
	private boolean isPlaying = false;
	private boolean looping = false;
	private double stopTime;
	private double lastTime;
	private double fadeTo = 1.0d;
	private double fadeFactor = 0.0d;
	
	public MP3Sound(String file){
		this.mediaPlayer = new MediaPlayer( new Media(new File(file).toURI().toString()));
		this.looping = false;
		this.stopTime = this.mediaPlayer.getStopTime().toSeconds();
		seek(0);
	} 
	 
	public void update() {
		double currentTime = this.mediaPlayer.getCurrentTime().toSeconds();
		
		// loop sound
		if(this.looping && currentTime >= this.stopTime) { seek(0); play(); }
		
		// check if still playing
		if(currentTime != this.lastTime) { this.isPlaying = true; }else { this.isPlaying = false; }
		
		//fade sound
		double volume = this.getVolume();
		if(fadeTo != volume) {
			volume += (Math.signum(fadeTo-volume)*this.fadeFactor*EZ.getDeltaTime()/1000);
			if(volume<0) { volume = 0; }else if(volume>1) { volume = 1f; }
			setVolume(volume);
		}
		
		this.lastTime = currentTime;
	}
	public void seek( double time ) { this.mediaPlayer.seek(new Duration(time)); }
	public void play(){ this.mediaPlayer.play(); }
	public void stop() { this.mediaPlayer.stop(); }
	public void pause() { this.mediaPlayer.pause(); }
	public boolean isPlaying() { return this.isPlaying; }
	
	
	public void fadeTo( double value , double factor) { this.fadeTo = value; this.fadeFactor = factor; }
	public void setVolume( double value ) { this.mediaPlayer.setVolume(value); }
	public void setLoops(boolean loops) { this.looping = loops; }
	
	public double getVolume() { return this.mediaPlayer.getVolume(); }
	public double getCurrentTime() { return this.mediaPlayer.getCurrentTime().toSeconds(); }
	
	public void dispose() { this.mediaPlayer.dispose(); }
	 
}

class WAVSound {
	
	private EZSound sound;
	private FloatControl gainControl;
	/**
	 * Creates a .wav sound object with volume and seek controls.
	 * @param file 
	 */
	public WAVSound( String file ) {
		this.sound = new EZSound(file);
		this.gainControl = (FloatControl)this.sound.sound.getControl(FloatControl.Type.MASTER_GAIN);
	}
	/**
	 * 
	 * @param time - position of sound in seconds
	 */
	public void seek(double time) { this.sound.setMicrosecondPosition((int)(time*1000000)); }
	/**
	 * Play the sound.
	 */
	public void play() { this.sound.play(); }
	public boolean isPlaying() { return this.sound.isPlaying(); }
	/**
	 * Stop the sound.
	 */
	public void stop() { this.sound.stop(); }
	/**
	 * Play the sound in a loop.
	 */
	public void loop() { this.sound.loop(); }
	/**
	 * Pause the sound. Remembers the last position when paused.
	 */
	public void pause() { this.sound.pause(); }
	/**
	 * set the gain of the sound
	 * @param value 
	 */
	public void setGain(float value) { this.gainControl.setValue(value); }
	public float getGain() { return this.gainControl.getValue(); }
	/**
	 * Disposes the sound when done. Do not use sound after calling.
	 */
	public void dispose() { this.sound.sound.close(); }
	
}

/*
 * This class handles user input from the keyboard
 */
class Keyboard extends KeyAdapter {
	public static boolean[] Keys = new boolean[256];
	public static boolean[] KeysJustPressed = new boolean[256];
	public static boolean[] KeysJustReleased = new boolean[256];
	
	public static boolean[] KeysJustPressedBuffer = new boolean[256];
	public static boolean[] KeysJustReleasedBuffer = new boolean[256];
	
	public static Keyboard keyboard;
	
	public Keyboard() {}
	
	/**
	 * Initialize keyboard
	 */
	public static void init() {
		keyboard = new Keyboard();
	}
	
	/**
	 * clear keys
	 */
	public static  void update() {
		for(int i = 0; i<256; i++) {
			KeysJustPressedBuffer[i] = KeysJustPressed[i];
			KeysJustPressed[i] = false;
			KeysJustReleasedBuffer[i] = KeysJustReleased[i];
			KeysJustReleased[i] = false;
		}
	}
	
	@Override
	public synchronized void keyPressed(KeyEvent e) {
    	if(e.getKeyCode()>=0 && e.getKeyCode()<256) {
    		if(!Keys[e.getKeyCode()]) {
    			KeysJustPressed[e.getKeyCode()] = true;
    		}
    		Keys[e.getKeyCode()] = true;
    		//System.out.print("Key Pressed: "); System.out.println(e.getKeyCode());
    	}
    }
	
	@Override
	public synchronized void keyReleased(KeyEvent e) {
    	if(e.getKeyCode()>=0 && e.getKeyCode()<256) {
    		if(Keys[e.getKeyCode()]) {
    			KeysJustReleased[e.getKeyCode()] = true;
    		}
    		Keys[e.getKeyCode()] = false;
    		//System.out.print("Key Released: "); System.out.println(e.getKeyCode());
    	}
    }
	/**
	 * Prints the Keys being pressed.
	 */
	public static void printInput() {
		boolean p = false;
		ArrayList<Integer> k = new ArrayList<Integer>();
		for(int i=0; i<Keys.length; i++) {
			if(Keys[i]) { k.add(i); p = true;}
		}
		if(p){
			System.out.print("Pressed Key #: ");
			for(int i=0; i<k.size(); i++) { System.out.print(k.get(i)+", "); }
			System.out.println("");
		}
	}
}


/*
 * Handles mouse user input
 */
class Mouse implements MouseListener, MouseMotionListener{
	public static Mouse mouse;
	public static boolean[] Buttons = new boolean[5];
	
	public static boolean[] ButtonsJustPressed = new boolean[5];
	public static boolean[] ButtonsJustReleased = new boolean[5];
	
	public static boolean[] ButtonsJustPressedBuffer = new boolean[5];
	public static boolean[] ButtonsJustReleasedBuffer = new boolean[5];
	
	public static Vector tempV1 = new Vector(0,0);
	public static Vector tempV2 = new Vector(0,0);
	
	public static Vector pos = new Vector(0,0);
	public static Vector worldPos = new Vector(0,0);
	private static boolean mouse_entered = false;
	
	private static EZGroup group;
	public static Collider collider;
	
	/**
	 * Initialize mouse
	 */
	public static void init() {
		mouse = new Mouse();
	}
	/**
	 * Initialize mouse
	 */
	private Mouse() {
		group = EZ.addGroup();
		collider = new BoxCollider(group, 2, 2, 0, 100);
		collider.addToCollisionGroups(101);
	}
	/**
	 * 
	 * @return - true if mouse is in window
	 */
	public static boolean isMouseEntered() {
		return mouse_entered;
	}
	/**
	 * 
	 * @return - mouse position in window
	 */
	public static Vector getMousePose() {
		return pos.copy();
	}
	/**
	 * clear buttons and update buffers
	 */
	public static void update() {
		for(int i = 0; i<5; i++) {
			ButtonsJustPressedBuffer[i] = ButtonsJustPressed[i];
			ButtonsJustPressed[i] = false;
			ButtonsJustReleasedBuffer[i] = ButtonsJustReleased[i];
			ButtonsJustReleased[i] = false;
		}
	}
	/**
	 * Prints the buttons pressed.
	 */
	public static void printInput() {
		boolean p = false;
		ArrayList<Integer> k = new ArrayList<Integer>();
		for(int i=0; i<Buttons.length; i++) {
			if(Buttons[i]) { k.add(i); p = true;}
		}
		if(p){
			System.out.print("Pressed Mouse Button #: ");
			for(int i=0; i<k.size(); i++) { System.out.print(k.get(i)+", "); }
			System.out.println("");
		}
	}
	
	@Override
	public synchronized void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void mouseEntered(MouseEvent e) {
		this.mouse_entered = true;
		
	}

	@Override
	public synchronized void mouseExited(MouseEvent e) {
		this.mouse_entered = false;
		
	}

	@Override
	public synchronized void mousePressed(MouseEvent e) {
		if(e.getButton()>=0 && e.getButton()<5) {
    		if(!Buttons[e.getButton()]) {
    			ButtonsJustPressed[e.getButton()] = true;
    		}
    		Buttons[e.getButton()] = true;
    		//System.out.print("Button Pressed: ");
    		//System.out.println(e.getButton());
    	}
	}

	@Override
	public synchronized void mouseReleased(MouseEvent e) {
		if(e.getButton()>=0 && e.getButton()<5) {
    		if(Buttons[e.getButton()]) {
    			ButtonsJustReleased[e.getButton()] = true;
    		}
    		Buttons[e.getButton()] = false;
    		//System.out.print("Button Released: ");
    		//System.out.println(e.getButton());
    	}
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		pos.set( e.getX(), e.getY() );
		group.translateTo(pos.x, pos.y);
		
		tempV1.set( pos.x, pos.y );
		tempV2.set( GE.worldGroup.getXCenter(), GE.worldGroup.getYCenter() );
		
		tempV2.rotate(-GE.worldGroup.getRotation()*Math.PI/180);
		tempV1.rotate(GE.cameraFocus.getRotation()*Math.PI/180);
		
		worldPos.set( tempV1.x-tempV2.x, tempV1.y-tempV2.y );
		worldPos.multiply(1/GE.worldGroup.getScale());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		pos.set( e.getX(), e.getY() );
		group.translateTo(pos.x, pos.y);
		
		tempV1.set( pos.x, pos.y );
		tempV2.set( GE.worldGroup.getXCenter(), GE.worldGroup.getYCenter() );
		
		tempV2.rotate(-GE.worldGroup.getRotation()*Math.PI/180);
		if( GE.cameraFocus != null ) tempV1.rotate(GE.cameraFocus.getRotation()*Math.PI/180);
		
		worldPos.set( tempV1.x-tempV2.x, tempV1.y-tempV2.y );
		worldPos.multiply(1/GE.worldGroup.getScale());
	}
	
}

class Vector implements java.io.Serializable{
	private static final long serialVersionUID = 1667134325855308942L;
	Vector me;
	double x;
	double y;
	
	/**
	 * Create a new vector
	 * @param vx - x component
	 * @param vy - y component
	 */
	public Vector(double vx, double vy) {
		this.x = vx; this.y = vy;
		this.me = this;
	}
	
	/**
	 * Set the vector components
	 * @param x - set x component value
	 * @param y - set y component value
	 */
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * 
	 * @return - a copy of this vector
	 */
	public Vector copy() {
		return new Vector(this.x,this.y);
	}
	
	/**
	 * Copies the input vector
	 * @param v - vector to copy
	 */
	public void copy(Vector v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	/**
	 * this cross v
	 * @param v
	 * @return - cross product
	 */
	public double cross(Vector v) {
		return (this.x*v.y)-(this.y*v.x);
	}
	
	/**
	 * this cross v
	 * @param vx - x component of v
	 * @param vy - y component of v
	 * @return - cross product
	 */
	public double cross(double vx, double vy) {
		return (this.x*vy)-(this.y*vx);
	}
	
	/**
	 * this cross a scalar
	 * @param s - scalar to cross
	 * @return - the vector scales by s, and rotated -90 degrees
	 */
	public Vector crossVS(double s) {
		return new Vector( s*this.y, -s*this.x);
	}
	
	/**
	 * s scalar cross this
	 * @param s - scalar to cross
	 * @return - the vector scales by s, and rotated +90 degrees
	 */
	public Vector crossSV(double s) {
		return new Vector( -s*this.y, s*this.x);
	}
	
	/**
	 * Dot product with v
	 * @param v - vector to dot with
	 * @return - dot product
	 */
	public double dot(Vector v) {
		return (this.x*v.x) + (this.y*v.y);
	}
	
	/**
	 * Dot product with v
	 * @param vx - x component of v
	 * @param vy - y component of v
	 * @return - dot product
	 */
	public double dot(double vx, double vy) {
		return (this.x*vx) + (this.y*vy);
	}
	
	/**
	 * Scale vector by s
	 * @param s
	 */
	public void multiply(double s) {
		this.x *= s;
		this.y *= s;
	}
	
	/**
	 * add this with v
	 * @param v
	 */
	public void add(Vector v) {
		this.x += v.x;
		this.y += v.y;
	}
	
	/**
	 * adds vx and vy to the x and y components of this respectively
	 * @param vx
	 * @param vy
	 */
	public void add(double vx, double vy) {
		this.x += vx;
		this.y += vy;
	}
	
	/**
	 * subtracts v from this
	 * @param v
	 */
	public void subtract(Vector v) {
		this.x -= v.x;
		this.y -= v.y;
	}
	
	/**
	 * subtracts vx and vy from the x and y components of this respectively
	 * @param vx
	 * @param vy
	 */
	public void subtract(double vx, double vy) {
		this.x -= vx;
		this.y -= vy;
	}
	
	/**
	 * 
	 * @return - the length of this
	 */
	public double length() {
		return Math.pow( Math.pow(this.x,2)+Math.pow(this.y,2) ,0.5);
	}
	
	/**
	 * 
	 * @return - a unit vector in the direction of this
	 */
	public Vector getUnitVector() {
		double m = this.length();
		return new Vector(this.x/m, this.y/m);
	}
	
	/**
	 * Set the length of this vector to 1.
	 */
	public void normalize() {
		double m = this.length();
		this.x /= m; this.y /= m;
		if(Double.isNaN(this.x)) this.x = 0;
		if(Double.isNaN(this.y)) this.y = 0;
	}
	
	/**
	 * Rotate this by theta
	 * @param theta - in radians
	 */
	public void rotate(double theta) {
		
		double x,y;
		x = (this.x*Math.cos(theta))-(this.y*Math.sin(theta));
		y = (this.x*Math.sin(theta))+(this.y*Math.cos(theta));
		this.x = x;
		this.y = y;
	}
	
	/**
	 * get the angle between this and v
	 * @param v
	 * @return - angle in radians
	 */
	public double angleBetween(Vector v) {
		double m = this.dot(v)/(this.length()*v.length());
		m=Math.acos(m);
		if(Double.isNaN(m) || m == 0.0) {
			if(this.cross(v)<0) {
				m=2*Math.PI;
			}else {
				m=0;
			}
		}
		return m;
	}
	
	/**
	 * Get the component of this on v
	 * @param v
	 * @return - the length of this in the direction of v
	 */
	public double compOn(Vector v) {
		return -v.dot(this)/v.length();
	}
	
	/**
	 * get the projection of this on v
	 * @param v
	 * @return - the vector projection of this on v
	 */
	public Vector projOnto(Vector v) {
		Vector r = v.getUnitVector();
		r.multiply(compOn(v)*-1);
		return r;
	}
	
}
