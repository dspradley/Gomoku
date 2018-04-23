/*
 * Daniel Spradley
 * Lab 2 Gomoku
 * 8/30/17
 * 
 */

import java.util.Random;

import cs251.lab2.*;

public class Gomoku implements GomokuModel {
	// had to add implements and GomokuModel

	// 2d array of type square that is accessible anywhere due it being in
	// class.
	private Square board[][];
	private StringBuilder stringBuilder;
	private Square currentTurn;
	private boolean computerOn;

	// 1 for human, 0 for human.
	private int whoseTurn;

	// 1 will be human1, 2 human2, and 3 computer.
	private static int[] playersTurn = new int[3];

	// Constructor to call our blank board array & boardString
	public Gomoku() {
		blankMovesArray();
		boardStringCreation();
	}

	private void boardStringCreation() {
		// StringBuilders are objects like strings except they can be modified.
		// Treated like variable length arrays.
		StringBuilder stringBuildertemp = new StringBuilder();
		for (int row = 0; row < getNumRows(); row++) {

			for (int col = 0; col < getNumCols(); col++) {
				// Get the element @ that position and save it to a string.
				// toChar is for the char value instead of enum constant
				stringBuildertemp.append(board[row][col].toChar());
			}
			// Adding a new line character. Must be here or it screws with GUI.
			stringBuildertemp.append("\n");
		}

		// Assigns our temp to actual stringBuilder.
		stringBuilder = stringBuildertemp;
	}

	// when called by our constructor this initializes the array in a blank
	// state
	private void blankMovesArray() {
		// Initialized with default requirements
		board = new Square[getNumRows()][getNumCols()];
		// Now we place an empty state in each one
		for (int row = 0; row < getNumRows(); row++) {

			for (int col = 0; col < getNumCols(); col++) {
				// Constant called from our square enum
				board[row][col] = Square.EMPTY;
			}
		}
	}

	/*
	 * This method returns a string representation of the board. The GUI calls
	 * this method whenever it needs to know what the board looks like in order
	 * to be able to redraw it. The string that is returned must match the
	 * specific format described in the interface documentation
	 */
	@Override
	public String getBoardString() {
		// Returns a string that contains the character sequence in the builder
		return stringBuilder.toString();
	}

	@Override
	/*
	 * left the default values in place
	 */
	public int getNumCols() {
		return DEFAULT_NUM_COLS;
	}

	@Override
	// left the default values in place
	public int getNumInLineForWin() {
		return SQUARES_IN_LINE_FOR_WIN;
	}

	@Override
	// left the default values in place
	public int getNumRows() {
		return DEFAULT_NUM_ROWS;
	}

	@Override
	/*
	 * This method is called by the GUI whenever a click occurs on the game
	 * board. It’s supposed to update the state of the game board, and the
	 * return value is a value thatdescribes the current state of the game after
	 * the newly entered click was applied.
	 */
	public Outcome handleHumanPlayAt(int row, int col) {
		// Random number will pick who goes first. 1 for human or 2 player
		// human.
		if (playersTurn[0] == 1) {

			// Check if the spot is empty. If not we place a human click
			if (board[row][col] == Square.EMPTY) {
				// Click is set to whatever current turn is set too.
				board[row][col] = getCurrentTurn();

				// Now we must update the boards string;
				boardStringCreation();

				// Checking if the human has won.
				if (getComputerOn() == true) {
					if (winCheck()) {
						System.out.println("Human has won");
						return Outcome.RING_WINS;
					}
				}

				// If we aren't playing with computer the sign will change every
				// click.

				if (getComputerOn() == false) {
					if (getCurrentTurn() == Square.CROSS) {
						setCurrentTurn(Square.RING);
						// Ring will be a second player as 0.
						playersTurn[2] = 0;
					} else {
						setCurrentTurn(Square.CROSS);
						// Cross will be our second player as 1.
						playersTurn[2] = 1;
					}
					// Now we will check if either player has won.
					if (winCheck()) {
						if (getCurrentTurn() == Square.CROSS) {
							return Outcome.RING_WINS;
						} else {
							return Outcome.CROSS_WINS;
						}
					}
				}
				// Can't place if something is there.
			} else {
				System.out.println("Can't place here");
			}
		}

		// Computer is set to on.
		if (getComputerOn() == true) {
			computerMoves(row, col);
			boardStringCreation();
			if (winCheck()) {
				System.out.println("Computer has won");
				return Outcome.CROSS_WINS;
			}
			setCurrentTurn(Square.RING);
			// Set the next turn for human.
			playersTurn[0] = 1;
		}

		// Can return a default outcome for now until we add an computer player.
		return Outcome.GAME_NOT_OVER;
	}

	/*
	 * The a.i is retarded suffice to say.
	 */
	public void computerMoves(int row, int col) {
		// Creating a random number for our computer players choices.
		Random rand = new Random();
		// Max is 5 with 1 as min.
		int randomNumber = rand.nextInt(5) + 1;
		int randomRow = rand.nextInt(getNumRows() - 1) + 1;
		int randomCol = rand.nextInt(getNumCols() - 1) + 1;
		// Looking up
		if (board[row + 1][col] == Square.EMPTY && randomNumber == 1) {
			// Cross will be computer player
			board[row + 1][col] = Square.CROSS;
			// Now downward
		} else if (board[row - 1][col] == Square.EMPTY && randomNumber == 2) {
			board[row - 1][col] = Square.CROSS;
			// looking right
		} else if (board[row][col + 1] == Square.EMPTY && randomNumber == 3) {
			board[row][col + 1] = Square.CROSS;
			// look left
		} else if (board[row][col - 1] == Square.EMPTY && randomNumber == 4) {
			board[row][col - 1] = Square.CROSS;
		} else if (board[randomRow][randomCol] == Square.EMPTY
				&& randomNumber == 5) {
			board[randomRow][randomCol] = Square.CROSS;
		}

	}

	/*
	 * Going to check for wins here. If a win is detected in any of our boolean
	 * checks we send a true.
	 */

	private boolean winCheck() {
		// Checking every spot in the board.
		for (int row = 0; row < getNumRows(); row++) {

			for (int col = 0; col < getNumCols(); col++) {
				// Spot has to be filled with something
				if (board[row][col].toChar() != Square.EMPTY.toChar()) {
					// Checks every scenario
					if (downward(row, col) || leftDiagnol(row, col)
							|| rightDiagnol(row, col) || accross(row, col)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/*
	 * These methods below all basically do a similar thing. From the origin
	 * they go in different directions and check if we have enough in a row to
	 * make a win.
	 */

	private boolean leftDiagnol(int x, int y) {
		int check = 0;
		Square origin = board[x][y];
		if (x == 0 || y == 0) {
			return false;
		}
		for (int row = x, col = y; row < getNumRows() && col < getNumCols(); row--, col--) {
			if (origin.toChar() == board[row][col].toChar()) {
				check++;
			} else {
				break;
			}
		}
		if (check >= getNumInLineForWin()) {
			System.out.println("L DIAGNOL");
			return true;
		}
		return false;
	}

	private boolean rightDiagnol(int x, int y) {
		int check = 0;
		Square origin = board[x][y];
		if (x == 0 || y == 0) {
			return false;
		}
		for (int row = x, col = y; row < getNumRows() && col < getNumCols(); row--, col++) {
			if (origin.toChar() == board[row][col].toChar()) {
				check++;
			} else {
				break;
			}
		}
		if (check >= getNumInLineForWin()) {
			System.out.println("R DIAGNOL");
			return true;
		}
		return false;
	}

	private boolean downward(int x, int y) {

		int check = 0;
		Square origin = board[x][y];
		for (int row = x; row < getNumRows(); row++) {
			if (origin.toChar() == board[row][y].toChar()) {
				check++;
			} else {
				break;
			}
		}

		if (check >= getNumInLineForWin()) {
			System.out.println("DOWNWARD");
			return true;
		}
		return false;
	}

	private boolean accross(int x, int y) {
		int check = 0;
		Square origin = board[x][y];
		for (int col = y; col < getNumCols(); col++) {
			if (origin.toChar() == board[x][col].toChar()) {
				check++;
			} else {
				break;
			}
		}
		if (check >= getNumInLineForWin()) {
			System.out.println("ACROSS");
			return true;
		}
		return false;
	}

	@Override
	/*
	 * This method configures what sort of computer player (if any) will be
	 * used. I should have used arrays more here but it works.
	 */
	public void setComputerPlayer(String arg0) {
		// If computer is entered computer is turned on
		// HAVE TO USE .EQUALS!! compares string objects not content.

		if (arg0.equals("COMPUTER")) {

			setCurrentTurn(Square.RING);
			setComputerOn(true);

			// If computer is enabled the 2nd spot in our array is set to 0.
			playersTurn[1] = 0;

			// Launching from the IDE without arguments wouldn't engage else. No
			// clue why.
		} else {
			// else false
			setCurrentTurn(Square.RING);
			setComputerOn(false);

			// If computer is off the 2nd spot in our array is 1. Can do
			// multi-player.
			playersTurn[1] = 1;

		}

	}

	@Override
	// This method is called by the GUI whenever a new game is begun. It should
	// set up
	// whatever bookkeeping is needed to start a fresh game.
	public void startNewGame() {
		System.out
				.println("If this isn't a new game the loser will go first. Computer requires an onscren click to start.");

		// Create a new board Array
		blankMovesArray();
		// Create a new board String
		boardStringCreation();
		// Whoever won last game will not go first.
		if (playersTurn[0] == 1) {
			playersTurn[0] = 0;

		} else {
			playersTurn[0] = 1;
		}

		// Had an issue with new game not starting without a computer player on
		// a newgame.
		if (getComputerOn() == false) {
			playersTurn[0] = 1;
		}

	}

	public static void main(String[] args) {
		// This creates a new game that calls upon our constructor
		Gomoku game = new Gomoku();
		if (args.length > 0) {
			game.setComputerPlayer(args[0]);

		}

		// Picks a random number to start the game.
		Random rand = new Random();
		int randomNumber = rand.nextInt(2) + 0;
		// 1 will be human1, 0 will be computer.
		playersTurn[0] = randomNumber;

		// Picking our random first player.
		GomokuGUI.showGUI(game);

	}

	/*
	 * Different get/setters for needed values.
	 */
	public boolean getComputerOn() {
		return computerOn;
	}

	public void setComputerOn(boolean computerOn) {
		this.computerOn = computerOn;
	}

	// This is our current turn getter. Will return the symbol that is set.
	public Square getCurrentTurn() {
		return currentTurn;
	}

	// This is our current turn setter. Will change current turn to symbol of
	// choice
	public void setCurrentTurn(Square currentTurn) {
		this.currentTurn = currentTurn;
	}

	// 1 for human, 0 for computer
	public int getWhoseTurn() {
		return whoseTurn;
	}

	public void setWhoseTurn(int whoseTurn) {
		this.whoseTurn = whoseTurn;
	}

}
