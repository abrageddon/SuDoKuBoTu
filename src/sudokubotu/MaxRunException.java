package sudokubotu;

public class MaxRunException extends Exception {

	Object v;

	public MaxRunException(Object v) {
		this.v = v;
	}

}
