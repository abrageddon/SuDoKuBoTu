package sudokubotu;

import java.util.Collections;
import java.util.LinkedList;

// 1.0 rating

public class LastRemainingMaskFactory extends SDKMaskFactory {
	
	private static class LRVertexExpander implements VertexExpander<SDKMask> {
		
		@Override
		public Iterable<SDKMask> expand(SDKMask mask) {
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
	
	private static class LRGoalCondition implements Condition<SDKMask> {

		SDKBoard board;
		public LRGoalCondition(SDKBoard board) {
			this.board = board;
		}
		@Override
		public boolean satisfiedWith(SDKMask e) {
			SDKBoard tempBoard = e.applyTo(board);
			tempBoard.updateConstraints();
			int clueCount = tempBoard.getNumberOfClues();
			for(SDKSquare s : tempBoard.getAllSquares()) {
				if ( !isLastRemaining(tempBoard, s) || 81 - clueCount < 8)
					return false;
			}
			return true;
		}
		
	}
	
	private static class LRFailCondition implements Condition<SDKMask> {

		private SDKBoard board;
		
		public LRFailCondition(SDKBoard board) {
			this.board = board;
		}
			
		@Override
		public boolean satisfiedWith(SDKMask e) {
			SDKBoard tempBoard = e.applyTo(board);
			
			return !SDKAnalysis.hasUniqueSolution(tempBoard);
		}
		
	}
	
	public static boolean isLastRemaining(SDKBoard board,SDKSquare square) {
		LinkedList<SDKSquare> colSquares = board.getSquaresInCol(square.col);
		colSquares.remove(square);
		for ( SDKSquare s : colSquares ) {
			if ( !s.isLocked() )
				return false;
		}
		LinkedList<SDKSquare> rowSquares = board.getSquaresInRow(square.row);
		rowSquares.remove(square);
		for ( SDKSquare s : rowSquares ) {
			if ( !s.isLocked() )
				return false;
		}
		LinkedList<SDKSquare> zoneSquares = board.getSquaresInZone(square);
		zoneSquares.remove(square);
		for ( SDKSquare s : zoneSquares ) {
			if ( !s.isLocked() )
				return false;
		}
		return true;
	}
	
	public static SDKMask createMaskForBoard(SDKBoard solvedBoard,int maxRemoved) {
		SDKMask mask = new SDKMask();
		// randomly choose a position if removing position causes another to have one less than max domain size, undo
		SDKBoard board = new SDKBoard(solvedBoard.copyBoard());
		
		DepthGraphSearch<SDKMask> dgs = new DepthGraphSearch<SDKMask>(
			mask,
			new LRFailCondition(board), 
			new LRGoalCondition(board), 
			new LRVertexExpander()
		);
		return dgs.getGoalState();
	}
}
