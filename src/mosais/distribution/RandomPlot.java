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
import java.util.Random;

/**
 * A distribution of points that generates all points randomly without adjusting for
 * density of distribution.
 *
 * @author Andrew Trismen
 */
public class RandomPlot extends PlotTree {
	
	/** Make sure points will not lie on the edge and break bounding */
	public static final int EDGE_BUFFER = 1;

	/**
	 * Create a new random plot with the given number of points within the given dimensions.
	 * 
	 * @param numPoints Number of points to generate for the plot.
	 * @param width Width of the area in which to place points.
	 * @param height Height of the area in which to place points.
	 */
	public RandomPlot(int numPoints, int width, int height) {
		super(numPoints, width, height);
	}

	/**
	 * Create a distribution of points that are randomly generated.
	 */
	@Override
	public void createDistribution() {
		BoundingBox box = (BoundingBox)this.root.getModel();
		int maxX = (int)box.getWidth();
		int maxY = (int)box.getHeight();
		Random rnd = new Random();							//use a seed for debugging 
		for (int i = 1; i <= getNumPoints(); i++) {
			//make sure that points don't fall on outer edges
			int x = rnd.nextInt(maxX - 1) + EDGE_BUFFER;
			int y = rnd.nextInt(maxY - 1) + EDGE_BUFFER;
			insert(new Point(x, y));
		}
	}

}
