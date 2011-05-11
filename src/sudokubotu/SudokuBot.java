package sudokubotu;

import java.awt.Point;
import java.util.Iterator;

public class SudokuBot {

    private SDKBoard board;
    private SDKBoard solver;
    private double difficulty = 0.0;

    public SudokuBot(SDKBoard currentBoard) {
        board = new SDKBoard(currentBoard.copyBoard());
        initSolverBoard();
    }

    public SDKBoard getSolution() {
        if (!solver.isSolved()) {


            if (easySolve()) {
                System.out.println("Easy Solver Worked");
                difficulty = 1.2;
            } else {
                System.out.println("Easy Solver Failed");
            }

//            if (bruteForceSolve()) {
//                System.out.println("Brute Force Solver Worked");
//                difficulty = 10.0;
//            } else {
//                System.out.println("Brute Force Solver Failed");
//            }

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

        if (!solver.isSolved()) {//solve if not solved yet
            getSolution();
        }

        return difficulty;
    }

    private boolean easySolve() {
//        solver.updateConstraints();

        while (!solver.isSolved()) {
            Point mcv = mostConstrainedValue();

            //DEBUG
            System.out.println(mcv + " <-mcv- " + solver.getSquarePossible(mcv.x, mcv.y));

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
                System.out.println(mcv + " -try-> " + soleCandidate);

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
        //TODO brute force solve

        return false;
    }

    private void initSolverBoard() {
        solver = new SDKBoard(board.getN());
        for (int row = 0; row < board.getN(); row++) {
            for (int col = 0; col < board.getN(); col++) {
                solver.setSquareValue(row, col, board.getSquareValue(row, col));
                solver.setSquareLock(row, col, board.isSquareLocked(row, col));
            }
        }

        solver.updateConstraints();
    }
}
