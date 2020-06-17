export default {
	setupInput: function(input) {
		
	},
	
	getValue: function(input) {
		var value = $(input.ele).val();
		return Number(value);
	},
	
	onInput: function(input) {
		// TODO: a lot
	},
	
	validate: function(input) {
		var value = $(input.ele).val();
		
		// TODO: same
		
//		if(!date.isValid()) {
//			input.setError("Unable to parse date");
//			return false;
//		}
		
		return true;
	}
};