package microsofia.boot.loader;

import org.eclipse.aether.graph.DependencyNode;

/**
 * ClassLoader builder. It takes the resolved dependency graph node, the parent classloader and creates a graph of NodeClassLoader.
 * The builder should later be updated once <a href="http://openjdk.java.net/projects/jigsaw/">Jigsaw</a> is released 
 * so that we stop creating NodeClassLoader and use it instead.
 * 
 * */
public class ClassLoaderBuilder{
	private DependencyNode rootNode;
	private ClassLoader parentClassLoader;
	
	public ClassLoaderBuilder(){
	}

	public DependencyNode getRootNode() {
		return rootNode;
	}

	public ClassLoaderBuilder setRootNode(DependencyNode rootNode) {
		this.rootNode = rootNode;
		return this;
	}

	public ClassLoader getParentClassLoader() {
		return parentClassLoader;
	}

	public ClassLoaderBuilder setParentClassLoader(ClassLoader parentClassLoader) {
		this.parentClassLoader = parentClassLoader;
		return this;
	}

	public ClassLoader create() throws Exception{
		BootClassLoader bootClassLoader=new BootClassLoader(parentClassLoader, rootNode);
		return bootClassLoader;
	}
}
