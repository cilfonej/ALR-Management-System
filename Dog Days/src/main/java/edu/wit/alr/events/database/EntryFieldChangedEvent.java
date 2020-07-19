package edu.wit.alr.events.database;

import edu.wit.alr.database.model.DBObject;

public class EntryFieldChangedEvent extends DatabaseEvent {
	private static final long serialVersionUID = -4332106372934258351L;

	private String field;
	
	private Object oldValue;
	private Object newValue;
	
	public EntryFieldChangedEvent(DBObject entry, String field, Object oldValue, Object newValue) {
		super(entry);
		this.field = field;
		
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public String getField() { return field; }

	public Object getOldValue() { return oldValue; }
	public Object getNewValue() { return newValue; }
}