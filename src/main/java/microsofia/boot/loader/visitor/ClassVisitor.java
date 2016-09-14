package microsofia.boot.loader.visitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Visitor which aim is to search for a class given its name and its file path
 * */
public class ClassVisitor extends AbstractVisitor<Class<?>> {
	private static Log log		= LogFactory.getLog(ClassVisitor.class.getName());
	private String name;
	private String classFilePath;

	public ClassVisitor(String name, String classFilePath) {
		this.name = name;
		this.classFilePath = classFilePath;
	}

	public String getName() {
		return name;
	}

	public Class<?> visit(INode node) {
		try {
			Class<?> clazz = node.findClassInNode(name, classFilePath);
			if (log.isDebugEnabled()) {
				log.debug("Class " + name + (clazz == null ? " not" : "") + " found in " + node);
			}
			return clazz;
		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
				log.debug("Error while loading class " + name + " in " + node, ex);
			}
		}
		return null;
	}
}