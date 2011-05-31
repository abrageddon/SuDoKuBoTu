package sudokubotu;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

// 1.0 rating

public class LastRemainingMaskFactory extends SDKMaskFactory {
	
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
		LinkedList<SDKSquare> squares = board.getAllSquares();
		
		Collections.shuffle(squares);
		
		for ( SDKSquare square : squares ) {
			int value = square.getValue();
			try {
				if ( maxRemoved-- < 0 )
					break;
				square.setLocked(false);
				square.setValue(0);
				board.updateConstraints();
				
				if ( !isLastRemaining(board,square) )
					throw new FailingRemoveException("Causes other square's domain to be too large");
				
				mask.set(square.row, square.col, false);
			} catch (FailingRemoveException e) {
				// reset square before the remove
				square.setValue(value);
				square.setLocked(true);
				board.updateConstraints();
			}
		}
		return mask;
	}
}
