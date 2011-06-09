package sudokubotu;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


// Graph traversal
// Each vertex is a state of the graph with holes removed
// goal state is a state where the next easiest solve step is a direct hidden pair
// naive? there is no information, unless:
//	count of clues
//	number of candidates


public class DirectHiddenPairMaskFactory extends SDKMaskFactory {
	
	private static class DHPVertexExpander implements VertexExpander<SDKMask> {
		
		@Override
		public Collection<SDKMask> expand(SDKMask mask) {
			LinkedList<SDKMask> masks = new LinkedList<SDKMask>();
			LinkedList<SDKSquare> squares = mask.mask.getAllSquaresWithClues();
			if (squares.size() < 20)
				return masks;
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
	
	public static class DHPGoalCondition implements Condition<SDKMask> {

		SDKBoard board;
		public DHPGoalCondition(SDKBoard board) {
			this.board = board;
		}
		@Override
		public boolean satisfiedWith(SDKMask e) {
			SDKBoard tempBoard = e.applyTo(board);
			
			boolean hasDirectHiddenPair = false;
			for(SDKSquare s : tempBoard.getAllSquares()) {
				if ( isDirectHiddenPair(s, tempBoard) ) {
					hasDirectHiddenPair = true;
					break;
				}
			}
			
			if ( !hasDirectHiddenPair )
				return false;
			
			for(SDKSquare s : tempBoard.getAllSquares()) {
				boolean isHiddenSingle = HiddenSingleMaskFactory.isHiddenSingle(s, tempBoard);
				boolean isLastRemaining = LastRemainingMaskFactory.isLastRemaining(tempBoard, s);
				if ( isHiddenSingle || isLastRemaining )
					return false;
			}
			
			return true;
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
	
	public static SDKMask createMaskForBoard(SDKBoard solvedBoard,int maxRemoved) throws MaxRunException {
		SDKBoard board = new SDKBoard(solvedBoard.copyBoard());
//		SDKMask mask = HiddenSingleMaskFactory.createMaskForBoard(solvedBoard, maxRemoved);
//		board = mask.applyTo(board);
		SDKMask mask = new SDKMask();
		mask.rule = "Direct Hidden Pair";
		mask.difficulty = 2.0;
		FringeGraphSearch<SDKMask> dgs = new FringeGraphSearch<SDKMask>(
			mask,
			new DHPFailCondition(board), 
			new DHPGoalCondition(board), 
			new DHPVertexExpander()
		);
//		return mask;
		mask = dgs.getGoalState();
		
		
		return mask;
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
		
		//return ((zoneHas?1:0) + (colHas?1:0) + (rowHas?1:0) == 1);
		return zoneHas || colHas || rowHas;
	}

	private static boolean hasDirectPairThatSolves( LinkedList<SDKSquare> sqrs, SDKSquare s, SDKBoard b ) {
		HashSet<Integer> hs = new HashSet<Integer>();
		
		//remove candidates that are not in the pair from the direct pair
		//look for a square in the zone that has a lone candidate (a candidate that only it contains in the zone)
		for(SDKSquare s1 : sqrs)
		{
			for(SDKSquare s2 : sqrs) {
				if ( s1.equals(s2) )
					continue;
				HashSet<Integer> candidatePair = getCandidatePair(s1, s2, sqrs);
				if ( candidatePair.size() == 2) {
					LinkedList<SDKSquare> sqrs2 = (LinkedList<SDKSquare>) sqrs.clone();
					sqrs2.remove(s1);
					sqrs2.remove(s2);
					
					SDKSquare s11 = new SDKSquare(s1.row,s1.col);
					SDKSquare s21 = new SDKSquare(s2.row,s2.col);
					s11.setPossible(candidatePair);
					s21.setPossible(candidatePair);
					sqrs2.add(s11);
					sqrs2.add(s21);
					
					if ( hasLoneCandidate(s,sqrs2) ) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static boolean hasLoneCandidate(SDKSquare s,
			LinkedList<SDKSquare> sqrs ) {
		HashSet<Integer> hs = s.getPossible();
		for(SDKSquare s2 : sqrs)
			hs.removeAll(s2.getPossible());
		return hs.size() == 1;
	}

	private static HashSet<Integer> getCandidatePair(SDKSquare s1, SDKSquare s2,
			LinkedList<SDKSquare> sqrs) {
		HashSet<Integer> sharedSet = s1.getPossible();
		sharedSet.retainAll(s2.getPossible());
		
		if ( sharedSet.size() < 2 )
		{
			sharedSet.clear();
			return sharedSet;
		}
		
		
		LinkedList<SDKSquare> sqrs2 = (LinkedList<SDKSquare>) sqrs.clone();
		sqrs2.remove(s1);
		sqrs2.remove(s2);
		//if one other sqrs has an intersection greater than one then return false
		for (SDKSquare s : sqrs2) {
			sharedSet.removeAll(s.getPossible());
		}
		return sharedSet;
	}
	
}
