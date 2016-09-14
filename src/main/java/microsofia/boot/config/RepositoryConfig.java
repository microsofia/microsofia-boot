package microsofia.boot.config;

import javax.xml.bind.annotation.*;

import org.eclipse.aether.repository.RemoteRepository;

/**
 * Configuration object used to configure a Remote Repository.
 * The following configuration is like the <a href="https://maven.apache.org/settings.html">Maven one</a>
 * 
 * @see org.eclipse.aether.repository.RemoteRepository
 * */
@XmlRootElement(name="repository")
@XmlAccessorType(XmlAccessType.FIELD)
public class RepositoryConfig {
	@XmlElement
	private String id;
	@XmlElement
	private String name;
    @XmlElement(name="releases")
    private RepositoryPolicyConfig releasePolicy;
    @XmlElement(name="snapshots")
    private RepositoryPolicyConfig snapshotPolicy;
    @XmlElement
    private String url;
    //TODO Authentication

    public RepositoryConfig(){
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RepositoryPolicyConfig getReleasePolicy() {
		return releasePolicy;
	}

	public void setReleasePolicy(RepositoryPolicyConfig releasePolicy) {
		this.releasePolicy = releasePolicy;
	}

	public RepositoryPolicyConfig getSnapshotPolicy() {
		return snapshotPolicy;
	}

	public void setSnapshotPolicy(RepositoryPolicyConfig snapshotPolicy) {
		this.snapshotPolicy = snapshotPolicy;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public RemoteRepository.Builder createRemoteRepositoryBuilder(){
		RemoteRepository.Builder builder=new RemoteRepository.Builder(getId(),"default",getUrl());
		builder.setReleasePolicy((getReleasePolicy()!=null ? getReleasePolicy().createRepositoryPolicy() : null));
		builder.setSnapshotPolicy((getSnapshotPolicy()!=null ? getSnapshotPolicy().createRepositoryPolicy() : null));
		builder.setRepositoryManager(true);

		return builder;
	}
}
