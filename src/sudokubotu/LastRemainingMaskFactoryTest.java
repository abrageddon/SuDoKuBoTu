package sudokubotu;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class LastRemainingMaskFactoryTest {

	@Before
	public void setUp() throws Exception {
	}

	@Theory
	public void puzzlesWillOnlyHaveOneOrZeronValueInEachSquaresDomain(SDKBoard board) {
		for(int i = 0; i < 100; i ++) {
			SDKMask mask = LastRemainingMaskFactory.createMaskForBoard(board, 81);
			SDKBoard puzzle = mask.applyTo(board);
			LinkedList<SDKSquare> squares = puzzle.getAllSquares();
			for(SDKSquare s : squares)
				assertTrue(s.getPossible().size() == 1 || s.getPossible().size() == 0);
		}
	}
	
	public static @DataPoints SDKBoard[] boards = {
		SDKBoard.generateSolvedBoard(),
		SDKBoard.generateSolvedBoard(),
		SDKBoard.generateSolvedBoard(),
		SDKBoard.generateSolvedBoard() };
	

}
