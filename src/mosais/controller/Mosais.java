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
package mosais.controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import mosais.GUI.MosaisGUI;
import mosais.distribution.*;
import mosais.model.Tesselation;

/**
 * A program to add a mosaic effect to any image.
 *
 * @author Andrew Trismen
 */
public class Mosais {
	
	/** Default fractional area for numPoint calculation */
	public static final double DEFAULT_FRAC_AREA = 1300;
	
	/** Difference between density levels for fractional area determination */
	public static final double FRAC_AREA_STEP = 600;
	
	/** instance of a tesselation */ 
	private Tesselation tesselation;
	
	/** instance of program GUI */
	private MosaisGUI gui;
	
	/** 
	 * Generate an instance of the app GUI.
	 */
	public Mosais() {		
		gui = new MosaisGUI(this);
		gui.setVisible(true);
	}
	
	public static void main(String[] args) {
		Mosais mosais = new Mosais();		
	}
	
	/** 
	 * Start the mosaic generation, called from GUI.
	 * 
	 * @param image Image to apply mosaic to.
	 * @param density Magnitude of density of points in distribution.
	 * @param type Type of distribution, Random or Uniform.
	 * @param pointsOnly Whether to show only distribution points.
	 */
	public void start(BufferedImage image, 
			int density, 
			String type, 
			boolean pointsOnly) {
		
		//numPoints calculations are based on the idea of the fractional area
		//the fractional area is the theoretical area surrounding a uniform
		//distribution of points in the area of the image. The number represents
		//dividing the area of the image by the number of points distributed 
		// in the area. By dividing area by the fractional area, you get the number 
		//of points needed to achieve that density. fractional area ranges from
		// ~250 to 2500
		double fractionalArea = 0;
		if (density >= 1 && density <= 5) {
			fractionalArea = 2500 - (FRAC_AREA_STEP * (density - 1));
		} else {
			fractionalArea = DEFAULT_FRAC_AREA;
		}	
		
		if (type.equalsIgnoreCase("RANDOM")) {
			//determine number of points as whole area / frac area
			
			int numPoints = (int)(image.getWidth() * image.getHeight() / fractionalArea);
			PlotTree tree = new RandomPlot(numPoints, image.getWidth(), image.getHeight());
			tesselation = new Tesselation(image, tree);
			
		// in the case of the poisson disc distribution, we want to determine density
		// as the distance between points. this is approximated by taking the square root
		// of the fractionalArea, because that approximates the distance between points
		// in a hypothetical distribution. The full transformation is:
		// numPoints = sqrt(w*h)/sqrt(w*h/fracArea)
		// numPoints^2 = w*h/(w*h/fracArea)
		// numPoints^2 = fracArea
		// numPoints = sqrt(fracArea)
		} else if (type.equalsIgnoreCase("UNIFORM")) {
			int numPoints = (int)Math.sqrt(fractionalArea);
			PlotTree tree = new PoissonPlot(numPoints, image.getWidth(), image.getHeight());
			tesselation = new Tesselation(image, tree);
		}
		
		tesselation.createDistribution();
		
		//Apply the distribution data either as a mosaic or points
		if (pointsOnly) {
			tesselation.drawPoints(Color.WHITE.getRGB());
		} else {
			tesselation.applyMosaic();
		}
		
		
		
		//free up resources and reset for another run
		//cleanUp();
		
	}
	
	/**
	 * To run after completing a run of processing. Nulls references to the 
	 * data structures to save space and prepare for another run.
	 * 
	 * Consider removing this and all cleanup code or improving
	 */
	private void cleanUp() {
		tesselation.cleanUp();
		tesselation = null;
	}

}
