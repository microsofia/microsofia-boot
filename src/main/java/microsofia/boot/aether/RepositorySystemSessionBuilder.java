package microsofia.boot.aether;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RepositoryPolicy;

import microsofia.boot.config.Settings;

/**
 * RepositorySystemSession builder
 * */
public class RepositorySystemSessionBuilder {
	private RepositorySystem repositorySystem;
	private Settings settings;
	
	public RepositorySystemSessionBuilder(){
	}	
	
	public RepositorySystem getRepositorySystem() {
		return repositorySystem;
	}

	public RepositorySystemSessionBuilder setRepositorySystem(RepositorySystem repositorySystem) {
		this.repositorySystem = repositorySystem;
		return this;
	}

	public Settings getSettings() {
		return settings;
	}

	public RepositorySystemSessionBuilder setSettings(Settings settings) {
		this.settings = settings;
		return this;
	}

	/**
	 * Uses the passed Settings in order to configure and create the session
	 * */
	public RepositorySystemSession create(){
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        session.setOffline(settings.getOffline()!=null ? settings.getOffline().booleanValue() : false);
        session.setIgnoreArtifactDescriptorRepositories(true);
        
        session.setChecksumPolicy(RepositoryPolicy.CHECKSUM_POLICY_WARN);
        session.setUpdatePolicy(settings.getUpdatePolicy());
        
        LocalRepository localRepo = new LocalRepository(settings.getLocalRepository());
        session.setLocalRepositoryManager( repositorySystem.newLocalRepositoryManager( session, localRepo ) );

        session.setRepositoryListener( new microsofia.boot.aether.RepositoryListener() );
        session.setTransferListener( new microsofia.boot.aether.TransferListener() );

        /*TODO 	should allow the followig to be configured via settings file? (if needed)
         * 		session.setResolutionErrorPolicy();
        		session.setArtifactDescriptorPolicy(artifactDescriptorPolicy)
        		session.setSystemProperties(systemProperties)
        		session.setUserProperties(userProperties)
        		session.setConfigProperties(configProperties)*/
        
        return session;
    }
}
