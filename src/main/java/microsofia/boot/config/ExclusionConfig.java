package microsofia.boot.config;

import javax.xml.bind.annotation.*;

import org.eclipse.aether.graph.Exclusion;

/**
 * Configuration object used to configure an Aether Exclusion object.
 * 
 * @see org.eclipse.aether.graph.Exclusion
 */
@XmlRootElement(name="exclusion")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExclusionConfig {
	@XmlElement
	private String groupId;
	@XmlElement
    private String artifactId;
	@XmlElement
    private String classifier;
	@XmlElement
    private String extension;

    public ExclusionConfig(){
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

	public Exclusion createExclusion(){
		return new Exclusion(getGroupId(), getArtifactId(), getClassifier(), getExtension());
	}
}
