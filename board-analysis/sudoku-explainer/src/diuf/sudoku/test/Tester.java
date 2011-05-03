/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku.test;

import java.io.*;
import java.util.*;

import diuf.sudoku.*;
import diuf.sudoku.solver.*;
import diuf.sudoku.solver.checks.BruteForceAnalysis;


public class Tester {

    /**
     * Analyze all the Sudokus of a given file, and store the results
     * in another given file. The content of the result file is also
     * printed on the console.
     * @param args the two file names
     */
    public static void main(String[] args) {
        if (args.length != 2)
            throw new IllegalArgumentException("Expected arguments: fileName log");
        String fileName = args[0];
        String logFile = args[1];
        LineNumberReader reader = null;
        PrintWriter writer = null;
        try {
            Reader reader0 = new FileReader(fileName);
            reader = new LineNumberReader(reader0);
            Writer writer0 = new FileWriter(logFile);
            BufferedWriter writer1 = new BufferedWriter(writer0);
            BruteForceAnalysis bfa = new BruteForceAnalysis(false);
            writer = new PrintWriter(writer1);
            String line = reader.readLine();
            int puzzlesSolved = 0;
            while (line != null) {
            	String[] temp = line.split(",");
            	String puzzleId = temp[0];
            	line = temp[1];
                line = line.trim();
                if (line.length() >= 81) {
                    Grid grid = new Grid();
                    for (int i = 0; i < 81; i++) {
                        char ch = line.charAt(i);
                        if (ch >= '1' && ch <= '9') {
                            int value = (ch - '0');
                            grid.setCellValue(i % 9, i / 9, value);
                        }
                    }
                    Solver solver = new Solver(grid);
                    solver.rebuildPotentialValues();
                    try {
                    	if ( bfa.getCountSolutions(grid) > 1)
                    		throw new UnsupportedOperationException("Invalid number of solutions");
                        Map<Rule,Integer> rules = solver.solve(null);
                        Map<String,Integer> ruleNames = solver.toNamedList(rules);
                        double difficulty = 0;
                        String hardestRule = "";
                        for (Rule rule : rules.keySet()) {
                            if (rule.getDifficulty() > difficulty) {
                                difficulty = rule.getDifficulty();
                                hardestRule = rule.getName();
                            }
                        }
                        writer.println(String.format("INSERT INTO explainer_stats (puzzle_id,hardest_rule,difficulty) VALUES (%s,'%s',%f)",puzzleId,hardestRule,difficulty));
                        for (String rule : ruleNames.keySet()) {
                            int count = ruleNames.get(rule);
                            writer.println(String.format("INSERT INTO rules (puzzle_id,rule_string,rule_count) VALUES (%s,'%s',%s)",puzzleId,rule,Integer.toString(count)));
                        }
                    } catch (UnsupportedOperationException ex) {
                        //writer.println(String.format("INSERT INTO explainer_stats (puzzle_id,hardest_rule,difficulty) VALUES (%s,'%s',%f)",puzzleId,"fail",null));
                    }
                    writer.flush();
                } else
                    System.out.println("Skipping incomplete line: " + line);
                line = reader.readLine();
                
				System.out.println("Puzzles Solved: "+(puzzlesSolved++));
            }
            writer.close();
            reader.close();
        } catch(FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (writer != null)
                    writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.print("Finished.");
    }

}
