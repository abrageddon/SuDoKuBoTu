package sudokubotu;


public class Cell
	{
		private int x;
		private int y;
		private int n;

                // TODO -- CLARK -- Document this mystery class

		public Cell(int n)
		{
			this.n = n;
			x = 0;
			y = 0;
		}
		public Cell(int n, int x, int y)
		{
			this.n = n;
			this.x = x;
			this.y = y;
		}
		public Cell Next()
		{
			if(x < n-1)
				return new Cell(n,1+x,y);
			else if(y < n-1)
				return new Cell(n,0,1+y);
			else 
				return this;
		}
		public Cell Prev()
		{
			if(x > 1)
				return new Cell(n,x-1,y);
			else if(y > 1) 
				return new Cell(n,0,y-1); 
			else 
				return this;
			
		}
		public int getX()
		{
			return x;
		}
		public int getY()
		{
			return y;
		}
		public int getN()
		{
			return n;
		}
	}