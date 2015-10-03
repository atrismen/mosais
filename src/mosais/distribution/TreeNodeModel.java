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

import java.util.Map;
import mosais.distribution.Quadtree.Quadrant;

/**
 * This is an interface for quadtrees to communicate with the concept they're
 * modelling without needing to know what exactly they are modelling. A tree
 * node model can be anything that can be recursively subdivided into 4 discrete 
 * subparts, and thus a child quadtree node would model one of these subparts.  
 *
 * @author Andrew Trismen
 */
public interface TreeNodeModel {
	
	/**
	 * Return true if the given object is within the domain of this model
	 * 
	 * @param o Object to check for containment in this domain.
	 * @return True if o is part of this model domain, false otherwise.
	 */
	boolean isMember(Object o);
	
	/**
	 * Divide this model into 4 discrete components and assign each component
	 * to a quadrant that can map to quadtree child nodes.
	 * 
	 * @return A map of child models of this model keyed to quadtree quadrants.
	 */
	Map<Quadrant, TreeNodeModel> subdivide();
	
	/**
	 * Test if a given domain fully or partially overlaps this domain. Used for
	 * searching based on domain.
	 * 
	 * @param o Other domain to test against.
	 * @return True if o overlaps this domain, false otherwise.
	 */
	boolean isCodomain(TreeNodeModel o);
}
