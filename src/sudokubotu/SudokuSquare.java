import java.util.LinkedList;

public class SudokuSquare
	{
		Integer num = 0;
		LinkedList<Integer> prevNums;
		LinkedList<Integer> possible;
		boolean perm = false;
		public SudokuSquare()
		{
			num =0;
			prevNums = new LinkedList<Integer>();
			possible = new LinkedList<Integer>();
		}
		public SudokuSquare(int n)
		{
			num = n;
			prevNums = new LinkedList<Integer>();
			possible = new LinkedList<Integer>();
			perm = true;
		}
		public void Clear() {
			prevNums = new LinkedList<Integer>();
			possible = new LinkedList<Integer>();
			num =0;
			
		}
	}