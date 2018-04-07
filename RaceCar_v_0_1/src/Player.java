
public class Player extends Car{
	
	public Player(String filename, int width, int height) {
		super(filename, width, height);
	}

	public void update(){
		double thetaP = -this.group.getRotation()*Math.PI/180;
		
		// get input
		Vector v = new Vector(0,0);
		double w = 0;

		Keyboard.printInput();
		
		if(Keyboard.Keys[37] || Keyboard.Keys[65]) {		//left
			w += -1;
		}
		if(Keyboard.Keys[39] || Keyboard.Keys[68]){ 		//right
			w += 1;
		}
		if(Keyboard.Keys[38] || Keyboard.Keys[87]) { 		//up
			v.y += -1;
		}
		if(Keyboard.Keys[40] || Keyboard.Keys[83]) { 		//down
			v.y += 1;
		}
		
		// set player velocity
		v.normalize();
		v.multiply(this.moveSpeed);
		if(v.length()>0) {
			v.rotate(-thetaP);
			this.hit_box.velocity.copy(v);
		}
		
		double s = this.getVelocity().length();
		Vector dir = new Vector(0,1);
		dir.rotate(-thetaP);
		dir.multiply(s);
		s = Math.signum( this.getVelocity().dot(dir));
		dir.multiply(s);
		
		this.setVelocity(dir.x, dir.y);
		
		// set player angular velocity
		if(this.getVelocity().length() > 0){
			w *= -s*this.turnSpeed * this.getVelocity().length()/this.moveSpeed;
			this.setAngularVelocity( w );
		}
		
		//Rotates the screen
		Vector vt = new Vector(this.group.getXCenter(), this.group.getYCenter());
		vt.rotate(thetaP);
		GE.worldGroup.rotateTo(-this.group.getRotation());
		GE.worldGroup.translateTo(-vt.x+(EZ.getWindowWidth()/2), -vt.y+(EZ.getWindowHeight()/2));
	}
}
