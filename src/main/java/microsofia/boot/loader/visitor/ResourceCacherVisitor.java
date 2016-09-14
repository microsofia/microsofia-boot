package microsofia.boot.loader.visitor;

/**
 * Visitor which aim is to invoke the hashcode caching of the resources contained in the node
 * */
public class ResourceCacherVisitor extends AbstractVisitor<String> {
	
	public ResourceCacherVisitor() {
		super();
	}

	@Override
	public String visit(INode node) {
		node.cacheResourcesHashCode();
		return null;
	}	
}