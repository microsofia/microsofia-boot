package microsofia.boot.loader.visitor;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Visitor which aim is to search for the URL of a resource
 * */
public class ResourceVisitor extends AbstractVisitor<URL> {
	private static Log log		= LogFactory.getLog(ResourceVisitor.class.getName());
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
			if (log.isDebugEnabled()) {
				log.debug("Resource " + name + (url == null ? " not" : "") + " found in " + node + (url == null ? "" : " ( " + url + " )"));
			}
			return url;
		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
				log.debug("Error while loading resource " + name + " in " + node, ex);
			}
		}
		return null;
	}
}