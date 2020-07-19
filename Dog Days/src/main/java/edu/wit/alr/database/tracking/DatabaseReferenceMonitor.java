package edu.wit.alr.database.tracking;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.wit.alr.database.model.DBObject;

public class DatabaseReferenceMonitor<T extends DBObject> {
	
	private ReferenceQueue<T> watchQueue = new ReferenceQueue<>();
	private Map<Integer, WatchNode<T>> trackedEntries = new ConcurrentHashMap<>();
	
	// lazy-init thread when we have objects to track
	private Thread thread = null;

	/**
	 *	Start to monitor given handle object for becoming weakly reachable. When the
	 *	handle isn't used anymore, the given listener will be called.
	 * 
	 *	@param obj		the object that will be monitored
	 *	@param listener the listener that will be called upon release of the handle
	 */
	public void monitor(T obj, Runnable listener) {
		// create weak-reference to object, so we can be notified when it is collected
		WatchNode<T> node = trackedEntries.computeIfAbsent(obj.getId(), id -> new WatchNode<>(id, listener));
		node.add(obj, watchQueue);
		
		// lazy start thread once a reference is added
		synchronized(DatabaseReferenceMonitor.class) {
			if(thread == null) {
				thread = new Thread(this::watch, "DatabaseReferenceMonitor - Thread");
				thread.setDaemon(true);
				thread.start();
			}
		}
	}
	
	/** 
	 * 	Removes the list of watched references from the monitor.
	 * 	They're listener will no longer fire.
	 */
	public void stopMonitor(int key) {
		trackedEntries.remove(key);
	}

	/** Thread's run method, performs the actual reference monitoring */
	public void watch() {
		try {
			// check if there are any tracked entries left
			while(!trackedEntries.isEmpty()) {
				try {
					@SuppressWarnings("unchecked")
					// get the expired reference
					WatchNode<T>.NodeReference reference = (WatchNode<T>.NodeReference) watchQueue.remove();
					WatchNode<T> node = reference.remove(); // remove it from the node list
					
					// make sure the node is still the one being tracked
					if(trackedEntries.get(node.getKey()) != node) continue;
					
					// attempt to perform the cleanup operation on the entity
					if(node.attemptCleanup()) {
						// if there were no references left, remove the monitor
						trackedEntries.remove(node.getKey());
					}
				} catch(InterruptedException e) {
					break;
				}
			}
		} finally {
			// when no entries are left, destroy the thread
			synchronized(DatabaseReferenceMonitor.class) {
				thread = null;
			}
		}
	}
	
	private static class WatchNode<T> {
		private List<NodeReference> referances;
		private Runnable listener;
		private int key;

		public WatchNode(int key, Runnable listener) {
			this.listener = listener;
			this.referances = new LinkedList<>();
		}

		public int getKey() { return key; }
		
		public void add(T obj, ReferenceQueue<T> queue) {
			referances.add(new NodeReference(obj, queue));
		}
		
		public boolean attemptCleanup() {
			// check if there are still live-references
			if(!referances.isEmpty()) 
				return false;
			
			try {
				// attempt to cleanup the resource
				if(listener != null) 
					listener.run();
				
			} catch(Exception e) { 
				// TODO: an error occurs during reference cleanup
				//		 can only really log the error :/
			}
			
			// return that the entity is gone
			return true;
		}
		
		public class NodeReference extends PhantomReference<T> {
			public NodeReference(T referent, ReferenceQueue<T> queue) {
				super(referent, queue);
			}

			public WatchNode<T> remove() {
				referances.remove(this);
				return WatchNode.this;
			}
		}
	}
}
