package edu.wit.alr.database.tracking;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseMonitorIntegrator implements Integrator {

	@Autowired
	private DatabaseEventMonitor listener;
	
	public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		EventListenerRegistry registry = serviceRegistry.getService(EventListenerRegistry.class);
		listener.appendListener(registry);
	}

	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {

	}
}
