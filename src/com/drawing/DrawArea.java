package com.drawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class DrawArea extends JComponent{

	//image in which we're going to draw
	private Image image;
	//Graphics2D object > used to draw on
	public Graphics2D g2;
	//Mouse coordinates
	public int thickness = 1;

	public DrawArea() {	
		setDoubleBuffered(false);
	}
	
	protected void paintComponent(Graphics g) {
		if(image == null) {
			image = createImage(2000, 2000);							//image			> createImage   > drawImage
			g2 = (Graphics2D) image.getGraphics();						//Graphics2D 	> getGraphics	> get graphic context 	
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setStroke(new BasicStroke(thickness));
			clear();
		}
		g.drawImage(image, 0, 0, null);
	}
	
	//new we create exposed methods
	public void clear() {
		g2.setPaint(Color.white);
		g2.fillRect(0, 0, getSize().width, getSize().height);
		g2.setPaint(Color.black);
		repaint();
	}
	
	public void red() {
		//apply red color on g2 context
		g2.setPaint(Color.red);
	}
	public void black() {
		g2.setPaint(Color.black);
	}
	public void magenta() {
		g2.setPaint(Color.magenta);
		}
	public void green() {
		g2.setPaint(Color.green);
	}
	public void blue() {
		g2.setPaint(Color.blue);
	}
	
	public void white() {
		g2.setPaint(Color.WHITE);
	}
	public void mixed(int red, int blue, int green) {
		g2.setPaint(new Color(red, blue, green));
	}
	public void thickPlus() {
		if(thickness < 30) {
			thickness = thickness+2;
			g2.setStroke(new BasicStroke(thickness));
		}
	}
	public void thickMinus() {
		if(thickness > 1) {
			thickness = thickness-2;
			g2.setStroke(new BasicStroke(thickness));
		}
	}
}
