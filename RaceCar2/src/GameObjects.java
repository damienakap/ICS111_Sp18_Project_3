
/**
 * @author Damien Apilando
 * e-mail: damienakap@gmail.com
 * 
 * ICS111
 * lecturer Jason Leigh
 * 
 * Note:
 * 	This class file defines most of the non-player entities and game objects the can be places on a map.
 * 
 */

import java.awt.Color;
import java.util.ArrayList;

public class GameObjects {
	
}

abstract class BasicGameObject{
	
	EZGroup group;
	EZImage image;
	Collider hitBox;
	
	public abstract void update();
	
	public abstract void dispose();
	
}

class Missile1 extends BasicGameObject{
	
	boolean active = false;
	int state = 0;
	boolean stateInit = false;
	
	int timer = 0;
	
	EZGroup target;
	private BasicParticleEmitter emitter1,emitter2,emitter3;
	
	double damage = 25;
	double slowHitObjectPercent = 0.8;
	double maxSpeed = 30;
	double acceloration = 5;
	
	public Missile1(double x, double y, double rot, double scale, EZGroup targ, double vx, double vy) {
		this.target = targ;
		
		this.group = EZ.addGroup();
		this.image = EZ.addImage("img/missile_1.png", 0, 0);
		this.image.scaleTo(scale);
		
		this.hitBox = GE.addBoxCollider(this.group, (int)(70*scale), (int)(100*scale), 2, 3);
		this.hitBox.addToCollisionGroups(3);
		this.hitBox.addToCollisionGroups(10);
		this.hitBox.setMass(3);
		this.hitBox.velocity.set(vx, vy);
		
		this.emitter1 = GE.addEmitter(this.group, 10, (int)(20*scale), (int)(20*scale), new Color( 150,100,0, 150 ), 2, 15, 1000);
		this.emitter2 = GE.addEmitter(this.group, 10, (int)(20*scale), (int)(20*scale), new Color( 150,0,0, 150 ), 2, 15, 1000);
		this.emitter3 = GE.addEmitter(this.group, 40, (int)(40*scale), (int)(40*scale), new Color( 150,150,0, 150 ), 20, 5, 200);
		this.emitter1.placeBelow(true);
		this.emitter2.placeBelow(true);
		
		this.group.addElement(this.image);
		this.group.translateTo((int)x, (int)y);
		this.group.rotateTo(rot);
		
		GE.worldGroup.addElement(this.group);
	}
	
	private void activeState() {
		
		if(this.target!=null) {
			Vector t = new Vector( this.target.getXCenter()-this.group.getXCenter(), this.target.getYCenter()-this.group.getYCenter());
			t.normalize();
			t.multiply(this.acceloration);
			this.hitBox.velocity.add( t );
			if(this.hitBox.velocity.length()>this.maxSpeed) {
				this.hitBox.velocity.normalize();
				this.hitBox.velocity.multiply(this.maxSpeed);
			}
			
			this.group.rotateTo( Math.signum(this.hitBox.velocity.x)*this.hitBox.velocity.angleBetween(new Vector(0,-1)) * 180/Math.PI);
			
		}
		
		if(this.hitBox.velocity.length()>2.5) {
			this.emitter1.active = true;
			this.emitter2.active = true;
		}else {
			this.emitter1.active = false;
			this.emitter2.active = false;
		}
		for( Collider c : this.hitBox.hitObjectList ) {
			if(c.parentEntity != null) {
				c.parentEntity.health -= this.damage;
				if(c.parentEntity.health<0) { c.parentEntity.health = 0; }
				c.velocity.multiply(1-this.slowHitObjectPercent);
				setState(2);
			}
		}
	}
	
	public void setState(int s) {
		this.state = s;
		this.stateInit = false;
	}
	
	@Override
	public void update() {
		switch(state) {
			case 1:
				if(!this.stateInit) {
					this.hitBox.active = true;
					this.image.show();
					this.stateInit = true;
					this.timer = 0;
					this.stateInit = true;
				}
				activeState();
				if(this.timer>5) { setState(2); }
				break;
			case 2:
				if(!this.stateInit) {
					this.emitter1.active = false;
					this.emitter2.active = false;
					this.emitter3.active = true;
					this.image.hide();
					this.hitBox.active = false;
					this.hitBox.velocity.multiply(0);
					this.timer = 0;
					this.stateInit = true;
				}
				if(this.timer>500) {
					this.emitter3.active = false;
					this.timer = 0;
					setState(0);
				}
				this.timer += EZ.getDeltaTime();
				break;
			default:
				if(!this.stateInit) {
					this.image.hide();
					this.hitBox.active = false;
					this.hitBox.velocity.multiply(0);
					this.emitter1.active = false;
					this.emitter2.active = false;
					this.emitter3.active = false;
					this.timer = 0;
					this.stateInit = true;
				}
				break;
		}
	}

	@Override
	public void dispose() {
		GE.removeEmitter(this.emitter1);
		GE.removeEmitter(this.emitter2);
		GE.removeEmitter(this.emitter3);
		GameManager.disposeEZImage(this.image);
		this.hitBox.dispose();
		GameManager.disposeEZGroup(this.group);
	}
	
}

class RoadBlock1 extends BasicGameObject{
	
	int ms;
	
	public RoadBlock1(double x, double y, double rot, double scale) {
		this.group = EZ.addGroup();
		this.image = EZ.addImage("img/road_block_1.png", 0, 0);
		this.image.scaleTo(scale);
		
		this.hitBox = GE.addBoxCollider(this.group, (int)(100*scale), (int)(6*scale), 3, 3);
		this.hitBox.addToCollisionGroups(3);
		this.hitBox.addToCollisionGroups(10);
		this.hitBox.setMass(3);
		
		this.group.addElement(this.image);
		this.group.translateTo((int)x, (int)y);
		this.group.rotateTo(rot);
		
		this.ms = Math.max(this.image.getWidth(), this.image.getWidth())/2;
		
		GE.worldGroup.addElement(this.group);
	}
	
	@Override
	public void update() {
		double d = this.ms*this.image.getScale()*GE.worldGroup.getScale();
		if( (this.group.getWorldXCenter()<-d || this.group.getWorldXCenter()>EZ.getWindowWidth()+d) ||
				( this.group.getWorldYCenter()<-d || this.group.getWorldYCenter() > EZ.getWindowHeight()+d )
			) {
			this.image.hide();
			this.hitBox.active = false;
		}else {
			this.image.show();
			this.hitBox.active = true;
		}
	}

	@Override
	public void dispose() {
		GameManager.disposeEZImage(this.image);
		this.hitBox.dispose();
		GameManager.disposeEZGroup(this.group);
	}
	
}

class BusStop extends BasicGameObject{
	
	int ms;
	
	public BusStop(double x, double y, double rot, double scale) {
		this.group = EZ.addGroup();
		this.image = EZ.addImage("img/bus_stop.png", 0, 0);
		this.image.scaleTo(scale);
		
		this.hitBox = GE.addBoxCollider(this.group, (int)(260*scale), (int)(100*scale), 4, 10);
		this.hitBox.addToCollisionGroups(3);
		
		this.group.addElement(this.image);
		this.group.translateTo((int)x, (int)y);
		this.group.rotateTo(rot);
		
		this.ms = Math.max(this.image.getWidth(), this.image.getWidth())/2;
		
		GE.worldGroup.addElement(this.group);
	}

	@Override
	public void update() {
		double d = this.ms*this.image.getScale()*GE.worldGroup.getScale();
		if( (this.group.getWorldXCenter()<-d || this.group.getWorldXCenter()>EZ.getWindowWidth()+d) ||
				( this.group.getWorldYCenter()<-d || this.group.getWorldYCenter() > EZ.getWindowHeight()+d )
			) {
			this.image.hide();
			this.hitBox.active = false;
		}else {
			this.image.show();
			this.hitBox.active = true;
		}
	}

	@Override
	public void dispose() {
		GameManager.disposeEZImage(this.image);
		this.hitBox.dispose();
		GameManager.disposeEZGroup(this.group);
	}
	
}

class Building1 extends BasicGameObject{
	
	int ms;
	
	public Building1( double x, double y, double rot, double scale ) {
		this.group = EZ.addGroup();
		this.image = EZ.addImage("img/building_1.png", 0, 0);
		this.image.scaleTo(scale);
		
		this.hitBox = GE.addBoxCollider(this.group, (int)(72*scale), (int)(100*scale), 4, 10);
		this.hitBox.addToCollisionGroups(3);
		
		this.group.addElement(this.image);
		this.group.translateTo((int)x, (int)y);
		this.group.rotateTo(rot);
		
		this.ms = Math.max(this.image.getWidth(), this.image.getWidth())/2;
		
		GE.worldGroup.addElement(this.group);
	}
	
	@Override
	public void update() {
		double d = this.ms*this.image.getScale()*GE.worldGroup.getScale();
		if( (this.group.getWorldXCenter()<-d || this.group.getWorldXCenter()>EZ.getWindowWidth()+d) ||
				( this.group.getWorldYCenter()<-d || this.group.getWorldYCenter() > EZ.getWindowHeight()+d )
			) {
			this.image.hide();
			this.hitBox.active = false;
		}else {
			this.image.show();
			this.hitBox.active = true;
		}
	}

	@Override
	public void dispose() {
		GameManager.disposeEZImage(this.image);
		this.hitBox.dispose();
		GameManager.disposeEZGroup(this.group);
	}
	
}

class StreetLamp extends BasicGameObject{
	
	private EZImage light;
	int ms;
	
	public StreetLamp( double pos_x, double pos_y, double rot_degrees, double scale) {
		
		
		
		this.group = EZ.addGroup();
		this.light = EZ.addImage("img/light.png", 200, 0);
		this.image = EZ.addImage("img/street_lamp.png", 200, 0);
		this.hitBox = GE.addBoxCollider(this.group, (int)(20*scale), (int)(20*scale), 4, 10);
		this.hitBox.addToCollisionGroups(3);
		
		this.light.scaleTo(scale);
		this.image.scaleTo(2*scale/5);
		
		this.group.addElement(this.light);
		this.group.addElement(this.image);
		
		Vector p = new Vector(-200,0);
		p.rotate(rot_degrees*Math.PI/180);
		
		this.group.rotateTo(rot_degrees);
		this.group.translateTo(pos_x+p.x, pos_y+p.y);
		GE.worldGroup.addElement(this.group);
		
		this.ms = Math.max(this.light.getWidth(), this.light.getWidth())/2;
		
	}
	
	@Override
	public void update() {
		double d = this.ms*this.light.getScale()*GE.worldGroup.getScale();
		if( (this.group.getWorldXCenter()<-d || this.group.getWorldXCenter()>EZ.getWindowWidth()+d) ||
				( this.group.getWorldYCenter()<-d || this.group.getWorldYCenter() > EZ.getWindowHeight()+d )
			) {
			this.image.hide();
			this.light.hide();
			this.hitBox.active = false;
		}else {
			this.image.show();
			this.light.show();
			this.hitBox.active = true;
		}
	}

	@Override
	public void dispose() {
		GameManager.disposeEZImage(this.image);
		this.hitBox.dispose();
		GameManager.disposeEZGroup(this.group);
	}
	
}

class ParkedCar extends BasicGameObject{
	
	int ms;
	
	public ParkedCar(double posX, double posY) {
		this.group = EZ.addGroup();
		this.image = EZ.addImage("img/car.png", 0, 0 );
		this.image.scaleTo(6);
		this.group.addElement(this.image);
		this.group.translateTo( posX, posY );
		
		int r = 50;
		int q = (int)(0.70711*r);
		this.hitBox = GE.addBoxCollider( this.group, 150, 350, 3, 3 );
		this.hitBox.setMass(10);
		this.hitBox.momentInertia *= 0.1;
		this.hitBox.elasticity = 0.8;
		
		this.hitBox.addToCollisionGroups(10);
		this.hitBox.addToCollisionGroups(3);
		
		GE.worldGroup.addElement(this.group);
		
		this.ms = Math.max(this.image.getWidth(), this.image.getWidth())/2;
		
	}

	@Override
	public void dispose() {
		GameManager.disposeEZImage(this.image);
		this.hitBox.dispose();
		GameManager.disposeEZGroup(this.group);
	}

	@Override
	public void update() {
		double d = this.ms*this.image.getScale()*GE.worldGroup.getScale();
		if( (this.group.getWorldXCenter()<-d || this.group.getWorldXCenter()>EZ.getWindowWidth()+d) ||
				( this.group.getWorldYCenter()<-d || this.group.getWorldYCenter() > EZ.getWindowHeight()+d )
			) {
			this.image.hide();
			this.hitBox.active = false;
		}else {
			this.image.show();
			this.hitBox.active = true;
		}
		
	}
	
}

class TrashCan extends BasicGameObject{
	
	private BasicParticleEmitter emitter;
	int ms;
	
	public TrashCan( double x, double y, double rot, double scale) {
		this.group = EZ.addGroup();
		
		this.emitter = GE.addEmitter(this.group, 10, 50, 50, new Color( 150,150,150, 150 ), 5, 30, 1000);
		
		this.image = EZ.addImage("img/trashcan_1.png", 0, 0);
		this.image.scaleTo(scale);
		
		int r = (int)(50*scale);
		int q = (int)(0.70711*r);
		this.hitBox = GE.addPolyCollider(
				this.group,
				new int[] {-q, 0, q, r, q, 0, -q, -r},
				new int[] {-q,-r,-q, 0, q, r,  q,  0},
				3, 3
			);
		this.hitBox.setMass(2);
		this.hitBox.elasticity = 0.8;
		
		this.hitBox.addToCollisionGroups(10);
		this.hitBox.addToCollisionGroups(3);
		
		this.group.addElement(this.image);
		this.group.translateTo((int)x, (int)y);
		this.group.rotateTo(rot);
		
		GE.worldGroup.addElement(this.group);
		
		this.ms = Math.max(this.image.getWidth(), this.image.getWidth())/2;
	}

	@Override
	public void dispose() {
		GE.removeEmitter(this.emitter);
		GameManager.disposeEZImage(this.image);
		this.hitBox.dispose();
		GameManager.disposeEZGroup(this.group);
		
	}

	@Override
	public void update() {
		
		double d = this.ms*this.image.getScale()*GE.worldGroup.getScale();
		if( (this.group.getWorldXCenter()<-d || this.group.getWorldXCenter()>EZ.getWindowWidth()+d) ||
				( this.group.getWorldYCenter()<-d || this.group.getWorldYCenter() > EZ.getWindowHeight()+d )
			) {
			this.image.hide();
			this.hitBox.active = false;
			this.emitter.active = false;
		}else {
			this.image.show();
			this.hitBox.active = true;
			if(this.hitBox.velocity.length()>5) {
				this.emitter.active = true;
			}else {
				this.emitter.active = false;
			}
		}
	}
	
	
}
