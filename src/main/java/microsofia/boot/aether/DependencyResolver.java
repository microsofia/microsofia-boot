package microsofia.boot.aether;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;
import microsofia.boot.config.Settings;

/** 
 * Calls Aether RepositorySystem resolveDependencies by transforming config objects into DependencyRequest
 * 
 * */
public class DependencyResolver {
	private Settings settings;
	private RepositorySystem repositorySystem;
	private RepositorySystemSession session;
	
	public DependencyResolver(){
	}
	
	/**
	 * Returns the Aether Settings used
	 * */
	public Settings getSettings() {
		return settings;
	}

	/**
	 * Sets the Aether Settings
	 * */
	public DependencyResolver setSettings(Settings settings) {
		this.settings = settings;
		return this;
	}

	/**
	 * Returns the Aether RepositorySystem used
	 * */
	public RepositorySystem getRepositorySystem() {
		return repositorySystem;
	}

	/**
	 * Sets the Aether RepositorySystem
	 * */
	public DependencyResolver setRepositorySystem(RepositorySystem repositorySystem) {
		this.repositorySystem = repositorySystem;
		return this;
	}

	/**
	 * Returns the Aether RepositorySystemSession used
	 * */
	public RepositorySystemSession getSession() {
		return session;
	}

	/**
	 * Sets the Aether RepositorySystemSession
	 * */
	public DependencyResolver setSession(RepositorySystemSession session) {
		this.session = session;
		return this;
	}

	/**
	 * Builds the DependencyRequest, calls RepositorySystem.resolveDependencies and checks for any error.
	 * 
	 * @return DependencyResult the returned result from resolveDependencies
	 * */
	public DependencyResult resolve() throws Exception{
		Dependency dependency=settings.getDependency().createDependency();
		
		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot(dependency);
		collectRequest.setRepositories(settings.createRepositories());

		DependencyRequest dependencyRequest=new DependencyRequest();
		dependencyRequest.setCollectRequest(collectRequest);
    
		DependencyResult result=repositorySystem.resolveDependencies(session, dependencyRequest);
		
		if (result.getCollectExceptions()!=null && result.getCollectExceptions().size()>0){
			throw result.getCollectExceptions().get(0);//return the first one. TODO: should return an exception wrapping them all
		}
		
		return result;
	}
}
