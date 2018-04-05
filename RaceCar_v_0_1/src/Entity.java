import java.util.ArrayList;

/*
 * Defines the base for all entities.
 */
public class Entity{
	protected String name;
	protected EZElement graphic;
	protected Collider hit_box;
	protected double health;
	protected int[] actions;
	protected int faction;
	public double moveSpeed;
	
	protected Sprite teleportSprite1;
	protected Sprite teleportSprite2;
	protected Sprite deathSprite;
	
	protected EZGroup group;
	protected EZGroup worldGroup;
	
	/**
	 * Sets the entities velocity
	 * @param x - Velocity in the x direction
	 * @param y - Velocity in the y direction
	 */
	public void setVelocity(double x, double y) { this.hit_box.velocity.set(x, y);}
	public Vector getVelocity() {
		return this.hit_box.velocity.copy();
	}
	/**
	 * Apply a force on the entity
	 * @param x - Force in the x direction
	 * @param y - Force in the y direction
	 */
	public void applyForce(double x, double y) { this.hit_box.velocity.set(x*this.hit_box.iMass*0.5, y*this.hit_box.iMass*0.5); }
	/**
	 * 
	 * @return - Entities' name
	 */
	public String getName() { return this.name; }
	/**
	 * 
	 * @param var - added to health
	 */
	public void applyToHealth(double var) { this.health += var;}
	/**
	 * 
	 * @return - World position as a vector
	 */
	public Vector getPosition() {return new Vector(this.group.getWorldXCenter(), this.group.getWorldYCenter());}
	/**
	 * Moves the object in the objects' local-x direction
	 * @param d - distance to move
	 */
	public void moveForward(double d){ this.group.moveForward(d); }
	/**
	 * Rotates the entity by theta
	 * @param theta - in degrees
	 */
	public void rotateBy(double theta) { this.group.rotateBy(theta); }
	/**
	 * Rotates the entity to theta from the positive x-axis
	 * @param theta - in degrees
	 */
	public void rotateTo(double theta) { this.group.rotateTo(theta); }
	/**
	 * Sets the entities velocity relative to its local axis
	 * @param x
	 * @param y
	 */
	public void setLocalVelocity(double x, double y) {
		Vector v = new Vector( x, y);
		v.rotate(this.group.getRotation() * Math.PI/180);
		this.hit_box.velocity.copy(v);
	}
	/**
	 * Local translation
	 * @param x - x position
	 * @param y - y position
	 */
	public void translateTo(double x, double y) { this.group.translateTo(x, y); }
	public void translateBy( double x, double y) { this.group.translateBy(x, y); }
	/**
	 * Set the entities' faction
	 * @param f - Faction number
	 */
	public void setFaction(int f) { this.faction = f; }
	/**
	 * 
	 * @return - Entities' faction number
	 */
	public int getFaction() { return this.faction; }
	/**
	 * Disposes the entities graphics
	 */
	public void dispose() {
		EZ.removeEZElement(this.graphic);
		this.hit_box.dispose();
		EZ.removeEZElement(this.group);
	}
	
}
