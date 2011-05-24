package sudokubotu;

import java.util.Map;

import diuf.sudoku.Grid;
import diuf.sudoku.solver.Rule;
import diuf.sudoku.solver.Solver;
import diuf.sudoku.solver.checks.BruteForceAnalysis;

public class SDKAnalysis {
	private double difficulty = 0;
	private String hardestRule = "";
	
	public static boolean hasUniqueSolution(SDKBoard b) {
		diuf.sudoku.Grid grid = new Grid();
    	BruteForceAnalysis bfa = new BruteForceAnalysis(false);
    	for(SDKSquare s : b.getAllSquares()){
            if (s.isLocked()){
    		grid.setCellValue(s.row, s.col, s.getValue());
            }
        }
		return bfa.getCountSolutions(grid) == 1;
	}
	
	public SDKAnalysis(SDKBoard board) {
		diuf.sudoku.Grid grid = new Grid();
    	BruteForceAnalysis bfa = new BruteForceAnalysis(false);
    	for(SDKSquare s : board.getAllSquares()){
            if (s.isLocked()){
    		grid.setCellValue(s.row, s.col, s.getValue());
            }
        }
    	Solver solver = new diuf.sudoku.solver.Solver(grid);
        solver.rebuildPotentialValues();
        try {
        	if ( bfa.getCountSolutions(grid) > 1)
        		throw new UnsupportedOperationException("Invalid number of solutions");
            Map<Rule,Integer> rules = solver.solve(null);
            for (Rule rule : rules.keySet()) {
                if (rule.getDifficulty() > difficulty) {
                    difficulty = rule.getDifficulty();
                    hardestRule = rule.getName();
                }
            }
        } catch (UnsupportedOperationException ex) {
            System.out.println(ex.getMessage());
        }
	}
	public double getRank() {
		return difficulty;
	}
	public String getHardestRule() {
		return hardestRule;
	}
}
