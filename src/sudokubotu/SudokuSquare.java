package sudokubotu;

import java.util.HashSet;

public class SudokuSquare extends SDKSquare {

    HashSet<Integer> prevNums;
    HashSet<Integer> possible;

    public SudokuSquare() {
        Clear();
    }

    public SudokuSquare(int n) {
        Clear();
        setValue(n);
        setLocked(Boolean.TRUE);
    }

    SudokuSquare(Integer boardValue, boolean squareLocked) {
        Clear();
        setValue(boardValue);
        setLocked(squareLocked);
    }

    public final void Clear() {
        prevNums = new HashSet<Integer>();
        possible = new HashSet<Integer>();
        setValue(0);
        setLocked(Boolean.FALSE);
    }
}
