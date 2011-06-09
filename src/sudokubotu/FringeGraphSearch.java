package sudokubotu;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class FringeGraphSearch<E> {
	public HashSet<E> explored = new HashSet<E>();
	private E goal = null;
	private Condition<E> fCond;
	private Condition<E> gCond;
	private VertexExpander<E> vExp;
	private PriorityQueue<E> fringe;
	private E startState;
	private int maxRun = 0;
	
	public FringeGraphSearch(E startState, Condition<E> fCond,Condition<E> gCond,VertexExpander<E> vExp) {
		this.startState = startState;
		this.fCond = fCond;
		this.gCond = gCond;
		this.vExp = vExp;
		this.fringe = new PriorityQueue<E>();
	}
	
	public E getGoalState() throws MaxRunException {
		return search();
	}
	
	public E search() throws MaxRunException {
		fringe.add(startState);
		E v = startState;
		while ( !fringe.isEmpty() ) {
			v = fringe.remove();
			
			if (maxRun++ > 81)
				throw new MaxRunException(startState);
			
			if (explored.contains(v)) {
				continue;
			}
			if ( fCond.satisfiedWith(v) ) {
				continue;
			}
			if ( gCond.satisfiedWith(v) ) {
				return v;
			}
			fringe.addAll(vExp.expand(v));
			
			explored.add(v);
		}
		return v;
	}
	
	
}
