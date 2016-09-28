package microsofia.boot;

import microsofia.boot.config.DependencyConfig;
import microsofia.boot.config.MirrorConfig;
import microsofia.boot.config.ProxyConfig;
import microsofia.boot.config.RepositoryConfig;
import microsofia.boot.config.Settings;

/**
 * Generates a settings template using POJO configuration.
 * */
public class SettingsTemplateBuilder {

	public static Settings createSettings(){
		Settings settings=new Settings();
		
		settings.setLocalRepository("d:\\.m2");
		settings.setOffline(false);
		settings.setUpdatePolicy("always");

		DependencyConfig dependency=new DependencyConfig();
		dependency.setGroupId("sample.groupid");
		dependency.setArtifactId("sample.artifactid");
		dependency.setVersion("1.0");
		dependency.setScope("test");
		dependency.getProperties().put("k1", "v1");
		dependency.getProperties().put("k2", "v2");
		settings.setDependency(dependency);

		ProxyConfig proxy=new ProxyConfig();
		proxy.setActive(true);
		proxy.setHost("localhost");
		proxy.setId("proxy1");
		proxy.setPort(1111);
		proxy.setProtocol("http");
		settings.getProxies().add(proxy);

		MirrorConfig mirror=new MirrorConfig();
		mirror.setId("m1");
		mirror.setMirrorOf("repo1");
		mirror.setUrl("http://mvnrepo/");
		mirror.setName("mirror1");
		settings.getMirrors().add(mirror);

		RepositoryConfig repo=new RepositoryConfig();
		repo.setId("repo1");
		repo.setName("repo1");
		repo.setUrl("http://mvnrepo/");
		settings.getRepositories().add(repo);
		
		return settings;
	}
}
