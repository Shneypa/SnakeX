/*
 * 						*******************
 * 						**	SNAKE GAME	 **
 * 						*******************
 * 
 * 
 *****************************************************************
 * 1.  PLANNING.  GENERAL MECHANICS.  WHAT WE NEED IN THIS PROJECT.  
 * *************
 * 
*
	CANVAS, on which everything will be drawn
	GRID, on which all the movement will happen
	
	GRID can be filled with one of the TILE TYPES:
		SNAKE_BODY_SQUARES
		FRUIT_SQUARES
		EMPTY_SQUARES
	
	SNAKE itself is a LINKED LIST of POINTS
	
	MOVEMENT of SNAKE will be created by CREATING a new HEAD for the SNAKE and DELETING last box of its TAIL
	
	DIRECTION variable is also needed so we know WHERE this NEW HEAD should be CREATED
	
	Before we "create new head" we need to CHECK that SQUARE for:
		- FRUIT (snake gets longer, tail square is not deleted this time)
		- SNAKE_BODY_SQUARE (snake crashes into itself, game over)
		- GAME AREA WALLS (snake crashes into the wall, game over) 
		- EMPTY (create new head, delete last tale square) 
	
*/

/*
	GAME LOOP is made on a separate THREAD
	
	Everything is created when PAINT method of the CANVAS is called
	its like a constructor for CANVAS because it paints the CANVAS
	
	so in the paint method we also initialize our snake (as linked list of Points), fruit (as a Point), globalGraphics and the THREAD (if its still uninitialized) 
*
 * 
 * 
 * ****************
 * 2. CONCEPTS USED
 * ****************
 * 
 * CANVAS & GRID
 * 			   GRID is our "game area". 
 * 
 * 
 * LINKED LIST
 *				Snake is a linked list. 
 *				When it moves, we create a new head and delete a tail. 
 * 				We create a new head based on the direction variable.
 * 				When snake lands on fruit, we add one point to snake's head
 * 				
 * 				If new point is out of bounds, we lose.
 * 				If new point is already within the list, it means snake crashed into its own body.
 * 
 * RANDOM GENERATOR
 * 				Randomly generates location of new fruit.
 * 	
 * RECURSIVE calling of RANDOM GENERATOR
 * 				Keeps generating fruit until it's in a spot not occupied by snake body. 
 * 
 * KEY LISTENER
 * 				Listens to which keys user is pressing.
 * 				Changes direction variable accordingly. 
 * 
 * SWITCH
 * 				Specifies how to move for every case of direction variable
 * 
 * THREAD
 *				We use a thread to have an update() and paint() methods.
 *				Also to be able to set a delay e.g. 50 milliseconds (so snake doesn't travel at the speed of light)
 * 
 * DOUBLE BUFFERING
 * 
 * 				We paint new image onto a Buffer Canvas first. 
 * 				Then we place new image on our main Canvas. 
 * 				This prevents flashing or other graphical bugs. 
 * 
 * WRITING INTO FILE and READING FROM FILE
 * 
 * 				To store highscore and see last record when game starts.
 * 
 * EXCEPTION HANDLING
 * 				
 * 				Extensively used when writing and reading files. 
 * 				Also remember, that reader and writer have to be closed !!!
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 *  * ****************
 * BUGS, ERRORS and FIXES
 * ****************
 * 
 * ACTIVE:
 * ******
 * When snake is generated, but hasn't started moving yet... the Move method is still being run continuously and the compareScore() keeps being called...
 * So in console it keeps printing High Score. 
 * 
 * If you make a VERY FAST turn you somehow can still crash into yourself even with minimal snake size (3 body segments). 
 * 
 * RARE ERROR: placeFruit()
 * 
 * 
 * 
 * 
 * FIXED:
 * ******
 * 1) Snake could crash into itself if direction changed to opposite. 
 * Fixed by restricting that in the keyPressed() method.  example:    if(direction != NORTH)  etc... 
 * 
 * 2) Screen flashing cured by correct double buffering
 * 
 */

