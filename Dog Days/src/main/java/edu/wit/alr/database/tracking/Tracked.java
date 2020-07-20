package edu.wit.alr.database.tracking;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Tracked {
	
	@AliasFor("name")
	public String value() default "";
	
	@AliasFor("value")
	public String name() default "";
	
	public Class<? extends ValueExtractor> extractor() default ValueExtractor.DefaultValueExtractor.class; 
}
