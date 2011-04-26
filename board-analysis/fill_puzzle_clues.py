#for each difficulty
#	for each file
#		insert(data_from_file,difficulty
difficulties = ["Easy","Medium","Hard","Insane"];
import os,sqlite3
PUZZLE_DIR = "./Gameboards/"
conn = sqlite3.connect('sudoku.db')
c = conn.cursor()
c.execute("SELECT id,puzzle FROM puzzles");
"""
CREATE TABLE puzzle_clues (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	puzzle_id INTEGER,
	x INTEGER,
	y INTEGER,
	clue INTEGER
);
"""
c1 = conn.cursor()
for puzzle_id,puzzle in c:
	clues = zip([puzzle_id for i in range(0,81)],range(0,81), puzzle.replace('[','').replace(']','').split(',') )
	c1.executemany("INSERT INTO puzzle_clues (puzzle_id,clue_index,clue) VALUES (?,?,?)",clues)

c1.close()
c.close()
conn.commit()

# c.close()
# conn.commit()
