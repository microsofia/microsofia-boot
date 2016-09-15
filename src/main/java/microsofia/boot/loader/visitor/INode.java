package microsofia.boot.loader.visitor;

import java.net.URL;
import java.util.List;

/**
 * Interface that represents one node classloader which aim is to handle one artifact.
 * 
 * */
public interface INode {

	/**
	 * Id of the node.
	 * 
	 * @return the id of the node
	 * */
	public Long getId();
	
	/**
	 * Classloader represented by this node
	 * 
	 * @return the classloader of the node
	 * */
	public ClassLoader getClassLoader();
	
	/**
	 * Dependencies of the node
	 * 
	 * @return the dependencies of the node
	 * */
	public INode[] getDependencies();

	/**
	 * Find in the current node the class with the given name and the given file path
	 * 
	 * @param name the class name
	 * @param classFilePath the file path of the class
	 * @return the found class
	 * */
	public Class<?> findClassInNode(String name, String classFilePath) throws Exception;
	
	/**
	 * Find in the current node the given resource
	 * 
	 * @param the path of the resource
	 * @return the URL of the resource
	 * */
	public URL findResourceInNode(String name) throws Exception;
	
	/**
	 * Find in the current node the library given its name
	 * 
	 * @param the name of the library
	 * @return the path of the library
	 * */
	public String findLibraryInNode(String libname) throws Exception;
	
	/**
	 * Find all the URL of the given resource path
	 * 
	 * @param the resource path
	 * @return all the URL with the given resource path
	 * */
	public List<URL> findResourcesInNode(String name) throws Exception ;
	
	/**
	 * Caches the hash code of all file names contained in this node
	 * 
	 * */
	public void cacheResourcesHashCode();
}
