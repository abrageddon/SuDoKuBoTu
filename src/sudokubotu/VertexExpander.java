package sudokubotu;

import java.util.Collection;

public interface VertexExpander<E> {
	public Collection<E> expand(E e);
}
