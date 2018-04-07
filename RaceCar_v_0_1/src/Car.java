
public class Car extends Entity{
	
	public double turnSpeed;
	public EZImage image;
	
	public Car( String filename, int width, int height) {
		this.group = EZ.addGroup();
		this.hit_box = GE.addBoxCollider(this.group, width, height, 3, 10);
		this.hit_box.addToCollisionGroups(10);
		this.hit_box.elasticity = 0.3;
		GE.worldGroup.addElement(this.group);
		
		if(filename!= null) {
			this.image = EZ.addImage(filename, 0, 0);
			this.group.addElement(this.image);
		}
		
		this.moveSpeed = 2;
		this.turnSpeed = 1.5;
		
	}
}
