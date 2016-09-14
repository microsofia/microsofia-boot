package microsofia.boot.config;

import javax.xml.bind.annotation.*;

import org.eclipse.aether.repository.RemoteRepository;

/**
 * Configuration object used to configure Remote Repository mirrors.
 * The following mirror configuration is like the <a href="https://maven.apache.org/settings.html">Maven one</a>
 * 
 * @see org.eclipse.aether.repository.RemoteRepository
 * */
@XmlRootElement(name="mirror")
@XmlAccessorType(XmlAccessType.FIELD)
public class MirrorConfig {
	@XmlElement
	private String id;
	@XmlElement
	private String name;
	@XmlElement
	private String url;
	@XmlElement
	private String mirrorOf;
	
	public MirrorConfig(){
	}

	/**
	 * Returns the id of the represented mirror remote repository.
	 * 
	 * */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of the represented mirror remote repository.
	 * 
	 * */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the name of the represented mirror remote repository.
	 * 
	 * */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the represented mirror remote repository.
	 * 
	 * */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the URL of the represented mirror remote repository.
	 * 
	 * */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL of the represented mirror remote repository.
	 * 
	 * */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Returns the id of the mirrored remote repository.
	 * 
	 * */
	public String getMirrorOf() {
		return mirrorOf;
	}

	/**
	 * Sets the id of the mirrored remote repository.
	 * 
	 * */
	public void setMirrorOf(String mirrorOf) {
		this.mirrorOf = mirrorOf;
	}

	public RemoteRepository createRemoteRepository(RemoteRepository.Builder mirroredRemoteRepositoryBuilder){
		RemoteRepository mirroredRemoteRepository=mirroredRemoteRepositoryBuilder.build();
		RemoteRepository.Builder builder=new RemoteRepository.Builder(mirroredRemoteRepository);
		builder.setId(getId());
		builder.setUrl(getUrl());
		builder.addMirroredRepository(mirroredRemoteRepository);
		RemoteRepository mirrorRepository=builder.build();
		return mirrorRepository;
	}
}
