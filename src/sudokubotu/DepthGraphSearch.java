package sudokubotu;

import java.util.HashSet;

public class DepthGraphSearch<E> {
	public HashSet<E> explored = new HashSet<E>();
	private E goal = null;
	private Condition<E> fCond;
	private Condition<E> gCond;
	private VertexExpander<E> vExp;
	private E startState;
	
	public DepthGraphSearch(E startState, Condition<E> fCond,Condition<E> gCond,VertexExpander<E> vExp) {
		this.startState = startState;
		this.fCond = fCond;
		this.gCond = gCond;
		this.vExp = vExp;
	}
	
	public E getGoalState() {
		recDepthSearch(startState);
		return goal;
	}
	
	public void recDepthSearch(E vertex) {
		if ( goal != null )
			return;
		Iterable<E> i = vExp.expand(vertex);
		explored.add(vertex);
		for(E e : i) {
			if (explored.contains(e)) {
				continue;
			}
			if ( fCond.satisfiedWith(e) )
				continue;
			if ( gCond.satisfiedWith(e) ) {
				goal = e;
				break;
			}
			recDepthSearch(e);
		}
	}
}
