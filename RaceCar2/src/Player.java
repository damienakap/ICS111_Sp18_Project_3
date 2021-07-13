
/**
 * @author Damien Apilando
 * e-mail: damienakap@gmail.com
 * 
 * ICS111
 * lecturer Jason Leigh
 * 
 * Note:
 * 	This is the player class and handles user input to control the players car.
 * This also handles the user hud.
 * 
 */

import java.awt.Color;

public class Player extends Car{
	
	private EZText placeText, lapText, worldPosText, raceEndText, raceEndTimerText, raceStartText, raceStartTimerText;
	private EZText pauseText;
	private EZGroup hud;
	private EZRectangle pauseBackground;
	
	private EZImage headLight1, headLight2;
	private EZCircle waypoint1, waypoint2;
	private double baseAcceloration;
	private boolean headLightsOn = false;
	private double CollidedTime = 0;
	
	private Missile1 missile;
	
	public Player(String filename, int width, int height) {
		super(filename, width, height);
		this.group.placeBelow(GameManager.screenFilter);
		
		this.headLight1 = EZ.addImage("img/cone_Light.png", 0, 0);
		this.headLight1.scaleTo(6);
		this.headLight1.rotateTo(-90);
		this.headLight1.translateTo(
				this.image.getScale()*this.image.getWidth()*2,
				-this.image.getScale()*this.image.getHeight()*2
				-this.headLight1.getScale()*this.headLight1.getHeight()*2
				);
		
		this.headLight2 = EZ.addImage("img/cone_Light.png", -60, -150-600);
		this.headLight2.scaleTo(6);
		this.headLight2.rotateTo(-90);
		this.headLight2.translateTo(
				-this.image.getScale()*this.image.getWidth()*2 ,
				-this.image.getScale()*this.image.getHeight()*2
				-this.headLight2.getScale()*this.headLight2.getHeight()*2
				);
		
		this.group.addElement(this.headLight1);
		this.group.addElement(this.headLight2);
		
		this.headLight1.hide();
		this.headLight2.hide();
		
		this.image.scaleTo(6);
		this.moveSpeed = 22;
		this.baseSpeed = this.moveSpeed;
		this.acceloration = 20;
		this.baseAcceloration = this.acceloration;
		this.turnSpeed = 1.5;
		this.baseTurnSpeed = this.turnSpeed;
		this.hit_box.setMass(8);
		this.hit_box.elasticity = 0.8;
		this.translateTo(0, 0);
		
		this.missile = new Missile1(0, 1000, 0, 2, null,0,5);
		MapLoader.objects.add( this.missile );
		
		
		//focus the screen on player
		//GE.setScreenFocus(this.group.getXCenter(), this.group.getYCenter(), this.group.getRotation(), 0, 200);
		GE.setScreenFocusObject(this.group, 0, 200);
		
	}
	
	public void initHud() {
		
		this.hud = EZ.addGroup();
		
		this.worldPosText = EZ.addText(0, 0, "");
		this.worldPosText.setFont("fonts/Raleway-Black.ttf");
		this.worldPosText.setFontSize(30);
		this.worldPosText.setColor(Color.white);
		this.worldPosText.hide();
		
		this.waypoint1 = EZ.addCircle(0, 0, 100, 100, new Color( 255,255,100,70 ), true);
		this.waypoint2 = EZ.addCircle(0, 0, 90, 90, new Color( 255,255,100,70 ), true);
		
		this.placeText = EZ.addText(EZ.getWindowWidth()-100, EZ.getWindowHeight()-100, "");
		this.placeText.setFont("fonts/Raleway-Black.ttf");
		this.placeText.setFontSize(60);
		this.placeText.setColor(Color.white);
		
		this.lapText = EZ.addText(EZ.getWindowWidth()-100, EZ.getWindowHeight()-150, "Lap: 1");
		this.lapText.setFont("fonts/Raleway-Black.ttf");
		this.lapText.setFontSize(30);
		this.lapText.setColor(Color.red);
		
		this.raceEndText = EZ.addText(EZ.getWindowWidth()/2, 60, "");
		this.raceEndText.setFont("fonts/Raleway-Black.ttf");
		this.raceEndText.setFontSize(30);
		this.raceEndText.setColor(Color.red);
		
		this.raceEndTimerText = EZ.addText(EZ.getWindowWidth()/2, 100, "");
		this.raceEndTimerText.setFont("fonts/Raleway-Black.ttf");
		this.raceEndTimerText.setFontSize(60);
		this.raceEndTimerText.setColor(Color.white);
		
		this.raceStartText = EZ.addText(EZ.getWindowWidth()/2, 60, "");
		this.raceStartText.setFont("fonts/Raleway-Black.ttf");
		this.raceStartText.setFontSize(30);
		this.raceStartText.setColor(Color.red);
		
		this.raceStartTimerText = EZ.addText(EZ.getWindowWidth()/2, 100, "");
		this.raceStartTimerText.setFont("fonts/Raleway-Black.ttf");
		this.raceStartTimerText.setFontSize(60);
		this.raceStartTimerText.setColor(Color.white);
		
		this.pauseBackground = EZ.addRectangle(
				(int)(EZ.getWindowWidth()/2), (int)(EZ.getWindowHeight()/2), 
				(int)(EZ.getWindowWidth()*1.1), (int)(EZ.getWindowHeight()*1.1), 
				new Color(0,0,0,150), true
			);
		this.pauseBackground.hide();
		
		this.pauseText = EZ.addText(EZ.getWindowWidth()/2, 200, "Paused");
		this.pauseText.setFont("fonts/Raleway-Black.ttf");
		this.pauseText.setFontSize(60);
		this.pauseText.setColor(Color.white);
		this.pauseText.hide();
		
		this.hud.addElement(this.waypoint1);
		this.hud.addElement(this.waypoint2);
		this.hud.addElement(this.placeText);
		this.hud.addElement(this.lapText);
		this.hud.addElement(this.raceEndTimerText);
		this.hud.addElement(this.raceEndText);
		this.hud.addElement(this.raceStartTimerText);
		this.hud.addElement(this.raceStartText);
		this.hud.addElement(this.pauseBackground);
		this.hud.addElement(this.pauseText);
		this.hud.placeBelow(GameManager.screenFilter);
		
	}
	
	public void update(){
		
		this.moving = false;
		
		//update player hud
		updateHud();
		
		switch(state) {
			case 1:
				if(!stateInit) {
					if(GE.game_state == GE.game_state_change_to) {
						GameManager.screenFilterFadeFactor = 600;
						GameManager.screenFilterMode = 1;
					}
				}
				updateControl();
				break;
			case 2:
				respawn();
				updateControl();
			default:
				break;
		}
		
		if(this.moving) {
			if(this.idleSound.isPlaying()) { this.idleSound.stop(); }
			if(!this.moveSound.isPlaying()) { this.moveSound.loop(); }
		}else {
			if(this.moveSound.isPlaying()) { this.moveSound.stop(); }
			if(!this.idleSound.isPlaying()) { this.idleSound.loop(); }
		}
		
	}
	
	private void respawn() {
		if(!stateInit) {
			GameManager.setScreenFilterMode(2,500);
			this.health = 100;
			stateInit = true;
		}
		
		if( GameManager.screenFilterMode == 0 ) {
			this.health = 100;
			this.group.translateTo(this.lastNode.graphic.getXCenter(), this.lastNode.graphic.getYCenter());
			this.setVelocity(0, 0);
			this.group.rotateTo(this.lastNode.VisibleNodeDistance[0]);
			setState(this.stateLast);
		}
	}
	
	private void updateHud() {
		
		if(GameManager.paused) {
			this.pauseBackground.show();
			this.pauseText.show();
		}else {
			this.pauseBackground.hide();
			this.pauseText.hide();
		}
		
		this.worldPosText.translateTo(Mouse.pos.x, Mouse.pos.y);
		this.worldPosText.setMsg( (int)Mouse.worldPos.x + " : " + (int)Mouse.worldPos.y );
		
		if(GameManager.raceFinished) {
			this.raceEndText.setMsg("Race Finished");
			this.raceEndTimerText.setMsg( ((int)GameManager.raceFinishedTimer/60000) + ":" + ((int)GameManager.raceFinishedTimer%60000/1000));
		}else {
			this.lapText.setMsg("Lap: "+(1+(this.trackProgress-1)/61) );
		}
		
		switch(this.place) {
			case 1: this.placeText.setMsg("1st"); break;
			case 2: this.placeText.setMsg("2nd"); break;
			case 3: this.placeText.setMsg("3rd"); break;
			default: this.placeText.setMsg(this.place+"th"); break;
		}
		
		if(GameManager.raceTime < 3000) {
			this.raceStartText.show();
			this.raceStartTimerText.show();
			this.raceStartTimerText.setMsg( "" + String.format( "%.2f",(float)GameManager.raceStartTimer/1000.0f) );
			if(GameManager.raceTime <= 0) {
				this.raceStartText.setMsg("Ready");
			}else {
				this.raceStartText.setMsg("Go");
			}
		}else {
			this.raceStartText.hide();
			this.raceStartTimerText.hide();
		}
		
		// update waypoint position
		updateWaypoint();
	}
	
	private void updateWaypoint() {
		if(this.nextNode != null) {
			
			this.waypoint1.show();
			this.waypoint2.show();
			Vector node_pos = this.nextNode.getWorldPosition();
			Vector pos = new Vector( this.image.getWorldXCenter(), this.image.getWorldYCenter() );//this.getWorldPosition();
			
			this.waypoint1.translateTo(node_pos.x, node_pos.y);
			if( this.waypoint1.getWorldXCenter() < 0 ) { this.waypoint1.translateTo( 0, this.waypoint1.getWorldYCenter() ); }
			if( this.waypoint1.getWorldYCenter() < 0 ) { this.waypoint1.translateTo( this.waypoint1.getWorldXCenter(), 0 ); }
			if( this.waypoint1.getWorldXCenter() > EZ.getWindowWidth() ) { this.waypoint1.translateTo( EZ.getWindowWidth(), this.waypoint1.getWorldYCenter() ); }
			if( this.waypoint1.getWorldYCenter() > EZ.getWindowHeight() ) { this.waypoint1.translateTo( this.waypoint1.getWorldXCenter(), EZ.getWindowHeight() ); }
			
			this.waypoint2.translateTo(this.waypoint1.getXCenter(), this.waypoint1.getYCenter());
			
			double node_dist = Math.pow( (node_pos.x-pos.x)*(node_pos.x-pos.x) + (node_pos.y-pos.y)*(node_pos.y-pos.y) , 0.5);
			//System.out.println(this.bestLapTime);
			//this.currentLapTime += EZ.getDeltaTime();
			if( node_dist < 600*GE.worldGroup.getScale() ) {
				this.waypoint1.hide();
				this.waypoint2.hide();
				if(this.nextNode.visibleNodeInds.get(0)==1) {
					if(this.bestLapTime<0 || this.bestLapTime>this.currentLapTime) {
						this.bestLapTime = (int)(System.currentTimeMillis()-this.currentLapTime);
					}
					this.currentLapTime = System.currentTimeMillis();
				}
				this.lastNode = this.nextNode;
				this.nextNode = MapLoader.navigationNodes.get( this.nextNode.visibleNodeInds.get(0) );
				this.trackProgress += 1;
				return;
			}
			
		}else {
			this.waypoint1.hide();
			this.waypoint2.hide();
			this.nextNode = MapLoader.navigationNodes.get(0);
			this.lastNode = MapLoader.navigationNodes.get(MapLoader.navigationNodes.size()-1);
			this.currentLapTime = System.currentTimeMillis();
		}
	}
	
	private void updateControl() {
		double thetaP = this.group.getRotation()*Math.PI/180;
		
		// get input
		if(Keyboard.KeysJustPressedBuffer[82] || this.health <= 0) {
			setState(2);
		}
		
		if( Keyboard.KeysJustPressedBuffer[69] && this.missile.state == 0) {
			int ii = GameManager.cars.indexOf(this);
			if( ii>0 ) {
				this.missile.target = GameManager.cars.get(ii-1).group;
			}else {
				this.missile.target = GameManager.cars.get(GameManager.cars.size()-1).group;
				this.missile.hitBox.velocity.copy( this.hit_box.velocity );
				this.missile.hitBox.velocity.multiply(3);
			}
			
			Vector vr = new Vector(0, -300-this.image.getScale()*this.image.getHeight()/2 - this.missile.image.getScale()*this.missile.image.getHeight()/2 );
			vr.rotate(this.group.getRotation()*Math.PI/180);
			this.missile.group.translateTo(this.group.getXCenter()+vr.x, this.group.getYCenter()+vr.y);
			this.missile.setState(1);
		}
		
		Vector v = new Vector(0,0);
		double w = 0;

		Keyboard.printInput();
		
		if(Keyboard.KeysJustPressedBuffer[70]) {
			if(this.headLightsOn) { this.headLightsOn = false;
			}else { this.headLightsOn = true; }
		}
		
		if(this.headLightsOn) {
			this.headLight1.show();
			this.headLight2.show();
		}else {
			this.headLight1.hide();
			this.headLight2.hide();
		}
		
		if(Keyboard.KeysJustPressedBuffer[45] || Keyboard.KeysJustPressedBuffer[109]) {
			double ss = GE.worldGroup.getScale()-0.1;
			if(ss<0.1) {ss=0.1;}
			GE.worldGroup.scaleTo(ss);
		}
		if(Keyboard.KeysJustPressedBuffer[61] || Keyboard.KeysJustPressedBuffer[107]) {
			double ss = GE.worldGroup.getScale()+0.1;
			if(ss>2) {ss=2;}
			GE.worldGroup.scaleTo(ss);
		}
		if(Keyboard.Keys[37] || Keyboard.Keys[65]) {		//	left/A
			w += -1;
		}
		if(Keyboard.Keys[39] || Keyboard.Keys[68]){ 		//	right/D
			w += 1;
		}
		if(Keyboard.Keys[38] || Keyboard.Keys[87]) { 		//	up/W
			v.y += -1;
		}
		if(Keyboard.Keys[40] || Keyboard.Keys[83]) { 		//	down/S
			v.y += 1;
		}
		
		// rotate velocity to car forwards direction 
		double s = this.getVelocity().length();
		Vector dir = new Vector(0,1);
		dir.rotate(thetaP);
		dir.multiply(s);
		s = Math.signum( this.getVelocity().dot(dir));
		dir.multiply(s);
		
		double theta = dir.angleBetween(this.getVelocity())/2;
		
		this.CollidedTime -= EZ.getDeltaTime();
		if( this.CollidedTime<0 ) { this.CollidedTime = 0; }
		if( this.hit_box.hitObjectList.size()>2) { 
			this.crashSound.play();
			this.CollidedTime = 100;
		}
		
		if( this.CollidedTime>0 ) { this.moveSpeed = this.baseSpeed*0.9; 
		}else { this.moveSpeed = this.baseSpeed; }
		
		if( !Keyboard.Keys[16] && this.CollidedTime == 0) {								//	shift
			dir.multiply(Math.abs(Math.cos(theta)));
			this.setVelocity(dir.x, dir.y);
			this.turnSpeed = this.baseTurnSpeed;
			this.acceloration = this.baseAcceloration;
			
			this.hud.translateTo(0,0);
			
		}else {
			this.turnSpeed = 1.5*this.baseTurnSpeed;
			this.acceloration = this.baseAcceloration*( 1+2*Math.abs(Math.sin(theta)) );
			double shake_amp = 10;
			this.hud.translateTo(shake_amp*2*(GameManager.rand.nextDouble()-0.5), shake_amp*2*(GameManager.rand.nextDouble()-0.5) );
		}
		
		// set player velocity
		if(v.length()>0) {
			if(!Keyboard.Keys[32]) {
				v.normalize();
				v.rotate(thetaP);
				v.multiply(this.acceloration*EZ.getDeltaTime()/1000.0d);
				this.addToVelocity(v.x,v.y);
				this.moving = true;
			}
			if(this.getVelocity().length() > this.moveSpeed) {
				Vector u = this.getVelocity();
				u.normalize();
				u.multiply(this.moveSpeed);
				this.setVelocity( u.x, u.y);
			}
		}
		
		// apply car breaks
		if(Keyboard.Keys[32]) {
			Vector u = this.getVelocity();
			u.multiply(0.98);
			this.setVelocity(u.x, u.y);
		}
		
		// set player angular velocity
		if(this.getVelocity().length() > 0){
			//w *= -s*this.turnSpeed * this.getVelocity().length()/this.moveSpeed;
			this.setAngularVelocity( -Math.signum(s)*w*this.turnSpeed* this.getVelocity().length()/this.moveSpeed);
		}
		
		if(this.getVelocity().length()>2) { 
			this.smokeEmmiter.active = true; 
		}else { 
			this.smokeEmmiter.active = false;
		}
		
	}
	
	public void dispose() {
		
		this.hit_box.dispose();
		this.missile.dispose();
		MapLoader.objects.remove(this.missile);
		GE.removeEmitter( this.smokeEmmiter );
		
		GameManager.disposeEZImage(this.image);
		GameManager.disposeEZImage(this.headLight1);
		GameManager.disposeEZImage(this.headLight2);
		this.hud.removeElement(this.waypoint1);
		this.hud.removeElement(this.waypoint2);
		this.hud.removeElement(this.lapText);
		this.hud.removeElement(this.placeText);
		this.hud.removeElement(this.raceEndText);
		this.hud.removeElement(this.raceEndTimerText);
		this.hud.removeElement(this.raceStartText);
		this.hud.removeElement(this.raceStartTimerText);
		this.hud.removeElement(this.pauseText);
		this.hud.removeElement(this.pauseBackground);
		EZ.removeEZElement(this.waypoint1);
		EZ.removeEZElement(this.waypoint2);
		EZ.removeEZElement(this.lapText);
		EZ.removeEZElement(this.placeText);
		EZ.removeEZElement(this.raceEndText);
		EZ.removeEZElement(this.raceEndTimerText);
		EZ.removeEZElement(this.raceStartText);
		EZ.removeEZElement(this.raceStartTimerText);
		EZ.removeEZElement(this.worldPosText);
		EZ.removeEZElement(this.pauseText);
		EZ.removeEZElement(this.pauseBackground);
		
		this.nextNode = null;
		GameManager.disposeEZGroup(this.hud);
		GameManager.disposeEZGroup(this.group);
	}
	
}
