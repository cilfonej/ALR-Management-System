package edu.wit.alr.services.inflators;

public interface Inflator<Type, Data> {
	public Type inflate(Data data);
}
