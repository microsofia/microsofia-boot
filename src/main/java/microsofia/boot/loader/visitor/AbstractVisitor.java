package microsofia.boot.loader.visitor;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Abstract node visitor. All visitors should extend this class.
 * */
public abstract class AbstractVisitor<T> implements IVisitor<T> {
	private Deque<INode> deque;//nodes to visit
	private Set<Long> visited;//already visited nodes, in order to avoid cyclic dependencies

	protected AbstractVisitor() {
		deque = new LinkedList<INode>();
		visited = new HashSet<Long>();
	}

	protected void enqueue(INode... ncls) {
		for (INode ncl : ncls) {
			Long id = ncl.getId();
			if (!visited.contains(id)) {
				visited.add(id);
				deque.offerLast(ncl);
			}
		}
	}

	protected void clearQueue() {
		deque.clear();
	}

	public T traverse(INode ncl) {
		clearQueue();
		enqueue(ncl);
		return traverse();
	}

	protected T traverse() {
		INode ncl = null;
		while ((ncl = deque.pollFirst()) != null) {
			T t = visit(ncl);
			if (t != null) {//the visitor stops when a result is found
				return t;
			}
			enqueue(ncl.getDependencies());
		}
		return null;
	}
}