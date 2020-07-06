
export default {
	bindInputs: function(button, field, value) {
		var $input = $$("[data-form-input='" + field + "'], [data-group-input='" + field + "']");
		if($input.length < 1) {
			console.warn("Could not find field: " + field);
			return;
		}
		
		var input = $input[0].input
		if(!input) {
			console.warn("Field '" + field  + "' is missing *.input on element");
			return;
		}
		
		// notify the input of a revert-button, and reset its value to default
		input.setupRevertButton(button);
		input.setValue(value);
	},
	
	revertField: function(button, field, value) {
		var $input = $$("[data-form-input='" + field + "'], [data-group-input='" + field + "']");
		if($input.length < 1) {
			console.warn("Could not find field: " + field);
			return;
		}
		
		var input = $input[0].input
		if(!input) {
			console.warn("Field '" + field  + "' is missing *.input on element");
			return;
		}
		
		input.setValue(value);
	}
};
