import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Graphics;


public class SnakeApplet extends Applet {
	private SnakeCanvas c; 
	
	public void init() {
		//System.out.println("Calling init() method in SnakeApplet.java");
		c = new SnakeCanvas();
		c.setPreferredSize(new Dimension(800,600));
		c.setVisible(true);
		c.setFocusable(true);
		this.add(c);
		this.setVisible(true);
		this.setSize(new Dimension(800,600));
	
	}
	
	public void paint(Graphics g) {
		//System.out.println("Calling paint() method in SnakeApplet.java");
		this.setSize(new Dimension(800,600));
	}
}
