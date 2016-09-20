package microsofia.boot.loader.visitor;

import java.net.URL;

/**
 * Visitor which aim is to search for the URL of a resource
 * */
public class ResourceVisitor extends AbstractVisitor<URL> {
	private String name;

	public ResourceVisitor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public URL visit(INode node) {
		try {
			URL url = node.findResourceInNode(name);
			return url;
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}