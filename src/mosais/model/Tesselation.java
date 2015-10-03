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
package mosais.model;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import mosais.distribution.BoundingBox;
import mosais.distribution.PlotTree;

/**
 * This class models the generation of a tessalation by coordinating the generation
 * of a distribution and using the distributed points to redraw the image selected 
 * by the user.
 *
 * @author Andrew Trismen
 */
public class Tesselation {
	
	/** The image to be modified */
	private BufferedImage image;
	
	/** The quadtree backed data structure storing the distribution */
	private PlotTree tree;
	
	/**
	 * Construct a new tesselation to modify the given image using the given plot tree.
	 * 
	 * @param image Image to modify.
	 * @param tree PlotTree implementation to store and process points of the distribution.
	 * @throws IllegalArgumentException if either parameter is null.
	 */
	public Tesselation(BufferedImage image, PlotTree tree) {
		if (image == null || tree == null) {
			throw new IllegalArgumentException("Input cannot be null.");
		}
		
		this.image = image;
		this.tree = tree;
	}
	
	/**
	 * Generate the distribution of points in an area the size of the image.
	 */
	public void createDistribution() {
		tree.createDistribution();
	}
	
	/** 
	 * Draw the points generated in the distribution.
	 * 
	 * @param rgb The RGB color to draw the pixels.
	 */
	public void drawPoints(int rgb) {
		List<Point> pointList = tree.getDistributionAsList();
		for (Point p : pointList) {
			image.setRGB(p.x, p.y, rgb);
		}
	}
	
	/**
	 * Create a mosaic pattern in the image by setting all pixels in the image to the 
	 * same color as the closest distribution point.
	 */
	public void applyMosaic() {
		
		int w = image.getWidth();
		int h = image.getHeight();
		
		//get array of all pixels
		int[] pixels = null;
		pixels = image.getRGB(0, 0, w, h, null, 0, w);
		
		//for tracking coords of current pixel
		int x = 0;
		int y = 0;
		
		// To accelerate search, cache closest neighbor found on previous run, 
		// start searching from there
		Point previousNeighbor = null;
		
		for (int i = 0; i < pixels.length; i++) {

			//skip if the pixel is a plot point
			if (previousNeighbor == null || !(x == previousNeighbor.x && y == previousNeighbor.y)) {
				
				//Start search from previous closest neighbor
				BoundingBox searchZone = null;
				if (previousNeighbor != null) {
					double zoneDimension = 2 * (previousNeighbor.distance(x, y));
					searchZone = new BoundingBox(x - zoneDimension / 2, 
							y - zoneDimension / 2, 
							zoneDimension,
							zoneDimension);	
				}
				
				//find the closest distribution point
				Point closest = tree.getClosestNeighbor(new Point(x, y), searchZone);
				// cache for next search
				previousNeighbor = closest;
				//get the color of that pixel and set the current pixel to that color
				pixels[i] = image.getRGB(closest.x, closest.y);
			}
			
			//update x & y
			x++;

			if (x == w) {
				x = 0;
				y++;
			}
		}
		
		image.setRGB(0, 0, w, h, pixels, 0, w);
	}

	/**
	 * To run after completing a run of processing. Nulls references to the 
	 * data structures to save space and prepare for another run.
	 */
	public void cleanUp() {
		tree.cleanUp();
		tree = null;
		image = null;
	}
}
