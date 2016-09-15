package microsofia.boot.config;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.*;
import javax.xml.bind.annotation.*;

import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;

/**
 * Settings of the Aether session, which looks like the
 * <a href="https://maven.apache.org/settings.html">Maven settings file</a>.
 * <br />
 * Example: <br />
 * <br />
<pre>
	&lt;settings>
	  &lt;!-- Path to the local repository that will be created and updated -->
	  &lt;localRepository>d:\.m2&lt;/localRepository>
	  
	  &lt;!-- 
	   | Default: false
	  &lt;offline>false&lt;/offline>
	  -->
	  
	  &lt;!-- Root dependency that contains the main that you want to execute -->
	  &lt;dependency>
	  	&lt;groupId>microsofia&lt;/groupId>
		&lt;artifactId>sample&lt;/artifactId>
		&lt;version>1.0<&lt;version>
	  &lt;/dependency>
	
	  &lt;!-- List of proxies, only one should be active -->
	  &lt;proxies>
		&lt;proxy>
			&lt;active>true&lt;/active>
			&lt;protocol>http&lt;/protocol>
			&lt;host>proxy&lt;/host>
			&lt;port>1111&lt;/port>
		&lt;/proxy>
	  &lt;/proxies>
	
	  &lt;!-- List of mirrors-->
      &lt;mirrors>
         &lt;mirror>
           &lt;id>amirror&lt;/id>
           &lt;mirrorOf>centraltt&lt;/mirrorOf>
           &lt;url>http://downloads.planetmirror.com/pub/maven2&lt;/url>
        &lt;/mirror>
      &lt;/mirrors>
	  
	  &lt;!-- List of repositories -->
		&lt;repositories>
			&lt;repository>
				&lt;id>mvncentral&lt;/id>
				&lt;name>mvncentral&lt;/name>
				&lt;url>http://snapshots.maven.codehaus.org/maven2&lt;/url>
			&lt;/repository>
		&lt;/repositories>
	   
	&lt;/settings>

</pre>
 * 
 * 
 * @see org.eclipse.aether.repository.RemoteRepository
 * @see org.eclipse.aether.RepositorySystem
 * @see org.eclipse.aether.RepositorySystemSession
 */
@XmlRootElement(name="settings")
@XmlAccessorType(XmlAccessType.FIELD)
public class Settings{
	@XmlElement
	private String localRepository;
	@XmlElement
	private Boolean offline;
	@XmlElement
	private String updatePolicy;
	@XmlElement
	private DependencyConfig dependency;
	@XmlElementWrapper(name="proxies")
	@XmlElement(name="proxy")
	private List<ProxyConfig> proxies;
	@XmlElementWrapper(name="mirrors")
	@XmlElement(name="mirror")
	private List<MirrorConfig> mirrors;
	@XmlElementWrapper(name="repositories")
	@XmlElement(name="repository")
	private List<RepositoryConfig> repositories;

	public Settings(){
		proxies=new ArrayList<>();
		mirrors=new ArrayList<>();
		repositories=new ArrayList<>();
	}

	public String getLocalRepository() {
		return localRepository;
	}

	public void setLocalRepository(String localRepository) {
		this.localRepository = localRepository;
	}

	public Boolean getOffline() {
		return offline;
	}

	public void setOffline(Boolean offline) {
		this.offline = offline;
	}

	public String getUpdatePolicy() {
		return updatePolicy;
	}

	public void setUpdatePolicy(String updatePolicy) {
		this.updatePolicy = updatePolicy;
	}

	public DependencyConfig getDependency() {
		return dependency;
	}

	public void setDependency(DependencyConfig dependency) {
		this.dependency = dependency;
	}

	public List<ProxyConfig> getProxies() {
		return proxies;
	}

	public void setProxies(List<ProxyConfig> proxies) {
		this.proxies = proxies;
	}

	public List<MirrorConfig> getMirrors() {
		return mirrors;
	}

	public void setMirrors(List<MirrorConfig> mirrors) {
		this.mirrors = mirrors;
	}

	public List<RepositoryConfig> getRepositories() {
		return repositories;
	}

	public void setRepositories(List<RepositoryConfig> repositories) {
		this.repositories = repositories;
	}
	
	public void writeTo(OutputStream out) throws Exception{
		Marshaller marshaller=jaxbContext.createMarshaller();
		marshaller.marshal(this, out);
	}
	
	protected Proxy createProxy(){
		for (ProxyConfig c : getProxies()){
			if (c.getActive().booleanValue()){
				return c.createProxy();
			}
		}
		return null;					
	}
	
	public List<RemoteRepository> createRepositories(){
		Proxy proxy=createProxy();
		
		Map<String,RemoteRepository.Builder> repos=new HashMap<>();
		
		for (RepositoryConfig c : getRepositories()){
			RemoteRepository.Builder builder=c.createRemoteRepositoryBuilder();
			builder.setProxy(proxy);

			repos.put(c.getId(), builder);
		}
		
		for (MirrorConfig c : getMirrors()){
			RemoteRepository.Builder mirrored=repos.get(c.getMirrorOf());
			if (mirrored!=null){
				c.createRemoteRepository(mirrored);
			}
		}

		return repos.values().stream().map(RemoteRepository.Builder::build).collect(Collectors.toList());
	}
	
	
	private static JAXBContext jaxbContext=null;
	static{
		try{
			jaxbContext=JAXBContext.newInstance(Settings.class);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static Settings readFrom(InputStream in) throws Exception{
		Unmarshaller unmarshaller=jaxbContext.createUnmarshaller();
		return (Settings)unmarshaller.unmarshal(in);
	}
}
 