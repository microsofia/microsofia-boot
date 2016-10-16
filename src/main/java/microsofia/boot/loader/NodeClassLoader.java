package microsofia.boot.loader;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.aether.graph.DependencyNode;

import microsofia.boot.loader.resource.Resource;
import microsofia.boot.loader.visitor.AbstractVisitor;
import microsofia.boot.loader.visitor.ClassVisitor;
import microsofia.boot.loader.visitor.INode;
import microsofia.boot.loader.visitor.LibraryVisitor;
import microsofia.boot.loader.visitor.ResourceVisitor;

/**
 * Represents a node classloader. All the nodes have the BootClassLoader as parent.
 * */
public class NodeClassLoader extends ClassLoader implements INode{
	private static Log log	= LogFactory.getLog(NodeClassLoader.class.getName());
	private static String osNameArch	= "_" + System.getProperty("os.name").split(" ")[0].toLowerCase() + "_" + System.getProperty("os.arch").split(" ")[0].toLowerCase() + ".";
	private static long classLoaderId;
	private static Field classesField;

	static {
		try {
			classesField = ClassLoader.class.getDeclaredField("classes");
			classesField.setAccessible(true);
		} catch (NoSuchFieldException ex) {
			if (log.isErrorEnabled()) {
				log.error("Could not find the \"classes\" field by reflection in class ClassLoader. Lookup of dynamically defined classes will not work.", ex);
			}
		}
	}

	//the parent classloader of the node
	private final BootClassLoader bootClassLoader;

	//the unique node id
	private Long nodeClassLoaderId;
	
	//the Aether DependencyNode that corresponds to the current node
	private DependencyNode node;
	
	//the node dependencies
	private Map<String, NodeClassLoader> depdendenciesNodeClassLoaders;
	private NodeClassLoader [] dependenciesNodeClassLoadersArray;
	
	//all the node classloader that have this node as dependency
	private List<NodeClassLoader> parentNodeClassLoaders;

	//all the classes loaded by this node
	private Map<String, Class<?>> classes;
	private List<Class<?>> jvmClasses;

	//the resource represented by this node/artifact
	private Resource resource;
	private String artifactId;

	//hashcode of resource contained by this node
	private int [] sortedAllReourcesHashCodes;

	@SuppressWarnings("unchecked")
	protected NodeClassLoader(BootClassLoader bootClassLoader, DependencyNode node) throws Exception {
		this.bootClassLoader = bootClassLoader;
		synchronized (NodeClassLoader.class) {
			this.nodeClassLoaderId = Long.valueOf(classLoaderId++);
		}
		this.node = node;
		this.artifactId = node.getArtifact().toString();
		this.bootClassLoader.addNodeClassLoader(this);
		this.depdendenciesNodeClassLoaders = Collections.synchronizedMap(new LinkedHashMap<String, NodeClassLoader>());
		this.dependenciesNodeClassLoadersArray = new NodeClassLoader [0];
		this.parentNodeClassLoaders = new Vector<NodeClassLoader>();
		this.classes = new Hashtable<String, Class<?>>();
		if (classesField != null) {
			this.jvmClasses = (List<Class<?>>)classesField.get(this);
		}
		if (log.isDebugEnabled()) {
			log.debug("Creating classloader " + this);
		}

		initArtifactFiles();
		
		//building the dependencies/children
		List<DependencyNode> children=node.getChildren();
		if (children!=null){
			for (DependencyNode n : children){
				if (n.getDependency().getArtifact().getFile()!=null){
					NodeClassLoader nodeClassLoader = bootClassLoader.getNodeClassLoader(n.getDependency().getArtifact().toString());
					if (nodeClassLoader==null) {
						nodeClassLoader = new NodeClassLoader(this.bootClassLoader, n);
					}
					addChild(nodeClassLoader);

				} else {
					if (log.isDebugEnabled()) {
						log.debug("Dependency " + n.getDependency().getArtifact() + " is excluded from classloader " + this);
					}
				}
			}
		}
	}

	/**
	 * Returns the unique id of the node
	 * */
	public Long getId() {
		return nodeClassLoaderId;
	}

	/**
	 * Returns classloader of the node, itself
	 * */
	@Override
	public ClassLoader getClassLoader(){
		return this;
	}
	
	/**
	 * Returns the id of the artifact
	 * */
	public String getArtifactId() {
		return artifactId;
	}

	@Override
	public NodeClassLoader[] getDependencies() {
		NodeClassLoader[] tmp;
		synchronized (nodeClassLoaderId) {
			tmp = dependenciesNodeClassLoadersArray;
		}
		return tmp;
	}

	/**
	 * In order to have a fast lookup after startup, the node caches the hashcode path of all its resources.
	 * */
	public void cacheResourcesHashCode() {
		this.bootClassLoader.tempResourcesHashCodes.clear();
		try {
			Iterator<String> iterator = resource.getAllEntriesIncludingFolders();
			while(iterator.hasNext()) {
				String next = iterator.next();
				if(next.endsWith("/")) {
					next = next.substring(0, next.length() - 1);
				}
				int hashCode = next.hashCode();
				this.bootClassLoader.tempResourcesHashCodes.add(hashCode);
				this.bootClassLoader.globalResourcesHashCodes.add(hashCode);
			}
		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
				log.debug("Error while caching the resources of " + this, ex);
			}
			throw new RuntimeException(ex);
		}
		sortedAllReourcesHashCodes = new int[this.bootClassLoader.tempResourcesHashCodes.size()];
		for(int i = 0 ; i < sortedAllReourcesHashCodes.length; i++) {
			sortedAllReourcesHashCodes[i] = this.bootClassLoader.tempResourcesHashCodes.get(i).intValue();
		}
		Arrays.sort(sortedAllReourcesHashCodes);
	}
	
	private synchronized boolean addChild(NodeClassLoader nodeClassLoader) {
		if (depdendenciesNodeClassLoaders.put(nodeClassLoader.getArtifactId(), nodeClassLoader) == null) {
			updateChildrenNodeClassLoadersArray();
			nodeClassLoader.parentNodeClassLoaders.add(this);
			return true;
		}
		updateChildrenNodeClassLoadersArray();
		return false;
	}
	
	private synchronized void updateChildrenNodeClassLoadersArray() {
		NodeClassLoader[] tmp = depdendenciesNodeClassLoaders.values().toArray(new NodeClassLoader[depdendenciesNodeClassLoaders.size()]);
		synchronized (nodeClassLoaderId) {
			dependenciesNodeClassLoadersArray = tmp;
		}
	}

	private void initArtifactFiles() {
		resource = Resource.createResource(node.getDependency().getArtifact().getFile());
		if (log.isDebugEnabled()) {
			log.debug("Artifact of " + this + " is " + resource);
		}
	}

	protected <T> T traverse(AbstractVisitor<T> visitor) {
		return visitor.traverse(this);
	}

	/**
	 * Loading the class consists of first checking in the parent, and if not found traversing the graph 
	 * starting by the current node and following its dependencies.
	 * */
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> clazz = null;

		ClassLoader parent = this.bootClassLoader.getParent();
		if (parent != null) {
			try {
                clazz = parent.loadClass(name);
			} catch (ClassNotFoundException ex) {
                if (log.isDebugEnabled()) {
                	log.debug("Class " + name + " not found in parent classloader " + parent + " of classloader " + this, ex);
                }
			}
		}
		if (clazz == null) {
            clazz = findClass(name);
		}
		if (resolve) {
			resolveClass(clazz);
		}
		if (log.isDebugEnabled()) {
			ClassLoader cl = clazz.getClassLoader();
			log.debug("Class " + name + " requested from " + this + " found in " + (cl != null ? cl : "[system]"));
		}
		return clazz;
	}

	/**
	 * findClass delegates to a ClassVisitor instance
	 * */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> clazz = null;
		String classFilePath = name.replace('.', '/') + ".class";
		if (BootClassLoader.resourceHashMissing(this.bootClassLoader.sortedGlobalReourcesHashCodes, classFilePath)) {
			if (log.isDebugEnabled()) {
            	log.debug("Class " + name + " not found in the global cache of resources hash codes");
            }
			throw new ClassNotFoundException(name);
		}
		ClassVisitor classVisitor = new ClassVisitor(name, classFilePath);
		clazz = traverse(classVisitor); //search this node and its children
		if (clazz == null) {
			if (log.isDebugEnabled()) {
				log.debug("Class " + name + " not found in " + this + " or in any of its descendants.");
			}
			clazz = findClass(classVisitor);
		}
		if (clazz != null) {
			ClassLoader cl = clazz.getClassLoader();
			if (cl instanceof NodeClassLoader && cl != this) {
				if (this.addChild((NodeClassLoader) cl)) {
					if (log.isDebugEnabled()) {
						log.debug("Adding " + cl + " as child of " + this + " after loading " + clazz.getName());
					}
				}
			}
		}
		if (clazz == null) {
			throw new ClassNotFoundException(name);
		}
		return clazz;
	}

	/**
	 * Try to load locally the class without any traversing of the graph. The method is called by the ClassVisitor.
	 * */
	public Class<?> findClassInNode(String name, String classFilePath) throws Exception {
		if (sortedAllReourcesHashCodes != null && BootClassLoader.resourceHashExists(sortedAllReourcesHashCodes, classFilePath)) {
			Class<?> clazz = classes.get(name);
			if (clazz != null) {
				return clazz;
			} else if (jvmClasses != null) {
				if (jvmClasses.size() != classes.size()) {
					if (log.isDebugEnabled()) {
						log.debug("Jvm loaded classes different. reconciling " + this);
					}
					for (Class<?> clazz0 : jvmClasses) {
						classes.put(clazz0.getName(), clazz0);
					}
					clazz = classes.get(name);
					if (clazz != null) {
						return clazz;
					}
				}
			}
			byte[] bytecode = resource.getEntryBytes(classFilePath);
			if (bytecode != null) {
				URL url = resource.getEntryRoot(classFilePath); //should not return null because bytecode is not null.
				ProtectionDomain protectionDomain = this.bootClassLoader.getProtectionDomain(url);

				//building the class
				int index = name.lastIndexOf('.');
				if (index != -1) {
					String pkgname = name.substring(0, index);
					Package pkg = getPackage(pkgname);
					if (pkg == null) {
						definePackage(pkgname, null, null, null, null, null, null, null);
					}
				}
                synchronized(NodeClassLoader.this){
                    clazz=classes.get(name);
                    if (clazz==null){
                        clazz = defineClass(name, bytecode, 0, bytecode.length, protectionDomain);
                        classes.put(name, clazz);
                    }
                }
				return clazz;
			}
		} else {
			if (log.isDebugEnabled()) {
            	log.debug("Class " + name + " not found in the local cache of resources hash codes for " + this);
            }
		}
		return null;
	}

	/**
	 * Other strategies are used when the class is not found by traversing the direct dependencies.
	 * Should be reviewed if needed...
	 * */
	private Class<?> findClass(ClassVisitor classVisitor) {
		for (NodeClassLoader parent : parentNodeClassLoaders.toArray(new NodeClassLoader[0])) {
			Class<?> clazz = parent.traverse(classVisitor);
			if (clazz != null) {
				return clazz;
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Class " + classVisitor.getName() + " not found in parents of " + this + " or in any of their descendants.");
		}

		try {
			Class<?> clazz = this.bootClassLoader.findClass(classVisitor);
			if (clazz != null) {
				return clazz;
			}
		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
				log.debug(ex.toString(), ex);
			}
		}
		return null;
	}

	@Override
	public URL getResource(String name) {
		return findResource(name);
	}

	/**
	 * Delegates to a ResourceVisitor
	 * */
	@Override
	protected URL findResource(String name) {			
		if (BootClassLoader.resourceHashMissing(this.bootClassLoader.sortedGlobalReourcesHashCodes, name)) {
			if (log.isDebugEnabled()) {
            	log.debug("Resource " + name + " not found in the global cache of resources hash codes");
            }
			return null;
		}
		ResourceVisitor resourceVisitor = new ResourceVisitor(name);
		URL url = traverse(resourceVisitor);
		if (url == null) {
			try {
				url = findResource(resourceVisitor);
			} catch (Throwable th) {
				throw new RuntimeException("Error while finding resource " + name, th);
			}
		}
		return url;
	}

	/**
	 * Try to load locally the resource without any traversing of the graph. The method is called by the ResourceXXVisitor.
	 * */
	public URL findResourceInNode(String name) throws Exception {
		if (sortedAllReourcesHashCodes != null && BootClassLoader.resourceHashExists(sortedAllReourcesHashCodes, name)) {
			List<URL> urls = resource.getEntry(name);
			if (urls != null) {
				return urls.get(0);
			}
		} else {
			if (log.isDebugEnabled()) {
            	log.debug("Resource " + name + " not found in the local cache of resources hash codes for " + this);
            }
		}
		return null;
	}

	/**
	 * Other strategies are used when the resource is not found by traversing the direct dependencies.
	 * Should be reviewed if needed...
	 * */
	private URL findResource(ResourceVisitor resourceVisitor) throws Exception {
		for (NodeClassLoader parent : parentNodeClassLoaders.toArray(new NodeClassLoader[0])) {
			URL url = parent.traverse(resourceVisitor);
			if (url != null) {
				return url;
			}
		}
		return this.bootClassLoader.findResource(resourceVisitor);
	}

	/**
	 * Delegates to a ResourcesVisitor
	 * */
    @Override
	public Enumeration<URL> getResources(String name) throws IOException {
		Vector<URL> urls = new Vector<URL>();
		
		Enumeration<URL> parentUrls = bootClassLoader.getResources(name);
		if (parentUrls != null) {
			while (parentUrls.hasMoreElements()) {
				URL url = parentUrls.nextElement();
				if (!urls.contains(url)) {
					urls.add(url);
				}
			}
		}

		Enumeration<URL> foundUrls = findResources(name);
		if (foundUrls != null) {
			while (foundUrls.hasMoreElements()) {
				URL url = foundUrls.nextElement();
				if (!urls.contains(url)) {
					urls.add(url);
				}
			}
		}

		return urls.elements();
	}

    /**
	 * Delegates to a BootClassLoader (is it needed?)
	 * */
	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		return bootClassLoader.findResources(name);
	}

	/**
	 * Try to load locally the resource without any traversing of the graph. The method is called by the ResourceXXVisitor.
	 * */
	public List<URL> findResourcesInNode(String name) throws Exception {
		if (sortedAllReourcesHashCodes != null && BootClassLoader.resourceHashExists(sortedAllReourcesHashCodes, name)) {
			return resource.getEntry(name);
		} else {
			if (log.isDebugEnabled()) {
            	log.debug("Resources " + name + " not found in the local cache of resources hash codes for " + this);
            }
		}
		return null;
	}

	/**
	 * Delegates to a LibraryVisitor
	 * */
	@Override
	protected String findLibrary(String libname) {
		LibraryVisitor libraryVisitor = new LibraryVisitor(libname);
		String path = traverse(libraryVisitor);
		if (path != null) {
			return path;
		}
		for (NodeClassLoader parent : parentNodeClassLoaders.toArray(new NodeClassLoader[0])) {
			path = parent.traverse(libraryVisitor);
			if (path != null) {
				return path;
			}
		}
		return this.bootClassLoader.findLibrary(libraryVisitor);
	}

	/**
	 * Try to load locally the library path without any traversing of the graph. The method is called by the LibraryVisitor.
	 * */
	public String findLibraryInNode(String libname) throws Exception {
		Iterator<String> entriesIter = resource.getAllEntries();
		while (entriesIter.hasNext()) {
			String entry = entriesIter.next();
			int slashIndex = entry.lastIndexOf('/');
			String fileName = entry.substring(slashIndex > 0 ? slashIndex + 1 : 0);
			if (fileName.startsWith(libname) ||
				fileName.startsWith(libname + osNameArch) ||
				fileName.startsWith("lib" + libname + osNameArch) ||
				fileName.startsWith(libname + ".") ||
				fileName.startsWith("lib" + libname + ".")) {
				byte[] bytes = resource.getEntryBytes(entry);
				return getTmpFile(bytes, entry, libname);
			}
		}
		return null;
	}

	private synchronized String getTmpFile(byte[] bytes, String entry, String libname) throws IOException {
		File tmpFile = this.bootClassLoader.getLibDir(libname);
		if (tmpFile == null) {
			tmpFile = new File(this.bootClassLoader.getParentLibDir(), entry);
			tmpFile.getParentFile().mkdirs();
			tmpFile.deleteOnExit();
			DataOutputStream dataOutputStream = null;
			try {
				dataOutputStream = new DataOutputStream(new FileOutputStream(tmpFile));
				dataOutputStream.write(bytes);
			} finally {
				if (dataOutputStream != null) {
					dataOutputStream.flush();
					dataOutputStream.close();
				}
			}
			this.bootClassLoader.putLibDir(libname, tmpFile);
			tmpFile.deleteOnExit();
		}
		return tmpFile.getAbsolutePath();
	}

	@Override
	public String toString() {
		return "NodeClassLoader[Id: " + nodeClassLoaderId + "][Artifact: " + getArtifactId() + "]";
	}
}