export default {
	setupInput: function(input) {
		
	},
	
	getValue: function(input) {
		var value = $(input.ele).val();
		return numeral(value).value();
	},
	
	onInput: function(input) {
		var value = $(input.ele).val();
		var param = input.params;
		
		// if the input is blank, return
		if(/^\s*$/.test(value)) return;
		
		// record if the string ends with a dot, as Numeral.js will remove it
		var trailingDot = param['type'] == 'decimal' && /\.$/.test(value);
		
		// user Numeral.js to parse and clean input
		var num_val = numeral(value).value() || 0;
		
		// if 'min-value' is defined
		if(typeof param["min-value"] !== 'undefined') {
			var min = Number(param["min-value"]);
			// clamp value to min-value
			if(num_val < min) num_val = min;
		}
		
		// if 'max-value' is defined
		if(typeof param["max-value"] !== 'undefined') {
			var max = Number(param["max-value"]);
			// clamp value to max-value
			if(num_val > max) num_val = max;
		}
		
		var default_pattern = param['type'] == 'decimal' && "0[.]0[0]" || "0";
		var formatted = numeral(num_val).format(param["pattern"] || default_pattern);
		
		// Update display value
		
		$(input.ele).val(formatted + (trailingDot && '.' || ''));
	},
	
	validate: function(input) {
		var value = $(input.ele).val();
		var parsed = numeral(value).value();
		
		if(isNaN(parsed)) {
			input.setError("Unable to parse number");
			return false;
		}
		
		return true;
	}
};