CREATE TABLE puzzles (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	puzzle TEXT,
	difficulty TEXT
);
CREATE TABLE explainer_stats (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	puzzle_id INTEGER,
	hardest_rule TEXT,
	difficulty FLOAT
);
CREATE TABLE rules (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	puzzle_id INTEGER,
	rule_string TEXT,
	rule_count INTEGER
);
CREATE TABLE puzzle_clues (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	puzzle_id INTEGER,
	clue_index INTEGER,
	clue INTEGER
);
CREATE TABLE solutions (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	puzzle_id INTEGER,
	solution TEXT
);
CREATE TABLE benchmarks (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	solver TEXT,
	solve_time INTEGER,
	node_count INTEGER,
	solution_id INTEGER,
	puzzle_id INTEGER
);