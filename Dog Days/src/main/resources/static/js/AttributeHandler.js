var AttrHandler = (function(){
	
	var attrib_listeners = {};
	
	// setup Observer to detect when new nodes are added to the DOM
	var observer = new MutationObserver(function(changes) {
		for(var record of changes) {
			if(record.addedNodes) {
				for(var node of record.addedNodes) {
					if(node.nodeType != Node.ELEMENT_NODE) continue;
					
					// look at all the attribute-listeners and check the node for the attribute
					for(var attr in attrib_listeners) {
						if(node.hasAttribute(attr)) {
							// invoke all the listeners
							$.each(attrib_listeners[attr], (index, listener) => {
								typeof listener === 'function' && listener(node);
							});
						}
						
						// check if node contains child with attribute
						$(node).find("[" + attr + "]").each((index, ele) => {
							$.each(attrib_listeners[attr], (index, listener) => {
								typeof listener === 'function' && listener(ele);
							});
						});
						
					}
				}
			}
		}
	});
	
	return {
		init: function() {
	    	// bind Observer to the root of the DOM
			observer.observe($("body")[0], { childList: true, subtree: true });
		},
		
		addAttribListener: function(attr_name, callback) {
			var listeners = attrib_listeners[attr_name] || [];
			listeners.push(callback);
			attrib_listeners[attr_name] = listeners;
		}
	}
})();

// call init after DOM is loaded
$(AttrHandler.init);