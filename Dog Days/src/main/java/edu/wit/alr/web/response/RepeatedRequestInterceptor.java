package edu.wit.alr.web.response;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 	This intercepter is responsible for detecting repeated requests from 
 * 	the <code>Request.js</code> library. <br/>
 * 
 * 	This happens when a redirect link
 * 	is sent back to <code>Request.js</code>. The intercepter responds with
 * 	a {@link NativeRedirectResponse} with containing the requested resource's URI.
 * 
 * 	@author cilfonej
 */

@Order(0)
@Component
public class RepeatedRequestInterceptor extends OncePerRequestFilter {
	private static final String REQUEST_ID_HEADER = "X-Request-ID";
	private static final long LIFESPAN_MILLI = 60_000; // lifespan of a request
	
	@Autowired 
	private ObjectMapper mapper;
	
	// lazy-init the map to a new instance
	private ConcurrentHashMap<String, Node> previousRequests = new ConcurrentHashMap<>();
	private Object lock = new Object(); // used to ensure only one thread updates "prev" at once
	private Node prev;
	
	// Data-structure:
	//		Quick lookup if request has happened
	//		check if that request is < TIMEOUT
	//		delete any entry older then TIMEOUT
	//		parallel operation (thread-safe)
	
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		
		String requestUUID = request.getHeader(REQUEST_ID_HEADER);
		
		// if there is no header, skip 
		if(requestUUID != null) {
			
			// get the previous request-node, if it exists
			Node node = previousRequests.get(requestUUID);
			
			// check if the current node is still valid/exists
			if(node != null && node.validateChain()) { 
				// if so, this is a redirect-request
				String uri = request.getPathInfo();
				NativeRedirectResponse redirectResponse = new NativeRedirectResponse(uri == null ? "/" : uri);

				response.setContentType("application/json");
				response.setStatus(HttpStatus.OK.value());
				
				// send native-redirect as response
				mapper.writeValue(response.getOutputStream(), redirectResponse);
				
				// request has been "handled"
				return;
				
			} else {
				// this is a new-request, or an expired-request
				synchronized(lock) {
					// record that the request happened and move on
					previousRequests.put(requestUUID, prev = new Node(prev, requestUUID));
				}
			}
		}
		
		chain.doFilter(request, response); 
	}
	
	private class Node {
		private Node prev;
		
		private String key;
		private Instant expireAt;
		
		public Node(Node prev, String key) {
			this.prev = prev;
			this.key = key;
			
			this.expireAt = Instant.now().plusMillis(LIFESPAN_MILLI);
		}
		
		/** 
		 * 	Checks if the current, or previous, node has expired
		 * 	If it has, then the node is removed from the map
		 */
		public boolean validateChain() {
			Instant now = Instant.now();
			boolean isExpired = expireAt.isBefore(now);
			
			if(isExpired) {
				// remove current node
				previousRequests.remove(key);
			}
			
			// if the previous node expired, only if this node has not
			if(isExpired || (prev != null && prev.expireAt.isBefore(now))) {
				Node node = prev;
				while(node != null) {
					// remove previous nodes
					previousRequests.remove(node.key); 
					node = node.prev;
				}
			}
			
			return !isExpired;
		}
	}
}
