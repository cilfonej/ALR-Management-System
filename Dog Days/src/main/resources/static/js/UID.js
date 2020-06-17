// ======================================== Globals ======================================== \\

var current_uid = null;

function $jsx(name) {
	return jsx[current_uid][name];
}

function $$(query) {
	return $("*[data-uid='" + current_uid + "'] " + query);
}

// ======================================== UID Library Functions ======================================== \\

var UID = { 
	/** Exposes a function to be run in a UID context */
	expose:	function(func) {
		return function(uid, ...args) {
			return UID.exe(uid, func.bind(null, ...args));
		}
	},
		
	/** Run a provided function within a given UID context */
	exe: function(uid, func) {
		// if a DOM Element is provided
		if(isElement(uid) || uid instanceof jQuery) {
			// search for the closest UID, and use that
			uid = $(uid).closest("*[data-uid]").attr("data-uid");
		}

		if(!uid) throw "Missing UID context! UID must be provided";
		
		// record old value (for nested calls)
		let old_uid = current_uid;
		current_uid = uid;
		
		try {
			return func();
		} finally {
			// pop old value back to current
			current_uid = old_uid;
		}
	},

	/** 
	 * 	Binds a function to a UID context, to be run later 
	 * 	uid: <optional> the context to bind to, else the current UID context is used if ignored
	 */
	bind: function(uid, func) {
		// check if use current UID
		if(typeof uid === 'function') {
			if(!current_uid) throw "Not in bound-scope! Cannot determine UID";
			
			func = uid;
			uid = current_uid;
		}
		
		return (...args) => UID.exe(uid, func.bind(null, ...args));
	},
	
	/** Performs clean-up process for UID element, including JSX unmount */
	disconnect: function(uid) {
		jsx.disconnectJSX(uid);
	},
	
	peekUID: function() { return current_uid; }
}

//======================================== Generic Util Functions ======================================== \\

/** Returns if object is a DOM Node */
function isNode(o) {
	// check if JS supports DOM2
	return typeof Node === "object" ? 
		/* if supported*/ o instanceof Node : 
		o && typeof o === "object" && typeof o.nodeType === "number" && typeof o.nodeName === "string";
}

/** Returns if object is a DOM Element */
function isElement(o){
	// check if JS supports DOM2
	return typeof HTMLElement === "object" ? 
		/* if supported*/ o instanceof HTMLElement :
		o && typeof o === "object" && o !== null && o.nodeType === 1 && typeof o.nodeName === "string";
}
