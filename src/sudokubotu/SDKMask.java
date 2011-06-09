/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudokubotu;

/**
 *
 * @author Neisius
 */
public class SDKMask implements Comparable<SDKMask>{

    SDKBoard mask = new SDKBoard();
	String rule;
	double difficulty;

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
    
    public SDKMask getInverseMask() {
    	SDKMask inv = new SDKMask();
    	for (int row = 0; row < mask.getN(); row++) {
            for (int col = 0; col < mask.getN(); col++) {
                if (get(row, col))
                	inv.set(row,col,false);
                else
                	inv.set(row, col, true);
            }
    	}
    	return inv;
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

	@Override
	public int compareTo(SDKMask arg0) {
		return new MaskComparator().compare(this, arg0);
	}

	public String getRule() {
		return this.rule;
	}

	public double getDifficulty() {
		return this.difficulty;
	}
}
