package microsofia.boot.config;

import javax.xml.bind.annotation.*;

import org.eclipse.aether.repository.RepositoryPolicy;

/**
 * Configuration object used to configure a Repository policy.
 * The following configuration is like the <a href="https://maven.apache.org/settings.html">Maven one</a>
 * 
 * @see org.eclipse.aether.repository.RepositoryPolicy
 * */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RepositoryPolicyConfig {
	@XmlElement
	private boolean enabled;
	@XmlElement
	private String updatePolicy;
	@XmlElement
	private String checksumPolicy;

    public RepositoryPolicyConfig(){
    }

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getUpdatePolicy() {
		return updatePolicy;
	}

	public void setUpdatePolicy(String updatePolicy) {
		this.updatePolicy = updatePolicy;
	}

	public String getChecksumPolicy() {
		return checksumPolicy;
	}

	public void setChecksumPolicy(String checksumPolicy) {
		this.checksumPolicy = checksumPolicy;
	}

	public RepositoryPolicy createRepositoryPolicy(){
		return new RepositoryPolicy(isEnabled(),getUpdatePolicy(),getChecksumPolicy());
	}
}
