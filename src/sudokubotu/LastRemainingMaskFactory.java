package sudokubotu;

import java.util.Collections;
import java.util.LinkedList;

// 1.0 rating

public class LastRemainingMaskFactory extends SDKMaskFactory {
	
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
				for ( SDKSquare square2 : squares ) {
					if ( square2.getPossible().size() > 1 )
						throw new FailingRemoveException("Causes other square's domain to be too large");
				}
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
