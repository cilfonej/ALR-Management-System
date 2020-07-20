package edu.wit.alr.database.tracking;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.wit.alr.database.tracking.ValueExtractor.ExtractionException;

class FieldSet {
	private static Map<Class<?>, Set<FieldData>> field_cache = new HashMap<>();
	private static Map<Class<?>, FieldSet> set_cache = new HashMap<>();

	public static FieldSet of(Class<?> clazz) {
		return set_cache.computeIfAbsent(clazz, FieldSet::new);
	}
	
	
// ======================================================================================= \\
// ======================================================================================= \\
	
	
	private Set<FieldData> fields;
	
	private FieldSet(Class<?> clazz) {
		this.fields = Collections.unmodifiableSet(buildFieldSet(clazz));
	}
	
	public boolean isEmpty() { return fields.isEmpty(); }
	public Set<FieldData> getFields() { return fields; }
	
	private static Set<FieldData> buildFieldSet(Class<?> clazz) {
		Set<FieldData> fieldSet = new LinkedHashSet<>();
		
		while(clazz != null) {
			// attempt to get the fields from the cache, if they're not found then collect and cache them
			Set<FieldData> fields = field_cache.computeIfAbsent(clazz, FieldSet::collectTrackableFields);
			fieldSet.addAll(fields);
		
			// check the parent-class for fields
			clazz = clazz.getSuperclass();
		}
		
		return fieldSet;
	}
	
	private static Set<FieldData> collectTrackableFields(Class<?> clazz) {
		Set<FieldData> fields = new LinkedHashSet<>();
		
		for(Field field : clazz.getDeclaredFields()) {
			// check for @Tracked on the field
			Tracked meta = field.getAnnotation(Tracked.class);
			if(meta == null) continue; // if the annotation is missing; continue

			// ensue the field can be accessed
			field.setAccessible(true);
			
			// add field to the list
			fields.add(new FieldData(meta, field));
		}
		
		return fields;
	}
	
	public static class FieldData {
		private Tracked meta;
		private Field field;
		
		private ValueExtractor extractor;
		
		public FieldData(Tracked meta, Field field) {
			this.meta = meta;
			this.field = field;
			
			this.extractor = ValueExtractor.getByClass(meta.extractor());
		}
		
		public Object extractValue(Object obj) throws ExtractionException {
			return extractor.extractValue(this, obj);
		}
		
		public String getName() {
			String v = meta.value();
			return v == null || v.isBlank() ? field.getName() : v;
		}

		public Tracked getMeta() { return meta; }
		public Field getField() { return field; }
	}
}
