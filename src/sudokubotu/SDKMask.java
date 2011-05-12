/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudokubotu;

/**
 *
 * @author Neisius
 */
public class SDKMask {

    SDKBoard mask = new SDKBoard();

    public void set(int row, int col, boolean bit) {
        mask.setSquareLock(row, col, bit);
    }

    public void get(int row, int col) {
        mask.isSquareLocked(row, col);
    }

    public int getNumberOfClues() {

        return mask.getNumberOfClues();
    }
}
