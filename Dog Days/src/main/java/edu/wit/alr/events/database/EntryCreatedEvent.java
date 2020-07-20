package edu.wit.alr.events.database;

import edu.wit.alr.database.model.DBObject;

public class EntryCreatedEvent extends DatabaseEvent {
	private static final long serialVersionUID = 4571707185136531646L;
	
	public EntryCreatedEvent(DBObject newEntery) {
		super(newEntery);
	}
}
