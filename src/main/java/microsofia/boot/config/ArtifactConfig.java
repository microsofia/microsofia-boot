package microsofia.boot.config;

import java.io.File;
import java.util.*;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

/**
 * Configuration object used to configure an Aether Artifact object.
 * 
 * @see org.eclipse.aether.artifact.Artifact
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ArtifactConfig {
	@XmlElement
	private String groupId;
	@XmlElement
	private String artifactId;
	@XmlElement
	private String version;
	@XmlElement
	private String classifier;
	@XmlElement
	private String extension;
    @XmlJavaTypeAdapter(PropertyConfig.PropertiesAdapters.class)
	private Map<String, String> properties;

    public ArtifactConfig(){
    	properties=new HashMap<>();
    	extension="jar";
    }
    
    public String getGroupId() {
		return groupId;
	}

    public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

    public String getArtifactId() {
		return artifactId;
	}

    public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

    public String getVersion() {
		return version;
	}

    public void setVersion(String version) {
		this.version = version;
	}

    public String getClassifier() {
		return classifier;
	}

    public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

    public String getExtension() {
		return extension;
	}
    
    public void setExtension(String extension) {
		this.extension = extension;
	}
    
    public Map<String, String> getProperties() {
		return properties;
	}
    
    public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}   

    public Artifact createArtifact(){
		return new DefaultArtifact(getGroupId(),getArtifactId(),getClassifier(),getExtension(),getVersion(),properties,(File)null);
	}
}