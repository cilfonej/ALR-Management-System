package edu.wit.alr.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

	@Autowired
	private ApplicationEventPublisher publisher;
	
	public void fireEvent(ApplicationEvent event) {
		publisher.publishEvent(event);
	}
}
