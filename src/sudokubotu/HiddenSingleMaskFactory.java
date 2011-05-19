package sudokubotu;

public class HiddenSingleMaskFactory extends SDKMaskFactory {
	public static SDKMask createMaskForBoard(SDKBoard solvedBoard,int maxRemoved) {
		SDKBoard board = new SDKBoard(solvedBoard.copyBoard());
		SDKMask mask = new SDKMask();
		
		return mask;
	}
}
