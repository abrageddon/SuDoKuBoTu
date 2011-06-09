package sudokubotu;

import java.util.Comparator;
import java.util.LinkedList;

public class MaskComparator implements Comparator<SDKMask> {

	private int emptyZoneCount(SDKMask mask) {
		int n = 0;
		for(int i = 0; i < 9; i++) {
			LinkedList<SDKSquare> sqrz = mask.mask.getSquaresInZone(i);
			sqrz.retainAll(mask.mask.getAllSquaresWithClues());
			if ( sqrz.isEmpty() ) {
				n++;
			}
		}
		return n;
	}
	
	@Override
	public int compare(SDKMask arg0, SDKMask arg1) {
		return ((emptyZoneCount(arg1)*36)-arg1.getNumberOfClues()) - (emptyZoneCount(arg0)*36-arg0.getNumberOfClues());
	}

}
