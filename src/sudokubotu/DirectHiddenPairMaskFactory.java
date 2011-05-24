package sudokubotu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;


public class DirectHiddenPairMaskFactory extends SDKMaskFactory {

	public static SDKMask createMaskForBoard(SDKBoard solvedBoard,int maxRemoved) {
		SDKBoard board = new SDKBoard(solvedBoard.copyBoard());
		SDKMask mask = LastRemainingMaskFactory.createMaskForBoard(solvedBoard, maxRemoved);
		board = mask.applyTo(board);
		LinkedList<SDKSquare> squares = board.getAllSquaresWithClues();
		for(SDKSquare s : squares) {
			int v = s.getValue();
			try {
				s.setLocked(false);
				s.setValue(0);
				board.updateConstraints();
				
				if ( s.getPossible().size() < 3 )
					throw new FailingRemoveException("domain not large enough for direct hidden pair");
				
				LinkedList<SDKSquare> zsqrs = board.getSquaresInZone(s);
				zsqrs.remove(s);
				LinkedList<SDKSquare> csqrs = board.getSquaresInCol(s.col);
				csqrs.remove(s);
				LinkedList<SDKSquare> rsqrs = board.getSquaresInRow(s.row);
				rsqrs.remove(s);
				
				boolean zoneHas = hasDirectPairThatSolves(zsqrs,s,board);
				boolean colHas = hasDirectPairThatSolves(csqrs,s,board);
				boolean rowHas = hasDirectPairThatSolves(rsqrs,s,board);
				
				if ((zoneHas?1:0) + (colHas?1:0) + (rowHas?1:0) != 1)
					throw new FailingRemoveException("Does not have direct pair");
				
				if ( SDKAnalysis.hasUniqueSolution(board) )
					throw new FailingRemoveException("Board is non-unique solvable");
				
				mask.set(s.row, s.col, false);
			} catch (FailingRemoveException e) {
				s.setValue(v);
				s.setLocked(true);
			}
		}
		return mask;
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
