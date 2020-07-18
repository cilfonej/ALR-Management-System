var Request = (function() {
	
	function sendRequest(url, data, config, done, error, final) {
		// check if no 'config' was provided
		if(typeof config === 'function') {
			// make sure there was only 5 parameters
			if(typeof final !== 'undefined')
				throw "config must be a plain JSON-Object";
			
			// shift args
			final = error; error = done; done = config;
			config = {};
		}
		
		config = config || {};
		
		var request = {
			"url": url,
			"method": "POST",
			
			"headers": {},
			
			"contentType": "application/json",
			// unless [config.raw_data == true] then 'stringify' the provided data
			"data": config.raw_data && data || JSON.stringify(data)
		};
		
		// merge 'request' and 'config' with 'config' values having priority 
		var request_param = {...request, ...config};
		
		// tags request with unique ID
		request_param.headers["X-Request-ID"] = generateRequestUUID();
		
		$.ajax(request_param)
			.done((data, status, xhr) => handleStandardResponse(xhr, data) || done && done.call(xhr, data))
			.fail((xhr, status, errorMsg) => error && error.call(xhr, errorMsg, xhr.status))
			.always((xhr, status) => final && final.call(null));
	}
	
	function handleStandardResponse(xhr, data) {
		switch(data.action) {
			case "native_redirect":
				window.location.replace(data.uri);
			return true;
		
			case "redirect":
				history.pushState(null, "", data.url);
				$(".page_content").html(data.pageHTML);
			return true;
		}
	}
	
	// Source from
	// https://www.w3resource.com/javascript-exercises/javascript-math-exercise-23.php
	function generateRequestUUID() {
		var dt = new Date().getTime();
		
		var uuid = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function(c) {
			var r = (dt + Math.random() * 16) % 16 | 0;
			dt = Math.floor(dt / 16);
			return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
		});
		
		return uuid;
	}
	
//	======================================== Export ======================================== \\
	
	return {
		"send": sendRequest,
	};
})();