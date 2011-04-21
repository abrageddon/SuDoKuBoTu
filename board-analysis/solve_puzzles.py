import sqlite3,time
from Numberjack import *
#load puzzle from db
#start timer
#solve puzzle output nodes searched and solver name
#write solution to solution table with puzzle id
#create benchmark write nodes searched,time taken, solvution id, solver name puzzle id

def solve_all_puzzles():
	puzzle_count = 0
	conn = sqlite3.connect('sudoku.db')
	c = conn.cursor()
	ci = conn.cursor()
	c.execute('SELECT * FROM puzzles');
	for pid,puzzle,difficulty in c:
		solver = 'MiniSat'
		solution,solve_time,solve_size = solve_puzzle(3,puzzle)
		ci.execute('INSERT INTO solutions (solution,puzzle_id) VALUES (?,?)',(solution,pid))
		ci.execute('INSERT INTO benchmarks (solver,solve_time,node_count,solution_id,puzzle_id) VALUES (?,?,?,?,?)',(solver,solve_time,solve_size,ci.lastrowid,pid))
		puzzle_count+=1
		if puzzle_count%10 == 0:
			print 'compeleted:',puzzle_count
	ci.close()
	c.close()
	conn.commit()

def get_model(N,puzzle):
    grid = Matrix(N*N,N*N,1,N*N,'cell_')

    sudoku = Model( [AllDiff(row) for row in grid.row],
                    [AllDiff(col) for col in grid.col],
                    [AllDiff(grid[x:x+N, y:y+N].flat) for x in range(0,N*N,N) for y in range(0,N*N,N)],
                    [(x == int(v)) for (x,v) in zip(grid.flat, puzzle.replace('[','').replace(']','').split(',') ) if v != '0']
                   )
    return grid,sudoku

def solve_puzzle(N,puzzle):
    grid,sudoku = get_model(N,puzzle)
    solver = sudoku.load('MiniSat')
    solver.setVerbosity(0)
    solver.setTimeLimit(30)
    start = time.clock()
    solver.solve()
    end = time.clock()
    return str(grid),str(end - start),str(solver.getNodes())

solve_all_puzzles()

