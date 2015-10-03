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
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import mosais.distribution.Quadtree.Quadrant;

/**
 * An area in a coordinate plane.
 * The area is defined by the point in the top left corner plus the width and height.
 *
 * @author Andrew Trismen 
 */
public class BoundingBox implements TreeNodeModel {
    
    /** The base point for this area */
    private Point2D.Double base;
    
    /** The width of this area */
    private double width;
    
    /** The height of this area */
    private double height;
    
    
    /**
     * Construct a new Bounding box with the given base and dimensions.
     *
     * @param base Top left corner of bounding box.
     * @param width Width of the new bounding box.
     * @param height Height of the new bounding box.
     */
    public BoundingBox(Point2D.Double base, double width, double height) {
        this.base = base;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Construct a new Bounding box with a base at the given coordinates and dimensions.
     *
     * @param x X coordinate of top left corner of bounding box.
     * @param y Y coordinate of top left corner of bounding box.
     * @param width Width of the new bounding box.
     * @param height Height of the new bounding box.
     */
    public BoundingBox(double x, double y, double width, double height) {
        resize(x, y, width, height);
    }
    
    /**
     * Change the size and base of this bounding box.
     * 
     * @param x X coordinate of the new base of the box.
     * @param y Y coordinate of the new base of the box.
     * @param width Width of the new box.
     * @param height Height of the new box.
     */
    public void resize (double x, double y, double width, double height) {
    	this.base = new Point2D.Double(x, y);
    	this.width = width;
    	this.height = height;
    }    
    
    /**
     * Return true if the given point is within this bounding box.
     *
     * @param p Point to be checked against this bounding box.
     * 
     * @return True if p is contained in this bounding box, else false.
     */
    public boolean contains(Point p) {
        return p.getX() >= base.getX() &&
               p.getX() < base.getX() + width &&
               p.getY() >= base.getY() &&
               p.getY() < base.getY() + height;
    }
    
    
    /**
     * Return true if the given bounding box intersects this bounding box.
     *
     * @param otherBox Bounding box to be checked for intersection against this.
     *
     * @return True if otherBox intersects this bounding box, false otherwise.
     */
    public boolean intersects(BoundingBox otherBox) {
        return base.getX() + width > otherBox.getBase().getX() &&
               otherBox.getBase().getX() + otherBox.getWidth() > base.getX() &&
               base.getY() + height > otherBox.getBase().getY() &&
               otherBox.getBase().getY() + otherBox.getHeight() > base.getY();    
    }
    
    /**
     * Return the base of this bounding box.
     *
     * @return The base of this bounding box.
     */
    public Point2D.Double getBase() {
        return base;
    }
    
    
    /**
     * Return the width of this bounding box.
     *
     * @return The width of this bounding box.
     */
    public double getWidth() {
        return width;
    }
    
    
    /**
     * Return the height of this bounding box.
     *
     * @return The height of this bounding box.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Return true if the given object is a Point and is contained within this bounding box.
     * Return false if the point is not contained.
     * 
     * @param o Object to check for containment in this box.
     * @return True is object is a point and is contained, false otherwise.
     * @throws IllegalArgumentException if o is not a Point object.
     */
	@Override
	public boolean isMember(Object o) throws IllegalArgumentException {
		if (!(o instanceof Point)) {
			throw new IllegalArgumentException("Parameter must be a Point object");
		}
		
		Point p = (Point)o;
		return contains(p);
	}

	/**
	 * Subdivide this box into 4 quadrants and map to the quadtree quadrants.
	 * 
	 * @return a map of the 4 quadrants of this box to the 4 quadtree quadrants
	 */
	@Override
	public Map<Quadrant, TreeNodeModel> subdivide() {
		Map<Quadrant, TreeNodeModel> map = new HashMap<Quadrant, TreeNodeModel>();
		
		double subWidth = getWidth() / 2;
		double subHeight = getHeight() / 2;
		double baseX = getBase().getX();
		double baseY = getBase().getY();
		
		map.put(Quadrant.NW, new BoundingBox(baseX, baseY, subWidth, subHeight));
		map.put(Quadrant.NE, new BoundingBox(baseX + subWidth, baseY, subWidth, subHeight));
		map.put(Quadrant.SW, new BoundingBox(baseX, baseY + subHeight, subWidth, subHeight));
		map.put(Quadrant.SE, new BoundingBox(baseX + subWidth, baseY + subHeight, subWidth, subHeight));
		
		return map;
	}
	
	/**
	 * Test if another box intersects this box fully or partially.
	 * 
	 * @param o Box to check for intersection with this box.
	 * @return True if otherBox intersects this box, false otherwise.
	 * @throws IllegalArgumentException if otherBox is not a BoundingBox object.
	 */
	public boolean isCodomain(TreeNodeModel o) {
		if (!(o instanceof BoundingBox)) {
			throw new IllegalArgumentException("Input is not a compatible type.");
		}
		
		BoundingBox otherBox = (BoundingBox)o;
		return intersects(otherBox);
	}
	
	/**
	 * Determine if 2 bounding boxes are equal. 2 boxes are equal if they have the same base
	 * and the same height and width.
	 * 
	 * @param o Object to compare for equality.
	 * @return True if o is equal to this bounding box; false if it is not, or is not a box
	 */
	public boolean equals(Object o) {
		if (!(o instanceof BoundingBox)) {
			return false;
		}
		BoundingBox other = (BoundingBox)o;
		return this.getBase().equals(other.getBase()) &&
			   this.getWidth() == other.getWidth() &&
			   this.getHeight() == other.getHeight();
	}
	
	/**
	 * create a unique hash of this object
	 * 
	 */
	public int hashCode() {
		int result = 0;
		int primeMultiplier = 37;
		result += (int)base.x;
		result += (int)base.y;
		result += (int)width;
		result += (int)height;
		return result * primeMultiplier;
	}
}