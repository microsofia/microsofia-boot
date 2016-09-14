package microsofia.boot.config;

import javax.xml.bind.annotation.*;

import org.eclipse.aether.repository.Proxy;

/**
 * Configuration object used to configure proxies. Only one Proxy can be active at a time.
 * The following configuration is like the <a href="https://maven.apache.org/settings.html">Maven one</a>
 * 
 * @see org.eclipse.aether.repository.Proxy
 * */
@XmlRootElement(name="proxy")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProxyConfig {
	@XmlElement
	private String id;
	@XmlElement
	private Boolean active;
	@XmlElement
	private String protocol;//type http or https
	@XmlElement
	private String host;
	@XmlElement
	private int port;
    //TODO add username, password, nonProxyHosts

    public ProxyConfig(){
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Proxy createProxy(){
		return new Proxy(getProtocol(),getHost(),getPort());
	}
}
