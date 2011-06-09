package sudokubotu;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theory;

import sudokubotu.DirectHiddenPairMaskFactory.DHPGoalCondition;

public class DirectHiddenPairMaskFactoryTest {

	
	
	@Test
	public void testMaskGoalCondition() {
		SDKBoard board = new SDKBoard();
//		int[][] simpleBoard =   {{8,0,0,0,7,0,0,0,0},
//								{1,6,0,9,0,3,0,0,0},
//								{2,3,7,0,4,5,0,0,0},
//								{0,7,0,0,0,0,6,0,0},
//								{9,0,8,0,3,0,4,0,5},
//								{0,0,6,0,0,0,0,8,0},
//								{5,8,1,2,6,4,7,9,3},
//								{6,4,3,7,9,1,5,2,8},
//								{7,0,0,3,5,8,1,4,6}};
		int[][] simpleBoard =	{{3,7,4,1,8,9,5,2,6},
			{8,5,2,3,6,4,7,9,1},
			{9,1,6,2,5,7,0,0,8},
			{0,0,0,5,9,8,0,0,0},
			{0,0,0,4,2,3,0,0,0},
			{0,0,0,6,7,1,0,0,0},
			{5,9,1,7,4,2,8,6,3},
			{6,2,3,8,1,5,0,7,0},
			{7,4,8,9,3,6,1,5,2}};
		board.loadSimpleBoard(simpleBoard);
		board.updateConstraints();
		SDKMask mask = new SDKMask();
		for(int i = 0; i < simpleBoard.length; i ++) {
			for(int j = 0; j < simpleBoard[0].length; j ++) {
				if ( simpleBoard[i][j] != 0) {
					mask.set(i, j, true);
				}
				else
				{
					mask.set(i,j,false);
				}
			}
		}
		
		DirectHiddenPairMaskFactory.DHPGoalCondition gc = new DHPGoalCondition(board);
		
		
		assertTrue(gc.satisfiedWith(mask));
	}
	public static @DataPoints SDKBoard[] boards = {new SDKBoard(),new SDKBoard()};
}
