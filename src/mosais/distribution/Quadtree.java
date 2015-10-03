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

import java.util.*;

/**
 * A tree data structure where every inner node has exactly four children. Used to 
 * model two dimensional areas, recursively breaking the area down in quadrants.
 * 
 * Data elements can only be stored at the leaf nodes. Whenever the node is divided,
 * all data points stored at that node must be distributed to the child nodes.
 *
 * @author Andrew Trismen
 * @param E The type of data element stored in this quadtree.
 */
public class Quadtree<E> {
	
	/**
	 * The 4 children of a quad tree node, pictured as geographic quadrants. 
	 * Can represent any model consisting of 4 subparts of a whole.
	 *
	 * @author Andrew Trismen
	 */
	public enum Quadrant {
		NW, NE, SW, SE
	}
    
    /** Child nodes of this quad tree node. Null if this node is a leaf node */
    private Map<Quadrant, Quadtree<E>> children;
    
    /** Parent of this node. Null if this is the root node */
    private Quadtree<E> parent;
    
    /** List of all points currently located at this node. Null if this subdivides */
    private List<E> data;
    
    /** An object that represents the domain modeled by this quadtree node. */
    private TreeNodeModel model;
    
    /** Maximum number of elements to be stored at this node before splitting. */
    private int maxElements;
    
    /**
     * Construct a new quadtree with the given parent node, or null if this node is the 
     * root of the tree.
     * 
     * @param parent Parent node of this quadtree node, or null if this is root.
     */
    public Quadtree(Quadtree<E> parent, TreeNodeModel model, int maxElements) {
    	this.parent = parent;
    	this.model = model;
    	this.maxElements = maxElements;
    	//prepare this node to store data.
    	this.data = new ArrayList<E>();
    }
    
    /**
     * Get the model associated with this node.
     * 
     * @return The model of this node.
     */
    public TreeNodeModel getModel() {
    	return model;
    }
    
    /** 
     * Return a List of all elements stored at this node, or null if this is an
     * inner node.
     * 
     * @return a List containing all of the elements at this node, or null if this 
     * is an inner node.
     */
    public List<E> getData() {
    	return data;
    }
    
    /**
     * Return the parent of this tree node.
     * @return The parent of this node, or null if this node is root.
     */
    public Quadtree<E> getParent() {
    	return parent;
    }
    
    /**
     * Check if this node contains children or is a leaf node.
     * @return True if this node contains child nodes, false otherwise.
     */
    public boolean hasChildren() {
    	return !(children == null);
    }
    
    /**
     * Return the children of this node, or null if this node is a leaf.
     * @return The map of children in this node, or null if this is a leaf node.
     */
    public Map<Quadrant, Quadtree<E>> getChildren() {
    	return children;
    }
    
    /**
     * Get a specific child node by quadrant.
     * 
     * @param q Quadrant of the requested child node.
     * @return The child node associated with the given quadrant.
     * @throws NullPointerException If this node does not contain children.
     */
    public Quadtree<E> getChildNode(Quadrant q) throws NullPointerException {
    	
    	if (children == null) {
    		throw new NullPointerException("This is a leaf node.");
    	}
    	
    	return children.get(q);
    	
    }
    
    /**
     * Insert the given element into the quadtree. 
     * If this is a leaf node, insert here.
     * If this is an inner node, insert into the apporpriate child node.
     * 
     * If inserting the element exceeds the maximum data store for this node, spawn
     * 
     * @param element Element to be inserted.
     */
    public void insert(E element) {
    	// no children == leaf node, insert here
    	if (!hasChildren()) {
    		data.add(element);
    		
    		//Check if node should be subdivided
    		if (data.size() > maxElements) {
    			spawnChildNodes();
    		}
    		
    	//Child nodes, delegate insertion to appropriate child	
    	} else {
    		for (Quadrant q : Quadrant.values()) {    			
    			if (children.get(q).isMember(element)) {
    				children.get(q).insert(element);
    			}
    		}
    	}
    }
    
    /**
     * Check if the given element is within the domain of this quadtree node's model.
     * 
     * @param element Element to check for membership in the modeled domain.
     * @return True if element is a member of the domain, false otherwise.
     */
    public boolean isMember(E element) {
    	return model.isMember(element);
    }

    /**
     * Create children nodes of this node, distributing all data contained in this node 
     * to the appropriate children.
     * 
     * Client code must call getData() and determine mapping before calling this method.
     */
    private void spawnChildNodes() {
    	
    	//initialize the collection of children and get subparts of the model
    	children = new HashMap<Quadtree.Quadrant, Quadtree<E>>();
    	Map<Quadrant, TreeNodeModel> modelSubParts = model.subdivide();
    	
    	//create a new node for each quadrant with respective model subpart
    	for (Quadrant quad : Quadrant.values()) {    		
    		Quadtree<E> child = new Quadtree<E>(this, modelSubParts.get(quad), this.maxElements);
    		   		
    		//Walk list of data points, moving those that belong in this subnode
    		Iterator<E> i = data.iterator();
    		while (i.hasNext()) {
    			E elem = i.next();
    			if (child.isMember(elem)) {
    				child.insert(elem);
    				i.remove();
    			}
    		}
    		//insert child node into this nodes children list
    		children.put(quad, child);
    	}
    	//remove all data from current node.
    	data = null;    	
    }
}
