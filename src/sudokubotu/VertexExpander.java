package sudokubotu;

public interface VertexExpander<E> {
	public Iterable<E> expand(E e);
}
