package sudokubotu;

import diuf.sudoku.Grid;
import diuf.sudoku.solver.Rule;
import diuf.sudoku.solver.Solver;
import diuf.sudoku.solver.checks.BruteForceAnalysis;
import java.awt.Point;
import java.util.Iterator;
import java.util.Map;

public class SudokuBot {

    private SDKBoard board;
    private SDKBoard solver;

    public SudokuBot(SDKBoard currentBoard) {
        board = new SDKBoard(currentBoard.copyBoard());
        initSolverBoard();
    }

    public SDKBoard getSolution() {
        if (!solver.isSolved()) {


            if (easySolve()) {
                System.out.println("Easy Solver Worked");
            } else {
                System.out.println("Easy Solver Failed");
                if (bruteForceSolve()) {
                    System.out.println("Brute Force Solver Worked");
                } else {
                    System.out.println("Brute Force Solver Failed");
                }
            }
            System.out.println("Unique?" + isUnique());

        }

        //Convert board to proper format
        SDKBoard solution = new SDKBoard(board.getN());
        for (int i = 0; i < board.getN(); i++) {
            for (int j = 0; j < board.getN(); j++) {
                solution.setSquareValue(i, j, solver.getSquareValue(i, j));//set values to solution
                solution.setSquareLock(i, j, board.isSquareLocked(i, j));//lock only original puzzle squares
            }
        }
        return solution;
    }

    public double getBoardDifficulty() {
        // TODO calculate difficulty
        diuf.sudoku.Grid grid = new Grid();
        BruteForceAnalysis bfa = new BruteForceAnalysis(false);
        for (SDKSquare s : board.getAllSquares()) {
            if (s.isLocked()) {
                grid.setCellValue(s.row, s.col, s.getValue());
            }
        }
        Solver expSolver = new diuf.sudoku.solver.Solver(grid);
        expSolver.rebuildPotentialValues();
        try {
            if (bfa.getCountSolutions(grid) > 1) {
                throw new UnsupportedOperationException("Invalid number of solutions");
            }
            Map<Rule, Integer> rules = expSolver.solve(null);
            double difficulty = 0;
            for (Rule rule : rules.keySet()) {
                if (rule.getDifficulty() > difficulty) {
                    difficulty = rule.getDifficulty();
                }
            }
            return difficulty;
        } catch (UnsupportedOperationException ex) {
            System.out.println(ex.getMessage());
        }
        return 0.0;
    }

    private boolean easySolve() {
        solver.updateConstraints();

        while (!solver.isSolved()) {
            Point mcv = mostConstrainedValue();

            //DEBUG
//            System.out.println(mcv + " <-mcv- " + solver.getSquarePossible(mcv.x, mcv.y));

            if (!setSoleCandidate(mcv)) {
                return false;
            } // fail if cant be solved by direct constraint checking

            solver.updateConstraints();

        }
        return true;
    }

    private boolean setSoleCandidate(Point mcv) {
        if (mcv != null && solver.getSquarePossible(mcv.x, mcv.y).size() == 1) {
            Iterator value = solver.getSquarePossible(mcv.x, mcv.y).iterator();
            if (value.hasNext()) {
                int soleCandidate = (Integer) value.next();

                //DEBUG
//                System.out.println(mcv + " -try-> " + soleCandidate);

                solver.setSquareValue(mcv.x, mcv.y, soleCandidate);


                value.remove();
            }
        } else {
            return false; // no sole candidate
        }
        return true;
    }

    private Point mostConstrainedValue() {
        Point mcv = null; //most constrained value
        for (int row = 0; row < solver.getN(); row++) {
            for (int col = 0; col < solver.getN(); col++) {
                if (!solver.getSquarePossible(row, col).isEmpty() && mcv == null) {
                    mcv = new Point(row, col);
                } else if (!solver.getSquarePossible(row, col).isEmpty()
                        && (solver.getSquarePossible(mcv.x, mcv.y).size()
                        > solver.getSquarePossible(row, col).size())) {
                    mcv = new Point(row, col);
                }
            }
        }
        return mcv;
    }

    private boolean bruteForceSolve() {
        // brute force solve
        int[][] simpleBoard = board.getSimpleBoard();
        if (solve(0, 0, simpleBoard)) {
            solver.loadSimpleBoard(simpleBoard);
            return true;
        }
        /*        if (solveReverse(solver.getN()-1, solver.getN()-1, simpleBoard)) {
        solver.loadSimpleBoard(simpleBoard);
        return true;
        }*/
        return false;
    }

    private boolean solve(int i, int j, int[][] cells) {
        // i and j must start at 0
        if (i == solver.getN()) {
            i = 0;
            if (++j == solver.getN()) {
                return true;
            }
        }
        if (cells[i][j] != 0) // skip filled cells
        {
            return solve(i + 1, j, cells);
        }

        for (int val = 1; val <= solver.getN(); ++val) {
            if (legal(i, j, val, cells)) {//valid
                cells[i][j] = val;
                if (solve(i + 1, j, cells)) {
                    return true;
                }
            }
        }
        cells[i][j] = 0; // reset on backtrack
        return false;
    }

    private boolean solveReverse(int i, int j, int[][] cells) {
        // TODO reverse solve
        // i and j start at N-1 for the board
        if (i == -1) {
            i = solver.getN() - 1;
            if (--j == -1) {
                return true;
            }
        }
        if (cells[i][j] != 0) // skip filled cells
        {
            return solveReverse(i - 1, j, cells);
        }

        for (int val = 1; val <= solver.getN(); ++val) {
            if (legal(i, j, val, cells)) {//valid
                cells[i][j] = val;
                if (solveReverse(i - 1, j, cells)) {
                    return true;
                }
            }
        }
        cells[i][j] = 0; // reset on backtrack
        return false;
    }

    boolean isUnique() {
        diuf.sudoku.Grid grid = new Grid();
        BruteForceAnalysis bfa = new BruteForceAnalysis(false);
        for (SDKSquare s : board.getAllSquares()) {
            if (s.isLocked()) {
                grid.setCellValue(s.row, s.col, s.getValue());
            }
        }
        Solver solver = new diuf.sudoku.solver.Solver(grid);
        solver.rebuildPotentialValues();

        if (bfa.getCountSolutions(grid) > 1) {
            return false;
        }
        return true;
        /*
        int[][] solveForward = board.getSimpleBoard();
        int[][] solveBackward = board.getSimpleBoard();
        if (solve(0, 0, solveForward)) {
        if (solveReverse(solver.getN() - 1, solver.getN() - 1, solveBackward)) {
        for (int row = 0; row < solver.getN(); row++) {
        for (int col = 0; col < solver.getN(); col++) {
        if(solveForward[row][col] != solveBackward[row][col]){
        return false;
        }
        }
        }
        return true; // if solving foreward and backward are equiv
        }
        }
        return false;*/
    }

    boolean legal(int i, int j, int val, int[][] cells) {
        for (int k = 0; k < solver.getN();
                ++k) // row
        {
            if (val == cells[k][j]) {
                return false;
            }
        }

        for (int k = 0; k < solver.getN();
                ++k) // col
        {
            if (val == cells[i][k]) {
                return false;
            }
        }

        int boxRowOffset = (i / solver.getZonesInRow()) * solver.getZonesInRow();
        int boxColOffset = (j / solver.getZonesInRow()) * solver.getZonesInRow();
        for (int k = 0; k
                < solver.getZonesInRow();
                ++k) // box
        {
            for (int m = 0; m
                    < solver.getZonesInRow();
                    ++m) {
                if (val == cells[boxRowOffset + k][boxColOffset + m]) {
                    return false;
                }
            }
        }
        return true; // no violations, so it's legal
    }

    private void initSolverBoard() {
        solver = new SDKBoard(board.getN());

        for (int row = 0; row < board.getN(); row++) {
            for (int col = 0; col < board.getN(); col++) {
                solver.setSquareLock(row, col, false);
                solver.setSquareValue(row, col, board.getSquareValue(row, col));
                solver.setSquareLock(row, col, board.isSquareLocked(row, col));
            }
        }
        solver.updateConstraints();
    }
}
