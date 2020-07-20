package edu.wit.alr.database.tracking;

import java.util.HashMap;
import java.util.Map;

import edu.wit.alr.database.tracking.FieldSet.FieldData;

@FunctionalInterface
interface ValueExtractor {
	public Object extractValue(FieldData field, Object obj) throws ExtractionException;

// ======================================================================================= \\
// ======================================================================================= \\
	
	static final Map<Class<? extends ValueExtractor>, ValueExtractor> EXTRACTORS = new HashMap<>();
	
	public static ValueExtractor getByClass(Class<? extends ValueExtractor> clazz) {
		ValueExtractor extractor = EXTRACTORS.get(clazz);
		if(extractor != null) return extractor;

		try {
			extractor = clazz.getDeclaredConstructor().newInstance();
			EXTRACTORS.put(clazz, extractor);
			return extractor;
			
		} catch(ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
			throw new IllegalArgumentException("Could not build an instance of " + clazz.getSimpleName() + "! "
												+ "ValueExtractor MUST have a 'no-args' default constructor", e);
		}
	}
	
	public static class DefaultValueExtractor implements ValueExtractor {
		public Object extractValue(FieldData field, Object obj) throws ExtractionException {
			try {
				return field.getField().get(obj);
				
			} catch(IllegalArgumentException | IllegalAccessException e) {
				throw new ExtractionException(e);
			}
		}
	}
	
	public static class ExtractionException extends RuntimeException {
		private static final long serialVersionUID = 2251748608445624422L;

		public ExtractionException(String message, Throwable cause) {
			super(message, cause);
		}

		public ExtractionException(String message) {
			super(message);
		}
		
		public ExtractionException(Throwable cause) {
			super(cause);
		}
	}
}
