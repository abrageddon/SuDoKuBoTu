/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudokubotu;

/**
 *
 * @author Neisius
 */
public class SDKSquare {

    //Basic data for each square of Sudoku

    // value from 1 to n. 0 is blank
    private Integer value = 0;
    // values are locked when they are part of a loaded game board
    private Boolean locked = false;

    public Boolean isLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        if (!locked) {
            this.value = value;
        }
    }
}
