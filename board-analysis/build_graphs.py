#take in parameters
#	variable (x will be variable y will be frequency? (perhaps make it extendable for non-histogram type graphs?) )
#	filters (make extendable later for filter functions?)
#	will generate the file name based on the variable and filters and an id

import os, sqlite3, sys, getopt
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt


def make_graph(variable,filter_string):
	data = []
	#constraint = "p.id = pc.puzzle_id AND e.puzzle_id = p.id AND b.solution_id = s.id AND p.id = b.puzzle_id AND p.id = s.puzzle_id"
	constraint = "e.puzzle_id = p.id"
	#constraint = "p.id = b.puzzle_id"
	#constraint = "p.id = pc.puzzle_id"
	#tables = "puzzles p, puzzle_clues pc, benchmarks b, solutions s,explainer_stats e"
	#tables = "puzzles p, benchmarks b"
	#tables = "puzzles p, puzzle_clues pc"
	tables = "puzzles p, explainer_stats e"
	condition = filter_string
	q = ["SELECT %s FROM %s WHERE %s AND p.difficulty = '%s' AND %s" % (variable,tables,constraint,d,condition) for d in ['Easy','Medium','Hard','Insane']]
	conn = sqlite3.connect('sudoku.db')
	c = conn.cursor()
	for query in q:
		print query
		c.execute(query)
		data.append([x[0] for x in c])
	c.close()
	plt.hist(data, histtype='bar', normed=True,label=('Easy','Medium','Hard','Insane'))
	plt.xlabel(variable)
	plt.ylabel('frequency')
	plt.title(variable+" with "+filter_string)
	plt.grid(True)
	fixforfile = lambda f: "".join([x for x in f if x.isalpha() or x.isdigit()])
	filter_string = fixforfile(filter_string)
	variable = fixforfile(variable)
	plt.legend()
	plt.savefig('graph_%s(%s)'% (variable,filter_string))
	plt.close()
	

opts,args = getopt.getopt(sys.argv[1:],'v:f:d')

variable = 'node_count'
filter_string = '1'
for o,v in opts:
	if o == '-v':
		variable = v
	if o == '-f':
		filter_string = v

make_graph(variable,filter_string)
