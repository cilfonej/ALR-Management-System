package edu.wit.alr.exceptions;

import java.util.function.Consumer;
import java.util.function.Function;

public class ErrorUtils {
	@SuppressWarnings("unchecked")
	public static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
		throw (E) e;
	}
	
	public static interface VolatileRunnable {
		public void run() throws Exception;
		
	}
	
	public static interface VolatileConsumer<T> extends Consumer<T> {
		public void acceptWithException(T t) throws Exception;
		
		public default void accept(T t) {
			try {
				acceptWithException(t);
			} catch(Exception e) {
				// catch and throw exceptions
				throw new SneakyException(e);
			} 
		}
	}
	
	public static interface VolatileFunction<T, R> {
		public R apply(T t) throws Exception;
		
	}
	
	public static Runnable sneakyThrow(VolatileRunnable runnable) {
		return () -> {
			try {
				runnable.run();
			} catch(Exception e) {
				// catch and throw exceptions
				sneakyThrow(e);
			} 
		};
	}
	
	public static <T> Consumer<T> sneakyThrow(VolatileConsumer<T> consumer) {
		return t -> {
			try {
				consumer.accept(t);
			} catch(Exception e) {
				// catch and throw exceptions
				throw new SneakyException(e);
			} 
		};
	}
	
	public static <T, R> Function<T, R> sneakyThrow(VolatileFunction<T, R> function) {
		return t -> {
			try {
				return function.apply(t);
			} catch(Exception e) {
				// catch and throw exceptions
				throw new SneakyException(e);
			} 
		};
	}
	
	private static class SneakyException extends RuntimeException {
		private static final long serialVersionUID = 6658953565849214552L;
		
		public SneakyException(Throwable cause) {
			sneakyThrow(cause);
		}
	}
}
