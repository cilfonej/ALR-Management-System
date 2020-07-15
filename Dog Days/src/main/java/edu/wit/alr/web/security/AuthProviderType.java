package edu.wit.alr.web.security;

import java.util.NoSuchElementException;

public enum AuthProviderType {
	Google, Facebook, Yahoo, local;
	
	public static AuthProviderType of(String name) {
		for(AuthProviderType type : values()) {
			if(type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		
		throw new NoSuchElementException("No register provider with name: " + name);
	}
}
