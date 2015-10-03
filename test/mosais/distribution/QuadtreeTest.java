/**
 * 
 */
package mosais.distribution;

import static org.junit.Assert.*;

import java.awt.Point;

import mosais.distribution.Quadtree.Quadrant;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Andrew Trismen
 */
public class QuadtreeTest {
	
	Quadtree<Point> qt;

	/**
	 * Create 2 quadtrees, one containing 9 points, other empty
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		qt = new Quadtree<Point>(null, new BoundingBox(0, 0, 10, 10), 2);
		
	}

	/**
	 * Test method for {@link mosais.distribution.Quadtree#insert(java.lang.Object)}.
	 * 
	 * Insert points into qt to force a series of subdivides. this will also test
	 * the ismember function, haschildren, getdata, and getchildnode.
	 */
	@Test
	public void testInsert() {
		//points for inserting
		Point[] p = new Point[5];
		p[0] = new Point(2, 1);
		p[1] = new Point(9, 1);
		p[2] = new Point(7, 8);
		p[3] = new Point(4, 2);
		p[4] = new Point(2, 4);
		//before subdivide
		assertFalse(qt.hasChildren());
		qt.insert(p[0]);
		qt.insert(p[1]);
		qt.insert(p[2]);
		//first subdivide should have happened
		assertTrue(qt.hasChildren());
		//force NW to subdivide
		qt.insert(p[3]);
		qt.insert(p[4]);
		//check second subdivide
		assertTrue(qt.getChildNode(Quadrant.NW).hasChildren());
		//check that points are in the correct quadrants
		assertTrue(qt.getChildNode(Quadrant.NW).getChildNode(Quadrant.NW).getData().contains(p[0]));
		assertTrue(qt.getChildNode(Quadrant.NE).getData().contains(p[1]));
		assertTrue(qt.getChildNode(Quadrant.SE).getData().contains(p[2]));
		assertTrue(qt.getChildNode(Quadrant.NW).getChildNode(Quadrant.NE).getData().contains(p[3]));
		assertTrue(qt.getChildNode(Quadrant.NW).getChildNode(Quadrant.SW).getData().contains(p[4]));
	}
}
