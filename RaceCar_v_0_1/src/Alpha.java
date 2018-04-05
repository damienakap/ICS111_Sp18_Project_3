
/*
 * these are incomplete object types with limited function.
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

public class Alpha {
	
	private static ArrayList<Occludable> occludables = new ArrayList<Occludable>();
	
	public static Occludable addOccludable( String img, int w, int h, int g) {
		occludables.add(new Occludable(img, w, h, g));
		return occludables.get(occludables.size()-1);
	}
	
	public static void update() {
		
	}
	
}

class PolyImage extends EZElement{
	
	public Shape clip;
	public BufferedImage img;
	public BufferedImage bufferedImage;
	public EZElement element;
	public boolean rotates = false;
	public boolean tiles = false;
	public boolean useExtClip = false;
	private TexturePaint tp;
	private double scale = 1.0d;
	private double iOffsetX = 0;
	private double iOffsetY = 0;
	
	/**
	 * Creates a polygon image
	 * @param img - Image file
	 * @param ep - EZElement to copy bounds from
	 * @param rotates - If true, the image will rotate with the polygon
	 */
	public PolyImage(String img, EZElement ep, boolean rotates) {
		this.element = ep;
		this.rotates = rotates;
		try {this.img = ImageIO.read(new File(img));
		} catch (IOException e) { e.printStackTrace(); }
		//this.clip = this.element.getBounds();
		//this.element.hide();
		EZ.addElement(this);
	}
	
	/**
	 * Disposes the graphics of the polygon image
	 */
	public void dispose() {
		if(this.hasParent()) { this.getParent().removeElement(this); }
		this.bufferedImage = null;
		this.img = null;
		this.clip = null;
		this.tp = null;
		EZ.removeEZElement(this);
	}
	
	public void setClippedImgOffset( double x, double y ) {
		this.iOffsetX = x; this.iOffsetY = y;
	}
	
	@Override
	public void scaleTo(double s) {
		if(s<=0) {s=0.0000001;}
		this.scale = s;
	}
	
	@Override
	public double getScale() {
		return this.scale;
	}
	
	@Override
	public void paint(Graphics2D g2) {
		Shape os = g2.getClip();
		Paint opt = g2.getPaint();
		Color oc = g2.getColor();
	    if(tiles) {
	    	this.clip = this.element.getBounds();
	    	this.bufferedImage = this.img;
	    	int x = this.element.getWorldXCenter();
		    int y = this.element.getWorldYCenter();
		    this.tp = new TexturePaint(this.bufferedImage, new Rectangle(x, y, this.bufferedImage.getWidth(), bufferedImage.getHeight()));
		    
		    //g2.setPaint(this.tp);
		    //g2.setClip(this.clip);
		    //g2.draw(this.clip);
		    //g2.fill(this.clip);
	    	//g2.setClip(os);
	    	//g2.setPaint(opt);
	    	
	    	this.bufferedImage = null;
	    	
	    }else {
	    	if(!useExtClip) { this.clip = this.element.getBounds(); }
	    	AffineTransform tx = new AffineTransform();
			if(this.rotates) {
				tx.rotate(
						this.element.getParent().getRotation()*Math.PI/180,
						this.element.getWorldXCenter(),
						this.element.getWorldYCenter()
					);
				
			}
			Vector v = new Vector(this.element.getWorldXCenter() - this.img.getWidth()*this.scale/2,
					this.element.getWorldYCenter() - this.img.getHeight()*this.scale/2);
			//v.rotate(-this.element.getRotation()*Math.PI/180);
			tx.translate(v.x,v.y);
			tx.scale(this.scale, this.scale);
			AffineTransformOp op = new AffineTransformOp(tx,
		        AffineTransformOp.TYPE_BILINEAR);
		    
	    	this.bufferedImage = op.filter(this.img, null);
	    	
	    	
	    	g2.setClip(this.clip);
	    	//g2.setColor(Color.GREEN);
	    	//g2.fill(this.clip);
	    	//g2.drawImage(bufferedImage, 0, 0, this.element.getWidth(), this.element.getHeight(), 0, 0, this.element.getWidth(), this.element.getHeight(), null);
	    	g2.drawImage(this.bufferedImage, 0,0, null);
	    }
	    //g2.setPaint(opt);
	    //g2.setClip(os);
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getXCenter() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getYCenter() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setColor(Color c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFilled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFilled(boolean f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void translateTo(double x, double y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void translateBy(double x, double y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void identity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Shape getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

}

class Occludable{
	
	public EZGroup group;
	public PolyImage PI;
	public BoxCollider collider;
	private Polygon vision;
	private Segment ray;
	
	private ArrayList<RayHitPoint> testPoints = new ArrayList<RayHitPoint>();
	private ArrayList<RayHitPoint> hitPoints = new ArrayList<RayHitPoint>();
	private ArrayList<Double> hitPointsDFilter = new ArrayList<Double>();
	private ArrayList<RayHitPoint> hitPointsFiltered = new ArrayList<RayHitPoint>();
	private RayHitPoint[] hitPointsArray;
	
	private ArrayList<Vector> testVerts = new ArrayList<Vector>();
	private ArrayList<Segment> testLines = new ArrayList<Segment>();
	
	public Occludable( String imageName, int w, int h, int g) {
		this.group = EZ.addGroup();
		this.collider = new BoxCollider(this.group, w, h, 1, g);
		this.collider.addToCollisionGroups(10);
		
		this.ray = new Segment(2000);
		this.group.addElement(this.ray.line);
		this.ray.line.setParent(this.group);
		
		this.PI = new PolyImage( imageName, this.collider.graphic, false);
		this.PI.useExtClip = true;
		this.PI.pullToFront();
		this.PI.setParent(this.group);
		this.group.addElement(this.PI);
		//this.PI.setLayer(18);
		this.vision = new Polygon();
		
		
		this.ray.line.show();
		this.ray.line.pullToFront();
	}
	
	public void scaleTo(double s) {
		this.PI.scaleTo(s);
	}
	
	public void setParent(EZGroup g) {
		this.group.setParent(g);
	}
	
	public void dispose() {
		if(this.group.hasParent()) { this.group.removeParent(); }
		EZ.removeEZElement(this.group);
		this.PI.dispose();
	}
	
	public void update() {
		this.testPoints.clear();
		this.testLines.clear();
		this.hitPoints.clear();
		this.testVerts.clear();
		this.hitPointsDFilter.clear();
		this.hitPointsFiltered.clear();
		
		//this.ray.rotateTo( -(Math.PI/4));
		//Vector rayOrigin = new Vector(
		//	this.ray.worldPoints[0].x, this.ray.worldPoints[0].y
		//);
		// get Collision object verts
		for( Collider c : this.collider.hitObjectList ) {
			c.updateLines();
			for( Segment s : c.lines ) {
				this.testLines.add(s);
				this.testVerts.add(s.worldPoints[0]);
			}	
		}
		
		// get self collider verts
		this.collider.updateLines();
		for( Segment s : this.collider.lines ) {
			this.testLines.add(s);
			this.testVerts.add(s.worldPoints[0]);
		}
		
		// raycast to lines
		Vector endPoint = new Vector(0,0);
		RayHitPoint rhp0 = null;
		RayHitPoint rhp1;
		for( Vector tv : this.testVerts ) {
			endPoint.copy( tv );
			endPoint.add( -this.ray.worldPoints[0].x, -this.ray.worldPoints[0].y);
			endPoint.normalize();
			
			this.ray.direction.copy(endPoint);
			
			this.ray.rotateBy(0.0001);
			for( Segment s : this.testLines ) {
				rhp1 = this.ray.intersects(s, true);
				if(rhp1 != null) {
					if(rhp0 == null || rhp0.T1 > rhp1.T1) {
						rhp0 = rhp1;
					}
				}
			}
			if(rhp0 != null) {
				this.hitPoints.add(rhp0);
			}
			rhp0 = null;
			
			this.ray.rotateBy(-0.0002);
			for( Segment s : this.testLines ) {
				rhp1 = this.ray.intersects(s, true);
				if(rhp1 != null) {
					if(rhp0 == null || rhp0.T1 > rhp1.T1) {
						rhp0 = rhp1;
					}
				}
			}
			if(rhp0 != null) {
				this.hitPoints.add(rhp0);
			}
			rhp0 = null;
			
		}
		
		// filter points with common angles;
		for(int i=0; i<this.hitPoints.size(); i++) {
			if(!this.hitPointsDFilter.contains(this.hitPoints.get(i).angle)) {
				this.hitPointsFiltered.add( this.hitPoints.get(i) );
				this.hitPointsDFilter.add(this.hitPoints.get(i).angle);
			}
		}
		
		// convert ArrayList to array
		this.hitPointsArray = new RayHitPoint[this.hitPointsFiltered.size()];
		for(int i=0; i<this.hitPointsFiltered.size(); i++) {
			this.hitPointsArray[i] = this.hitPointsFiltered.get(i);
		}
		
		// sort hitpoints by angle
		if(this.hitPointsArray.length>1) {
			Arrays.sort( this.hitPointsArray );
		}
		
		double x, y;
		this.vision.reset();
		for(int i = 0; i < this.hitPointsArray.length; i++) {
			//System.out.println(this.hitPointsArray[i].point.x);
			this.vision.addPoint(
				(int)(this.hitPointsArray[i].point.x), 
				(int)(this.hitPointsArray[i].point.y) 
				);
		}
		
		
		this.PI.clip = this.vision;
		
	}
	
	public void setSourcePointOffset( double x, double y ) {
		this.ray.translateTo(x, y);
	}
	
}

class SpawnNode {
	
	private Vector position;
	private double radius;
	private Random rand;
	
	public SpawnNode(double x, double y, double r){
		this.position = new Vector(x,y);
		this.radius = r;
		this.rand = new Random();
	}
	
	public Vector getSpawnPoint() {
		rand.setSeed(System.nanoTime());
		double theta = rand.nextDouble()*2*Math.PI;
		rand.setSeed(System.nanoTime());
		double r = rand.nextDouble()*this.radius;
		
		return new Vector( r*Math.cos(theta), r*Math.sin(theta));
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
}


