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
package mosais.GUI;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class DrawPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	/** the image being drawn */
	BufferedImage image;
	
	public DrawPanel(BufferedImage image) {
		this.image = image;
	}
	
	/**
	 * Redraw this component
	 * @param g Where to draw
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, null);
	}
	
}
