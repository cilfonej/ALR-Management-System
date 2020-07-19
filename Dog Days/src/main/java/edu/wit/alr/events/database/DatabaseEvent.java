package edu.wit.alr.events.database;

import org.springframework.context.ApplicationEvent;

import edu.wit.alr.database.model.DBObject;

abstract class DatabaseEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1053812078861274306L;

	public Class<? extends DBObject> type;
	public String typeName;
	
	public DatabaseEvent(DBObject source) {
		super(source);
		
		this.type = source.getClass();
		this.typeName = type.getSimpleName();
	}

	public Class<? extends DBObject> getType() { return type; }
	public String getTypeName() { return typeName; }
}
