package microsofia.boot.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Configuration object used to un/marshal Artifact properties.
 * 
 * @see ArtifactConfig
 * */
public class PropertiesConfig{
	@XmlElement(name="property")
	public PropertyConfig[] properties;

	public PropertiesConfig(){
	}
	
	public PropertiesConfig(PropertyConfig[] properties){
		this.properties=properties;
	}
	
	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class PropertyConfig{
		@XmlAttribute
		private String name;
		@XmlAttribute
		private String value;
		
		public PropertyConfig(){
		}
	
		public PropertyConfig(String n,String v){
			name=n;
			value=v;
		}
		
		public String getName() {
			return name;
		}
	
		public void setName(String name) {
			this.name = name;
		}
	
		public String getValue() {
			return value;
		}
	
		public void setValue(String value) {
			this.value = value;
		}
	}

	public static class PropertiesAdapters extends XmlAdapter<PropertiesConfig, Map<String, String>>{
		
		public PropertiesAdapters(){
		}
			
		@Override
		public Map<String,String> unmarshal(PropertiesConfig properties) throws Exception{
			Map<String,String> map=new HashMap<>();
			if (properties!=null){
				for (PropertyConfig c: properties.properties){
					map.put(c.getName(),c.getValue());
				}
			}
	        return map;
	    }

	    @Override
	    public PropertiesConfig marshal(Map<String,String> properties) throws Exception{
	    	List<PropertyConfig> list=new ArrayList<>();
	    	if (properties!=null){
	    		properties.entrySet().forEach(it->{
	    			list.add(new PropertyConfig(it.getKey(), it.getValue()));
	    		});
	    	}
	        return new PropertiesConfig(list.toArray(new PropertyConfig[0]));
	    }
	}
}
