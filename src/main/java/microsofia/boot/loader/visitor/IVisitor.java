package microsofia.boot.loader.visitor;

/**
 * Interface that abstracts node visitors
 * */
public interface IVisitor<T> {

	public T visit(INode classLoader);

}