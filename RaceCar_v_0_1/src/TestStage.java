import java.awt.Color;
import java.awt.geom.GeneralPath;

public class TestStage {
	
	private static Car player;
	
	public static BoxCollider box_col;
	public static EZGroup box;
	
	public static EZImage backgroundImage;
	
	public static void init() {
		GE.backgroundColor = new Color(0,0,0,255);
		GameManager.screenFilter.setColor( new Color(100,0,0,0) );
		PhysicsManager.setGravity(0, 0);
		
		backgroundImage = EZ.addImage("img/wall_tile_01.png", 0, 0);
		GE.worldGroup.addElement(backgroundImage);
		backgroundImage.scaleTo(10);
		
		player = new Car("img/wall_tile_01.png", 100,100);
		
		box = EZ.addGroup();
		box_col = new BoxCollider(box, 50, 50, 3, 10);
		box_col.addToCollisionGroups(10);
		GE.worldGroup.addElement(box);
		box.setParent(GE.worldGroup);
		box_col.graphic.show();
		box_col.elasticity = 0.001;
		box.pullToFront();
		
		//light.group.rotateTo(40);
		//player.hit_box.graphic.pullToFront();
		
	}
	
	public static void update() {
		System.out.println(backgroundImage.getWorldXCenter());
		double thetaP = -player.group.getRotation()*Math.PI/180;
		
		// get input
		Vector v = new Vector(0,0);
		double w = 0;
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
		v.multiply(player.moveSpeed);
		w *= 2*player.turnSpeed;
		
		if(v.length()>0) {
			v.rotate(-thetaP);
			player.hit_box.velocity.copy(v);
		}
		
		if(player.getVelocity().length()> player.moveSpeed/4){
			player.hit_box.angularVelocity = w*player.getVelocity().length()/player.moveSpeed;
		}
		
		//Rotates the screen
		Vector vt = new Vector(player.group.getXCenter(), player.group.getYCenter());
		vt.rotate(thetaP);
		GE.worldGroup.rotateTo(-player.group.getRotation());
		GE.worldGroup.translateTo(-vt.x+(EZ.getWindowWidth()/2), -vt.y+(EZ.getWindowHeight()/2));
		
	}
	
}
