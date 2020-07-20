package edu.wit.alr.config;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

/**
 * 	Allows for the use of *.yaml files in {@link org.springframework.context.annotation.PropertySource @PropertySource}.
 * 
 * 	<p>
 *  Code based on: https://stackoverflow.com/a/51392715
 * 
 * 	@author cilfonej
 */
public class YamlPropertyLoaderFactory extends DefaultPropertySourceFactory {
	private final YamlPropertySourceLoader LOADER = new YamlPropertySourceLoader();
	
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        if(resource == null)
            return super.createPropertySource(name, resource);

        List<PropertySource<?>> results = LOADER.load(resource.getResource().getFilename(), resource.getResource());
        if(results.size() <= 0) 
        	throw new IOException("Unable to load resource: " + name);
        
        return results.get(0);
    }
}