package sudokubotu;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

//buildign a csp is a csp?

public class HiddenSingleMaskFactory extends SDKMaskFactory {
	//apply lastremaining mask
	//general strategy:
	// decide to remove or not if it turns into a hidden single hint
	// if it does then check if it causes others to go beyond hidden single
	// if so then undo
	
	public static boolean isHiddenSingle(SDKSquare s, SDKBoard board) {
		LinkedList<SDKSquare> zsqrs = board.getSquaresInZone(s);
		zsqrs.remove(s);
		LinkedList<SDKSquare> csqrs = board.getSquaresInCol(s.col);
		csqrs.remove(s);
		LinkedList<SDKSquare> rsqrs = board.getSquaresInRow(s.row);
		rsqrs.remove(s);
		HashSet<Integer> zonePossibles = s.getPossible();
		HashSet<Integer> rowPossibles = s.getPossible();
		HashSet<Integer> colPossibles = s.getPossible();
		
		for(SDKSquare s2 : rsqrs)
			rowPossibles.removeAll(s2.getPossible());
		
		for(SDKSquare s2 : csqrs)
			colPossibles.removeAll(s2.getPossible());
		
		for(SDKSquare s2 : zsqrs)
			zonePossibles.removeAll(s2.getPossible());
		
		return rowPossibles.size() + colPossibles.size() + zonePossibles.size() != 1;
	}
	
	public static SDKMask createMaskForBoard(SDKBoard solvedBoard,int maxRemoved) {
		SDKBoard board = new SDKBoard(solvedBoard.copyBoard());
		SDKMask mask = new SDKMask();
		
		//apply the lowest level mask to create a base
		mask = LastRemainingMaskFactory.createMaskForBoard(board,81);
		board = mask.applyTo(board);
		board.updateConstraints();
		
		LinkedList<SDKSquare> squares = board.getAllSquaresWithClues();
		
		Collections.shuffle(squares);
		
		for(SDKSquare s : squares) {
			int value = s.getValue();
			if ( maxRemoved-- < 0 )
				break;
			try {
				s.setLocked(false);
				s.setValue(0);
				board.updateConstraints();
				//scan the block to see if there is a value in the domain where the current square is the only one
				//do the same for columns and rows
				if (isHiddenSingle(s,board))
					throw new FailingRemoveException("Removing does not cause hidden single.");
				
				if (!SDKAnalysis.hasUniqueSolution(board))
					throw new FailingRemoveException("The puzzle no longer has a unique solution.");
				
				mask.set(s.row, s.col, false);
				
			} catch (FailingRemoveException e){
				s.setValue(value);
				s.setLocked(true);
			}
		}
		
		return mask;
	}
}
