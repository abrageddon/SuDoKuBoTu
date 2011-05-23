package sudokubotu;

// Generates masks to be placed on to solved boards
//
// MaskFactory maps a solved board to a domain of puzzles
// A Mask creates a single puzzle in that domain of puzzles from the solved board
//
// Creates a CSP with specific constraints

public abstract class SDKMaskFactory {
	@SuppressWarnings("serial")
	public static class FailingRemoveException extends Exception {

		public FailingRemoveException(String string) {
			super(string);
		}
		
	}
}
