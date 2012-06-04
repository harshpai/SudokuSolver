import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	// Provided grid data for main/testing
	// The instance variable strategy is up to you.
	private int[][] grid,firstSolution;
	private Integer numSolutions;
	private long elapsedTime;
	private final HashSet<Integer> allValues = new HashSet<Integer>();
	private List<Spot> emptySpots;

	//Inner class Spot that represents a single spot in the game
	private class Spot implements Comparable<Spot>{

		// ivars that hold the location in the grid
		private int row,col,assignableSetSize;

		public Spot(int row, int col){
			this.row = row;
			this.col = col;
			assignableSetSize = computeAssignableSetSize();

			// An empty spot can be filled with any number from 1 through 9
			// Initially fill the hashset with all values, then make a copy
			// of this hashset and removeAll elemnts that are in the same row,
			// column or square, thus leaving us with a set of assignable
			// integers for that spot.
			for(int i =1;i<=SIZE;i++)
				allValues.add(i);
		}

		/**
		 * Calculates the number of possible values that can be assigned to this spot, if it is empty.
		 * Else returns 0.
		 * */
		private int computeAssignableSetSize() {
			if(get() > 0)
				return 0;

			// our strategy is to build a set of values present in the same row,
			// col and square. This set contains values that cannot be assigned.
			// Hence no of values that can be assigned = SIZE - size of the set
			return SIZE-getNonAssignableValues().size();
		}

		/**
		 * Returns a set containing integers that cannot be assigned to "this" spot.
		 * Our strategy is to build a set of values present in the same row,
		 * column and square.
		 * */
		private Set<Integer> getNonAssignableValues(){
			Set<Integer> nonAssignableValues = new HashSet<Integer>();

			// add values in the same row
			for (int i =0 ; i<SIZE;i++ )
			{
				if(grid[row][i]>0)		nonAssignableValues.add(grid[row][i]);
			}

			// add values in the same column
			for (int i =0 ; i<SIZE;i++ )
			{
				if(grid[i][col]>0)		nonAssignableValues.add(grid[i][col]);
			}

			// starting row and column of each (square) part
			int partInitRow = (row/3)*3;
			int partInitCol = (col/3)*3;

			// add values in the same square
			for (int i =0 ; i<PART;i++ )
			{
				for (int j =0 ; j<PART;j++ )
				{
					if(grid[partInitRow + i][partInitCol + j]>0)	nonAssignableValues.add(grid[partInitRow + i][partInitCol + j]);
				}
			}

			return nonAssignableValues;
		}

		/**
		 * Returns a set containing integers that can be assigned to "this" spot.
		 * */
		public Set<Integer> getAssignableValues(){

			Set<Integer> assignableValues = new HashSet<Integer>(allValues);
			assignableValues.removeAll(getNonAssignableValues());
			return assignableValues;
		}

		/**
		 * Assigns a value to the spot in the grid
		 * */
		public void set(int value){
			grid[row][col] = value;
		}

		/**
		 * Returns the spot value
		 * @return spot value
		 * */
		public int get(){
			return grid[row][col];
		}

		/**
		 * Compares the spot to the given object for sort order.
		 * (This is the standard sorting override, implemented here
		 * since we implement the "Comparable" interface.)
		 * Increasing order by assignableSetSize.
		 * This is the Java 5 version, generic for Comparable<Spot>, so
		 * the arg is type Spot.
		 * 
		 * @return negative/0/positive to indicate ordering vs. given object
		 */
		@Override
		public int compareTo(Spot anotherSpot) {
			return this.assignableSetSize-anotherSpot.assignableSetSize;
		}

		/**
		 * Debug utility
		 * */
		@Override
		public String toString() {
			return "\nRow:"+row+" Col:"+col+" Set size:"+assignableSetSize;

		}
	}


	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
			"1 6 4 0 0 0 0 0 2",
			"2 0 0 4 0 3 9 1 0",
			"0 0 5 0 8 0 4 0 7",
			"0 9 0 0 0 6 5 0 0",
			"5 0 0 1 0 2 0 0 8",
			"0 0 8 9 0 0 0 3 0",
			"8 0 9 0 4 0 2 0 0",
			"0 7 3 5 0 9 0 0 1",
			"4 0 0 0 0 0 6 7 9");


	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
			"530070000",
			"600195000",
			"098000060",
			"800060003",
			"400803001",
			"700020006",
			"060000280",
			"000419005",
			"000080079");

	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
			"3 7 0 0 0 0 0 8 0",
			"0 0 1 0 9 3 0 0 0",
			"0 4 0 7 8 0 0 0 3",
			"0 9 3 8 0 0 0 1 2",
			"0 0 0 0 4 0 0 0 0",
			"5 2 0 0 0 6 7 9 0",
			"6 0 0 0 2 1 0 4 0",
			"0 0 0 5 3 0 9 0 0",
			"0 3 0 0 0 0 0 5 1");


	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;

	// Provided various static utility methods to
	// convert data formats to int[][] grid.

	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}


	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}

		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}


	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}


	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
	//	public static void main(String[] args) {
	//		Sudoku sudoku;
	//		sudoku = new Sudoku(hardGrid);
	//
	//		System.out.println(sudoku); // print the raw problem
	//		int count = sudoku.solve();
	//		System.out.println("solutions:" + count);
	//		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
	//		System.out.println(sudoku.getSolutionText());
	//	}




	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		grid = new int[SIZE][SIZE];
		firstSolution = new int[SIZE][SIZE];
		copyIntegerArray(ints, grid);
	}

	/**
	 * Private helper method that deep copies a 2d integer array.
	 * */
	private void copyIntegerArray(int[][] source,int[][] destination){
		for (int i = 0; i < source.length; i++)
			System.arraycopy(source[i], 0, destination[i], 0, source[i].length);
	}

	/**
	 * Sets up based on given puzzle in the text form.
	 * */
	public Sudoku(String text){
		this(textToGrid(text));
	}


	/**
	 * Renders the Sudoku as a String made of 9 lines that shows the rows of the grid,
	 * with each number preceded by a space.
	 * */
	// this is just a wrapper call
	@Override
	public String toString() {
		return toStringHelper(grid);
	}

	/**
	 * Private helper method that renders the Sudoku as a String made of 9 lines that
	 * shows the rows of the grid, with each number preceded by a space.
	 * */
	private String toStringHelper(int[][] puzzle)
	{
		StringBuilder buff = new StringBuilder();
		for (int i = 0; i <puzzle.length;i++)
		{
			for (int j = 0 ; j < puzzle[i].length; j++)
			{
				buff.append(puzzle[i][j]);
				buff.append(" ");
			}
			buff.append("\n");
		}
		return buff.toString();
	}

	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 * @return number of solutions found, at most 100
	 */
	public int solve() {
		elapsedTime = System.currentTimeMillis();
		numSolutions = 0;
		emptySpots = computeEmptySpots();
		rescursiveSolve(0);
		elapsedTime = System.currentTimeMillis()-elapsedTime;
		return numSolutions;
	}

	/**
	 * This is the method that actually solves the sudoku puzzle
	 * using classic recursive backtracking algorithm.
	 * */
	private void rescursiveSolve(int index)
	{
		if(numSolutions == 100) 		return;
		if(index==emptySpots.size())
		{
			if(++numSolutions == 1)
			{
				copyIntegerArray(grid, firstSolution);
			}
		}
		else
		{
			Spot currentSpot = emptySpots.get(index);
			Set<Integer> assignableValues = currentSpot.getAssignableValues();
			for(Integer i : assignableValues)
			{
				currentSpot.set(i);
				rescursiveSolve(index+1);
				currentSpot.set(0);
			}
		}
	}

	/**
	 * Returns an ArrayList containing empty spots in the sudoku grid.
	 * The spots are sorted by assignableSetSize in increasing order.
	 * */
	private ArrayList<Spot> computeEmptySpots() {
		ArrayList<Spot> emptySpots = new ArrayList<Spot>();

		for(int i = 0 ; i < SIZE; i++)
		{
			for(int j = 0 ; j < SIZE; j++)
			{
				Spot currentSpot = new Spot(i,j);
				if(currentSpot.get()==0)
					emptySpots.add(currentSpot);
			}
		}

		Collections.sort(emptySpots);

		return emptySpots;
	}


	/**
	 * Returns the text form of the first solution found.
	 * If no solution was found returns an empty string
	 * */
	public String getSolutionText() {
		if(numSolutions>0)
			return toStringHelper(firstSolution);
		return "";
	}

	/**
	 * Returns time taken to solve puzzle in milli seconds.
	 * */
	public long getElapsed() {
		return elapsedTime;
	}

}
