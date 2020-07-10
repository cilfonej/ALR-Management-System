package edu.wit.alr.web.preprocessor;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.context.IContext;

public class SubContext implements IContext {
	private IContext superContext;
	private Map<String, Object> variables;

	public SubContext(IContext superContext, Map<String, Object> variables) {
		this.superContext = superContext;
		this.variables = variables == null ? new LinkedHashMap<>(10) : new LinkedHashMap<>(variables);
	}
	
	public Locale getLocale() { return superContext.getLocale(); }

	public boolean containsVariable(String name) {
		return variables.containsKey(name) || superContext.containsVariable(name);
	}

	public Set<String> getVariableNames() {
		// ensure the base set is mutable
		Set<String> vars = new HashSet<>(variables.keySet());
		vars.addAll(superContext.getVariableNames());
		return vars;
	}

	public Object getVariable(String name) {
		return variables.containsKey(name) ? variables.get(name) : superContext.getVariable(name);
	}
	
	public void setVariable(String name, Object value) { this.variables.put(name, value); }
	public void removeVariable(String name) { this.variables.remove(name); }
	public void clearVariables() { this.variables.clear(); }
}
