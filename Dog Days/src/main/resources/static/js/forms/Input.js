var Input = (function() {
	var DEFAULT_PARAMS = {
		"min-length": 0
	};
	
	function setupInput(element, params) {
		var $ele = $(element);
		if($ele.length < 1) throw "cannot setup empty-input";
		element = $ele[0];
		
		// clone DEFAULT_PARAMS and merge with given params
		var base_params = Object.assign({}, DEFAULT_PARAMS);
		params = Object.assign(base_params, params);
		
		// save params to input DOM-Element
		element.input_param = params;
		element.input_counter = $ele.siblings(".form-input_counter");
		
		// bind input event-listeners
		$ele.on("input", e => onInput(element))
			.on("blur", e => validate(element));
		
		onInput(element);
	}
	
	function onInput(element) {
		var $ele = $(element);
		var param = element.input_param;
		
		// if there's a max-length
		if(param["max-length"]) {
			var max = param["max-length"];
			var val = $ele.val();
			
			// if value is longer then max-length
			if(val.length > max) {
				// reduce to only [max-length]
				$ele.val(val.substring(0, max));
			}
		}
		
		// --- ALL VISUAL UPDATE AFTER HERE ---
		
		var counter = element.input_counter;
		// if there is an element counter
		if(counter && counter.length > 0) {
			var len = $ele.val().length;
			var max = param["max-length"];
			counter.html(len + "&thinsp;/&thinsp;" + max);
		}
	}
	
	function validate(input) {
		var param = input.input_param || DEFAULT_PARAMS;
	}
	
	return {
		"setupInput": setupInput,
	};
})();