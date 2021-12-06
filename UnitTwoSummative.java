/**
* 
* [UnitTwoSummative.java]
* 
* Computer Science Unit 2 Summative
* @author Kyle Zheng
* @version Eclipse macOS April 5, 2021
* Submitted April 5, 2021
* 
* Breif description of code
* 1) User input
* 2) Stores the city map into a file
* 3) Locate the row and collumn for 'S' (car's start location)
* 	Note: You can input 'S' at any row as long as it is at the leftmost collumn
* 4) Use recursion to scan through all possible paths
* 5) If a path has been discovered (reaches destination) compare that to the previous best path
* 6) If the current path uses less energy, store that path into a class variable and set that as the new best path
* 7) After the program scanned through all possible paths, the program outputs the best path into a different file
* 
* Note: Maze, map and city mean the same thing and are used interchangeably
* Another note: The block comments that describes the methods are kind of useless. Ignoring that would save a lot of time.
*/

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
* UnitTwoSummative 
* This class is an algorithm that determines the best route across a city (provided by user) while being aware of elevation changes along the way.
*/
public class UnitTwoSummative {

	// class variables
	public static int rows;
	public static int collumns;
	public static int highestBatteryLevel = 0; // worst possible path would be when battery level reaches 0
	public static char[][] bestPossiblePath; // stores the best path
	public static boolean pathFound = false; // checks to see if at least one possible path has been calculated

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		
		// user input (for file)
		String fileName = "";
		System.out.print("Input filename to store city map: ");
		fileName = sc.next(); // file name (inputted by user)
		File storeInput = new File(fileName); // create a file object (named by the user)
	
		// user input (for maze)
		System.out.println("Insert city map below (use proper format)");
		rows = sc.nextInt();
		collumns = sc.nextInt();
		int batteryTank = sc.nextInt();
		
		// stores the user's maze input into a 1D string array
		String[] stringRows = new String[rows + 1]; // nextLine() accounts for nextInt(), so the first index (0) has already been stored by nextInt()

		for (int i = 0; i < rows + 1; i++) {
			stringRows[i] = sc.nextLine(); // the first index (0) has already been stored by nextInt() (blank line)
		}
		
		sc.close(); // close scanner
		// end of user input

		
		char[][] maze; // stores city map
		// convert to 2D char array
		maze = convertToChar(stringRows);
		
		// store city map in a text file
		// try catch statement
		try {
			PrintWriter output = new PrintWriter(storeInput);
			output.println("Number of rows: " + rows);
			output.println("Number of collumns: " + collumns);
			output.println("Battery capacity: " + batteryTank);
			
			// print city (with best route)
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < collumns; j++) {
					output.print(maze[i][j]);
				}
				output.println();
			}

			output.close(); // close print writer
			System.out.println("The city map is stored in the text file: " + fileName);
			
		} catch (IOException e) {
			// output error message
			e.printStackTrace();
		}
				
		// determine the row where 'S' is located (find the start location of the car)
		int carRow = findRowS(maze);

		// determine the collumn where 'S' is located (find the start location of the car)
		int carCollumn = findCollumnS(maze);
		
		
		// determine the best possible route
		bestRoute(maze, carRow, carCollumn, batteryTank);
		
		
		if (pathFound == false) { // no paths has been calculated
			System.out.println("Unable to find a path. Try creating at least one possible path or adding more batteries.");
		
		} else { // at least one path has been calculated
			
			// output the city with best path in 'ROUTE.TXT' 
			File outputPath = new File("ROUTE.TXT"); // create a file object (ROUTE.TXT)
			
			// try catch statement
			try { 
				PrintWriter output = new PrintWriter(outputPath);
				
				output.println("I have calculated the trip that results in the most remaining amount of power.");
				output.println("Power usage: " + (batteryTank - highestBatteryLevel));
				output.println("Power remaining: " + highestBatteryLevel);
				
				// print city (with best route)
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < collumns; j++) {
						output.print(bestPossiblePath[i][j]);
					}
					output.println();
				}
				
				output.close(); // close print writer
				System.out.println("The city with the best path is stored in the text file: ROUTE.TXT");
				
			} catch (IOException e) {
				// output error message
				e.printStackTrace();
			}
		}
		
	}

	/**
	* convertToChar 
	* This method accepts a 1D string array (city map) as a parameter and converts it to a 2D char array.
	* This method returns the 2D char array.
	* @param map -> A 1D string array that holds data representing a city map.
	* @return newMap -> 2D char array that has been converted from the 1D string array.
	*/
	public static char[][] convertToChar(String[] map) {
		char[][] newMap = new char[rows][collumns]; // reminder: 'rows' and 'collumns' are class variables
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < collumns; j++) {
				newMap[i][j] = map[i + 1].charAt(j); // ignores the first string line since it is blank
			}
		}
		return newMap;
	}

	/**
	* findRowS 
	* This method accepts a 2D char array (city map) as a parameter and finds the row where 'S' is located (in that city map)
	* This method returns the row index where the 'S' is located (as an int).
	* @param myMaze -> A 2D char array (city map) that holds data representing a city map.
	* @return returnRow -> An integer that represents the row index where the 'S' is located. 
	*/
	public static int findRowS(char[][] myMaze) {
		int returnRow = 1; // the program does not assume 'S' will always be inputted, so I cannot return the index inside of the if statement
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < collumns; j++) {
				if (myMaze[i][j] == 'S') {
					returnRow = i;
				}
			}
		}
		return returnRow; // if the user did not input 'S' then it will return 1 (since that is the default location)
	}

	/**
	* findCollumnS 
	* This method accepts a 2D char array (city map) as a parameter and finds the collumn where 'S' is located (in that city map)
	* This method returns the collumn index where the 'S' is located (as an int).
	* @param myMaze -> A 2D char array (city map) that holds data representing a city map.
	* @return returnRow -> An integer that represents the collumn index where the 'S' is located. 
	*/
	public static int findCollumnS(char[][] myMaze) {
		int returnCollumn = 0; // the program does not assume 'S' will always be inputted, so I cannot return the index inside of the if statement
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < collumns; j++) {
				if (myMaze[i][j] == 'S') {
					returnCollumn = j;
				}
			}
		}
		return returnCollumn; // if the user did not input 'S' then it will return 0 (since that is the default location)
	}

	/**
	* bestRoute 
	* This method accepts the following...
	* 1. A 2D char array (city map)
	* 2. Row location of the car
	* 3. Collumn location of the car
	* 4. Current battery level (can also be referred to as power)
	* as a parameter and scans through all possible paths in the city map.
	* 
	* This is a recursive method. It scans through all possible paths.
	* 
	* Once all possible paths have been explored, the recursion method will end.
	* 
	* The best path will be stored in the class variable 'bestPossiblePath'
	* 
	* @param myMaze (the following below)
	* 1. A 2D char array (city map)
	* 2. Row location of the car (as an integer)
	* 3. Collumn location of the car (as an integer)
	* 4. Current battery level (can also be referred to as power) - (as an integer)
	* Doesn't return anything (void). As mentioned, the best path will be stored in the class variable 'bestPossiblePath'
	*/
	public static void bestRoute(char[][] myMap, int carY, int carX, int batteryLevel) {
		
		// spaced out the if statements for better readability
		
		// if there is no more battery left (base case)
		if (batteryLevel <= 0) {
			System.out.println("Does it look like I have unlimited batteries?");
		
			
		// if the car has not moved yet (not a base case)
		} else if (myMap[carY][carX] == 'S') {
			// 'S' is always located on the leftmost collumn in between two walls (above and below)
			// can only move right in this case
			if (myMap[carY][carX + 1] == ' ') {
				// for whatever reason myMap acts as a global variable, so I created a copy of it and passed that into the recursive call
				// placing an astrick directly on myMap is permanent (unless manually changed), so recalling to previous recursive calls will not remove the asterisk
				char[][] newMap = copyMap(myMap, carY, carX + 1); // create a copy of the map and place an asterisk at the new current car location
				bestRoute(newMap, carY, carX + 1, batteryLevel - 1); // recursive call
				
			// if there is a hill (same thing but decrease battery level by 5)
			} else if (myMap[carY][carX + 1] == 'H') {
				char[][] newMap = copyMap(myMap, carY, carX + 1);
				bestRoute(newMap, carY, carX + 1, batteryLevel - 5);
			}
		
			
		/*
		 *if a path has been discovered (reaches destination) (this is a base case)
		 * the program will treat this as a dead end, because there might stll be more paths have haven't been explored yet
		 * the program will begin to call back to previous recursive calls
		 * basically the car will go backwards to look for another path
		 */
		} else if (myMap[carY][carX + 1] == 'D' || myMap[carY][carX - 1] == 'D' || myMap[carY + 1][carX] == 'D' || myMap[carY - 1][carX] == 'D') {
			// checks to see if this is the most efficient path thus far
			compare(batteryLevel - 1, myMap); // the final step (to reach 'D') uses up one energy from the battery


		} else {
			
			// 8 possible distinct recursive calls (4 directions without hills + 4 directions with hills)
			
			// move down
			if (myMap[carY + 1][carX] == ' ') {
				char[][] newMap = copyMap(myMap, carY + 1, carX);
				bestRoute(newMap, carY + 1, carX, batteryLevel - 1); // down
			}
			
			// move down to hill
			if (myMap[carY + 1][carX] == 'H') {
				char[][] newMap = copyMap(myMap, carY + 1, carX);
				// decrease battery level by 5 instead of 1 due to hill
				bestRoute(newMap, carY + 1, carX, batteryLevel - 5); // down
			}
			
			// move right
			if (myMap[carY][carX + 1] == ' ') {
				char[][] newMap = copyMap(myMap, carY, carX + 1);
				bestRoute(newMap, carY, carX + 1, batteryLevel - 1); // right
			}
			
			// move right to hill
			if (myMap[carY][carX + 1] == 'H') {
				char[][] newMap = copyMap(myMap, carY, carX + 1);
				bestRoute(newMap, carY, carX + 1, batteryLevel - 5); // right
			}
			
			// move left
			// cannot be 'S' since it is already on the leftmost collumn, otherwise it will throw an out of bounds error
			if (myMap[carY][carX] != 'S' && myMap[carY][carX - 1] == ' ') {
				char[][] newMap = copyMap(myMap, carY, carX - 1);
				bestRoute(newMap, carY, carX - 1, batteryLevel - 1); // left
			}
			
			// move left to hill
			if (myMap[carY][carX - 1] == 'H') {
				char[][] newMap = copyMap(myMap, carY, carX - 1);
				bestRoute(newMap, carY, carX - 1, batteryLevel - 5); // left
			}
			
			// move up
			if (myMap[carY - 1][carX] == ' ') {
				char[][] newMap = copyMap(myMap, carY - 1, carX);
				bestRoute(newMap, carY - 1, carX, batteryLevel - 1); // up
			}
			
			// move up to hill
			if (myMap[carY - 1][carX] == 'H') {
				char[][] newMap = copyMap(myMap, carY - 1, carX);
				bestRoute(newMap, carY - 1, carX, batteryLevel - 5); // up
			}
			
			// if none of the above, then the program assumes that the car reached a dead end and will begin calling back to previous recursive calls
			// the program pretty much does the exact same thing when it reaches the destination (you can refer to that part above)
			
		}
		
	}
	
	/**
	* copyMap 
	* This method accepts the following...
	* 1. A 2D char array (city map)
	* 2. Row location of the car
	* 3. Collumn location of the car
	* as a parameter and creates a copy of the 2D char array. It also places an asterisk at the new current car location.
	* 
	* @param myMaze (the following below)
	* 1. A 2D char array (city map)
	* 2. Row location of the car (as an integer)
	* 3. Collumn location of the car (as an integer)
	* @return mapCopy -> A copy of the 2D char array (city map) - in the form of a 2D char array
	*/
	public static char[][] copyMap(char[][] map, int posY, int posX) {
		char[][] mapCopy = new char[rows][collumns];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				mapCopy[i][j] = map[i][j];
			}
		}
		mapCopy[posY][posX] = '*';
		return mapCopy;
	}

	/**
	* compare 
	* This method accepts a 2D char array (city map with a path) and the amount of battery remaining and determines whether it is the best path thus far.
	* Does this by comparing the amount of battery remaining.
	* @param myMaze -> 2D char array that holds data representing a path and the amount of battery remaining (as an integer)
	* Doesn't return anything (void). As mentioned, the best path will be stored in the class variable 'bestPossiblePath'
	*/
	public static void compare(int batteryLeft, char[][] pendingMap) {
		if (batteryLeft >= highestBatteryLevel) {
			pathFound = true; // at least one path has been found
			highestBatteryLevel = batteryLeft; // new highest battery level
			bestPossiblePath = pendingMap; // stores the best path in a class variable
		}
	}
}
