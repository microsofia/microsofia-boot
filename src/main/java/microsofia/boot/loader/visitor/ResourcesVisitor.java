package microsofia.boot.loader.visitor;

import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Visitor which aim is to search for all the URLs given a resource name
 * */
public class ResourcesVisitor extends AbstractVisitor<URL> {
	private static Log log		= LogFactory.getLog(ResourcesVisitor.class.getName());
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
			if (log.isDebugEnabled()) {
				String strUrls = "";
				if (foundUrls != null) {
					for (int i=0;i<foundUrls.size();i++) {
						strUrls += foundUrls.get(i).toString();
						if (i < foundUrls.size()-1) {
							strUrls += ", ";
						}
					}
				}
				log.debug("Resources of " + name + (foundUrls == null ? " not" : "") + " found in " + node+ (foundUrls == null ? "" : " ( " + strUrls + " )"));
			}
		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
				log.debug("Error while loading resource " + name + " in " + node, ex);
			}
		}
		return null;
	}
}