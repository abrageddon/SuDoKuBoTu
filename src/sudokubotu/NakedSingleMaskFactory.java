package sudokubotu;

import java.awt.Point;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
//Currently producing single NakedSingles and empties the rest of the board
//need to add way to re-populate board to make it unique and not remove the nakedSingle
public class NakedSingleMaskFactory extends SDKMaskFactory{
	public static SDKMask createMaskForBoard(SDKBoard solvedBoard,int maxRemoved){
		SDKMask mask = new SDKMask();
		SDKBoard puzzle = new SDKBoard(solvedBoard.copyBoard());
		Random rand = new Random();
		
		for(int i =0; i<puzzle.getN();i++)
			for(int j = 0;j<puzzle.getN();j++)
				puzzle.setSquareLock(i, j, true);
		
		Point selected = new Point(rand.nextInt(puzzle.getN()),rand.nextInt(puzzle.getN()));
		puzzle.setSquareLock(selected.x, selected.y, false);
		puzzle.setSquareValue(selected.x, selected.y, 0);
		puzzle.updateConstraints();
		
		
		SudokuBot test = new SudokuBot(puzzle);
		
		LinkedList<SDKSquare> notRemoveable = new LinkedList<SDKSquare>();
		//comment out------------------------------------------------
		/*
		LinkedList<SDKSquare> squares = puzzle.getAllSquaresWithClues();
		
		Collections.shuffle(squares);
		while(!squares.isEmpty()){
			SDKSquare current = squares.remove();
			int val = current.getValue();
			current.setLocked(false);
			current.setValue(0);
			puzzle.updateConstraints();
			if(puzzle.getSquarePossible(selected.x, selected.y).size()>1){
				current.setValue(val);
				current.setLocked(true);
				notRemoveable.add(current);
			}
		}
		//------------------------------------------------
		*/
		SDKSquare next = nextSquare(puzzle, notRemoveable);
		while(next!=null){
			int val = next.getValue();
			next.setLocked(false);
			next.setValue(0);
			test = new SudokuBot(puzzle);
			if(!test.isUnique()){
				next.setValue(val);
				next.setLocked(true);
				notRemoveable.add(next);
			}
			next  = nextSquare(puzzle, notRemoveable);
		}
		mask.mask = puzzle;
		return mask;
		
	}
	private static SDKSquare nextSquare(final SDKBoard board,LinkedList<SDKSquare> notRemovable){
		PriorityQueue<SDKSquare> queue = new PriorityQueue<SDKSquare>(81, new Comparator<SDKSquare>(){
			
			public int compare(SDKSquare o1, SDKSquare o2) {
				int square1 =0;
				int square2 =0;
				HashSet<SDKSquare> squares = new HashSet<SDKSquare>(board.getSquaresInCol(o1.row));
				squares.addAll(board.getSquaresInRow(o1.col));
				squares.addAll(board.getSquaresInZone(board.identifyZone(o1.row, o1.col)));
				Iterator<SDKSquare> iter = squares.iterator();
				while(iter.hasNext()){
					SDKSquare square = iter.next();
					if(square.getValue()==0){
						square1++;
						square1+=square.getPossible().size();
					}
					
				}
				squares = new HashSet<SDKSquare>( board.getSquaresInCol(o2.row));
				squares.addAll(board.getSquaresInRow(o2.col));
				squares.addAll(board.getSquaresInZone(board.identifyZone(o2.row, o2.col)));
				
				iter = squares.iterator();
				while(iter.hasNext()){
					SDKSquare square = iter.next();
					if(square.getValue()==0){
						square2++;
						square2+=square.getPossible().size();
					}
					
				}
				
				if(square1 < square2)
					return -1;
				else if(square1 > square2)
					return 1;
				else
					return 0;
			}
			});
		queue.addAll(board.getAllSquaresWithClues());
		queue.removeAll(notRemovable);
		return queue.poll();		
	}
	public static SDKMask random(SDKBoard solvedBoard,int maxRemoved){
		SDKMask mask = new SDKMask();
		mask.mask = new SDKBoard(solvedBoard.copyBoard());
		SudokuBot test = null;
		LinkedList<SDKSquare> squares = mask.mask.getAllSquares();
		Collections.shuffle(squares);
		for(int i =0; i<mask.mask.getN();i++)
			for(int j = 0;j<mask.mask.getN();j++)
				mask.mask.setSquareLock(i, j, true);
		while(maxRemoved-->0){
			SDKSquare next = squares.remove();
			int val = next.getValue();
			next.setLocked(false);
			next.setValue(0);
			test = new SudokuBot(mask.mask);
			if(!test.isUnique()){
				next.setValue(val);
				next.setLocked(true);
			}
		}
		return mask;
		
	}
}

