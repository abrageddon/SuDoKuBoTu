import sqlite3
conn = sqlite3.connect('sudoku.db')
c = conn.cursor()
c.execute('SELECT id,puzzle FROM puzzles');
f = open('flatpuzzles.dat','w')
[f.write("%s,%s\n" % (i,p.replace(',','').replace('0','.').replace('[','').replace(']',''))) for i,p in c]
f.close();
	