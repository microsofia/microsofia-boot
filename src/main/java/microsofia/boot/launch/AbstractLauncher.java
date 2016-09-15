package microsofia.boot.launch;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.resolution.DependencyResult;

import microsofia.boot.aether.DependencyResolver;
import microsofia.boot.aether.RepositorySystemBuilder;
import microsofia.boot.aether.RepositorySystemSessionBuilder;
import microsofia.boot.config.Settings;

/**
 * Base abstract class which resolves the dependency graph node.
 * */
public abstract class AbstractLauncher {
	private File settingsFile;
	protected Settings settings;

	protected AbstractLauncher(){
	}
	
	public File getSettingsFile() {
		return settingsFile;
	}

	public void setSettingsFile(File settings) {
		this.settingsFile = settings;
	}

	protected DependencyResult resolve() throws Throwable{
		if (settingsFile==null){
			settingsFile=new File(".\\settings.xml");
		}
		settings=Settings.readFrom(new FileInputStream(settingsFile));
		
		RepositorySystem repositorySystem=new RepositorySystemBuilder().create();
		RepositorySystemSession session=new RepositorySystemSessionBuilder().setSettings(settings).setRepositorySystem(repositorySystem).create();

		DependencyResolver resolver=new DependencyResolver();
		resolver.setRepositorySystem(repositorySystem);
		resolver.setSession(session);
		resolver.setSettings(settings);
		return resolver.resolve();
	}
}
