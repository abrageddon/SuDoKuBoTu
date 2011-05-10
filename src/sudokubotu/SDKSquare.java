package sudokubotu;

import java.util.HashSet;


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
    // possible values
    private HashSet<Integer> possible;

    SDKSquare() {
        setValue(0);
        setLocked(false);
        possible = new HashSet<Integer>();
    }
    
    SDKSquare(Integer value) {
        this();
        setValue(value);
        if (value != 0){
            setLocked(true);
        }
    }

    SDKSquare(Integer value, Boolean locked) {
        this(value);
        setLocked(locked);
    }

    public Boolean isLocked() {
        return locked;
    }

    public final void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Integer getValue() {
        return value;
    }

    public final void setValue(Integer value) {
        if (!locked) {
            this.value = value;
        }
    }

    public boolean addPossible(int val){
        return possible.add(value);
    }

    public boolean removePossible(int val){
        return possible.remove(value);
    }
    
    public boolean isPossible(int value){
        return possible.contains(value);
    }

    public HashSet<Integer> getPossible (){
        // Return a copy of possible values
//        HashSet<Integer> ret = new HashSet<Integer>((possible!=null?possible.size():0));
//        for (Integer val:possible){
//            ret.add(val);
//        }
        return possible;
    }
}
