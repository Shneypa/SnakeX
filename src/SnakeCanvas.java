import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

// BUG:  VERY fast turning causes self-collision :( 



public class SnakeCanvas extends Canvas implements Runnable, KeyListener {

// ******************************* VARIABLES DECLARATION ****************
	
	private final int W = 10; 	// BOX WIDTH IN PIXELS
	private final int H  = 10; 	// BOX HEIGHT IN PIXELS
	private final int BW = 50; 	// GRID WIDTH IN BOXES
	private final int BH = 50;	// GRID HEIGHT IN BOXES
	 
	private LinkedList<Point> snake;	// Point is a built-in class which has (x,y) locations
	private ArrayList<Point> oranges = new ArrayList<Point>();	
	
	private Point fruit;
	
	private int direction = Direction.NO_DIRECTION;
	
	private Thread mainThread;
	
	private int score = 0;
	private String highscore = "Nobody: 0";
	
	private Image menuImage = null;
	private boolean isInMenu = true; 
	private boolean isAtEndGame = false; 
	private boolean won = false; 
	private final int endGameTextXOffset = 100;
	private final int endGameTextYOffset = 50;
							
	
	// *********************************** METHODS ********************
	
	
	
	// PAINTS CANVAS first time
	// creates and starts the Thread 
	public void paint(Graphics g) {
		
		if (mainThread == null) {
			this.addKeyListener(this);
			this.setPreferredSize(new Dimension(800,600));
			
			mainThread = new Thread(this);
			mainThread.start();
			
			oranges.add(new Point(10,10));	// if we have no oranges, shit crashes ... 
		}
		
		if (isInMenu){
			// we're on menu screen
			drawMenu(g);
			
		} else if(isAtEndGame) {
			// draw end game screen
			drawEndGame(g);
			
		} else{
		
		// if we're just starting the game
		if (snake == null) {
			snake = new LinkedList<Point>();
			generateDefaultSnake();
			placeFruit();
			placeOranges();
			
		}
						
		if (highscore.equals("Nobody: 0")) {
			// initialize highscore
			highscore = this.getHighScore();
			System.out.println("highscore: " + highscore);
		}
			drawGrid(g);				
			drawSnake(g);
			drawFruit(g);
			drawOranges(g);
			drawScore(g);
		}
	}
	
	
	
	// MENU SCREEN
	// getting MENU image from file and displaying it
	public void drawMenu(Graphics g) {
		
		// if we're loading image for the first time
		if(this.menuImage == null) 	{
			
			// loading image from file
			try {
				URL imagePath = SnakeCanvas.class.getResource("SnakeMenu.png");
				menuImage = Toolkit.getDefaultToolkit().getImage(imagePath);
				
				}catch(Exception e){
					// if file doesn't exist
					e.printStackTrace();
				}
			}
		
		// drawing image
		g.drawImage(menuImage, 0, 0, 800, 600, this);
			
	}
	
	
	
	// END GAME SCREEN
	public void drawEndGame(Graphics g) {
		
		BufferedImage endGameImage = new BufferedImage(this.getPreferredSize().width, this.getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics endGameGraphics = endGameImage.getGraphics();
		
		endGameGraphics.setColor(Color.BLACK); // !!!  Otherwise we couldn't see it !!!
		 
		
		if(won) 
			endGameGraphics.drawString("Yay to go Snake! Eat Everything! You WON !", this.getPreferredSize().width/2 - endGameTextXOffset, this.getPreferredSize().height/2 - endGameTextYOffset);
		 else
			endGameGraphics.drawString("Watch it! Handle your snake better next time!", this.getPreferredSize().width/2 - endGameTextXOffset, this.getPreferredSize().height/2 - endGameTextYOffset);
		
		
		endGameGraphics.drawString("Score " + this.score, this.getPreferredSize().width/2 - endGameTextXOffset, this.getPreferredSize().height/2 - endGameTextYOffset + 20);
	
		endGameGraphics.drawString("Press \"Space\" to play again", this.getPreferredSize().width/2 - endGameTextXOffset, this.getPreferredSize().height/2 - endGameTextYOffset + 40);
	
		g.drawImage(endGameImage, 0, 0, this);
	}
	
	
	
	// UPDATE
	public void update(Graphics g) {
		// default update method which will contain our double buffering 
		
		Graphics offScreenGraphics; 			// graphics we will use to draw offscreen
		BufferedImage offScreen = null;
		Dimension d = this.getSize(); 			// width and height of the applet
		
		offScreen = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		offScreenGraphics = offScreen.getGraphics();
		offScreenGraphics.setColor(this.getBackground());
		offScreenGraphics.fillRect(0, 0, d.width, d.height); 		// clearing the screen
		offScreenGraphics.setColor(this.getForeground()); 			// black by default
		
		paint(offScreenGraphics);									// painting to the offscreen canvas
		
		//flip to main screen
		g.drawImage(offScreen, 0, 0, this);
		
	}
	
	
	// CREATES DEFAULT SNAKE
	public void generateDefaultSnake() {
		snake.clear();
		score = 0;
		
		snake.add(new Point (20,20));
		snake.add(new Point (19,20));
		snake.add(new Point (18,20));
		snake.add(new Point (17,20));
		
		direction = Direction.EAST;
		
	}
	
	
	// PLACE FRUIT
	// replace old fruit with a newly random-generated (that is not on the snake)
	// we keep generating by RECURSION until its not on the snake. 
	public void placeFruit() {
		Random rand = new Random();
		
		int randX = rand.nextInt(BW);
		int randY = rand.nextInt(BH);
		
		Point newFruit = new Point(randX, randY);
		while (snake.contains(newFruit)) {
			placeFruit();  						// RARELY THROWS ERROR
		}
		fruit = newFruit;
	}
	
	// PLACE FRUITS (ORANGES)   - this method is for the List of extra fruits (oranges) 
		// replace old fruit with a newly random-generated (that is not on the snake)
		// we keep generating by RECURSION until its not on the snake. 
		public void placeOranges() {
			System.out.println("PLACE SOME ORANGES !!!");
			
			Random rand = new Random();
			
			int randX = rand.nextInt(BW);
			int randY = rand.nextInt(BH);
			
			System.out.println("PLACE SOME ORANGES !!! 2");
			
			Point newFruit = new Point(randX, randY);
			
			while (snake.contains(newFruit)) {
				placeOranges();  						// RARELY THROWS ERROR ? or is it resolved? 
			}
			
			System.out.println("PLACE SOME ORANGES !!! 3 " );
			
			System.out.println("newFruit(x,y)  = (" + newFruit.x + "," + newFruit.y + ")");
																									// ************************
			oranges.add(newFruit);																	// ************************
		// 	oranges.add(newFruit); 			// <<< CHANGE THIS LINE FOR TESTING ! (DELETE / UNDELETE) *************************************************************************************
																									// *************************
			System.out.println("PLACE SOME ORANGES !!! 4");											// ************************
			
			// but we have to delete the old fruit !!!
			System.out.println("PlaceOranges: Orange Created: coordinate X = " + oranges.get(0).x + " coordinate Y = " + oranges.get(0).y  );
		}
	
	// MOVE METHOD
	// Checks directions every 50ms and sets where the snake's head should be
	// then the draw() method is called and draws the snake	
	public void move() {
		// we check to see where our snake's head is and assign it to Point head
		Point head = snake.peekFirst();
		
		//we create new Point which will store the new head
		Point newPoint = head;
		
		// depending on what direction is moving, we create the new head in correct relation to where the current head is
		switch (direction) {
		case Direction.NORTH: 
			newPoint = new Point(head.x, head.y - 1);
			break;
		case Direction.SOUTH:
			newPoint = new Point(head.x, head.y + 1);
			break;
		case Direction.WEST:
			newPoint = new Point(head.x - 1, head.y);
			break;
		case Direction.EAST:
			newPoint = new Point(head.x + 1, head.y);
			break;
		}
		
		// deleting the tail
				snake.remove(snake.peekLast());
		
		// check if we're creating new head on a Tile where there is a fruit
		if (newPoint.equals(fruit)) {
			// the snake hit fruit, now snake should grow
			// also we increase score by 10
			score += 10;
			
			Point addPoint = (Point) newPoint.clone();
			
			switch (direction) {
			case Direction.NORTH: 
				newPoint = new Point(head.x, head.y - 1);
				break;
			case Direction.SOUTH:
				newPoint = new Point(head.x, head.y + 1);
				break;
			case Direction.WEST:
				newPoint = new Point(head.x - 1, head.y);
				break;
			case Direction.EAST:
				newPoint = new Point(head.x + 1, head.y);
				break;
			}
			snake.push(addPoint);
			placeFruit();
		
			
				// for ORANGES: 
		} else  // check if we're creating new head on a Tile where there are fruits (oranges) from the List of fruits (oranges) 
		 if (oranges.contains(newPoint)) {		
			// the snake hit fruit, now snake should grow
			// also we increase score by 10
			score += 10;
			
			Point addPoint = (Point) newPoint.clone();
			
			switch (direction) {
			case Direction.NORTH: 
				newPoint = new Point(head.x, head.y - 1);
				break;
			case Direction.SOUTH:
				newPoint = new Point(head.x, head.y + 1);
				break;
			case Direction.WEST:
				newPoint = new Point(head.x - 1, head.y);
				break;
			case Direction.EAST:
				newPoint = new Point(head.x + 1, head.y);
				break;
			}
			snake.push(addPoint);
			oranges.remove(newPoint);
			placeOranges();
			
			
			
		}
		else if (newPoint.x < 0 || newPoint.x > (BW - 1)){	
			
			// collision check (or rather we check that we went out of bounds on X axis)
			
			compareScore();
			won = false;
			isAtEndGame = true;
			//generateDefaultSnake(); - we now do it after VK_SPACE is pressed in the KeyPressed() method. 
			
			return;
			
		} else if (newPoint.y < 0 || newPoint.y > (BH - 1)) {
			
			// we went out of bounds on Y axis, this should reset game
			
			compareScore();
			won = false;
			isAtEndGame = true;
	
			return;
		} else if (snake.contains(newPoint)) {
			
			// we hit our own body (that point already exists in the snake List) 
			// this check has to happen after the tail is deleted!
		
			compareScore();
			won = false;
			isAtEndGame = true;
	
			return;
		}
		else if (snake.size() == BW * BH) {
			// Snake took all of the grid, we won !
			compareScore();
			won = true;
			isAtEndGame = true; 
			
		}
		// if we reach this point, we're still good!
		snake.push(newPoint);
	}
	
	
	
	// SAVING HIGHSCORE 
	public void compareScore() {
		if(highscore == "Nobody: 0") 
			return;
		
		// format:      Name: 100
		System.out.println("Current record: " + highscore);
		
		if (highscore.equals(""))
			return;
		
		// if user set a record
		if (score > Integer.parseInt(highscore.split(": ")[1])) {
		
			String name = JOptionPane.showInputDialog("You set new record! What is you name?");
			highscore = name + ": " + score;
			
			
			// now we want to SAVE new record into a file
				File scoreFile = new File("highscore.dat");
			
				// if file doesn't exist yet, we create one
					if (!scoreFile.exists()) {
						try {
							scoreFile.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				// now we want to write into the file
					FileWriter writeFile = null;
					BufferedWriter writer = null;
					
					try {
						writeFile = new FileWriter(scoreFile);
						writer = new BufferedWriter(writeFile);
						writer.write(this.highscore);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {					// closing the writer 
						if(writer!=null) {
							try {
								writer.close();		// if we don't close a file, then we get a windows error "file still in use" 
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
		 }
	}
	
	// READ HIGHSCORE from a file
		// format:     Shneypa: 100 
		public String getHighScore() {
			
			FileReader fileReader = null; 
			BufferedReader reader = null; 
			
			try {
				fileReader = new FileReader("highscore.dat");
				reader = new BufferedReader(fileReader);
				return reader.readLine(); 				// reading first line from file since our file just consists of 1 line
				
			} catch (Exception e) {
					return "Nobody yet: 0";
			} finally {					// closing the reader
					  	try {
					  		if(reader!=null) {
					  			reader.close();	
					  		}
					  		
					  	} catch (IOException e) {
					  		e.printStackTrace();
					  	  }
			  }
		}
	
	// DRAW SCORE
	public void drawScore(Graphics g) {
		g.drawString("Score: " + score, 10, H * BH + 15);
		g.drawString("High Score: " + highscore, 10, H * BH + 30);
	}
	
	
	// DRAW GRID
	public void drawGrid(Graphics g){
		
		// setting color for grid
		g.setColor(Color.BLACK);
		
		// drawing a rectangle
		g.drawRect(0, 0, W*BW, H*BH);
		
		
		// Uncomment to draw the gridlines:
		
		// drawing vertical lines of the grid
		/*for(int x = W; x < BW * W ; x += W) {
			g.drawLine(x, 0, x, H*BH);
		}
		
		// drawing horizontal lines of the grid
		for(int y = H; y < BH * H; y += H) {
			g.drawLine(0, y, W *BW , y);
		}*/
		
	}
	
	// DRAW SNAKE
	public void drawSnake(Graphics g) {
		g.setColor(Color.BLUE);
		for (Point p : snake) {
			g.fillRect(p.x * W, p.y * H, W, H);
		}
		g.setColor(Color.BLACK);
	}
	
	// DRAW FRUIT
	public void drawFruit(Graphics g) {
		g.setColor(Color.RED);
		g.fillOval(fruit.x * W, fruit.y * H, W, H);
		g.setColor(Color.BLACK);
	}
	
	// DRAW FRUITS (ORANGES) 
		public void drawOranges(Graphics g) {
			g.setColor(Color.ORANGE);
			
			for ( int i = 0 ; i < oranges.size(); i ++ )  {
				
				g.fillOval(oranges.get(i).x * W, oranges.get(i).y * H, W, H);
			
			}
			g.setColor(Color.BLACK);
		}


	
	// KEYBOARD CONTROLS  
	
	// (WASD and ARROW keys) 
	// Also we restrict going into the opposite direction to the current direction, 
	// so snake can't twist back and crash into itself
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		
		case KeyEvent.VK_UP: case KeyEvent.VK_W:
			if (direction!= Direction.SOUTH) {
				direction = Direction.NORTH; 
				}
			break;
					
		case KeyEvent.VK_DOWN: case KeyEvent.VK_S:
			if (direction!= Direction.NORTH) 
				direction = Direction.SOUTH;
			break;
		
		case KeyEvent.VK_RIGHT: case KeyEvent.VK_D:
			if (direction!= Direction.WEST) 
				direction = Direction.EAST;
			break;

		case KeyEvent.VK_LEFT: 	case KeyEvent.VK_A:
			if (direction!= Direction.EAST)
				direction = Direction.WEST;
			break;
			
		case KeyEvent.VK_ENTER:
			if(isInMenu) {
				isInMenu = false; 	// get out of menu screen
				repaint();			// initialize everything
				}					
			break;
			
		case KeyEvent.VK_ESCAPE:
			isInMenu = true;
			break;
	
		case KeyEvent.VK_SPACE: 
			if(isInMenu) {				//
				isInMenu = false; 		// same as Enter if we're on menu screen
				repaint(); }			//
				if(isAtEndGame) {
					isAtEndGame = false;
					won = false;
					generateDefaultSnake();
					placeFruit();
					repaint();
			} break;
		}
	}

	public void keyReleased(KeyEvent e) {}
	
	public void keyTyped(KeyEvent e) {}
	

	
	// RUN METHOD
		public void run() {
		
			
			while (true) {					// runs indefinitely
											
				repaint(); 					// calls UPDATE and PAINT methods

				
				if(!isInMenu && !isAtEndGame && (direction != Direction.NO_DIRECTION))				// if we're still in menu screen, we won't call move() method yet. 
					move();	
				
			
				
				// time buffer to slow down execution
				try{
					Thread.currentThread();
					Thread.sleep(50); // 50 millisecond delay  =  game will run at 20 FPS
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

}
