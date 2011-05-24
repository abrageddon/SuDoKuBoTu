package sudokubotu;

import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
//Currently producing single NakedSingles and empties the rest of the board
//need to add way to re-populate board to make it unique and not remove the nakedSingle
public class NakedSingleMaskFactory extends SDKMaskFactory{
	public static SDKMask createMaskForBoard(SDKBoard solvedBoard,int maxRemoved){
		SDKMask mask = new SDKMask();
		SDKBoard puzzle = new SDKBoard(solvedBoard.copyBoard());
		Random rand = new Random();
		Point selected = new Point(rand.nextInt(puzzle.getN()),rand.nextInt(puzzle.getN()));
		puzzle.setSquareValue(selected.x, selected.y, 0);
		puzzle.updateConstraints();
		LinkedList<SDKSquare> squares = puzzle.getAllSquares();
		SudokuBot test = new SudokuBot(puzzle);
		HashMap<String, HashSet<Integer>> areasToLock  = new HashMap<String,HashSet<Integer>>();
		areasToLock.put("Row", new HashSet<Integer>());
		while(maxRemoved-->0){
			
			SDKSquare current = squares.remove();
			int curval = current.getValue();
			current.setValue(0);
			puzzle.updateConstraints();

			if(puzzle.getSquarePossible(selected.x, selected.y).size()>1 ){
				current.setValue(curval);
				current.setLocked(true);
			}
		}
		LinkedList<SDKSquare> row = puzzle.getSquaresInCol(selected.x);
		

		System.out.println(puzzle.toString());
		System.out.println(test.isUnique());
		mask.mask = puzzle;
		return mask;
		
	}
	

}
