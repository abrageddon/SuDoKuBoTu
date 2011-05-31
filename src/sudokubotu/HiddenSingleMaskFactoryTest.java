package sudokubotu;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
@RunWith(Theories.class)

public class HiddenSingleMaskFactoryTest {

	@Before
	public void setUp() throws Exception {
		
	}
	
	@Theory
	public void puzzlesWontExceed1p2Diffculty(SDKBoard b) {
		for(int i = 0; i < 100; i++) {
			SDKMask m = HiddenSingleMaskFactory.createMaskForBoard(b, 81);
			SDKBoard puzzle = m.applyTo(b);
			SDKAnalysis rank = new SDKAnalysis(puzzle);
			assertEquals(1.2,rank.getRank(),0.31);
		}
	}
	
	public static @DataPoints SDKBoard[] boards = {
		SDKBoard.generateSolvedBoard(),
		SDKBoard.generateSolvedBoard(),
		SDKBoard.generateSolvedBoard(),
		SDKBoard.generateSolvedBoard() 
	};
	

}
