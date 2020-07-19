package edu.wit.alr.database.tracking;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.event.spi.PreLoadEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

interface HibernatePreEventListener extends 
	PreInsertEventListener, PreLoadEventListener, PreUpdateEventListener, PreDeleteEventListener  {
	
	public void beforeInsert(Object entity);
	public void beforeLoad(Object entity);
	public void beforeSave(Object entity);
	public void beforeDelete(Object entity);
	
	default boolean onPreInsert(PreInsertEvent event) { beforeInsert(event.getEntity()); return false; }
	default void onPreLoad(PreLoadEvent event) { beforeLoad(event.getEntity()); }
	default boolean onPreUpdate(PreUpdateEvent event) { beforeSave(event.getEntity()); return false; }
	default boolean onPreDelete(PreDeleteEvent event) { beforeDelete(event.getEntity()); return false; }
	
	default void appendListener(EventListenerRegistry registry) {
		registry.appendListeners(EventType.PRE_INSERT, this);
		registry.appendListeners(EventType.PRE_LOAD, this);
		registry.appendListeners(EventType.PRE_UPDATE, this);
		registry.appendListeners(EventType.PRE_DELETE, this);
	}
}

interface HibernatePostEventListener extends 
	PostInsertEventListener, PostLoadEventListener, PostUpdateEventListener, PostDeleteEventListener {

	public void afterInsert(Object entity);
	public void afterLoad(Object entity);
	public void afterSave(Object entity);
	public void afterDelete(Object entity);

	default void onPostInsert(PostInsertEvent event) { afterInsert(event.getEntity()); }
	default void onPostLoad(PostLoadEvent event) { afterLoad(event.getEntity()); }
	default void onPostUpdate(PostUpdateEvent event) { afterSave(event.getEntity()); }
	default void onPostDelete(PostDeleteEvent event) { afterDelete(event.getEntity()); }
	
	public default boolean requiresPostCommitHanding(EntityPersister persister) { return false; }
	
	default void appendListener(EventListenerRegistry registry) {
		registry.appendListeners(EventType.POST_INSERT, this);
		registry.appendListeners(EventType.POST_LOAD, this);
		registry.appendListeners(EventType.POST_UPDATE, this);
		registry.appendListeners(EventType.POST_DELETE, this);
	}
}

public interface HibernateEventListener extends HibernatePreEventListener, HibernatePostEventListener {
	default void appendListener(EventListenerRegistry registry) {
		HibernatePreEventListener.super.appendListener(registry);
		HibernatePostEventListener.super.appendListener(registry);
	}
}
