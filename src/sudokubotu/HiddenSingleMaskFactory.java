package sudokubotu;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;


//buildign a csp is a csp?

public class HiddenSingleMaskFactory extends SDKMaskFactory {
	//apply lastremaining mask
	//general strategy:
	// decide to remove or not if it turns into a hidden single hint
	// if it does then check if it causes others to go beyond hidden single
	// if so then undo
	
	private static class HSVertexExpander implements VertexExpander<SDKMask> {
		
		@Override
		public Collection<SDKMask> expand(SDKMask mask) {
			LinkedList<SDKMask> masks = new LinkedList<SDKMask>();
			LinkedList<SDKSquare> squares = mask.mask.getAllSquaresWithClues();
			for(SDKSquare s : squares) {
				SDKMask nextMask = new SDKMask();
				nextMask.mask = new SDKBoard(mask.mask.copyBoard());
				nextMask.set(s.row, s.col, false);
				masks.add(nextMask);
			}
			Collections.shuffle(masks);
			return masks;
		}
		
	}
	
	private static class HSGoalCondition implements Condition<SDKMask> {

		SDKBoard board;
		public HSGoalCondition(SDKBoard board) {
			this.board = board;
		}
		@Override
		public boolean satisfiedWith(SDKMask e) {
			SDKBoard tempBoard = e.applyTo(board);
			tempBoard.updateConstraints();
			int emptyCount = tempBoard.getN()*tempBoard.getN() - tempBoard.getNumberOfClues();
			for(SDKSquare s : tempBoard.getAllSquares()) {
				boolean isHiddenSingle = HiddenSingleMaskFactory.isHiddenSingle(s, tempBoard);
				if ( isHiddenSingle && emptyCount > 30 )
					return true;
			}
			return false;
		}
		
	}
	
	private static class HSFailCondition implements Condition<SDKMask> {

		private SDKBoard board;
		
		public HSFailCondition(SDKBoard board) {
			this.board = board;
		}
			
		@Override
		public boolean satisfiedWith(SDKMask e) {
			SDKBoard tempBoard = e.applyTo(board);
			
			return !SDKAnalysis.hasUniqueSolution(tempBoard);
		}
		
	}
	
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
		
		return rowPossibles.size() == 1 || colPossibles.size() == 1 || zonePossibles.size() == 1;
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
		
		DepthGraphSearch<SDKMask> dgs = new DepthGraphSearch<SDKMask>(
			mask,
			new HSFailCondition(board), 
			new HSGoalCondition(board), 
			new HSVertexExpander()
		);
		try {
			return dgs.getGoalState();
		} catch (MaxRunException e) {
			return mask;
		}
		
	}
}
