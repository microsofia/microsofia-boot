package microsofia.boot.launch;

import java.lang.reflect.Method;

import org.eclipse.aether.resolution.DependencyResult;

import microsofia.boot.loader.ClassLoaderBuilder;

/**
 * Launcher that resolves the dependency graph node and then call the static main method
 * */
public class MainLauncher extends AbstractLauncher{
	private String mainClass;
	private String[] args;
	
	public MainLauncher(){
	}
	
	public String getMainClass() {
		return mainClass;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public void launch() throws Throwable{
		DependencyResult result=resolve();
		
		ClassLoader classLoader=new ClassLoaderBuilder().setParentClassLoader(MainLauncher.class.getClassLoader()).setRootNode(result.getRoot()).create();
		Class<?> cl=classLoader.loadClass(mainClass);
		Method m=cl.getMethod("main", String[].class);
		m.invoke(null, (Object)args);
	}
}
