
/**
 * @author Damien Apilando
 * e-mail: damienakap@gmail.com
 * 
 * ICS111
 * lecturer Jason Leigh
 * 
 * Note:
 * 	This class defines the NPC racers.
 * 
 */

import java.awt.Color;

public class AICar extends Car{
	
	public EZText nameTag;
	
	private int timer = 0;
	
	public AICar(String filename, int width, int height) {
		super(filename, width, height);
		
		this.image.scaleTo(6);
		this.moveSpeed = 22 + 5.4*2*(GameManager.rand.nextDouble()-0.5);
		this.baseSpeed = this.moveSpeed;
		this.acceloration = 20.5 + 5.5*2*(GameManager.rand.nextDouble()-0.5);
		this.turnSpeed = 3.0 + 1*2*(GameManager.rand.nextDouble()-0.5);
		this.baseTurnSpeed = this.turnSpeed;
		this.hit_box.setMass(5);
		this.hit_box.elasticity = 0.9;
		this.translateTo(0, 0);
	}
	
	public void update( Player player ) {
		
		this.moving = false;
		
		if(this.nameTag != null) {
			this.nameTag.translateTo(this.image.getWorldXCenter(), this.image.getWorldYCenter()-GE.worldGroup.getScale()*100);
		}
		
		
		switch(this.state) {
			case 1:
				updateControl();
				break;
			case 2:
				if(!this.stateInit) {
					this.timer = 5000;
					this.stateInit = true;
				}
				this.timer -= EZ.getDeltaTime();
				if(this.timer <=0 ) {
					this.timer = 0;
					this.group.translateTo(this.lastNode.graphic.getXCenter(), this.lastNode.graphic.getYCenter());
					this.group.rotateTo(this.lastNode.VisibleNodeDistance[0]);
					this.health = 100;
					setState(1);
				}
				break;
			default:
				break;
		}
		
		Vector dr = player.getPosition();
		dr.subtract( this.group.getXCenter(), this.group.getYCenter() );
		double r = dr.length();
		if(r > 6000) { r = 6000;}
		r /= 6000;
		
		this.idleSound.setGain( this.idleSoundMaxGain - (float)(r*(30+this.idleSoundMaxGain)) );
		this.moveSound.setGain( this.moveSoundMaxGain - (float)(r*(30+this.moveSoundMaxGain)) );
		this.crashSound.setGain( this.crashSoundMaxGain - (float)(r*(30+this.crashSoundMaxGain)) );
		
		if(this.moving) {
			if(this.idleSound.isPlaying()) { this.idleSound.stop(); }
			if(!this.moveSound.isPlaying()) { this.moveSound.loop(); }
		}else {
			if(this.moveSound.isPlaying()) { this.moveSound.stop(); }
			if(!this.idleSound.isPlaying()) { this.idleSound.loop(); }
		}
		
	}
	
	private void updateControl() {
		
		if(this.health<=0) {
			this.health=0;
			setState(2);
		}
		
		double thetaP = this.group.getRotation()*Math.PI/180;
		
		// rotate velocity to car forwards direction 
		double s = this.getVelocity().length();
		Vector dir = new Vector(0,1);
		dir.rotate(thetaP);
		dir.multiply(s);
		s = Math.signum( this.getVelocity().dot(dir));
		dir.multiply(s);
		
		if(this.hit_box.hitObjectList.size()<=2) {
			this.setVelocity(dir.x, dir.y);
			this.turnSpeed = this.baseTurnSpeed;
			this.moveSpeed = this.baseSpeed;
		}else {
			this.turnSpeed = 0.5*this.baseTurnSpeed;
			this.moveSpeed = 0.9*this.baseSpeed;
			this.crashSound.play();
		}
		
		if(this.nextNode != null) {
			Vector node_pos = this.nextNode.getPosition();
			Vector pos = this.getPosition();
			
			Vector node_dir = new Vector( node_pos.x-pos.x, node_pos.y-pos.y );
			dir = new Vector( 0, -1 );
			dir.rotate( this.group.getRotation() * Math.PI/180);
			
			double node_dist = Math.pow( node_dir.x*node_dir.x + node_dir.y*node_dir.y , 0.5);
			
			if( node_dist < 400 ) {
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
			
			double angleToNode = dir.angleBetween(node_dir);
			
			double kk = 0.8;
			
			if( angleToNode > kk*Math.PI/2) { angleToNode = kk*Math.PI/2; }
			if(dir.cross(node_dir)< 0 ) { angleToNode *= -1; }
			
			double turnRate = this.turnSpeed * Math.sin((1/kk)*angleToNode)* Math.abs(Math.sin((1/kk)*angleToNode));
			
			double move = this.acceloration*Math.cos((1/kk)*angleToNode);
			//if(move < this.moveSpeed/4) { move = this.moveSpeed/4; }
			dir.multiply(move*(double)EZ.getDeltaTime()/1000.0d);
			
			this.setAngularVelocity(turnRate);
			this.addToVelocity(dir.x, dir.y);
			if(this.getVelocity().length()>this.moveSpeed) {
				dir = this.getVelocity();
				dir.normalize();
				dir.multiply(this.moveSpeed);
				this.setVelocity(dir.x, dir.y);
			}
			
			this.moving = true;
			
		}else {
			this.currentLapTime = System.currentTimeMillis();
			this.nextNode = MapLoader.navigationNodes.get(0);
			this.lastNode = MapLoader.navigationNodes.get(MapLoader.navigationNodes.size()-1);
		}
		
		if(this.getVelocity().length()>2) { 
			this.smokeEmmiter.active = true; 
		}else { 
			this.smokeEmmiter.active = false;
		}
		
	}
	
	public void setNameTag() {
		this.nameTag = EZ.addText(this.image.getWorldXCenter(), this.image.getWorldYCenter()-(int)(GE.worldGroup.getScale()*100), this.name);
		//this.nameTag = EZ.addText(0, 0, this.name);
		this.nameTag.setFont("fonts/Raleway-Black.ttf");
		this.nameTag.setFontSize(15);
		this.nameTag.setColor(Color.ORANGE);
	}
	
	public void dispose() {
		
		GE.removeEmitter(this.smokeEmmiter);
		this.hit_box.dispose();
		
		GameManager.disposeEZImage(this.image);
		this.nextNode = null;
		this.lastNode = null;
		
		if(this.nameTag.hasParent()) { this.nameTag.getParent().removeElement(this.nameTag); }
		EZ.removeEZElement(this.nameTag);
		
		GameManager.disposeEZGroup(this.group);
	}
	
}
