package edu.wit.alr.database.tracking;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.wit.alr.database.model.DBObject;
import edu.wit.alr.database.tracking.FieldValueSet.FieldChange;
import edu.wit.alr.events.EventPublisher;
import edu.wit.alr.events.database.EntryCreatedEvent;
import edu.wit.alr.events.database.EntryDeletedEvent;
import edu.wit.alr.events.database.EntryFieldChangedEvent;

@Component
public class DatabaseEventMonitor implements HibernateEventListener {
	private static final long serialVersionUID = 7491748186571323562L;
	
	@Autowired 
	private EventPublisher publisher;
	private ObjectValueTracker tracker = new ObjectValueTracker();
	
	
	public void beforeLoad(Object entity) { }

	public void afterLoad(Object entity) {
		// ignore any none DBOjects saved to the database
		if(!(entity instanceof DBObject)) return;
		DBObject obj = (DBObject) entity;
		
		// record initial values of object
		tracker.track(obj);
	}
	
	
	public void beforeInsert(Object obj) { }
	
	public void afterInsert(Object entity) {
		// ignore any none DBOjects saved to the database
		if(!(entity instanceof DBObject)) return;
		DBObject obj = (DBObject) entity;
		
		// fire-event for the new object
		publisher.fireEvent(new EntryCreatedEvent(obj));
	}
	
	
	public void beforeSave(Object obj) { }
	
	public void afterSave(Object entity) {
		// ignore any none DBOjects saved to the database
		if(!(entity instanceof DBObject)) return;
		DBObject obj = (DBObject) entity;

		List<FieldChange> changes = tracker.compare(obj);
		if(changes == null) return; // no changes were tracked
		
		// for all of the @Tracked fields that changed (could be empty list)
		for(FieldChange change : changes) {
			// fire-event for the change
			publisher.fireEvent(new EntryFieldChangedEvent(
					obj, change.getName(), change.getOldValue(), change.getNewValue()));
		}
	}
	
	
	public void beforeDelete(Object obj) { }
	
	public void afterDelete(Object entity) {
		// ignore any none DBOjects saved to the database
		if(!(entity instanceof DBObject)) return;
		DBObject obj = (DBObject) entity;

		// fire-event for the old object
		publisher.fireEvent(new EntryDeletedEvent(obj));
	}
}
