package microsofia.boot.config;

import java.util.*;
import javax.xml.bind.annotation.*;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.Exclusion;

/**
 * Configuration object used to configure an Aether Dependency object.
 * DependencyConfig is extending ArtifactConfig instead of encapsulating it in order to ease XML configuration.
 * 
 * @see org.eclipse.aether.graph.Dependency
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DependencyConfig extends ArtifactConfig{
	@XmlElement
    private String scope;
	@XmlElement
    private Boolean optional;
	@XmlElementWrapper(name="exclusions")
	@XmlElement(name="exclusion")
    private List<ExclusionConfig> exclusions;

    public DependencyConfig(){
    	setExclusions(new ArrayList<>());
    }

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Boolean getOptional() {
		return optional;
	}

	public void setOptional(Boolean optional) {
		this.optional = optional;
	}

	public List<ExclusionConfig> getExclusions() {
		return exclusions;
	}

	public void setExclusions(List<ExclusionConfig> exclusions) {
		this.exclusions = exclusions;
	}

	public Dependency createDependency(){
		Dependency dependency=new Dependency(createArtifact(),getScope());
		dependency.setOptional(getOptional());
		
		List<Exclusion> exclusions=new ArrayList<Exclusion>();
		for (ExclusionConfig c : getExclusions()){
			exclusions.add(c.createExclusion());
		}
		dependency.setExclusions(exclusions);
		
		return dependency;
	}
}
