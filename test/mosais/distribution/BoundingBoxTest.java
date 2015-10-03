/**
 * 
 */
package mosais.distribution;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import mosais.distribution.Quadtree.Quadrant;

/**
 *
 * @author Andrew Trismen
 */
public class BoundingBoxTest {
	
	BoundingBox[] b;
	Point[] p;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		b = new BoundingBox[10];
		b[0] = new BoundingBox(0, 0, 3, 3);
		b[1] = new BoundingBox(-2, -1, 4, 2);
		b[2] = new BoundingBox(3, 3, 2, 2);
		b[3] = new BoundingBox(5, 3, 2, 2);
		b[4] = new BoundingBox(3, 5, 4, 2);
		b[5] = new BoundingBox(1, 5, 2, 4);
		b[6] = new BoundingBox(11, 3, 2, 2);
		b[7] = new BoundingBox(13, 3, 2, 2);
		b[8] = new BoundingBox(11, 5, 2, 2);
		b[9] = new BoundingBox(13, 5, 2, 2);
		
		p = new Point[15];
		p[0] = new Point(0, 0);
		p[1] = new Point(5, 3);
		p[2] = new Point(7, 3);
		p[3] = new Point(7, 5);
		p[4] = new Point(7, 7);
		p[5] = new Point(5, 7);
		p[6] = new Point(3, 7);
		p[7] = new Point(3, 5);
		p[8] = new Point(5, 5);
		p[9] = new Point(13, 5);
		p[10] = new Point(13, 7);
		p[11] = new Point(2, 5);
		p[12] = new Point(1, 7);
		p[13] = new Point(3, 3);
		p[14] = new Point(6, 4);
	}

	/**
	 * Test method for {@link mosais.distribution.BoundingBox#resize(double, double, double, double)}.
	 */
	@Test
	public void testResize() {
		b[5].resize(3, 3, 2, 2);
		assertEquals(b[5], b[2]);
	}

	/**
	 * Test method for {@link mosais.distribution.BoundingBox#contains(java.awt.Point)}.
	 */
	@Test
	public void testContains() {
		assertTrue(b[3].contains(p[14]));
		//assertTrue(b[6].contains(p[9]));
		//assertTrue(b[7].contains(p[9]));
		//assertTrue(b[8].contains(p[9]));
		assertTrue(b[9].contains(p[9]));
		//assertTrue(b[4].contains(p[5]));
		assertTrue(b[5].contains(p[11]));
		assertTrue(b[5].contains(p[12]));
		//assertTrue(b[5].contains(p[6]));
		
	}

	/**
	 * Test method for {@link mosais.distribution.BoundingBox#intersects(mosais.distribution.BoundingBox)}.
	 */
	@Test
	public void testIntersects() {
		b[0].intersects(b[1]);
		b[1].intersects(b[1]);
		b[6].intersects(b[7]);
		b[6].intersects(b[8]);
		b[6].intersects(b[9]);
		b[7].intersects(b[6]);
		b[7].intersects(b[8]);
		b[7].intersects(b[9]);
		b[8].intersects(b[6]);
		b[8].intersects(b[7]);
		b[8].intersects(b[9]);
		b[9].intersects(b[6]);
		b[9].intersects(b[7]);
		b[9].intersects(b[8]);		
	}

	/**
	 * Test method for {@link mosais.distribution.BoundingBox#subdivide()}.
	 */
	@Test
	public void testSubdivide() {
		Map<Quadrant, TreeNodeModel> expected1 = new HashMap<Quadrant, TreeNodeModel>();
		Map<Quadrant, TreeNodeModel> expected2 = new HashMap<Quadrant, TreeNodeModel>();
		
		expected1.put(Quadrant.NW, new BoundingBox(5, 3, 1, 1));
		expected1.put(Quadrant.NE, new BoundingBox(6, 3, 1, 1));
		expected1.put(Quadrant.SW, new BoundingBox(5, 4, 1, 1));
		expected1.put(Quadrant.SE, new BoundingBox(6, 4, 1, 1));
		expected2.put(Quadrant.NW, new BoundingBox(3, 5, 2, 1));
		expected2.put(Quadrant.NE, new BoundingBox(5, 5, 2, 1));
		expected2.put(Quadrant.SW, new BoundingBox(3, 6, 2, 1));
		expected2.put(Quadrant.SE, new BoundingBox(5, 6, 2, 1));
		
		Map<Quadrant, TreeNodeModel> actual1 = b[3].subdivide();
		Map<Quadrant, TreeNodeModel> actual2 = b[4].subdivide();
		
		assertTrue(expected1.get(Quadrant.NW).equals(actual1.get(Quadrant.NW)));
		assertTrue(expected1.get(Quadrant.NE).equals(actual1.get(Quadrant.NE)));
		assertTrue(expected1.get(Quadrant.SW).equals(actual1.get(Quadrant.SW)));
		assertTrue(expected1.get(Quadrant.SE).equals(actual1.get(Quadrant.SE)));
		
		assertTrue(expected2.get(Quadrant.NW).equals(actual2.get(Quadrant.NW)));
		assertTrue(expected2.get(Quadrant.NE).equals(actual2.get(Quadrant.NE)));
		assertTrue(expected2.get(Quadrant.SW).equals(actual2.get(Quadrant.SW)));
		assertTrue(expected2.get(Quadrant.SE).equals(actual2.get(Quadrant.SE)));
	}

}
