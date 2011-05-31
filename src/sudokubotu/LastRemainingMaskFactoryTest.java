package sudokubotu;

import static org.junit.Assert.assertEquals;
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
	public void puzzlesWontExceed1p0Difficulty(SDKBoard b) {
		for(int i = 0; i < 100; i++) {
			SDKMask m = LastRemainingMaskFactory.createMaskForBoard(b, 81);
			SDKBoard puzzle = m.applyTo(b);
			SDKAnalysis rank = new SDKAnalysis(puzzle);
			assertEquals(1.0,rank.getRank(),0.1);
		}
	}
	
	public static @DataPoints SDKBoard[] boards = {
		SDKBoard.generateSolvedBoard(),
		SDKBoard.generateSolvedBoard(),
		SDKBoard.generateSolvedBoard(),
		SDKBoard.generateSolvedBoard() };
	

}
