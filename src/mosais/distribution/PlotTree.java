/*
    Mosais - Mosaic maker.
    Copyright (C) 2015  Andrew Trismen

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
	Contact: Andrew Trismen - atrismen@gmail.com
 */
package mosais.distribution;

import java.awt.Point;
import java.util.*;

import mosais.distribution.Quadtree.Quadrant;

/**
 * A quadtree backed distribution of points in a two dimensional plane. 
 *
 * @author Andrew Trismen
 */
public abstract class PlotTree {
	
	/** Maximum number of points to store in a quadtree node */
	public static final int MAX_POINTS_PER_NODE = 10;
	
	/** Number of points to generate for the plot */
	private int numPoints;
	
	/** The root of the quadtree */
	protected Quadtree<Point> root;
	
	/** Parallel List store of points added to tree, to simplify passing data collection */
	private List<Point> pointList;
	
	/** Width of the plot */
	private int plotWidth;
	
	/** Height of the plot */
	private int plotHeight;
	
	
	/** 
	 * Generate the distribution of points. 
	 */
	public abstract void createDistribution();
	
	/**
	 * Construct a new distribution tree with the given number of points and dimensions.
	 * 
	 * @param numPoints Number of points to generate for the plot.
	 * @param width Width of the area in which to place points.
	 * @param height Height of the area in which to place points.
	 */
	public PlotTree(int numPoints, int width, int height) {
		this.numPoints = numPoints;
		this.root = new Quadtree<Point>(null, new BoundingBox(0, 0, width, height), MAX_POINTS_PER_NODE);
		this.pointList = new ArrayList<Point>();
		this.plotWidth = width;
		this.plotHeight = height;
	}	
	
	/**
	 * Get number of points that should be included in the plot.
	 * @return Number of points that will be in the plot.
	 */
	public int getNumPoints() {
		return numPoints;
	}
	
	/**
	 * @return Width of the distribution area.
	 */
	public int getPlotWidth() {
		return plotWidth;
	}
	
	/**
	 * @return Height of the distribution area.
	 */
	public int getPlotHeight() {
		return plotHeight;
	}
	
	/**
	 * Get all of the points currently in this distribution as a List.
	 * 
	 * @return List object containing all points in the distribution.
	 */
	public List<Point> getDistributionAsList() {
		return pointList;
	}
	
	/**
	 * Insert a point into the quadtree. The point will be inserted into the appropriate 
	 * node of the tree automatically. A copy of the reference will also be stored in a list
	 * for getting later.
	 * 
	 * @param p Point to insert into the tree.
	 * @throws IllegalArgumentException if p has a location outside the bounds of the tree.
	 */
	public void insert(Point p) {
		if (!root.isMember(p)) {
			throw new IllegalArgumentException("Point lies outside valid area.");
		}
		root.insert(p);
		pointList.add(p);
	}
	
	
	/**
	 * Find the closest neighbor currently in the quadtree to the given point. 
	 * 
	 * @param p Point to find the closest current neighbor of.
	 * @return Point that is closest to the given point p.
	 * @throws IllegalArgumentException If p lies outside the bounds of the tree
	 */
	public Point getClosestNeighbor(Point p, BoundingBox initialSearchZone) {
		if (!root.isMember(p)) {
			throw new IllegalArgumentException("Point lies outside valid area.");
		}
		
		if (initialSearchZone == null) {
			initialSearchZone = new BoundingBox(0, 0, plotWidth, plotHeight);
		}
		
		//place holder to start search for closest. Gets the first point in list
		Point closestNeighbor = pointList.get(0);
		closestNeighbor = searchTree(p, closestNeighbor, root, initialSearchZone);
		return closestNeighbor;
		
	}	
	
	/**
	 * Search the tree for a closest neighbor recursively.
	 * 
	 * @param target Point to find closest neighbor of
	 * @param closest Point that is the closest found to target
	 * @param searchNode The node of the tree being searched at this stage of the search
	 * @param searchZone The current area around target being searched.
	 * @return The closest point found to target.
	 */
	private Point searchTree(Point target, Point closest, Quadtree<Point> searchNode, BoundingBox searchZone) {

		// does the current search zone intersect the area of the node being looked at
		if (searchNode.getModel().isCodomain(searchZone)) {
			
			// current node has children, recurse into children
			if (searchNode.hasChildren()) {
				for (Quadrant quad : Quadrant.values()) {
					searchTree(target, closest, searchNode.getChildNode(quad), searchZone);					
				}
				
			//current node does not have children, search points at this node, if any 	
			} else {
				if (!searchNode.getData().isEmpty()) {
					//check if any points are closer to target than closest
					double closestDistance = target.distance(closest);
					for(Point candidate : searchNode.getData()) {
						double candidateDistance = target.distance(candidate);
						if (candidateDistance < closestDistance) {
							closestDistance = candidateDistance;
							//modify objects so they will carry over through recursive calls
							closest.move(candidate.x, candidate.y);
						    resizeSearchZone(target, closest, searchZone);
						}
					}
				}
			}
		} 
		return closest;
	}
	
	/**
	 * Change the dimensions of the search zone
	 * 
	 * @param target Point the search box is centered on.
	 * @param closest Closest point found to the target.
	 */
	private void resizeSearchZone(Point target, Point closest, BoundingBox searchZone) {
		double distance = target.distance(closest);
		double side = distance * 2;
		double baseX = target.getX() - distance;
		double baseY = target.getY() - distance;
		searchZone.resize(baseX, baseY, side, side);
	}
	
	/**
	 * To run after completing a run of processing. Nulls references to the 
	 * data structures to save space and prepare for another run.
	 */
	public void cleanUp() {
		root = null;
		pointList = null;
	}
}
