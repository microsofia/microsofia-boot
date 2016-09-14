package microsofia.boot.loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.aether.graph.DependencyNode;

import microsofia.boot.loader.visitor.*;

/**
 * Parent classloader of all the NodeClassLoader
 * */
public class BootClassLoader extends ClassLoader {
	private static Log log	= LogFactory.getLog(BootClassLoader.class.getName());
	private static long bootClassLoaderId;
	private long id;
	//the root dependency node
	private DependencyNode rootNode;	

	//all the created NodeClassLoader
	private Map<String, NodeClassLoader> nodeClassLoaders;
	private List<NodeClassLoader> rootNodeClassLoaders;//the root ones, only one for the moment

	private Map<URL, ProtectionDomain> protectionDomains;

	//root path for the libraries
	private File tmpLibDir;
	private Map<String, File> tmpLibs;

	//temporary objects for hashcode caching
	protected Set<Integer> globalResourcesHashCodes;
	protected List<Integer> tempResourcesHashCodes;
	protected int [] sortedGlobalReourcesHashCodes;

	public BootClassLoader(ClassLoader parent, DependencyNode rootNode) throws Exception {
		super(parent);
		synchronized (BootClassLoader.class) {
			this.id = bootClassLoaderId++;
		}
		this.rootNode=rootNode;
		this.nodeClassLoaders = new LinkedHashMap<String, NodeClassLoader>();
		this.rootNodeClassLoaders = new ArrayList<NodeClassLoader>();

		NodeClassLoader nodeClassLoader=new NodeClassLoader(this, rootNode);
		rootNodeClassLoaders.add(nodeClassLoader);

		this.tmpLibs = new LinkedHashMap<String, File>();
		this.protectionDomains = new LinkedHashMap<URL, ProtectionDomain>();
		initTmpLibDir();
		cacheResources();
	}

	public DependencyNode getRootNode() {
		return rootNode;
	}
	
	public NodeClassLoader getNodeClassLoader(String id) {
		return nodeClassLoaders.get(id);
	}
	
	public void addNodeClassLoader(NodeClassLoader cl){
		nodeClassLoaders.put(cl.getArtifactId(), cl);
	}
	
	public File getParentLibDir(){
		return tmpLibDir;
	}
	
	public File getLibDir(String s){
		return tmpLibs.get(s);
	}
	
	public void putLibDir(String s,File f){
		tmpLibs.put(s,f);
	}
	
	private void initTmpLibDir() throws IOException {
		tmpLibDir = File.createTempFile("libclassloader", null);
		tmpLibDir.delete();
		tmpLibDir.mkdir();
		tmpLibDir.deleteOnExit();
	}

	ProtectionDomain getProtectionDomain(URL url) {
		synchronized(protectionDomains){
			ProtectionDomain protectionDomain = null;
			if (url != null) {
				protectionDomain = (ProtectionDomain) protectionDomains.get(url);
				if (protectionDomain == null) {
					protectionDomain = new ProtectionDomain(new CodeSource(url, (java.security.cert.Certificate[]) null), new Permissions(), this, null);
					protectionDomains.put(url, protectionDomain);
				}
			}
			return protectionDomain;
		}
	}

	/**
	 * findClass delegates to ClassVisitor
	 * */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String classFilePath = name.replace('.', '/') + ".class";
		if (resourceHashMissing(sortedGlobalReourcesHashCodes, classFilePath)) {
			if (log.isDebugEnabled()) {
            	log.debug("Class " + name + " not found in the global cache of resources hash codes");
            }
			throw new ClassNotFoundException(name);
		}
		return findClass(new ClassVisitor(name, classFilePath));
	}

	Class<?> findClass(ClassVisitor classVisitor) throws ClassNotFoundException {
		Class<?> clazz = null;
		for (NodeClassLoader nodeClassLoader : rootNodeClassLoaders) {
			clazz = nodeClassLoader.traverse(classVisitor);
			if (clazz != null) {
				return clazz;
			}
		}
		throw new ClassNotFoundException(classVisitor.getName());
	}

	/**
	 * findResource delegates to ResourceVisitor
	 * */
	@Override
	protected URL findResource(String name) {
		ResourceVisitor resourceVisitor = new ResourceVisitor(name);
		if (resourceHashMissing(sortedGlobalReourcesHashCodes, resourceVisitor.getName())) {
			if (log.isDebugEnabled()) {
            	log.debug("Resource " + name + " not found in the global cache of resources hash codes");
            }
			return null;
		}
		return findResource(new ResourceVisitor(name));
	}

	protected URL findResource(ResourceVisitor resourceVisitor) {
		URL url = null;
		for (NodeClassLoader nodeClassLoader : rootNodeClassLoaders) {
			url = nodeClassLoader.traverse(resourceVisitor);
			if (url != null) {
				return url;
			}
		}
		return url;
	}

	/**
	 * findResources delegates to ResourcesVisitor
	 * */
	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		Vector<URL> urls = new Vector<URL>();
		ResourcesVisitor resourcesVisitor = new ResourcesVisitor(name, urls);
		findResources(resourcesVisitor);
		return urls.elements();
	}

	private void findResources(ResourcesVisitor resourcesVisitor) {
		if (resourceHashMissing(sortedGlobalReourcesHashCodes, resourcesVisitor.getName())) {
			if (log.isDebugEnabled()) {
            	log.debug("Resources " + resourcesVisitor.getName() + " not found in the global cache of resources hash codes");
            }
		}
		for (NodeClassLoader nodeClassLoader : rootNodeClassLoaders) {
			nodeClassLoader.traverse(resourcesVisitor);
		}
	}

	/**
	 * findLibrary delegates to LibraryVisitor
	 * */
	@Override
	protected String findLibrary(String libname) {
		return findLibrary(new LibraryVisitor(libname));
	}

	protected String findLibrary(LibraryVisitor libraryVisitor) {
		for (NodeClassLoader nodeClassLoader : rootNodeClassLoaders) {
			String path = nodeClassLoader.traverse(libraryVisitor);
			if (path != null) {
				return path;
			}
		}
		return null;
	}
	
	public int [] getGlobalAllResourcesHashCodes() {
		return sortedGlobalReourcesHashCodes;
	}

	/**
	 * caching resources hashcode delegates to ResourceCacherVisitor
	 * */
	private void cacheResources() {
		long startTime = System.currentTimeMillis();
		globalResourcesHashCodes = new HashSet<Integer>();
		tempResourcesHashCodes = new ArrayList<Integer>();
		ResourceCacherVisitor visitor = new ResourceCacherVisitor();
		for (NodeClassLoader nodeClassLoader : rootNodeClassLoaders) {
			nodeClassLoader.traverse(visitor);
		}
		tempResourcesHashCodes = null;
		sortedGlobalReourcesHashCodes = new int[globalResourcesHashCodes.size()];
		int index = 0;
		for(Iterator<Integer> iterator = globalResourcesHashCodes.iterator(); iterator.hasNext();) {			
			sortedGlobalReourcesHashCodes[index++] = iterator.next().intValue();
		}
		Arrays.sort(sortedGlobalReourcesHashCodes);
		globalResourcesHashCodes = null;
		long endTime = System.currentTimeMillis();
		if (log.isDebugEnabled()) {
			log.debug(String.format("Resources hash codes have been cached in %d ms. Global Caches number of entries %d", (endTime - startTime), sortedGlobalReourcesHashCodes.length));
		}
	}

    static boolean resourceHashMissing(int[] hashCodes, String resource) {
        return !resourceHashExists(hashCodes, resource);
    }

    static boolean resourceHashExists(int[] hashCodes, String resource) {
        if (resource.endsWith("/")) {
            resource = resource.substring(0, resource.length() - 1);
        }
        return Arrays.binarySearch(hashCodes, resource.hashCode()) >= 0;
    }
    
	@Override
	public String toString() {
		return "BootClassLoader[Id: " + this.id + "] " + super.toString();
	}
}
