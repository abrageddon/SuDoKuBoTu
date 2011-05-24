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

    public SDKMask() {
    	for(SDKSquare s : mask.getAllSquares())
    		s.setLocked(true);
    }
    
    public void set(int row, int col, boolean bit) {
        mask.setSquareLock(row, col, bit);
    }

    public boolean get(int row, int col) {
        return mask.isSquareLocked(row, col);
    }

    public int getNumberOfClues() {
        return mask.getNumberOfClues();
    }

    public SDKBoard applyTo(SDKBoard solved) {
        if (mask.getN() != solved.getN()) {
            return null;
        }//fail if not same size
        SDKBoard combined = new SDKBoard(solved);
        for (int row = 0; row < mask.getN(); row++) {
            for (int col = 0; col < mask.getN(); col++) {
                if (mask.isSquareLocked(row, col))
                {
                    combined.setSquareLock(row, col, false);
                    combined.setSquareValue(row, col, solved.getSquareValue(row, col));
                    combined.setSquareLock(row, col, true);
                }else{
                    combined.setSquareLock(row, col, false);
                    combined.setSquareValue(row, col, 0);
                }
            }
        }
        combined.updateConstraints();
        return combined;
    }
    
    @Override
    public String toString() {
        String out = "";

        for (int row = 0; row < mask.getN(); row++) {
            for (int col = 0; col < mask.getN(); col++) {
                out += (get(row, col)?"1":"0") + " ";
            }
            out += "\n";
        }

        return out;
    }
}
