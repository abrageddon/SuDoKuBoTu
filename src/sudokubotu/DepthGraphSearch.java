package sudokubotu;

import java.util.Collection;
import java.util.HashSet;

public class DepthGraphSearch<E> {
	public HashSet<E> explored = new HashSet<E>();
	private E goal = null;
	private Condition<E> fCond;
	private Condition<E> gCond;
	private VertexExpander<E> vExp;
	private E startState;
	private int maxRun = 0;
	
	public DepthGraphSearch(E startState, Condition<E> fCond,Condition<E> gCond,VertexExpander<E> vExp) {
		this.startState = startState;
		this.fCond = fCond;
		this.gCond = gCond;
		this.vExp = vExp;
	}
	
	public E getGoalState() throws MaxRunException {
		recDepthSearch(startState);
		return goal;
	}
	
	public void recDepthSearch(E vertex) throws MaxRunException {
		if ( goal != null )
			return;
		if ( maxRun++ > 300 ) {
			throw new MaxRunException(vertex);
		}
	
		Collection<E> i = vExp.expand(vertex);
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
