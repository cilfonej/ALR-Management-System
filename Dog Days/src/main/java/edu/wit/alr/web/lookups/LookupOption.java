package edu.wit.alr.web.lookups;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LookupOption {
	/** Type-name of the data, used to determine option render-function */
	protected String type;
	
	/** Value matched against user input during filtering */
	protected String filter;
	
	/** The text value displayed when the option is selected */
	protected String text;
	
	/** Unique identifier for each option in a set */ 
	protected String key;

	// no-args constructor
	LookupOption() { }
	
	LookupOption(String type, String filter, String text, Object key) {
		this.type = type;
		this.filter = filter;
		this.text = text;
		
		this.key = String.valueOf(key);
	}
	
	public static class LookupGroup {
		/** Value used to sort groups, lower number closer to top */
		protected int order;
		
		protected String name;
		protected List<LookupOption> options;
		
		public LookupGroup(String name, int order, List<LookupOption> options) {
			this.order = order;
		
			this.name = name;
			this.options = options;
		}
		
		public LookupGroup(String name, int order) {
			this(name, order, new ArrayList<>());
		}
		
		public LookupGroup(String name) {
			this(name, 0);
		}
		
		public LookupGroup addOption(LookupOption option) { 
			options.add(option); 
			return this; 
		}
		
		public static List<LookupGroup> fromMap(Map<String, List<LookupOption>> groupings) {
			List<LookupGroup> list = new ArrayList<>();
			
			for(Map.Entry<String, List<LookupOption>> groupEntry : groupings.entrySet()) {
				LookupGroup group = new LookupGroup(groupEntry.getKey());
				group.options = groupEntry.getValue();
				
				list.add(group);
			}
			
			list.sort((a, b) -> a.order - b.order);
			return list;
		}
	}
}
