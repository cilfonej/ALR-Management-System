package edu.wit.alr.database.tracking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wit.alr.database.model.DBObject;
import edu.wit.alr.database.tracking.FieldValueSet.FieldChange;

public class ObjectValueTracker {
	private DatabaseReferenceMonitor<DBObject> monitor = new DatabaseReferenceMonitor<>();
	private Map<Integer, FieldValueSet> tracking = new HashMap<>();

	// NOTE: This tracking process is based off the idea that: many instances
	//		 of an Object may be retrieved from the database, but only one of
	//		 those instances will ever be saved.
	
	public void track(DBObject obj) {
		// check if object has been persisted
		if(obj.getId() == 0) return; // if not, return
		
		// check if a version of the object is currently being tracked
		if(tracking.containsKey(obj.getId())) {
			// add another reference to the tracking list
			monitor.monitor(obj, () -> tracking.remove(obj.getId()));
			return; // then return
		}
		
		// extract the current values from the object
		FieldValueSet values = FieldValueSet.of(obj);
		if(values.isEmpty()) return; // if there are no values to track, return
		
		// ID is unique across all DBObject, so it's a safe key in this case
		// only non-new object with @Tracked fields are recorded
		tracking.put(obj.getId(), values);
		
		// add reference-monitor that removes the value-set if object is destroyed before being compared
		monitor.monitor(obj, () -> tracking.remove(obj.getId()));
	} 
	
	/**
	 * 	Compares the object's new-values against the values saved and returns
	 * 	a List of {@link FieldChange}. If the object has never been persisted,
	 * 	or no fields were marked as {@link Tracked}, then null may be returned.
	 * 
	 * 	<p>
	 * 	NOTE: This method will remove the reference to the supplied object upon
	 * 	returning, so be sure to only call it once per object.
	 * 
	 * 	@param obj the new version of the Object
	 * 	@return a list of changes, or null if no initial-state was recorded
	 */
	public List<FieldChange> compare(DBObject obj) {
		// check if object has been persisted
		if(obj.getId() == 0) return null; // if not, return
		
		FieldValueSet oldValues = tracking.remove(obj.getId());
		if(oldValues == null) return null; // nothing was tracked

		// no longer care what happens with the other instances
		monitor.stopMonitor(obj.getId());

		return oldValues.compare(obj);
	}
}
