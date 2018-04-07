import java.awt.Color;
import java.awt.geom.GeneralPath;

public class TestStage {
	
	private static Player player;
	
	public static BoxCollider box_col;
	public static EZGroup box;
	
	public static void init() {
		GE.backgroundColor = new Color(0,0,0,255);
		GameManager.screenFilter.setColor( new Color(100,0,0,0) );
		PhysicsManager.setGravity(0, 0);
		
		MapLoader.loadMap("maps/map_01.txt");
		
		player = new Player("img/wall_tile_01.png", 100,100);
		player.image.scaleTo(0.5);
		
		box = EZ.addGroup();
		box_col = new BoxCollider(box, 50, 50, 3, 10);
		box_col.setMass( 8 );
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
		player.update();
		
	}
	
}
