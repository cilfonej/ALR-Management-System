package edu.wit.alr.database.tracking;

import org.springframework.stereotype.Component;

import edu.wit.alr.database.model.DBObject;

@Component
public class DatabaseEventMonitor implements HibernateEventListener {
	private static final long serialVersionUID = 7491748186571323562L;
	
	private ObjectValueTracker tracker = new ObjectValueTracker();
	
	public void beforeLoad(Object entity) { }

	public void afterLoad(Object entity) {
		// ignore any none DBOjects saved to the database
		if(!(entity instanceof DBObject)) return;
		DBObject obj = (DBObject) entity;
		
		tracker.track(obj);
	}
	
	public void beforeInsert(Object obj) { }
	
	public void afterInsert(Object entity) {
		// ignore any none DBOjects saved to the database
		if(!(entity instanceof DBObject)) return;
		DBObject obj = (DBObject) entity;
		
		System.out.println("New Object: " + obj.getId());
	}
	
	public void beforeSave(Object obj) { }
	
	public void afterSave(Object entity) {
		// ignore any none DBOjects saved to the database
		if(!(entity instanceof DBObject)) return;
		DBObject obj = (DBObject) entity;

		System.out.println(tracker.compare(obj));
	}
	
	public void beforeDelete(Object obj) { }
	
	public void afterDelete(Object entity) {
		// ignore any none DBOjects saved to the database
		if(!(entity instanceof DBObject)) return;
		DBObject obj = (DBObject) entity;

		System.out.println("Deleted Object: " + obj.getId());
	}
}
