import Std from "./StandardRevert";

export default {
	bindInputs: function(button, field, value) {
		var $group = $$("[data-form-input='" + field + "'], [data-group-input='" + field + "']");
		
		UID.exe($group, () => {
			Std.bindInputs(button, "street", 	value && value.streetAddress || '');
			Std.bindInputs(button, "city", 		value && value.city || '');
			Std.bindInputs(button, "state", 	value && value.state || '');
			Std.bindInputs(button, "zipcode", 	value && value.postalCode || '');
			Std.bindInputs(button, "country", 	value && value.country || '');
		});
	},
	
	revertField: function(button, field, value) {
		var $group = $$("[data-form-input='" + field + "'], [data-group-input='" + field + "']");
		
		UID.exe($group, () => {
			Std.revertField(button, "street", 	value && value.streetAddress || '');
			Std.revertField(button, "city", 	value && value.city || '');        
			Std.revertField(button, "state", 	value && value.state || '');       
			Std.revertField(button, "zipcode", 	value && value.postalCode || '');  
			Std.revertField(button, "country", 	value && value.country || '');     
		});
	}
};