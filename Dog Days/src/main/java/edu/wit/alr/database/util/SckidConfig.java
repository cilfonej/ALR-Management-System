package edu.wit.alr.database.util;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE, FIELD})
@Retention(RUNTIME)
public @interface SckidConfig {
	public byte prefix() default -1;
}
