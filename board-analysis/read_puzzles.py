#for each difficulty
#	for each file
#		insert(data_from_file,difficulty
difficulties = ["Easy","Medium","Hard","Insane"];
import os,sqlite3
PUZZLE_DIR = "./Gameboards/"
conn = sqlite3.connect('sudoku.db')
c = conn.cursor()
for difficulty in difficulties:
	difficulty_dir = difficulty+"/"
	for filename in os.listdir(PUZZLE_DIR+difficulty):
		ofile = open(PUZZLE_DIR+difficulty_dir+filename)
		puzzle = ofile.read()
		c.execute("INSERT INTO puzzles (puzzle,difficulty) VALUES (?,?)",(puzzle,difficulty))
		ofile.close()
c.close()
conn.commit()
