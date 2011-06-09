package sudokubotu;

import java.util.HashSet;
import java.util.LinkedList;



// Graph traversal
// Each vertex is a state of the graph with holes removed
// goal state is a state where the next easiest solve step is a direct hidden pair
// naive? there is no information, unless:
//	count of clues
//	number of candidates


public class DirectHiddenPairMaskFactory extends SDKMaskFactory {
	
	private static class DHPVertexExpander implements VertexExpander<SDKMask> {
		
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
			return masks;
		}
		
	}
	
	private static class DHPGoalCondition implements Condition<SDKMask> {

		SDKBoard board;
		public DHPGoalCondition(SDKBoard board) {
			this.board = board;
		}
		@Override
		public boolean satisfiedWith(SDKMask e) {
			SDKBoard tempBoard = e.applyTo(board);
			tempBoard.updateConstraints();
			for(SDKSquare s : tempBoard.getAllSquares()) {
				boolean isHiddenSingle = HiddenSingleMaskFactory.isHiddenSingle(s, board);
				boolean isLastRemaining = LastRemainingMaskFactory.isLastRemaining(board, s);
				boolean isDirectHiddenPair = isDirectHiddenPair(s,board);
				if (!isHiddenSingle && !isLastRemaining && isDirectHiddenPair)
					return true;
			}
			return false;
		}
		
	}
	
	private static class DHPFailCondition implements Condition<SDKMask> {

		private SDKBoard board;
		
		public DHPFailCondition(SDKBoard board) {
			this.board = board;
		}
			
		@Override
		public boolean satisfiedWith(SDKMask e) {
			SDKBoard tempBoard = e.applyTo(board);
			
			return !SDKAnalysis.hasUniqueSolution(tempBoard);
		}
		
	}
	
	public static SDKMask createMaskForBoard(SDKBoard solvedBoard,int maxRemoved) {
		SDKBoard board = new SDKBoard(solvedBoard.copyBoard());
		SDKMask mask = HiddenSingleMaskFactory.createMaskForBoard(solvedBoard, maxRemoved);
		board = mask.applyTo(board);
		
		DepthGraphSearch<SDKMask> dgs = new DepthGraphSearch<SDKMask>(
			mask,
			new DHPFailCondition(board), 
			new DHPGoalCondition(board), 
			new DHPVertexExpander()
		);
		
		return dgs.getGoalState();
	}

	public static boolean isDirectHiddenPair(SDKSquare s, SDKBoard board) {
		LinkedList<SDKSquare> zsqrs = board.getSquaresInZone(s);
		zsqrs.remove(s);
		LinkedList<SDKSquare> csqrs = board.getSquaresInCol(s.col);
		csqrs.remove(s);
		LinkedList<SDKSquare> rsqrs = board.getSquaresInRow(s.row);
		rsqrs.remove(s);
		
		boolean zoneHas = hasDirectPairThatSolves(zsqrs,s,board);
		boolean colHas = hasDirectPairThatSolves(csqrs,s,board);
		boolean rowHas = hasDirectPairThatSolves(rsqrs,s,board);
		
		return ((zoneHas?1:0) + (colHas?1:0) + (rowHas?1:0) != 1);
	}

	private static boolean hasDirectPairThatSolves( LinkedList<SDKSquare> sqrs, SDKSquare s, SDKBoard b ) {
		HashSet<Integer> hs = new HashSet<Integer>();
		for(SDKSquare s2 : sqrs) {
			
			hs.addAll(s.getPossible());
			hs.retainAll(s2.getPossible());
			if ( hs.size() == 2 ) {
				HashSet<Integer> hs2 = new HashSet<Integer>();
				for(SDKSquare s3 : sqrs) {
					if (s2 == s3)
						continue;
					hs2.addAll(hs);
					hs2.retainAll(s3.getPossible());
					if (hs2.size() == 2) {
						HashSet<Integer> sPossible = new HashSet<Integer>();
						sPossible.addAll(s.getPossible());
						sPossible.removeAll(hs2);
						SDKSquare testSquare = new SDKSquare(s2.row, s2.col);
						for(int i : testSquare.getPossible()) {
							testSquare.addPossible(i);
						}
						if ( HiddenSingleMaskFactory.isHiddenSingle(testSquare, b) )
							return true;
					}
					
				}
			}
		}
		return false;
	}
	
}
