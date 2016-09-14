package microsofia.boot.loader.visitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Visitor which aim is to search for the path of a library
 * */
public class LibraryVisitor extends AbstractVisitor<String> {
	private static Log log		= LogFactory.getLog(LibraryVisitor.class.getName());
	private String name;

	public LibraryVisitor(String name) {
		this.name = name;
	}

	public String visit(INode node) {
		try {
			String path = node.findLibraryInNode(name);
			if (log.isDebugEnabled()) {
				log.debug("Library " + name + (path == null ? " not" : "") + " found in " + node + (path == null ? " " : " ( " + path + " )"));
			}
			return path;
		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
				log.debug("Error while loading library " + name + " in " + node, ex);
			}
		}
		return null;
	}
}