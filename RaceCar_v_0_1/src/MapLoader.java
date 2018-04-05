
import java.awt.Color;
import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class MapLoader {
	
	
	public static Random rand = new Random();
	public static ArrayList<Collider> wallColliders = new ArrayList<Collider>();
	public static ArrayList<EZGroup> walls = new ArrayList<EZGroup>();
	public static ArrayList<PolyImage> wallTextures = new ArrayList<PolyImage>();
	
	public static ArrayList<EZGroup> grounds = new ArrayList<EZGroup>();
	public static ArrayList<PolyImage> groundTextures = new ArrayList<PolyImage>();
	
	public static ArrayList<NavNode> navigationNodes = new ArrayList<NavNode>();
	public static ArrayList<SpawnNode> spawnNodes = new ArrayList<SpawnNode>();
	
	public static BufferedReader reader;
	
	private static ArrayList<Integer> tempList = new ArrayList<Integer>();
	
	public static void unLoadMap() {
		for(Collider c : wallColliders) { c.dispose(); }
		wallColliders.clear();
		for( PolyImage pi : wallTextures ) {pi.dispose();}
		wallTextures.clear();
		for( EZGroup g : walls ) { EZ.removeEZElement(g); }
		walls.clear();
		
		for( PolyImage pi : groundTextures ) { pi.dispose(); }
		groundTextures.clear();
		for( EZGroup g : grounds ) { EZ.removeEZElement(g); }
		grounds.clear();
		
		for( NavNode n : navigationNodes ) { n.dispose(); }
		navigationNodes.clear();
		for( SpawnNode s : spawnNodes ) { s.dispose(); }
		spawnNodes.clear();
		navigationNodes.add(new NavNode(0, 0));
		
	}
	
	public static void loadMap(String file) {
		for(NavNode n : navigationNodes) { n.dispose(); }
		navigationNodes.clear();
		try {
			reader = new BufferedReader( new FileReader(file)  );
			String line;
			String sub0;
			
			String imageDir;
			
			int mode = 0;
			/*									
				1: read wall data,				draw layer: 9
				2: read ground data, 			draw layer: 5
				3: read nav node data,			
				4: read spawn node data,		
				5: read collidable object data	draw layer: 8
				
				Note:
					player drawn on layer 7
					other entities drawn on layer 6
			*/
			
			// read Map data from file
			while( (line = reader.readLine()) != null ) {
				if(line.substring(0, 2).contains("//") ) {
					// Get the data type to be read.
					mode = Integer.valueOf( line.substring(2, 3) ) ;
				}else {
					tempList.clear();
					if(mode==1 || mode == 2) {
						sub0 = line.substring( 0, line.indexOf(","));
						line = line.substring(line.indexOf(",")+1, line.length());
						// Get Image directory
						imageDir = sub0;
						
						int[] pointsX = null, pointsY = null;
						// Get object Data
						for(int l=0; l<2; l++) {
							sub0 = line.substring( 0, line.indexOf(","));
							line = line.substring(line.indexOf(",")+1, line.length());
							
							while(!sub0.equals("#")) {
								
								tempList.add( Integer.valueOf(sub0) );
								
								sub0 = line.substring( 0, line.indexOf(","));
								line = line.substring(line.indexOf(",")+1, line.length());
								
							}
							
							if(l==0) {
								pointsX = new int[tempList.size()];
								for(int x=0; x<tempList.size(); x++) { pointsX[x] = tempList.get(x); }
							}else {
								pointsY = new int[tempList.size()];
								for(int x=0; x<tempList.size(); x++) { pointsY[x] = tempList.get(x); }
							}
							tempList.clear();
						}
						// get translation to location on map
						for(int l=0; l<2; l++) {
							sub0 = line.substring( 0, line.indexOf(","));
							line = line.substring(line.indexOf(",")+1, line.length());
							
							tempList.add(Integer.valueOf(sub0));
						}
						
						
						if(mode==1) {
							// Create a wall
							walls.add(EZ.addGroup());
							wallColliders.add( new PolyCollider(walls.get(walls.size()-1), pointsX, pointsY, 4, 10) );
							wallColliders.get(wallColliders.size()-1).addToCollisionGroups(10);
							wallTextures.add(new PolyImage(imageDir, wallColliders.get(wallColliders.size()-1).graphic, false));
							wallTextures.get(wallTextures.size()-1).tiles = true;
							GE.worldGroup.addElement(walls.get(walls.size()-1));
							walls.get(walls.size()-1).setParent(GE.worldGroup);
							
							walls.get(walls.size()-1).translateTo(tempList.get(0), tempList.get(1));
							wallTextures.get(wallTextures.size()-1).clip = wallColliders.get(wallColliders.size()-1).graphic.getBounds();
							
							wallTextures.get(wallTextures.size()-1).setLayer(9);
							
							line = null;
							
						}else if(mode==2) {
							// Create a ground texture
							grounds.add(EZ.addGroup());
							groundTextures.add( new PolyImage(imageDir, EZ.addPolygon(pointsX, pointsY, new Color(100,0,0,0), true), false) );
							groundTextures.get(groundTextures.size()-1).tiles = true;
							grounds.get(grounds.size()-1).addElement( groundTextures.get(groundTextures.size()-1).element );
							groundTextures.get(groundTextures.size()-1).element.setParent(grounds.get(grounds.size()-1));
							GE.worldGroup.addElement( grounds.get(grounds.size()-1) );
							grounds.get(grounds.size()-1).setParent(GE.worldGroup);
							
							grounds.get(grounds.size()-1).translateTo(tempList.get(0), tempList.get(1));
							groundTextures.get(groundTextures.size()-1).clip = groundTextures.get(groundTextures.size()-1).element.getBounds();
							
							groundTextures.get(groundTextures.size()-1).setLayer(7);
							
							line = null;
							
						}
					}else if(mode==3) {
						
						// get node x position
						sub0 = line.substring( 0, line.indexOf(","));
						line = line.substring(line.indexOf(",")+1, line.length());
						int x = Integer.valueOf(sub0);
						
						// get node y position
						sub0 = line.substring( 0, line.indexOf(","));
						line = line.substring(line.indexOf(",")+1, line.length());
						int y = Integer.valueOf(sub0);
						
						// create navigation node
						navigationNodes.add(new NavNode(x,y));
						
						for(int l=0; l<2; l++) {
							sub0 = line.substring( 0, line.indexOf(","));
							line = line.substring(line.indexOf(",")+1, line.length());
							
							while(!sub0.equals("#")) {
								
								tempList.add( Integer.valueOf(sub0) );
								
								sub0 = line.substring( 0, line.indexOf(","));
								line = line.substring(line.indexOf(",")+1, line.length());
								
							}
							if(l==0) {
								// set visible nodes
								for( int i=0; i<tempList.size(); i++) { navigationNodes.get(navigationNodes.size()-1).visibleNodeInds.add(tempList.get(i)); }
							}else {
								// set distance to visible nodes
								navigationNodes.get(navigationNodes.size()-1).VisibleNodeDistance = new int[tempList.size()];
								for( int i=0; i<tempList.size(); i++) { navigationNodes.get(navigationNodes.size()-1).VisibleNodeDistance[i] = tempList.get(i); }
							}
							tempList.clear();
						}
						
					}else if(mode==4) {
						
					}else if(mode==5) {
						
					}
					
					
				}
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
		public static ArrayList<NavNode> getNavPath(NavNode Start, NavNode End){
		ArrayList<NavNode> path = new ArrayList<NavNode>();
		ArrayList<NavNode> path2 = new ArrayList<NavNode>();
		path.add(Start);
		ArrayList<Integer> blackList = new ArrayList<Integer>();
		ArrayList<Integer> tempBlackList = new ArrayList<Integer>();
		
		
		int indOfEnd = navigationNodes.indexOf(End);
		int pathsFound = 0;
		boolean pathFound = false;
		int pathlength;
		int path2length;
		
		
		Vector VectoEnd = new Vector(End.position.x-Start.position.x, End.position.y-Start.position.y);
		Vector VecTemp = new Vector(0,0);
		
		double temp = -1;
		int nextNode = -1;
		int nodeLast = -1;
		
		blackList.add(navigationNodes.indexOf(Start));
		
		while(!pathFound) {
			
			if(path.size()==0) {
				pathFound = true;
			}else {
				for( int i=0; i<path.get(path.size()-1).visibleNodeInds.size(); i++) {
					if(!blackList.contains(path.get(path.size()-1).visibleNodeInds.get(i)) && !tempBlackList.contains(path.get(path.size()-1).visibleNodeInds.get(i)) ) {
						VectoEnd.set(End.position.x-path.get(path.size()-1).position.x,End.position.y-path.get(path.size()-1).position.y);
						VecTemp.set( navigationNodes.get(path.get(path.size()-1).visibleNodeInds.get(i)).position.x-path.get(path.size()-1).position.x,
								navigationNodes.get(path.get(path.size()-1).visibleNodeInds.get(i)).position.y-path.get(path.size()-1).position.y);
						if(temp<0 || temp >  VecTemp.angleBetween(VectoEnd)) {
							temp = VecTemp.angleBetween(VectoEnd);
							nextNode = path.get(path.size()-1).visibleNodeInds.get(i);
						}
						
						if(  path.get(path.size()-1).visibleNodeInds.get(i) == indOfEnd ) {nextNode = path.get(path.size()-1).visibleNodeInds.get(i); break;}
					}
				}
				temp = -1;
				
				if(nextNode == -1) {
					path.remove(path.size()-1); 
				}else {
					
					if(nodeLast == nextNode) {
						if(tempBlackList.contains(nextNode)) {
							blackList.add(navigationNodes.indexOf(path.get(path.size()-1)));
							path.remove(path.size()-1);
							tempBlackList.clear();
						}else { tempBlackList.add(nextNode); }
					}else {
						path.add(navigationNodes.get(nextNode));
						blackList.add(nextNode);
						if(path.get(path.size()-1).equals(End)) {
							pathsFound ++;
							if(pathsFound > 1) {
								pathFound = true;
							}else {
								if(path.size() > 1) {
									path2 = (ArrayList<NavNode>) path.clone();
									path.clear();
									path.add(Start);
									tempBlackList.clear();
									blackList.clear();
									blackList.add(navigationNodes.indexOf(Start));
									blackList.add(navigationNodes.indexOf(path2.get(path2.size()-2)) );
								}else { pathFound = true; }
							}
						}
					}
					if(path.contains(navigationNodes.get(nextNode))) {
						blackList.add(nextNode);
					}
				
				}
				nodeLast = nextNode;
				nextNode = -1;
			}
		
		}
		
		if(pathsFound == 2) {
			if(path.size() == 0) { return path2; }
			pathlength = 0;
			path2length = 0;
			for( int i=0; i<path.size()-3; i++) {
				pathlength += path.get(i).VisibleNodeDistance[path.get(i).visibleNodeInds.indexOf(navigationNodes.indexOf(path.get(i+1)))];
			}
			for( int i=0; i<path2.size()-2; i++) {
				path2length += path2.get(i).VisibleNodeDistance[path2.get(i).visibleNodeInds.indexOf(navigationNodes.indexOf(path2.get(i+1)))];
			}
			if(pathlength<path2length) {
				return path;
			}else if(pathlength==path2length){
				rand.setSeed(System.nanoTime());
				if(rand.nextInt(4)%2==0) {
					return path;
				}else {
					return path2;
				}
			}else{return path2;}
		}else {
			return path2;
		}
	}
	
	public static NavNode getClosestNodeTo( int x, int y) {
		NavNode closestNode;
		if(navigationNodes.size() > 0) { closestNode = navigationNodes.get(0); 
		}else { return null; }
		
		double dist = getDistance( x, y, (int)closestNode.position.x, (int)closestNode.position.y);
		double tempDist;
		for( NavNode n : navigationNodes) {
			tempDist = getDistance( x, y, (int)n.position.x, (int)n.position.y);
			if(tempDist<dist) {
				dist = tempDist;
				closestNode = n;
			}
		}
		return closestNode;
	}
	
	public static double getDistance( int x1, int y1, int x2, int y2) {
		return Math.pow( (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) , 0.5);
	}

}
