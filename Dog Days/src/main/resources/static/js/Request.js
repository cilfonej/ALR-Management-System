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
			
			"contentType": "application/json",
			// unless [config.raw_data == true] then 'stringify' the provided data
			"data": config.raw_data && data || JSON.stringify(data)
		};
		
		// merge 'request' and 'config' with 'config' values having priority 
		var request_param = {...request, ...config};
		
		$.ajax(request_param)
			.done((data, status, xhr) => handleStandardResponse(xhr, data) || done && done.call(xhr, data))
			.fail((xhr, status, errorMsg) => error && error.call(xhr, errorMsg, xhr.status))
			.always((xhr, status) => final && final.call(null));
	}
	
	function handleStandardResponse(xhr, data) {
		switch(data.action) {
			case "redirect":
				history.pushState({ url: data.url }, "", data.url);
				$(".page_content").html(data.pageHTML);
			return true;
			
			case "replace":
				$(data.query).replaceWith(data.replacementHTML);
			return true;
		}
	}

//	======================================== Setup ======================================== \\
	
	$(window).on("popstate", function(e) {
		if(e.originalEvent.state) {
			// TODO: should probably just re-request page content, and swap it
			location.reload();
		}
	});
	
//	======================================== Export ======================================== \\
	
	return {
		"send": sendRequest,
	};
})();