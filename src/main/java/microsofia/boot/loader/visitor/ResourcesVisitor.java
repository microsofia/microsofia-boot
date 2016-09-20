package microsofia.boot.loader.visitor;

import java.net.URL;
import java.util.List;

/**
 * Visitor which aim is to search for all the URLs given a resource name
 * */
public class ResourcesVisitor extends AbstractVisitor<URL> {
	private String name;
	private List<URL> urls;

	public ResourcesVisitor(String name, List<URL> urls) {
		this.name = name;
		this.urls = urls;
	}

	public String getName() {
		return name;
	}

	public URL visit(INode node) {
		try {
			List<URL> foundUrls = node.findResourcesInNode(name);
			if (foundUrls != null) {
				for (URL url : foundUrls) {
					if (!urls.contains(url)) {
						urls.add(url);
					}
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
		return null;
	}
}