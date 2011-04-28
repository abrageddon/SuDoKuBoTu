import java.util.Random;




public class Sudoku 
{
	private int totalNodes;
	public Sudoku()
	{
		totalNodes = 0;
	}
	public boolean Solve(SudokuSquare[][] board,Cell cells)
	{
		if(board[cells.getX()][cells.getY()].perm)
		{
			if(!cells.Next().equals(cells))
				return Solve(board,cells.Next());
			else{
				System.out.println("Solved");
				return true;
			}
		}
		if(board[cells.getX()][cells.getY()].possible.isEmpty())
		{
			for(int i = 0; i<cells.getN() ; i++)
				board[cells.getX()][cells.getY()].possible.add(i+1);
			board[cells.getX()][cells.getY()].possible.removeAll(board[cells.getX()][cells.getY()].prevNums);
			for(int i = 0; i<cells.getN() ; i++)
			{
				board[cells.getX()][cells.getY()].possible.remove((Integer)board[i][cells.getY()].num);
				board[cells.getX()][cells.getY()].possible.remove((Integer)board[cells.getX()][i].num);
			}
			for(int i = (cells.getX()/((int)Math.sqrt(cells.getN())))*((int)Math.sqrt(cells.getN())); i<(cells.getX()/((int)Math.sqrt(cells.getN()))+1)*((int)Math.sqrt(cells.getN())); i++)
				for(int j = (cells.getY()/((int)Math.sqrt(cells.getN())))*((int)Math.sqrt(cells.getN())); j<(cells.getY()/((int)Math.sqrt(cells.getN()))+1)*((int)Math.sqrt(cells.getN())); j++)
				{
					board[cells.getX()][cells.getY()].possible.remove((Integer)board[i][j].num);
				}
		}
		if(board[cells.getX()][cells.getY()].possible.isEmpty())
		{
			totalNodes++;
			if(cells.Prev().equals(cells))
			{
				return false;
			}
			else
			{
				board[cells.getX()][cells.getY()].Clear();
				
				cells = cells.Prev();
				while(board[cells.getX()][cells.getY()].perm && cells == cells.Prev())
				{
					cells = cells.Prev();
				}
				return Solve(board,cells);
			}
		}
		Random gen = new Random();
		int take =gen.nextInt(board[cells.getX()][cells.getY()].possible.size());
		board[cells.getX()][cells.getY()].num = board[cells.getX()][cells.getY()].possible.get(take);
		board[cells.getX()][cells.getY()].prevNums.add(board[cells.getX()][cells.getY()].possible.remove(take));
			
		if(!cells.Next().equals(cells))	
			return Solve(board,cells.Next());
		else
			return true;
	}
	public int Nodes()
	{
		return totalNodes;
	}
}
