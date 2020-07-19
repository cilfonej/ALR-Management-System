package edu.wit.alr.events.database;

import edu.wit.alr.database.model.DBObject;

public class EntryDeletedEvent extends DatabaseEvent {
	private static final long serialVersionUID = -8711180704819976773L;

	public EntryDeletedEvent(DBObject oldEntry) {
		super(oldEntry);
	}
}