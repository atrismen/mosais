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

/**
 * A distribution of points where a Poisson Disc algorithm is used to generate 
 * a uniform distribution.
 *
 * @author Andrew Trismen
 */
public class PoissonPlot extends PlotTree {
	
	/** Max times to try to find a valid candidate before declaring the seed inactive */
	public static final int MAX_CANDIDATES = 100;
	
	/** Minimum distance between any 2 points, higher = less dense, lower = more dense */
	public static final int MIN_DISTANCE = 8;
	
	/** Make sure points will not lie on the edge and break bounding */
	public static final int EDGE_BUFFER = 1;
	
	// make this based on the pixel density of the input image
	/** Minimum distance between any 2 points in the distribution. */
	private int minDistance;
	
	/** List of all points that can be used as seeds. */
	private List<Point> activePoints;

	/**
	 * Create a new Poisson disc based distribution plot. The numPoints field is unused in this
	 * implementation and can be ignored; included for compatibility with super class.
	 * 
	 * @param numPoints Ignored in this implementation, should be 0
	 * @param width Width of the area to distribute points over
	 * @param height Height of the area to distribute points over
	 */
	public PoissonPlot(int numPoints, int width, int height) {
		super(numPoints, width, height);
		
		// tweak this to get the right performance and effect
		//TODO: lean up dependency, density is determined in Mosais.java
		this.minDistance = numPoints;
		this.activePoints = new ArrayList<Point>();
	}

	/** 
	 * Create a Poisson Disc Distribution where all random points are generated in
	 * a disc around an existing seed point, and the inner ring of the disc defines
	 * the minimum distance 1 point is allowed to be to another.
	 */
	@Override
	public void createDistribution() {
		
		Random rnd = new Random();
		//generate an initial seed point randomly
		Point p = new Point(rnd.nextInt(getPlotWidth() + 1), rnd.nextInt(getPlotHeight()) + 1);
		insert(p);
		activePoints.add(p);
		
		//Begin generating points until there are no more active seed points.
		while (!activePoints.isEmpty()) {
			
			// get an active seed randomly
			
			Point seed = activePoints.get(rnd.nextInt(activePoints.size()));
			
			// Generate candidate coordinate limits based on max distance from seed.
			// candidates must be within the plot area.
			int minX = seed.x - (2 * minDistance);
			if (minX < 0) minX = 0;
			int minY = seed.y - (2 * minDistance);
			if (minY < 0) minY = 0;
			int maxX = seed.x + (2 * minDistance);
			if (maxX >= getPlotWidth()) maxX = getPlotWidth() - EDGE_BUFFER;
			int maxY = seed.y + (2 * minDistance);
			if (maxY >= getPlotHeight()) maxY = getPlotHeight() - EDGE_BUFFER;
			
			int candidatesTried = 0;
			
			//shortCircuit the loop if a candidate is selected
			boolean candidateFound = false;
			
			// Try up to 100 candidates before quitting
			while (candidatesTried < MAX_CANDIDATES && !candidateFound) {
				//Create candidate within 2 distances of the seed
				Point candidate = new Point(rnd.nextInt(maxX - minX + 1) + minX, rnd.nextInt(maxY - minY + 1) + minY);
				
				// If candidate is within the annulus, consider it an official candidate.
				// If not, do not count and restart loop.
				if (seed.distance(candidate) >= minDistance &&
					seed.distance(candidate) <= minDistance * 2) {
					
					// see if there is a neighbor less than minDistance from candidate
					// use a search zone of minDistance
					
					Point closestNeighbor = getClosestNeighbor(candidate, new BoundingBox(candidate.x - minDistance,
																						  candidate.y - minDistance,
																						  minDistance * 2,
																						  minDistance * 2));
					
					// candidate is far enough away from other points, add to distribution
					if (candidate.distance(closestNeighbor) >= minDistance) {
						insert(candidate);
						activePoints.add(candidate);
						candidateFound = true;
						
					// candidate is too close to another point, discard 	
					} else {
						candidatesTried++;
					}
				}
			}
			
			// remove seed from the active list if no candidate was found
			if (!candidateFound) {
				activePoints.remove(seed);
			}
		}
	}
}
