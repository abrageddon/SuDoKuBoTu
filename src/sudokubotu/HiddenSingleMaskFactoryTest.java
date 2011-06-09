package sudokubotu;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
@RunWith(Theories.class)

public class HiddenSingleMaskFactoryTest {

	@Before
	public void setUp() throws Exception {
		
	}
	
//	@Test
//	public void testSpeed() throws MaxRunException {
//		diuf.sudoku.generator.Generator gen = new Generator();
//		List<Symmetry> list = new LinkedList<Symmetry>();
//		list.add(Symmetry.None);
//		long c = 0;
//		for(int i = 0; i < 100; i ++) {
//			c = System.currentTimeMillis();
//			Grid g = gen.generate(list, 0.0, 1.5);
//			System.out.println((System.currentTimeMillis() - c));
//		}
//		System.out.println("\n");
//		for(int i = 0; i < 100; i ++) {
//			c = System.currentTimeMillis();
//			SDKBoard board = SDKBoard.generateSolvedBoard();
//			SDKMask mask = HiddenSingleMaskFactory.createMaskForBoard(board, 81);
//			board = mask.applyTo(board);
//			System.out.println((System.currentTimeMillis() - c));
//		}
//		
//		
//	}

	@Test
	public void checkerWorks() {
		int[][] test = {{0,0,0,0,0,0,0,0,0},
			{0,0,5,0,8,0,4,0,0},
			{0,4,6,7,2,9,0,5,1},
			{9,6,0,3,5,0,0,8,0},
			{0,0,0,0,6,0,9,3,5},
			{0,8,3,0,1,4,0,2,7},
			{1,7,0,5,0,0,2,0,3},
			{0,0,9,0,0,1,0,6,8},
			{0,5,4,2,3,8,7,0,0}};
		SDKBoard board = new SDKBoard();
		board.loadSimpleBoard(test);
		board.updateConstraints();
		assertTrue(HiddenSingleMaskFactory.isHiddenSingle(board.getSquare(5, 0), board));
	}
	
	@Theory
	public void puzzlesWontExceed1p2Diffculty(SDKBoard b) {
		for(int i = 0; i < 100; i++) {
			//SDKMask m = HiddenSingleMaskFactory.createMaskForBoard(b, 81);
//			SDKBoard puzzle = m.applyTo(b);
//			SDKAnalysis rank = new SDKAnalysis(puzzle);
//			assertEquals(1.2,rank.getRank(),0.1);
		}
	}
	
	public static @DataPoints SDKBoard[] boards = {
		SDKBoard.generateSolvedBoard(),
		SDKBoard.generateSolvedBoard(),
		SDKBoard.generateSolvedBoard(),
		SDKBoard.generateSolvedBoard() 
	};
	

}
