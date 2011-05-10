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
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author Neisius
 */
public class SDKBoard {

    // this class holds all the information for a sudoku board
    //
    private int n;
    private SDKSquare[][] board;

    public SDKBoard() {
        this(9);
    }

    public SDKBoard(int n) {
        createBoard(n);
    }

    public SDKBoard(SDKSquare[][] newBoard) {
        board = newBoard;
        n = newBoard.length;
    }

    public SDKSquare[][] copyBoard() {
        return board.clone();
    }

    public Integer getBoardValue(Integer row, Integer col) {
        return board[row][col].getValue();
    }

    public boolean setBoardValue(Integer row, Integer col, Integer v) {
        if (row >= 0 && row < n
                && col >= 0 && col < n
                && v >= 0 && v <= n
                && !board[row][col].isLocked()) {
            this.board[row][col].setValue(v);
            return true;
        }
        return false;
    }

    /**
     * @return the n size of the board, must be a square
     */
    public int getN() {
        return n;
    }

    public final void createBoard(Integer newN) {
        if (newN % ((int) Math.sqrt(newN)) == 0) {
            this.n = newN;
            board = new SDKSquare[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    board[i][j] = new SDKSquare();
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
                    out.print(getBoardValue(row, col).toString());
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

                n = rowList.size();

                if (n != getN() && n != 0 && n % 3 == 0) {
                    createBoard(n);
                }

                // lock loaded non-zero values
                for (int i = 0; i < col; i++) {
                    setBoardValue(row, i, rowList.pop());
                    if (getBoardValue(row, i) != 0) {
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
}
