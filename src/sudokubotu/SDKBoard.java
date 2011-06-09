package sudokubotu;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import diuf.sudoku.Grid;
import diuf.sudoku.solver.checks.BruteForceAnalysis;

/**
 *
 * @author Neisius
 */
public class SDKBoard {

    // this class holds all the information for a sudoku board
    //
    private SDKSquare[][] board;

    public SDKBoard() {
        this(9);
    }

    public SDKBoard(int n) {
        createBoard(n);
        updateConstraints();
    }

    public SDKBoard(SDKSquare[][] newBoard) {
        board = new SDKSquare[newBoard.length][newBoard.length];
        for (int row = 0; row < getN(); row++) {
            for (int col = 0; col < getN(); col++) {
                board[row][col] = new SDKSquare(newBoard[row][col].getValue(), newBoard[row][col].isLocked(), row, col);
                for (int posVal : newBoard[row][col].getPossible()) {
                    board[row][col].addPossible(posVal);
                }
            }
        }
        updateConstraints();
    }

    SDKBoard(SDKBoard solved) {
        this(solved.board);
    }

    public SDKSquare[][] copyBoard() {
        SDKSquare[][] copy = new SDKSquare[getN()][getN()];
        for (int row = 0; row < getN(); row++) {
            for (int col = 0; col < getN(); col++) {
                copy[row][col] = new SDKSquare(board[row][col].getValue(), board[row][col].isLocked(), row, col);
                for (int posVal : board[row][col].getPossible()) {
                    copy[row][col].addPossible(posVal);
                }
            }
        }

        return copy;
    }

    public static SDKBoard generateSolvedBoard() {
        Grid grid = new Grid();
        BruteForceAnalysis analyser = new BruteForceAnalysis(true);
        boolean result = analyser.solveRandom(grid, new Random());
        assert result;
        Grid solution = new Grid();
        grid.copyTo(solution);

        SDKBoard generated = new SDKBoard();
        for (SDKSquare s : generated.getAllSquares()) {
            int val = solution.getCellValue(s.row, s.col);
            s.setValue(val);
            s.setLocked(true);
        }

        return generated;
    }

    public Integer getSquareValue(Integer row, Integer col) {
        return board[row][col].getValue();
    }

    public boolean setSquareValue(Integer row, Integer col, Integer v) {
        if (row >= 0 && row < getN()
                && col >= 0 && col < getN()
                && v >= 0 && v <= getN()
                && !board[row][col].isLocked()) {
            this.board[row][col].setValue(v);
            return true;
        }
        return false;
    }

    /**
     * @return the n size of the board, must be a square
     */
    public final int getN() {
        return board.length;
    }

    public final void createBoard(Integer newN) {
        if (newN % ((int) Math.sqrt(newN)) == 0) {
            board = new SDKSquare[newN][newN];
            for (int i = 0; i < newN; i++) {
                for (int j = 0; j < newN; j++) {
                    board[i][j] = new SDKSquare(i, j);
                }
            }
        }
    }

    public boolean isSolved() {
        for (int row = 0; row < getN(); row++) {
            for (int col = 0; col < getN(); col++) {
                if (board[row][col].getValue() <= 0) {
                    return false;
                }
            }
        }

        if (!conflictedSquares().isEmpty()) {
            return false;
        }
        return true;
    }

    public Set<Point> conflictedSquares() {
        // returns conflicted squares set
        Set<Point> conflict = new HashSet<Point>();
        HashMap<Integer, Point> used = new HashMap<Integer, Point>();

        // Check Rows
        for (int row = 0; row < getN(); row++) {
            for (int col = 0; col < getN(); col++) {
                if (board[row][col].getValue() != 0) {
                    if (used.containsKey(board[row][col].getValue())) {
                        conflict.add(new Point(row, col));
                        conflict.add(used.get(board[row][col].getValue()));
                    } else if (board[row][col].getValue() != 0) {
                        used.put(board[row][col].getValue(), new Point(row, col));
                    }
                }
            }
            used.clear();
        }

        // Check Col
        for (int col = 0; col < getN(); col++) {
            for (int row = 0; row < getN(); row++) {
                if (board[row][col].getValue() != 0) {
                    if (used.containsKey(board[row][col].getValue())) {
                        conflict.add(new Point(row, col));
                        conflict.add(used.get(board[row][col].getValue()));
                    } else if (board[row][col].getValue() != 0) {
                        used.put(board[row][col].getValue(), new Point(row, col));
                    }
                }
            }
            used.clear();
        }


        // check zones
        for (int zone = 0; zone < getN(); zone++) {
            HashMap<Point, Integer> currentZone = getBoardZone(zone);

            for (Point pt : currentZone.keySet()) {
                int row = pt.x;
                int col = pt.y;
                if (board[row][col].getValue() != 0) {
                    if (used.containsKey(board[row][col].getValue())) {
                        conflict.add(new Point(row, col));
                        conflict.add(used.get(board[row][col].getValue()));
                    } else if (board[row][col].getValue() != 0) {
                        used.put(board[row][col].getValue(), new Point(row, col));
                    }
                }
            }
            used.clear();
        }


        return conflict;
    }

    public Integer identifyZone(Integer row, Integer col) {
        // identify zones by using integer division to find region
        int zonesInRow = (int) Math.sqrt(getN());
        return (row / zonesInRow) * zonesInRow + (col / zonesInRow);
    }

    public int getZonesInRow() {
        return (int) Math.sqrt(getN());
    }

    public HashMap<Point, Integer> getBoardZone(Integer zone) {
        // get Board Zone of n squares
        // for 9x9
        // 0 1 2
        // 3 4 5
        // 6 7 8

        int z = zone;
        int zonesInRow = getZonesInRow();

        int minRow = 0;
        int minCol = 0;
        int maxRow = getN() / zonesInRow;
        int maxCol = getN() / zonesInRow;

        //find the row by counting columns
        while (z >= zonesInRow) {
            z -= zonesInRow;
            minRow += getN() / zonesInRow;
            maxRow += getN() / zonesInRow;
        }

        // move over to proper column
        while (z > 0) {
            z -= 1;
            minCol += getN() / zonesInRow;
            maxCol += getN() / zonesInRow;
        }

        // get zone
        HashMap<Point, Integer> zonePts = new HashMap<Point, Integer>();
        for (int row = minRow; row < maxRow; row++) {
            for (int col = minCol; col < maxCol; col++) {
                zonePts.put(new Point(row, col), board[row][col].getValue());
            }
        }
        return zonePts;
    }

    public HashMap<Point, Integer> getBoardRow(Integer row) {
        // get Board row
        HashMap<Point, Integer> rowPts = new HashMap<Point, Integer>();
        for (int i = 0; i < getN(); i++) {
            rowPts.put(new Point(row, i), board[row][i].getValue());
        }
        return rowPts;
    }

    public HashMap<Point, Integer> getBoardCol(Integer col) {
        HashMap<Point, Integer> colPts = new HashMap<Point, Integer>();
        for (int i = 0; i < getN(); i++) {
            colPts.put(new Point(i, col), board[i][col].getValue());
        }
        return colPts;
    }

    public boolean saveBoard(File file) {
        try {
            FileWriter outFile = new FileWriter(file);
            PrintWriter out = new PrintWriter(outFile);

            for (int row = 0; row < getN(); row++) {
                for (int col = 0; col < getN(); col++) {
                    if (col != 0) {
                        out.print(",");
                    }
                    out.print(getSquareValue(row, col).toString());
                }
                out.println();
            }

            out.close();
            outFile.close();


        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean loadBoard(File file) {
        try {
            FileReader inFile = new FileReader(file);
            BufferedReader in = new BufferedReader(inFile);

            String line = null;
            int row = 0;
            int col = 0;
            LinkedList<Integer> rowList = new LinkedList<Integer>();

            while ((line = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, ",");

                while (st.hasMoreTokens()) {

                    rowList.add(Integer.valueOf(st.nextToken()));
                    col++;
                }

                int newN = rowList.size();

                if (newN != getN()
                        && newN != 0
                        && newN % ((int) Math.sqrt(newN)) == 0) {
                    createBoard(newN);
                }

                // lock loaded non-zero values
                for (int i = 0; i < col; i++) {
                    board[row][i].setLocked(false);
                    setSquareValue(row, i, rowList.pop());
                    if (getSquareValue(row, i) != 0) {
                        board[row][i].setLocked(true);
                    }
                }


                row++;
                col = 0;
                rowList.clear();
            }
            in.close();
            inFile.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    boolean isSquareLocked(int row, int col) {
        return board[row][col].isLocked();
    }

    void setSquareLock(int row, int col, boolean value) {
        board[row][col].setLocked(value);
    }

    @Override
    public String toString() {
        String out = "";

        for (int row = 0; row < getN(); row++) {
            for (int col = 0; col < getN(); col++) {
                out += getSquareValue(row, col) + " ";
            }
            out += "\n";
        }

        return out;
    }

    public final void updateConstraints() {
        // remove new values from all constraints
        for (int row = 0; row < getN(); row++) {
            for (int col = 0; col < getN(); col++) {
                if (!isSquareLocked(row, col)) {
                    for (int i = 1; i <= getN(); i++) {
                        //initalize possible values
                        board[row][col].addPossible(i);
                    }
                    //DEBUG
//                    System.out.println("("+row+","+col+")POS:"+board[row][col].getPossible());


                    //DEBUG
//                    System.out.println("("+row+","+col+")CON:"+getConstraints(row, col));

                    for (int usedVal : getConstraints(row, col)) {
                        //remove direct conflicts
                        board[row][col].removePossible(usedVal);
                    }
                    //DEBUG
//                    System.out.println("(" + row + "," + col + ")POS:" + board[row][col].getPossible());

                } else {
                    board[row][col].clearPossible();
                }
            }
        }
    }

    public HashSet<Integer> getConstraints(int row, int col) {
        // Returns a set of the numbers that square (row,col) can not be

        HashSet<Integer> cantBeValues = new HashSet<Integer>();

        // check only non set values
        if (!isSquareLocked(row, col)) {

            // get values in zone
            HashMap<Point, Integer> boardZone = getBoardZone(identifyZone(row, col));
            for (int value : boardZone.values()) {
                if (value != 0) {
                    cantBeValues.add(value);
                }
            }

            // get values in row
            HashMap<Point, Integer> boardRow = getBoardRow(row);
            for (int value : boardRow.values()) {
                if (value != 0) {
                    cantBeValues.add(value);
                }
            }

            // get values in col
            HashMap<Point, Integer> boardCol = getBoardCol(col);
            for (int value : boardCol.values()) {
                if (value != 0) {
                    cantBeValues.add(value);
                }
            }
        }

        return cantBeValues;
    }

    HashSet<Integer> getSquarePossible(int x, int y) {
        return board[x][y].getPossible();
    }

    public int getNumberOfClues() {
        int clues = 0;
        for (int row = 0; row < getN(); row++) {
            for (int col = 0; col < getN(); col++) {
                if (board[row][col].isLocked()) {
                    clues++;
                }
            }
        }
        return clues;
    }

    public LinkedList<SDKSquare> getAllSquares() {
        LinkedList<SDKSquare> squares = new LinkedList<SDKSquare>();
        for (int row = 0; row < getN(); row++) {
            for (int col = 0; col < getN(); col++) {
                squares.add(board[row][col]);
            }
        }
        return squares;
    }

    int[][] getSimpleBoard() {
        int[][] simpleBoard = new int[getN()][getN()];
        for (int row = 0; row < getN(); row++) {
            for (int col = 0; col < getN(); col++) {
                simpleBoard[row][col] = board[row][col].getValue();
            }
        }
        return simpleBoard;
    }

    public LinkedList<SDKSquare> getAllSquaresWithClues() {
        LinkedList<SDKSquare> squares = getAllSquares();
        LinkedList<SDKSquare> ret = new LinkedList<SDKSquare>();
        for (SDKSquare s : squares) {
            if (s.isLocked()) {
                ret.add(s);
            }
        }
        return ret;
    }

    public LinkedList<SDKSquare> getSquaresInZone(int zone) {
        LinkedList<SDKSquare> squares = new LinkedList<SDKSquare>();
        HashMap<Point, Integer> hm = getBoardZone(zone);
        for (Point p : hm.keySet()) {
            squares.add(board[p.x][p.y]);
        }
        return squares;
    }

    public LinkedList<SDKSquare> getSquaresInZone(SDKSquare square) {
        return getSquaresInZone(identifyZone(square.row, square.col));
    }

    void loadSimpleBoard(int[][] simpleBoard) {
        if (simpleBoard.length == board.length) {
            for (int row = 0; row < getN(); row++) {
                for (int col = 0; col < getN(); col++) {
                    board[row][col].setLocked(false);
                    board[row][col].setValue(simpleBoard[row][col]);
                    if (simpleBoard[row][col] != 0) {
                        board[row][col].setLocked(true);
                    }
                }
            }
        }
    }

//    void rearrange() {
//        // TODO pillar column row and ribbon permutations
//    }

    public LinkedList<SDKSquare> getSquaresInRow(int row) {

        LinkedList<SDKSquare> squares = new LinkedList<SDKSquare>();
        for (Point p : getBoardRow(row).keySet()) {
            squares.add(board[p.x][p.y]);
        }
        return squares;
    }

    public LinkedList<SDKSquare> getSquaresInCol(int col) {
        LinkedList<SDKSquare> squares = new LinkedList<SDKSquare>();
        for (Point p : getBoardCol(col).keySet()) {
            squares.add(board[p.x][p.y]);
        }
        return squares;
    }

	public SDKSquare getSquare(int i, int j) {
		return board[j][i];
	}
}
