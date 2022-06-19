/*
By Alisa Wu, Teacher: Mr Guglielmi
June 22, 2021
Desc: Classic minesweeper game. The objective is to uncover all squares on a 10 x10 grid that do not contain bombs. For the grade 11 computer science ISP.
*/

import java.awt.image.*; //imports images
import javax.imageio.ImageIO;
import java.awt.*;
import hsa.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class Minesweeper
{
    //general game data
    Console c;
    boolean isMenu = true;
    int gameState = 0; //0: default, 1: won, 2: lost

    //leaderboard data
    String[] playerName = new String [11];
    int[] playerScore = new int [11];
    int[] [] flags = new int [11] [2];
    long begin;

    //queue data
    int start = 0; //start of visited queue
    int end = 0; //end of visited queue
    boolean[] [] visited = new boolean [11] [11]; //queue of coords to check surroundings
    
    Color txt = new Color(113,139,235);
    Color title = new Color(30,40,150);
    Color bg = new Color(87,104,225);
    Color g = new Color(92,156,46);
    Color r = new Color(203,53,116);

    int[] [] board = {
	    { - 2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, },
	    { - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
	    { - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
	    { - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
	    { - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
	    { - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
	    { - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
	    { - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
	    { - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
	    { - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
	    { - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
	    { - 2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, }
	};

    int[] [] openedBoard = {
	    { - 2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, },
	    { - 2, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -2, },
	    { - 2, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -2, },
	    { - 2, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -2, },
	    { - 2, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -2, },
	    { - 2, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -2, },
	    { - 2, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -2, },
	    { - 2, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -2, },
	    { - 2, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -2, },
	    { - 2, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -2, },
	    { - 2, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -2, },
	    { - 2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, }
	};

    Minesweeper () //oop
    {
	c = new Console (40, 100);
    }


    public static void main (String[] args)  //main method
    {
	Minesweeper d = new Minesweeper ();
	d.openFile();
	d.menuControls ();
    }


    public void openFile ()  //opens class file
    {
	BufferedReader input;
	boolean fileExists = false;
	boolean isFileValid = false;

	try
	{
	    File file = new File ("Leaderboard"); ///checks if file exists
	    fileExists = file.exists ();
	    if (fileExists)
	    {
		input = new BufferedReader (new FileReader ("Leaderboard"));

		String line = "";
		isFileValid = fileValid ();

		if (isFileValid) // if file has identifying file header
		{

		    int count = 0;

		    line = input.readLine (); //skips header and class size lines
		    line = input.readLine ();

		    while (line != null) //stores student names and marks until there are no more lines to read
		    {
			line = input.readLine ();

			String[] words = line.split ("\\ ");
			playerName [count] = words [0];
			playerScore [count] = Integer.parseInt (words [1]);
			count++;
		    }
		}

	    }
	}


	catch (Exception e)
	{
	}
    }


    public void menuControls ()  // menu keyboard controls
    {
	int y_menu = 200;
	char keybInput = '|';

	while (isMenu)
	{
	    resetBoard ();        
	    if (keybInput == '|')
	    {
		menuDisplay (y_menu);
	    }
	    keybInput = Character.toLowerCase (c.getChar ());

	    switch (keybInput)
	    {
		case ' ': //select option
		    menuSelector (y_menu);
		    break;
		case 'w': //move up
		    if (y_menu > 200 && y_menu != 200)
			y_menu -= 100;
		    break;
		case 's': //mpve down
		    if (y_menu <= 400)
			y_menu += 100;
		    break;
	    }
	    if (isMenu)
		menuDisplay (y_menu);

	}
    }


    /*
    Private------------------------------------------------------------------------------------
    */

    
    //board update methods
    private void drawOpened ()  //displays tile number and tile graphic
    {
	c.setColor (txt);
	try
	{
	    BufferedImage uncoveredTile = ImageIO.read (new File ("uncoveredTile.png"));
	    BufferedImage coveredTile = ImageIO.read (new File ("coveredTile.png"));

	    for (int x = 1 ; x < 11 ; x++)
	    {
		for (int y = 1 ; y < 11 ; y++)
		{
		    if (openedBoard [x] [y] != -3)
		    {

			c.drawImage (uncoveredTile, 100 + (x - 1) * 60, 100 + (y - 1) * 60, null);
			c.setFont (new Font ("SansSerif", Font.PLAIN, 30));
			String text = Integer.toString (board [x] [y]);

			if (!"0".equals (text))
			{
			    c.drawString (text, 123 + (x - 1) * 60, 143 + (y - 1) * 60);
			}
			
		    }
		    if (openedBoard [x] [y] == -3) 
		    {
			c.drawImage (coveredTile, 100 + (x - 1) * 60, 100 + (y - 1) * 60, null);
		    }

		}

	    }
	}
	catch (IOException e)
	{
	}

    }


    private void uncoveredBoard (int x, int y)  //finds all empty and numeric values attached to the center tile
    {

	if (board [x] [y] == 9) //if tile chosen is 9 (bomb) then it changes gameState to loss
	{
	    gameState = 2;
	}
	if (gameState == 0) //if gameState is default, continue onto method
	{
	    int[] [] queue = new int [1000] [2];
	    boolean noFlags = true; 
	    start = 0; //start of queue
	    end = 0; //end of queue

	    int counter1 = 0;
	    while (counter1 < 10 && noFlags) //checks if tile is flag
	    {
		if (flags [counter1] [0] == x && flags [counter1] [1] == y)
		{
		    noFlags = false;

		}
		counter1++;
	    }

	    //puts coordinates in queue
	    queue [0] [0] = x;
	    queue [0] [1] = y;
	    end++;


	    if (noFlags) //starts queue if tile isn't a flag
	    {
		while (start < end) //if the start variable is greater than the end (no more coords appended to end) then stop
		{
		    queue = checkNearby (queue); //keeps on adding coords to queue until there are no more coords that fit the criteria

		}

	    }


	    checkBombs (); //checks tiles left on the board
	    drawOpened (); // graphics for opened tiles
	}

    }
    
     private void checkBombs ()  //checks number of tiles left on board
    {
	int bombs = 0;
	for (int x = 1 ; x < 11 ; x++)
	{
	    for (int y = 1 ; y < 11 ; y++)
	    {
		if (openedBoard [x] [y] == -3)
		    bombs++;

	    }

	}


	if (bombs == 10) //if the user has made it to 10 tiles left, then it is guaranteed the last 10 are all bombs, so the user wins
	    gameState = 1;
    }



    private int[] [] checkNearby (int[] [] queue)  //checks nearby 3x3
    {
	int x = queue [start] [0];
	int y = queue [start] [1];
	start++; //the start of the queue


	int[] [] uncheckedxy1 = new int [100] [2];
	int uncheckedx = 0;
	int uncheckedy = 0;
	if (board [x] [y] == 0) //if tile is empty then check adj
	{
	    openedBoard [x] [y] = board [x] [y];
	    if (board [x] [y - 1] == 0 && openedBoard [x] [y - 1] != 0 && !visited [x] [y - 1]) //#1 checks top adjacent tile
	    {
		queue [end] [0] = x; //appends new coords to end of queue
		queue [end] [1] = y - 1;
		openedBoard [x] [y - 1] = 0; //records the value of the tile
		visited [x] [y - 1] = true; //records that the tile is visited
		end++;


	    }
	    else if (board [x] [y - 1] > 0 && board [x] [y - 1] < 9 && openedBoard [x] [y - 1] == -3) //if tile is non-zero then record value of tile
	    {
		openedBoard [x] [y - 1] = board [x] [y - 1];
	    }
	    if (board [x - 1] [y] == 0 && openedBoard [x - 1] [y] != 0 && !visited [x - 1] [y] && !visited [x - 1] [y]) //#2 checks left adjacent tile
	    {
		queue [end] [0] = x - 1;
		queue [end] [1] = y;
		openedBoard [x - 1] [y] = 0;
		visited [x - 1] [y] = true;
		end++;


	    }
	    else if (board [x - 1] [y] > 0 && board [x - 1] [y] < 9 && openedBoard [x - 1] [y] == -3) //if tile is non-zero then record value of tile
	    {
		openedBoard [x - 1] [y] = board [x - 1] [y];
	    }
	    if (board [x + 1] [y] == 0 && openedBoard [x + 1] [y] != 0 && !visited [x + 1] [y]) //#3 checks right adjacent tile
	    {
		queue [end] [0] = x + 1;
		queue [end] [1] = y;
		openedBoard [x + 1] [y] = 0;
		visited [x + 1] [y] = true;
		end++;


	    }
	    else if (board [x + 1] [y] > 0 && board [x + 1] [y] < 9 && openedBoard [x + 1] [y] == -3) //if tile is non-zero then record value of tile
	    {
		openedBoard [x + 1] [y] = board [x + 1] [y];
	    }

	    if (board [x] [y + 1] == 0 && openedBoard [x] [y + 1] != 0 && !visited [x] [y + 1]) //#4 checks bottom adjacent tile
	    {
		queue [end] [0] = x;
		queue [end] [1] = y + 1;
		openedBoard [x] [y + 1] = 0;
		visited [x] [y + 1] = true;
		end++;

	    }
	    else if (board [x] [y + 1] > 0 && board [x] [y + 1] < 9 && openedBoard [x] [y + 1] == -3) //if tile is non-zero then record value of tile
	    {
		openedBoard [x] [y + 1] = board [x] [y + 1];
	    }


	    if (board [x - 1] [y - 1] == 0 && openedBoard [x - 1] [y - 1] != 0 && !visited [x - 1] [y - 1]) //top-left diagonal tile
	    {
		queue [end] [0] = x - 1;
		queue [end] [1] = y - 1;
		openedBoard [x - 1] [y - 1] = 0;
		visited [x - 1] [y - 1] = true;
		end++;

	    }
	    else if (board [x - 1] [y - 1] > 0 && board [x - 1] [y - 1] < 9 && openedBoard [x - 1] [y - 1] == -3) //if tile is non-zero then record value of tile
	    {
		openedBoard [x - 1] [y - 1] = board [x - 1] [y - 1];
	    }

	    if (board [x + 1] [y - 1] == 0 && openedBoard [x + 1] [y - 1] != 0 && !visited [x + 1] [y - 1]) //top-right diagonal tile
	    {
		queue [end] [0] = x + 1;
		queue [end] [1] = y - 1;
		openedBoard [x + 1] [y - 1] = 0;
		visited [x + 1] [y - 1] = true;
		end++;

	    }
	    else if (board [x + 1] [y - 1] > 0 && board [x + 1] [y - 1] < 9 && openedBoard [x + 1] [y - 1] == -3) //if tile is non-zero then record value of tile
	    {
		openedBoard [x + 1] [y - 1] = board [x + 1] [y - 1];
	    }
	    if (board [x - 1] [y + 1] == 0 && openedBoard [x - 1] [y + 1] != 0 && !visited [x - 1] [y + 1]) //bottom-leftdiagonal tile
	    {
		queue [end] [0] = x - 1;
		queue [end] [1] = y + 1;
		openedBoard [x - 1] [y + 1] = 0;
		visited [x - 1] [y + 1] = true;
		end++;
	    }
	    else if (board [x - 1] [y + 1] > 0 && board [x - 1] [y + 1] < 9 && openedBoard [x - 1] [y + 1] == -3) //if tile is non-zero then record value of tile
	    {
		openedBoard [x - 1] [y + 1] = board [x - 1] [y + 1];
	    }

	    if (board [x + 1] [y + 1] == 0 && openedBoard [x + 1] [y + 1] != 0 && !visited [x + 1] [y + 1]) //bottom-right diagonal tile
	    {
		queue [end] [0] = x + 1;
		queue [end] [1] = y + 1;
		openedBoard [x + 1] [y + 1] = 0;
		visited [x + 1] [y + 1] = true;
		end++;
	    }
	    else if (board [x + 1] [y + 1] > 0 && board [x + 1] [y + 1] < 9 && openedBoard [x + 1] [y + 1] == -3) //if tile is non-zero then record value of tile
	    {
		openedBoard [x + 1] [y + 1] = board [x + 1] [y + 1];
	    }
	}


	else if (board [x] [y] > 0 && board [x] [y] < 9) //if selelected tile is a non-zero then record tile as opened
	{
	    openedBoard [x] [y] = board [x] [y];
	}

	return queue;
    }

    //graphics methods


    private void displayGraphics (int x, int y, char bf)  //displays bomb or flag
    {
	int counter = 0;
	int counter1 = 0;
	boolean flagFound = false; //removes flag if flag is already on board
	try
	{
	    BufferedImage flag = ImageIO.read (new File ("flag.png"));



	    if (bf == '0' || bf == 'f')
	    {
		while (flags [counter] [0] != 0) //draws existing flags
		{

		    c.drawImage (flag, (flags [counter] [0] - 1) * 60 + 105, (flags [counter] [1] - 1) * 60 + 105, null);
		    counter++;
		}

		if (bf == 'f') //stores/draws new flag
		{
		    while (counter1 < 10 && !flagFound)
		    {
			if (flags [counter1] [0] == x && flags [counter1] [1] == y)
			{
			    flags [counter1] [0] = 0;
			    flags [counter1] [1] = 0;
			    flagFound = true;
			}
			counter1++;
		    }
		    if (openedBoard [x] [y] == -3 && counter < 10)
		    {
			flags [counter] [0] = x;
			flags [counter] [1] = y;

			c.drawImage (flag, (flags [counter] [0] - 1) * 60 + 105, (flags [counter] [1] - 1) * 60 + 105, null);
		    }


		}

	    }
	    if (bf == 'b') //reveals all bombs on board
	    {
		drawOpened();
		BufferedImage bomb = ImageIO.read (new File ("bomb.png"));
		for (int x_value = 1 ; x_value < 11 ; x_value++)
		{
		    for (int y_value = 1 ; y_value < 11 ; y_value++)
		    {
			if (board [x_value] [y_value] == 9)
			{
			    c.drawImage (bomb, (x_value - 1) * 60 + 105, (y_value - 1) * 60 + 105, null);
			    try
			    {
				Thread.sleep (500); //slows down animation
			    }
			    catch (Exception e)
			    {
			    }
			}
		    }
		}
	    }
	}


	catch (IOException e)
	{

	}


	c.setColor (bg);
	c.fillRect (625, 45, 180, 45);
	c.setColor (Color.white);
	c.setFont (new Font ("SansSerif", Font.PLAIN, 30));
	c.drawString ("" + (10 - counter), 630, 80);
	
    }
    
    private void playDisplay ()  //displays empty board
    {
	try
	{
	    BufferedImage emptyBoard = ImageIO.read (new File ("emptyBoard.png"));
	    c.drawImage (emptyBoard, 100, 100, null);
	}
	catch (IOException e)
	{
	}
    }
    
    private void background ()  //background
    {
	try
	{
	    BufferedImage background = ImageIO.read (new File ("background.png"));

	    c.drawImage (background, 0, 0, null);
	}


	catch (IOException e)
	{
	}
    }

    //leaderboard file methods


    private void enterName ()  //enter name for leaderboard
    {
	long stop = System.currentTimeMillis (); //stops timer
	String name;
	long score = -1;

	background (); //graphics and text
	c.setColor (Color.white);
	c.fillRect (250, 395, 300, 30);
	c.setFont (new Font ("SansSerif", Font.PLAIN, 30));
	c.drawString ("Enter your name: ", 250, 375);

	switch (gameState)
	{
	    case 1: //won
		c.setColor (g);
		c.setFont (new Font ("SansSerif", Font.PLAIN, 60));
		c.drawString ("YOU WON", 250, 290);
		score = (stop - begin) / 1000; //records difference of stop of timer and beginning and converts to sec
		break;
	    case 2: //loss
		c.setColor (r);
		c.setFont (new Font ("SansSerif", Font.PLAIN, 60));
		c.drawString ("YOU LOST", 250, 290);
		score = -1;
		break;
	}

	c.setCursor (21, 33);
	name = c.readLine (); //reads name
	addLeaderboard (name, score); //adds new data to leaderboard
	saveFile (); //saves leaderboard
	isMenu = true;
    }


    private void addLeaderboard (String name, long score)  //adds data to leaderboard
    {
	int i = 0;
	boolean scoreAdded = false; //stops while loop if score already is added

	while (i < 10 && !scoreAdded)
	{
	    if ((score != -1 && score < playerScore [i]) || (score != -1 && playerScore [i] == -1)) //if all 10 leaderboard spots are taken
	    {
		moveLeaderboard (i);
		playerName [i] = name;
		playerScore [i] = (int) score;
		scoreAdded = true;

	    }
	    else if (playerName [i] == null) //if score is not high enough and theres space in the leaderboard, it will fit the lowest avalible spot
	    {
		playerName [i] = name;
		playerScore [i] = (int) score;
		scoreAdded = true;
	    }
	    i++;
	}
    }


    private void moveLeaderboard (int position)  //moves leaderboard down to fit new score
    {
	String lastPlayerName = null;
	int lastPlayerScore = -1;

	for (int i = 10 ; i > position ; i--)
	{
	    if (playerName [i] == null) //fits lowest empty space
	    {
		lastPlayerName = playerName [i];
		lastPlayerScore = playerScore [i];
	    }
	    try
	    {
		if (playerName [i] != null) //if there are no empty spaces on the leaderboard, the leaderboard removes the worst score to fit better score

		    {
			playerName [i] = playerName [i - 1];
			playerScore [i] = playerScore [i - 1];
		    }
	    }
	    catch (Exception e)
	    {
	    }

	}
    }


    private void saveFile ()  //saves leaderboard file
    {
	PrintWriter output;
	try
	{
	    output = new PrintWriter (new FileWriter ("Leaderboard")); //creates new printwriter to write to file

	    output.println ("mwpr"); //writes file header and class size
	    output.println ("10");

	    int i = 0;
	    while (i < 10 && playerName [i] != null)
	    {
		output.println (playerName [i] + " " + playerScore [i]);
		i++;
	    }
	    output.close (); //saves
	}


	catch (IOException e)
	{

	}
    }


    private boolean fileValid ()  //checks if file is valid
    {
	boolean valid = false;
	String fileHeader;
	try
	{
	    String line = "";
	    BufferedReader in = new BufferedReader (new FileReader ("Leaderboard")); //creates new file reader

	    fileHeader = in.readLine ();
	    line = in.readLine ();
	    if ("mwpr".equals (fileHeader)) //if first line is mwpr, then lsize is set to the right  size
	    {
		valid = true;
	    }
	}
	catch (Exception e)
	{
	}
	return valid;
    }

    //play, instructions, highscores and exit methods
    
    private void play ()  //controls what the game does for each gameState
    {
	char gridSelect = '|';

	playDisplay (); //displays empty game board

	int x_grid = 1;
	int y_grid = 1;
	
	boolean firstTile = true;

	try
	{
	    BufferedImage background = ImageIO.read (new File ("flag.png"));

	    c.drawImage (background, 575, 50, null);
	}


	catch (IOException e)
	{
	}
	
	c.setColor (bg); //flags left info
	c.fillRect (625, 45, 180, 45);
	c.setColor (Color.white);
	c.setFont (new Font ("SansSerif", Font.PLAIN, 30));
	c.drawString ("" + 10, 630, 80);

	for (int x = 0 ; x < 12 ; x++) //creates brand new randomized board
	{
	    for (int y = 0 ; y < 12 ; y++)
	    {
		newBoard (x, y);
	    }
	}


	while (gameState == 0) //default game state
	{
	    if (gridSelect == '|')
	    {
		c.setColor (r);
		c.drawRect (100, 100, 60, 60);
	    }

	    gridSelect = Character.toLowerCase (c.getChar ());
	    drawOpened ();
	    displayGraphics (0, 0, '0');
	    switch (gridSelect)
	    {
		case '1':
		    if (firstTile) 
		    {
			newBoard(x_grid,y_grid);
			firstTile = false;
		    }
		    uncoveredBoard (x_grid, y_grid);
		    displayGraphics (x_grid, y_grid, 'f');
		    break;
		case '2':
		    displayGraphics (x_grid, y_grid, 'f');
		    break;
		case 'w':
		    if (y_grid > 1) //makes sure tile selector doesn't go out of bounds
		    {
			y_grid--;
		    }
		    break;
		case 'a':
		    if (x_grid > 1) //makes sure tile selector doesn't go out of bounds
		    {
			x_grid--;
		    }
		    break;
		case 's':
		    if (y_grid < 10) //makes sure tile selector doesn't go out of bounds
		    {
			y_grid++;
		    }
		    break;
		case 'd':
		    if (x_grid < 10) //makes sure tile selector doesn't go out of bounds
		    {
			x_grid++;
		    }
		    break;
	    }
	    c.setColor (r); //tile selector
	    c.drawRect (100 + (x_grid - 1) * 60, 100 + (y_grid - 1) * 60, 60, 60);
	}

	if (gameState == 1) //won
	{
	    enterName ();
	}

	else if (gameState == 2) //loss
	{
	    displayGraphics (0, 0, 'b');
	    enterName ();
	}
    }

    private void instructions ()  //instructions
    {
	String[] text = {"The objective is to uncover all squares on a 10 x10 grid that", "do not contain bombs. The number on a grid means that there ", "are that amount of bombs nearby around (squares, adjacent", "and diagonal). Press WASD to move to and from each square", " ", "Use 1 to reveal the square selected", "Use 2 to place / remove a flag", " ", "Press space to go back to the menu"};
	background ();
	c.setColor (title);
	c.setFont (new Font ("SansSerif", Font.PLAIN, 60));
	c.drawString ("INSTRUCTIONS", 175, 150);
	c.setFont (new Font ("SansSerif", Font.PLAIN, 23));
	c.setColor (Color.white);
	for (int i = 0 ; i < 9 ; i++)
	{
	    c.drawString (text [i], 100, 250 + i * 40);
	}


	while (!isMenu) //space to go back
	{
	    char back = c.getChar ();
	    if (back == ' ')
	    {
		isMenu = true;
	    }
	}
    }


    private void highScores ()  //displays high scores
    {
	background (); //graphics/text
	c.setColor (title);
	c.setFont (new Font ("SansSerif", Font.PLAIN, 60));
	c.drawString ("HIGH SCORES", 175, 150);
	c.setColor (Color.white);
	c.setFont (new Font ("SansSerif", Font.PLAIN, 23));

	int counter = 0;
	while (playerName [counter] != null && counter < 10) //displays all scores until last submission until 10
	{
	    String scoreText = Integer.toString (playerScore [counter]) + " sec";
	    if (playerScore [counter] == -1) //all failed scores are -1
		scoreText = "FAILED";

	    c.drawString ((counter + 1) + ". " + playerName [counter] + " " + scoreText, 100, 225 + counter * 40);
	    counter++;
	}


	c.drawString ("Press 1 to reset leaderboards", 100, 250 + counter * 40);
	c.drawString ("Press space to go back to the menu", 100, 250 + (counter + 1) * 40);

	while (!isMenu)
	{
	    char back = c.getChar ();
	    switch (back)
	    {
		case ' ': //go back to menu
		    isMenu = true;
		    break;
		case '1': //reset highscores
		    resetHighScores ();
		    highScores ();
		    break;
	    }

	}
    }


    private void resetHighScores ()  //resets high scores
    {
	for (int i = 0 ; i < 10 ; i++) //changes all player names to null/empty
	{
	    playerName [i] = null;
	}


	saveFile ();
    }


    private void goodbye ()  //exits program
    {
	//name, info
	background ();
	c.setColor (Color.white);
	c.setFont (new Font ("SansSerif", Font.PLAIN, 30));
	c.drawString ("By Alisa Wu", 50, 350);
	c.drawString ("Minesweeper Game - ICS301 June 22, 2021", 50, 400);
	c.drawString ("Press space to quit game", 50, 450);
	while (true)
	{
	    char input = c.getChar ();
	    if (input == ' ')
	    {
		System.exit (0); //if user presses space, exits
	    }
	}
    }

    //menu methods

    private void menuSelector (int y_menu)  //menu selector
    {
	switch (y_menu)
	{
	    case 200: //play
		isMenu = false;
		begin = System.currentTimeMillis ();
		background ();
		play ();
		break;
	    case 300: //instructions
		isMenu = false;
		instructions ();
		break;
	    case 400: //high scores
		isMenu = false;
		highScores ();
		break;
	    case 500: //exit
		isMenu = false;
		goodbye ();
		break;
	}
    }


    private void menuDisplay (int y_menu)  //displays menu graphics
    {
	String[] menuNames = {"play", "instructions", "highscores", "exit"};
	try
	{
	    background ();
	    c.setColor (title);
	    c.setFont (new Font ("SansSerif", Font.ITALIC, 60));

	    BufferedImage title = ImageIO.read (new File ("title.png"));
	    BufferedImage play = ImageIO.read (new File ("play.png"));
	    BufferedImage instructions = ImageIO.read (new File ("instructions.png"));
	    BufferedImage highscores = ImageIO.read (new File ("highscores.png"));
	    BufferedImage exit = ImageIO.read (new File ("exit.png"));

	    BufferedImage playSelect = ImageIO.read (new File ("playSelect.png"));
	    BufferedImage instructionsSelect = ImageIO.read (new File ("instructionsSelect.png"));
	    BufferedImage highscoresSelect = ImageIO.read (new File ("highscoresSelect.png"));
	    BufferedImage exitSelect = ImageIO.read (new File ("exitSelect.png"));


	    c.drawImage (title, 125, 50, null);
	    c.setFont (new Font ("SansSerif", Font.PLAIN, 30));
	    c.setColor (Color.white);
	    c.drawString ("Use w & s to go up and down", 200, 650);
	    c.drawString ("Press space to select", 250, 690);



	    c.drawImage (play, 240, 250, null);
	    c.drawImage (instructions, 240, 325, null);
	    c.drawImage (highscores, 240, 400, null);
	    c.drawImage (exit, 240, 475, null);


	    switch (y_menu)
	    {
		case 200:
		    c.drawImage (playSelect, 240, 250, null);
		    break;
		case 300:
		    c.drawImage (instructionsSelect, 240, 325, null);
		    break;
		case 400:
		    c.drawImage (highscoresSelect, 240, 400, null);
		    break;
		case 500:
		    c.drawImage (exitSelect, 240, 475, null);
		    break;
	    }
	}


	catch (IOException e)
	{
	}
    }

    //board methods

    private void resetBoard ()  //reset board everytime game resets back to menu
    {
	gameState = 0;
	for (int x = 1 ; x < 11 ; x++)
	{
	    flags [x - 1] [0] = 0;
	    flags [x - 1] [1] = 0;

	    for (int y = 1 ; y < 11 ; y++)
	    {
		openedBoard [x] [y] = -3;

	    }
	}
    }



    private int newBoard (int x_value, int y_value)  //creates new randomized board
    {
	int bomb = 0;
	int[] [] bombBoard = {
		{ - 2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, },
		{ - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
		{ - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
		{ - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
		{ - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
		{ - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
		{ - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
		{ - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
		{ - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
		{ - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
		{ - 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, },
		{ - 2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, }
	    };
	int counter = 0;
	for (int i = 0 ; i < 10 + counter ; i++) //makes 10 random bombs
	{
	    double x = Math.random () * 11 + 1;
	    double y = Math.random () * 11 + 1;
	    if (bombBoard [(int) x] [(int) y] == 0 && ((int) x < x_value - 1 || (int) x > x_value + 1) && ((int) y < y_value - 1 || (int) y > y_value + 1))
		bombBoard [(int) x] [(int) y] = -1;
	    else
		counter++; //if coords already have bomb, increase max loop

	}


	for (int x = 1 ; x < 11 ; x++) //finds the numbers of each tile: 9 - bomb, 1-8 - number of bombs adj, 0 - empty, -2 - border
	{
	    for (int y = 1 ; y < 11 ; y++)
	    {
		bomb = 0;
		if (bombBoard [x - 1] [y - 1] == -1)
		    bomb++;
		if (bombBoard [x] [y - 1] == -1)
		    bomb++;
		if (bombBoard [x + 1] [y - 1] == -1)
		    bomb++;
		if (bombBoard [x - 1] [y] == -1)
		    bomb++;
		if (bombBoard [x + 1] [y] == -1)
		    bomb++;
		if (bombBoard [x - 1] [y + 1] == -1)
		    bomb++;
		if (bombBoard [x] [y + 1] == -1)
		    bomb++;
		if (bombBoard [x + 1] [y + 1] == -1)
		    bomb++;
		if (bombBoard [x] [y] == -1)
		{
		    bomb = 9;
		}

		board [x] [y] = bomb; //number of adj bombs
	    }
	}
	return board [x_value] [y_value];
    }
}


