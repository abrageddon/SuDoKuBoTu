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
    
    SDKSquare(Integer v) {
        this();
        setValue(v);
        if (v != 0){
            setLocked(true);
        }
    }

    SDKSquare(Integer v, Boolean locked) {
        this(v);
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

    public final void setValue(Integer v) {
        if (!locked) {
            this.value = v;
        }
    }

    public boolean addPossible(int posVal){
        if (posVal != 0 ){//zero is never a possibility
            return possible.add(posVal);
        }else{return false;}
    }

    public boolean removePossible(int posVal){
        return possible.remove(posVal);
    }
    
    public boolean isPossible(int posVal){
        return possible.contains(posVal);
    }

    public HashSet<Integer> getPossible (){
        // Return a copy of possible values
//        HashSet<Integer> ret = new HashSet<Integer>((possible!=null?possible.size():0));
//        for (int posVal:possible){
//            ret.add(posVal);
//        }
        return possible;
    }

    void clearPossible() {
        possible.clear();
    }
}
