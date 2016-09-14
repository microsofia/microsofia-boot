package microsofia.boot.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Configuration object used to un/marshal Artifact properties.
 * 
 * @see ArtifactConfig
 * */
@XmlRootElement(name="property")
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyConfig{
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

	public void fill(Map<String,String> map){
		map.put(name, value);
	}

	public static class PropertiesAdapters extends XmlAdapter<PropertyConfig[], Map<String, String>>{
		
		public PropertiesAdapters(){
		}
			
		@Override
		public Map<String,String> unmarshal(PropertyConfig[] properties) throws Exception{
			Map<String,String> map=new HashMap<>();
			if (properties!=null){
				for (PropertyConfig c: properties){
					map.put(c.getName(),c.getValue());
				}
			}
	        return map;
	    }

	    @Override
	    public PropertyConfig[] marshal(Map<String,String> properties) throws Exception{
	    	List<PropertyConfig> list=new ArrayList<>();
	    	if (properties!=null){
	    		properties.entrySet().forEach(it->{
	    			list.add(new PropertyConfig(it.getKey(), it.getValue()));
	    		});
	    	}
	        return list.toArray(new PropertyConfig[0]);
	    }
	}
}
