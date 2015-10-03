/**
 * 
 */
package mosais.distribution;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

/**
 * Distribution method will be function tested with gui. Test of find closest neighbor
 * method will be automated.
 * 
 * @author Andrew Trismen
 */
public class PoissonPlotTest {
	
	PoissonPlot plot;
	Point[] p = new Point[5];

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		plot = new PoissonPlot(0, 10, 10);
		//points for inserting
		p[0] = new Point(2, 1);
		p[1] = new Point(9, 1);
		p[2] = new Point(7, 8);
		p[3] = new Point(4, 2);
		p[4] = new Point(2, 4);
		plot.insert(p[0]);
		plot.insert(p[1]);
		plot.insert(p[2]);
		plot.insert(p[3]);
		plot.insert(p[4]);
	}



	/**
	 * Test method for {@link mosais.distribution.PlotTree#getClosestNeighbor(java.awt.Point, mosais.distribution.BoundingBox)}.
	 */
	@Test
	public void testGetClosestNeighbor() {
		assertEquals(plot.getClosestNeighbor(new Point(8, 2), new BoundingBox(0, 0, 10, 10)), p[1]);
		assertEquals(plot.getClosestNeighbor(new Point(3, 1), new BoundingBox(0, 0, 10, 10)), p[0]);
		assertEquals(plot.getClosestNeighbor(new Point(2, 6), new BoundingBox(0, 0, 10, 10)), p[4]);
		assertEquals(plot.getClosestNeighbor(new Point(4, 1), new BoundingBox(0, 0, 10, 10)), p[3]);
	}

}
