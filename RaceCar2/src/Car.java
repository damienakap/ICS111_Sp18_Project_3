
/**
 * Copyright (c) 04/04/2018
 * @author Damien Apilando
 * e-mail: damienakap@gmail.com
 * 
 * ICS111
 * lecturer Jason Leigh
 * 
 * Note:
 * 	This is a super class for all cars.
 * 
 */

import java.awt.Color;

public class Car extends Entity{
	
	public boolean stateInit = false;
	public int state = 0;
	public int stateLast = 0;
	
	public BasicParticleEmitter smokeEmmiter;
	public String name;
	public int place;
	public int bestLapTime = -1;
	public long currentLapTime = 0;
	public int trackProgress = 0;
	public double progressToNextNode = 0;
	
	public double turnSpeed;
	public EZImage image;
	public double acceloration;
	public double baseSpeed;
	protected double baseTurnSpeed;
	protected NavNode nextNode, lastNode;
	
	protected WAVSound idleSound, moveSound, crashSound;
	protected int idleSoundMaxGain, moveSoundMaxGain, crashSoundMaxGain;
	
	protected boolean moving = false;
	
	public Car( String filename, int width, int height) {
		this.group = EZ.addGroup();
		
		this.idleSound = GE.addWAVSound("sounds/engine.wav");
		this.moveSound = GE.addWAVSound("sounds/engine_run.wav");
		this.crashSound = GE.addWAVSound("sounds/car_hit.wav");
		
		this.crashSound.stop();
		this.idleSound.stop();
		this.moveSound.stop();
		
		this.idleSoundMaxGain = 4;
		this.moveSoundMaxGain = -3;
		this.crashSoundMaxGain = 2;
		
		this.idleSound.setGain(4);
		this.moveSound.setGain(-3);
		this.crashSound.setGain(2);
		
		
		this.health = 100;
		
		this.smokeEmmiter = GE.addEmitter(this.group, 10, 40, 40, new Color( 100,100,100, 60 ), 10, 10, 400 );
		this.smokeEmmiter.active = false;
		this.smokeEmmiter.placeBelow(true);
		
		// create hitbox
		this.hit_box = GE.addBoxCollider(this.group, width, height, 3, 3);
		this.hit_box.addToCollisionGroups(10);
		this.hit_box.addToCollisionGroups(3);
		this.hit_box.elasticity = 0.90;
		
		this.hit_box.parentEntity = this;
		
		// create image
		if(filename!= null) {
			this.image = EZ.addImage(filename, 0, 0);
			this.group.addElement(this.image);
		}
		
		// put car in game world
		GE.worldGroup.addElement(this.group);
		
		this.moveSpeed = 3;
		this.baseSpeed = 3;
		this.turnSpeed = 1;
		this.acceloration = 0.1;
		
	}
	
	public void setState(int s) {
		this.stateLast = state;
		this.state = s;
		this.stateInit = false;
	}
	
	public CarData getCarData() {
		return new CarData(this.name, this.place, this.bestLapTime);
	}
	
	public int getTrackProgress() { return this.trackProgress; }
	
	public void setSpeed( double s ) { this.moveSpeed = s;}
	public void setBaseSpeed( double s ) { this.baseSpeed = s; }
	public void resetSpeed() { this.moveSpeed = this.baseSpeed; }
	
}
