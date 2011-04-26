#take in parameters
#	variable (x will be variable y will be frequency? (perhaps make it extendable for non-histogram type graphs?) )
#	filters (make extendable later for filter functions?)
#	will generate the file name based on the variable and filters and an id

import os, sqlite3, sys, getopt
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt


def make_graph(variable,filter_string):
	query = 'SELECT %s FROM puzzle_clues pc, puzzles p, benchmarks b, solutions s WHERE pc.puzzle_id = p.id AND p.id = b.puzzle_id AND b.solution_id = s.id AND s.puzzle_id = p.id AND  %s' % (variable,filter_string)
	print query
	conn = sqlite3.connect('sudoku.db')
	c = conn.cursor()
	c.execute( query )
	values = [x[0] for x in c]
	c.close()
	plt.hist(values,bins=50, normed=1, alpha=0.75)
	plt.xlabel(variable)
	plt.ylabel('frequency')
	plt.title(filter_string)
	plt.grid(True)
	filter_string = "".join([x for x in filter_string if x.isalpha() or x.isdigit()])
	plt.savefig('graph_%s(%s)'% (variable,filter_string))
	plt.close()

opts,args = getopt.getopt(sys.argv[1:],'v:f:d')

variable = 'node_count'
filter_string = '1'
one_for_each_diff = False
for o,v in opts:
	if o == '-v':
		variable = v
	if o == '-f':
		filter_string = v
	if o == '-d':
		one_for_each_diff = True

if one_for_each_diff:
	[make_graph(variable," difficulty = '%s' AND %s" % (d,filter_string)) for d in ['Easy','Medium','Hard','Insane'] ]
else:
	make_graph(variable,filter_string)	

