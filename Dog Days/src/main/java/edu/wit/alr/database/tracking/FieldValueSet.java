package edu.wit.alr.database.tracking;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.wit.alr.database.tracking.FieldSet.FieldData;

class FieldValueSet {
	private FieldSet fieldSet;
	private Map<FieldData, Object> values;
	
	private FieldValueSet() { }
	
	public boolean isEmpty() { 
		return fieldSet.isEmpty();
	}
	
	public static FieldValueSet of(Object obj) {
		Class<?> clazz = obj.getClass();
		FieldValueSet valueSet = new FieldValueSet();
		
		valueSet.fieldSet = FieldSet.of(clazz);
		valueSet.values = collectValues(valueSet.fieldSet, obj);
		
		return valueSet;
	}
	
	private static Map<FieldData, Object> collectValues(FieldSet fields, Object obj) {
		if(fields.isEmpty()) return Collections.emptyMap();
		
		Map<FieldData, Object> values = new LinkedHashMap<>();
		for(FieldData field : fields.getFields()) {
			// extract the current values for each field
			values.put(field, field.extractValue(obj));
		}
		
		return values;
	}
	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean compareValue(Object oldVal, Object newVal) {
		if(oldVal == newVal) return true;	// check if they're the same object
		if((oldVal == null) != (newVal == null)) return false; // check if only-one is null
		
//		if(oldVal instanceof Comparable) {
//			// if we fail to use the compare method, just move on to other options
//			try { return ((Comparable) oldVal).compareTo(newVal) == 0; } catch(Exception e) { }
//		}
//		
//		if(newVal instanceof Comparable) {
//			// if we fail to use the compare method, just move on to other options
//			try { return ((Comparable) newVal).compareTo(oldVal) == 0; } catch(Exception e) { }
//		}
		
		// if no other options, fallback to equals method
		return oldVal.equals(newVal);
	}
	
	public List<FieldChange> compare(Object obj) {
		if(isEmpty()) return Collections.emptyList();
		
		Map<FieldData, Object> newValues = collectValues(fieldSet, obj);
		List<FieldChange> changes = new LinkedList<>();
		
		// go through all the fields and look for differences
		newValues.forEach((field, newValue) -> {
			Object oldValue = values.get(field);
			if(!compareValue(oldValue, newValue)) {
				changes.add(new FieldChange(field, oldValue, newValue));
			}
		});
		
		return changes;
	}
	
	public static class FieldChange {
		private FieldData field;
		private Object oldValue, newValue;
		
		public FieldChange(FieldData field, Object oldValue, Object newValue) {
			this.field = field;
			
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		public String getName() { return field.getName(); }

		public Object getOldValue() { return oldValue; }
		public Object getNewValue() { return newValue; }

		public String toString() {
			return field.getName() + " = (" + oldValue + " -> " + newValue + ")";
		}
	}
}
